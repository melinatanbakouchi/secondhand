package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.config.ApiConfig;
import ir.secondhand.frontend.dto.response.AdvertisementImageResponse;
import ir.secondhand.frontend.dto.response.AdvertisementResponse;
import ir.secondhand.frontend.service.AdvertisementService;
import ir.secondhand.frontend.service.ConversationService;
import ir.secondhand.frontend.service.FavoriteService;
import ir.secondhand.frontend.service.RatingService;
import ir.secondhand.frontend.session.SessionManager;
import ir.secondhand.frontend.util.AlertHelper;
import ir.secondhand.frontend.util.Navigator;
import ir.secondhand.frontend.util.PriceFormatter;
import ir.secondhand.frontend.util.StatusLabelFactory;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class AdDetailController {

    @FXML private StackPane imagePane;
    @FXML private Label titleLabel;
    @FXML private HBox statusBox;
    @FXML private Label priceLabel;
    @FXML private Label metaLabel;
    @FXML private Label dateLabel;
    @FXML private Label ownerLabel;
    @FXML private Label ownerRatingLabel;
    @FXML private FlowPane actionsBox;
    @FXML private Label descriptionLabel;
    @FXML private VBox managementBox;
    @FXML private FlowPane imagesManagementBox;

    private final AdvertisementService advertisementService = new AdvertisementService();
    private final FavoriteService favoriteService = new FavoriteService();
    private final ConversationService conversationService = new ConversationService();
    private final RatingService ratingService = new RatingService();

    private Long advertisementId;
    private AdvertisementResponse currentAd;

    @FXML
    public void initialize() {
        Long adId = Navigator.consumePayload();
        if (adId == null) {
            AlertHelper.showError("آگهی مورد نظر پیدا نشد.");
            Navigator.switchTo("/fxml/home.fxml", "بازار دست دوم");
            return;
        }
        this.advertisementId = adId;
        loadAdvertisement();
    }

    private void loadAdvertisement() {
        try {
            currentAd = advertisementService.getById(advertisementId);
            render();
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت اطلاعات آگهی با خطا مواجه شد: " + ex.getMessage());
            Navigator.switchTo("/fxml/home.fxml", "بازار دست دوم");
        }
    }

    private void render() {
        titleLabel.setText(currentAd.getTitle());
        priceLabel.setText(PriceFormatter.format(currentAd.getPrice()));
        metaLabel.setText(safe(currentAd.getCityName()) + " • " + safe(currentAd.getCategoryTitle()));
        dateLabel.setText(currentAd.getCreatedAt() == null ? "" :
                "تاریخ ثبت: " + currentAd.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        ownerLabel.setText(currentAd.getOwnerFullName() + " - " + currentAd.getOwnerPhoneNumber());
        ownerRatingLabel.setText(currentAd.getSellerRatingCount() == 0
                ? "این فروشنده هنوز امتیازی دریافت نکرده است."
                : String.format("★ %.1f از ۵ (%d نظر ثبت‌شده)", currentAd.getSellerAverageRating(), currentAd.getSellerRatingCount()));
        descriptionLabel.setText(currentAd.getDescription());

        statusBox.getChildren().clear();
        statusBox.getChildren().add(StatusLabelFactory.create(currentAd.getStatus()));
        if (currentAd.getRejectionReason() != null && !currentAd.getRejectionReason().isBlank()) {
            Label reasonLabel = new Label("دلیل رد: " + currentAd.getRejectionReason());
            reasonLabel.getStyleClass().add("error-label");
            statusBox.getChildren().add(reasonLabel);
        }

        renderImage();
        renderActions();
    }

    private void renderImage() {
        imagePane.getChildren().clear();
        if (currentAd.getImages() != null && !currentAd.getImages().isEmpty()) {
            String path = currentAd.getImages().get(0).getImagePath();
            try {
                Image image = new Image(ApiConfig.FILES_BASE_URL + "/uploads/" + path, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(380);
                imageView.setFitHeight(280);
                imageView.setPreserveRatio(false);
                imagePane.getChildren().add(imageView);
                return;
            } catch (Exception ignored) {
                // ادامه به نمایش جای‌خالی
            }
        }
        Label placeholder = new Label("بدون تصویر");
        placeholder.getStyleClass().add("card-meta");
        imagePane.getChildren().add(placeholder);
    }

    private void renderActions() {
        actionsBox.getChildren().clear();
        managementBox.setManaged(false);
        managementBox.setVisible(false);

        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn()) {
            Button loginButton = new Button("برای گفت‌وگو و علاقه‌مندی وارد شوید");
            loginButton.getStyleClass().add("btn-secondary");
            loginButton.setOnAction(e -> Navigator.switchTo("/fxml/login.fxml", "ورود به سامانه"));
            actionsBox.getChildren().add(loginButton);
            return;
        }

        boolean isOwner = session.getUserId().equals(currentAd.getOwnerId());
        if (isOwner) {
            renderOwnerActions();
        } else {
            renderBuyerActions();
        }
    }

    private void renderOwnerActions() {
        Button editButton = new Button("ویرایش آگهی");
        editButton.getStyleClass().add("btn-secondary");
        editButton.setOnAction(e -> Navigator.switchTo("/fxml/ad_form.fxml", "ویرایش آگهی", currentAd.getId()));

        Button deleteButton = new Button("حذف آگهی");
        deleteButton.getStyleClass().add("btn-danger");
        deleteButton.setOnAction(e -> onDeleteAdvertisement());

        actionsBox.getChildren().addAll(editButton, deleteButton);

        if ("ACTIVE".equals(currentAd.getStatus())) {
            Button soldButton = new Button("علامت‌گذاری به‌عنوان فروخته‌شده");
            soldButton.getStyleClass().add("btn-primary");
            soldButton.setOnAction(e -> onMarkAsSold());
            actionsBox.getChildren().add(soldButton);
        }

        managementBox.setManaged(true);
        managementBox.setVisible(true);
        renderImageManagement();
    }

    private void renderImageManagement() {
        imagesManagementBox.getChildren().clear();
        if (currentAd.getImages() != null) {
            for (AdvertisementImageResponse image : currentAd.getImages()) {
                imagesManagementBox.getChildren().add(buildImageManagementItem(image));
            }
        }
        Button addImageButton = new Button("افزودن تصویر");
        addImageButton.getStyleClass().add("btn-secondary");
        addImageButton.setOnAction(e -> onAddImage());
        imagesManagementBox.getChildren().add(addImageButton);
    }

    private VBox buildImageManagementItem(AdvertisementImageResponse image) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(6));
        ImageView imageView = new ImageView(new Image(ApiConfig.FILES_BASE_URL + "/uploads/" + image.getImagePath(), true));
        imageView.setFitWidth(120);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(false);

        Button deleteButton = new Button("حذف");
        deleteButton.getStyleClass().add("btn-danger");
        deleteButton.setOnAction(e -> onDeleteImage(image.getId()));

        box.getChildren().addAll(imageView, deleteButton);
        return box;
    }

    private void renderBuyerActions() {
        boolean isFavorite;
        try {
            isFavorite = favoriteService.isFavorite(currentAd.getId());
        } catch (ApiException ex) {
            isFavorite = false;
        }

        Button favoriteButton = new Button(isFavorite ? "حذف از علاقه‌مندی‌ها" : "افزودن به علاقه‌مندی‌ها");
        favoriteButton.getStyleClass().add(isFavorite ? "btn-danger" : "btn-secondary");
        favoriteButton.setOnAction(e -> onToggleFavorite(favoriteButton, isFavorite));

        Button chatButton = new Button("شروع گفت‌وگو با فروشنده");
        chatButton.getStyleClass().add("btn-primary");
        chatButton.setOnAction(e -> onStartConversation());

        Button rateButton = new Button("ثبت امتیاز به فروشنده");
        rateButton.getStyleClass().add("btn-secondary");
        rateButton.setOnAction(e -> onRateSeller());

        actionsBox.getChildren().addAll(favoriteButton, chatButton, rateButton);
    }

    private void onToggleFavorite(Button button, boolean currentlyFavorite) {
        try {
            if (currentlyFavorite) {
                favoriteService.removeFavorite(currentAd.getId());
            } else {
                favoriteService.addFavorite(currentAd.getId());
            }
            renderActions();
        } catch (ApiException ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private void onStartConversation() {
        try {
            var conversation = conversationService.startConversation(currentAd.getId());
            Navigator.switchTo("/fxml/conversation_detail.fxml", "گفت‌وگو", conversation.getId());
        } catch (ApiException ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private void onRateSeller() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("ثبت امتیاز به فروشنده");

        Label scoreLabel = new Label("امتیاز (۱ تا ۵)");
        scoreLabel.getStyleClass().add("field-label");
        ComboBox<Integer> scoreCombo = new ComboBox<>();
        scoreCombo.getItems().addAll(1, 2, 3, 4, 5);
        scoreCombo.getSelectionModel().select(4);

        Label commentLabel = new Label("نظر شما (اختیاری)");
        commentLabel.getStyleClass().add("field-label");
        TextArea commentArea = new TextArea();
        commentArea.setPrefRowCount(3);
        commentArea.setWrapText(true);

        Button submitButton = new Button("ثبت امتیاز");
        submitButton.getStyleClass().add("btn-primary");
        submitButton.setOnAction(e -> {
            try {
                ratingService.submitRating(currentAd.getId(), scoreCombo.getValue(), commentArea.getText());
                AlertHelper.showInfo("امتیاز شما با موفقیت ثبت شد.");
                dialog.close();
                loadAdvertisement();
            } catch (ApiException ex) {
                AlertHelper.showError(ex.getMessage());
            }
        });

        VBox layout = new VBox(12, scoreLabel, scoreCombo, commentLabel, commentArea, submitButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        javafx.scene.Scene scene = new javafx.scene.Scene(layout, 320, 300);
        Navigator.applyRtlAndStyle(scene);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void onDeleteAdvertisement() {
        if (!AlertHelper.confirm("آیا از حذف این آگهی مطمئن هستید؟")) {
            return;
        }
        try {
            advertisementService.delete(currentAd.getId());
            AlertHelper.showInfo("آگهی با موفقیت حذف شد.");
            Navigator.switchTo("/fxml/my_ads.fxml", "آگهی‌های من");
        } catch (ApiException ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private void onMarkAsSold() {
        if (!AlertHelper.confirm("آیا این آگهی فروخته شده است؟")) {
            return;
        }
        try {
            advertisementService.markAsSold(currentAd.getId());
            loadAdvertisement();
        } catch (ApiException ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private void onAddImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب تصویر آگهی");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("فایل‌های تصویری", "*.jpg", "*.jpeg", "*.png", "*.webp"));
        File file = fileChooser.showOpenDialog(Navigator.getPrimaryStage());
        if (file == null) {
            return;
        }
        try {
            advertisementService.addImage(currentAd.getId(), file);
            loadAdvertisement();
        } catch (ApiException ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private void onDeleteImage(Long imageId) {
        try {
            advertisementService.deleteImage(currentAd.getId(), imageId);
            loadAdvertisement();
        } catch (ApiException ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
