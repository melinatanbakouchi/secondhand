package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.CategoryRequest;
import ir.secondhand.frontend.dto.request.CityRequest;
import ir.secondhand.frontend.dto.response.AdminStatsResponse;
import ir.secondhand.frontend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.frontend.dto.response.CategoryResponse;
import ir.secondhand.frontend.dto.response.CityResponse;
import ir.secondhand.frontend.dto.response.UserResponse;
import ir.secondhand.frontend.service.AdminService;
import ir.secondhand.frontend.service.CategoryService;
import ir.secondhand.frontend.service.CityService;
import ir.secondhand.frontend.session.SessionManager;
import ir.secondhand.frontend.util.AlertHelper;
import ir.secondhand.frontend.util.Navigator;
import ir.secondhand.frontend.util.PriceFormatter;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardController {

    @FXML private FlowPane statsContainer;
    @FXML private VBox pendingContainer;
    @FXML private VBox usersContainer;
    @FXML private VBox categoriesContainer;
    @FXML private VBox citiesContainer;

    private final AdminService adminService = new AdminService();
    private final CategoryService categoryService = new CategoryService();
    private final CityService cityService = new CityService();

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isLoggedIn() || !SessionManager.getInstance().isAdmin()) {
            AlertHelper.showError("دسترسی به این بخش فقط برای مدیر سیستم امکان‌پذیر است.");
            Navigator.switchTo("/fxml/home.fxml", "بازار دست دوم");
            return;
        }
        loadStats();
        loadPendingAdvertisements();
        loadUsers();
        loadCategories();
        loadCities();
    }

    /* ---------------- آمار کلی ---------------- */

    private void loadStats() {
        try {
            AdminStatsResponse stats = adminService.getStats();
            statsContainer.getChildren().clear();
            statsContainer.getChildren().addAll(
                    buildStatCard("تعداد کل کاربران", stats.getTotalUsers()),
                    buildStatCard("کاربران مسدود", stats.getBlockedUsers()),
                    buildStatCard("تعداد کل آگهی‌ها", stats.getTotalAdvertisements()),
                    buildStatCard("در انتظار بررسی", stats.getPendingAdvertisements()),
                    buildStatCard("آگهی‌های فعال", stats.getActiveAdvertisements()),
                    buildStatCard("آگهی‌های فروخته‌شده", stats.getSoldAdvertisements()));
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت آمار با خطا مواجه شد: " + ex.getMessage());
        }
    }

    private VBox buildStatCard(String title, long value) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(200);
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.getStyleClass().add("stat-value");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-label");
        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    /* ---------------- آگهی‌های در انتظار بررسی ---------------- */

    private void loadPendingAdvertisements() {
        try {
            List<AdvertisementSummaryResponse> pending = adminService.getPendingAdvertisements();
            pendingContainer.getChildren().clear();
            if (pending.isEmpty()) {
                pendingContainer.getChildren().add(buildEmptyLabel("در حال حاضر آگهی در انتظار بررسی وجود ندارد."));
            }
            for (AdvertisementSummaryResponse ad : pending) {
                pendingContainer.getChildren().add(buildPendingRow(ad));
            }
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت آگهی‌های در انتظار بررسی با خطا مواجه شد: " + ex.getMessage());
        }
    }

    private HBox buildPendingRow(AdvertisementSummaryResponse ad) {
        HBox row = new HBox(12);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(14));

        VBox textBox = new VBox(4);
        Label titleLabel = new Label(ad.getTitle());
        titleLabel.getStyleClass().add("card-title");
        Label metaLabel = new Label(safe(ad.getCityName()) + " • " + safe(ad.getCategoryTitle()) + " • " + PriceFormatter.format(ad.getPrice()));
        metaLabel.getStyleClass().add("card-meta");
        Label dateLabel = new Label(ad.getCreatedAt() == null ? "" :
                "تاریخ ثبت: " + ad.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        dateLabel.getStyleClass().add("card-meta");
        textBox.getChildren().addAll(titleLabel, metaLabel, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewButton = new Button("مشاهده جزئیات");
        viewButton.getStyleClass().add("btn-secondary");
        viewButton.setOnAction(e -> Navigator.switchTo("/fxml/ad_detail.fxml", ad.getTitle(), ad.getId()));

        Button approveButton = new Button("تایید");
        approveButton.getStyleClass().add("btn-primary");
        approveButton.setOnAction(e -> onApprove(ad.getId()));

        Button rejectButton = new Button("رد");
        rejectButton.getStyleClass().add("btn-danger");
        rejectButton.setOnAction(e -> onReject(ad.getId()));

        row.getChildren().addAll(textBox, spacer, viewButton, approveButton, rejectButton);
        return row;
    }

    private void onApprove(Long adId) {
        try {
            adminService.approveAdvertisement(adId);
            loadPendingAdvertisements();
            loadStats();
        } catch (ApiException ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private void onReject(Long adId) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("رد آگهی");

        Label label = new Label("دلیل رد آگهی را وارد کنید:");
        label.getStyleClass().add("field-label");
        TextArea reasonArea = new TextArea();
        reasonArea.setPrefRowCount(3);
        reasonArea.setWrapText(true);

        Button submitButton = new Button("رد آگهی");
        submitButton.getStyleClass().add("btn-danger");
        submitButton.setOnAction(e -> {
            String reason = reasonArea.getText() == null ? "" : reasonArea.getText().trim();
            if (reason.isEmpty()) {
                AlertHelper.showWarning("لطفا دلیل رد آگهی را وارد کنید.");
                return;
            }
            try {
                adminService.rejectAdvertisement(adId, reason);
                dialog.close();
                loadPendingAdvertisements();
                loadStats();
            } catch (ApiException ex) {
                AlertHelper.showError(ex.getMessage());
            }
        });

        VBox layout = new VBox(12, label, reasonArea, submitButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");
        javafx.scene.Scene scene = new javafx.scene.Scene(layout, 340, 220);
        Navigator.applyRtlAndStyle(scene);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /* ---------------- کاربران ---------------- */

    private void loadUsers() {
        try {
            List<UserResponse> users = adminService.getAllUsers();
            usersContainer.getChildren().clear();
            for (UserResponse user : users) {
                usersContainer.getChildren().add(buildUserRow(user));
            }
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت کاربران با خطا مواجه شد: " + ex.getMessage());
        }
    }

    private HBox buildUserRow(UserResponse user) {
        HBox row = new HBox(12);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(14));

        VBox textBox = new VBox(4);
        Label nameLabel = new Label(user.getFullName() + " (" + user.getUsername() + ")");
        nameLabel.getStyleClass().add("card-title");
        Label metaLabel = new Label(user.getPhoneNumber() + " • " + translateRole(user.getRole()) + " • " + translateStatus(user.getStatus()));
        metaLabel.getStyleClass().add("card-meta");
        textBox.getChildren().addAll(nameLabel, metaLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(textBox, spacer);

        if (!"ADMIN".equals(user.getRole())) {
            boolean blocked = "BLOCKED".equals(user.getStatus());
            Button toggleButton = new Button(blocked ? "رفع مسدودیت" : "مسدود کردن");
            toggleButton.getStyleClass().add(blocked ? "btn-primary" : "btn-danger");
            toggleButton.setOnAction(e -> onToggleUserBlock(user.getId(), blocked));
            row.getChildren().add(toggleButton);
        }
        return row;
    }

    private void onToggleUserBlock(Long userId, boolean currentlyBlocked) {
        try {
            if (currentlyBlocked) {
                adminService.unblockUser(userId);
            } else {
                adminService.blockUser(userId);
            }
            loadUsers();
            loadStats();
        } catch (ApiException ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private String translateRole(String role) {
        return "ADMIN".equals(role) ? "مدیر سیستم" : "کاربر عادی";
    }

    private String translateStatus(String status) {
        return "BLOCKED".equals(status) ? "مسدود" : "فعال";
    }

    /* ---------------- دسته‌بندی‌ها ---------------- */

    private void loadCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            categoriesContainer.getChildren().clear();
            categoriesContainer.getChildren().add(buildAddCategoryForm(categories));
            for (CategoryResponse category : categories) {
                categoriesContainer.getChildren().add(buildCategoryRow(category));
            }
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت دسته‌بندی‌ها با خطا مواجه شد: " + ex.getMessage());
        }
    }

    private HBox buildAddCategoryForm(List<CategoryResponse> categories) {
        HBox form = new HBox(10);
        form.getStyleClass().add("card");
        form.setPadding(new Insets(14));

        TextField titleField = new TextField();
        titleField.setPromptText("عنوان دسته‌بندی جدید");
        ComboBox<CategoryResponse> parentCombo = new ComboBox<>();
        parentCombo.setPromptText("زیرمجموعه (اختیاری)");
        parentCombo.getItems().addAll(categories);

        Button addButton = new Button("افزودن دسته‌بندی");
        addButton.getStyleClass().add("btn-primary");
        addButton.setOnAction(e -> {
            String title = titleField.getText() == null ? "" : titleField.getText().trim();
            if (title.isEmpty()) {
                AlertHelper.showWarning("عنوان دسته‌بندی را وارد کنید.");
                return;
            }
            Long parentId = parentCombo.getValue() == null ? null : parentCombo.getValue().getId();
            try {
                categoryService.createCategory(new CategoryRequest(title, null, parentId));
                loadCategories();
            } catch (ApiException ex) {
                AlertHelper.showError(ex.getMessage());
            }
        });

        form.getChildren().addAll(titleField, parentCombo, addButton);
        return form;
    }

    private HBox buildCategoryRow(CategoryResponse category) {
        HBox row = new HBox(12);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(14));

        VBox textBox = new VBox(4);
        Label titleLabel = new Label(category.getTitle());
        titleLabel.getStyleClass().add("card-title");
        Label metaLabel = new Label(category.getParentTitle() == null ? "دسته‌بندی اصلی" : "زیرمجموعه: " + category.getParentTitle());
        metaLabel.getStyleClass().add("card-meta");
        textBox.getChildren().addAll(titleLabel, metaLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteButton = new Button("حذف");
        deleteButton.getStyleClass().add("btn-danger");
        deleteButton.setOnAction(e -> {
            if (!AlertHelper.confirm("آیا از حذف این دسته‌بندی مطمئن هستید؟")) {
                return;
            }
            try {
                categoryService.deleteCategory(category.getId());
                loadCategories();
            } catch (ApiException ex) {
                AlertHelper.showError(ex.getMessage());
            }
        });

        row.getChildren().addAll(textBox, spacer, deleteButton);
        return row;
    }

    /* ---------------- شهرها ---------------- */

    private void loadCities() {
        try {
            List<CityResponse> cities = cityService.getAllCities();
            citiesContainer.getChildren().clear();
            citiesContainer.getChildren().add(buildAddCityForm());
            for (CityResponse city : cities) {
                citiesContainer.getChildren().add(buildCityRow(city));
            }
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت شهرها با خطا مواجه شد: " + ex.getMessage());
        }
    }

    private HBox buildAddCityForm() {
        HBox form = new HBox(10);
        form.getStyleClass().add("card");
        form.setPadding(new Insets(14));

        TextField nameField = new TextField();
        nameField.setPromptText("نام شهر جدید");

        Button addButton = new Button("افزودن شهر");
        addButton.getStyleClass().add("btn-primary");
        addButton.setOnAction(e -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            if (name.isEmpty()) {
                AlertHelper.showWarning("نام شهر را وارد کنید.");
                return;
            }
            try {
                cityService.createCity(new CityRequest(name));
                loadCities();
            } catch (ApiException ex) {
                AlertHelper.showError(ex.getMessage());
            }
        });

        form.getChildren().addAll(nameField, addButton);
        return form;
    }

    private HBox buildCityRow(CityResponse city) {
        HBox row = new HBox(12);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(14));

        Label nameLabel = new Label(city.getName());
        nameLabel.getStyleClass().add("card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteButton = new Button("حذف");
        deleteButton.getStyleClass().add("btn-danger");
        deleteButton.setOnAction(e -> {
            if (!AlertHelper.confirm("آیا از حذف این شهر مطمئن هستید؟")) {
                return;
            }
            try {
                cityService.deleteCity(city.getId());
                loadCities();
            } catch (ApiException ex) {
                AlertHelper.showError(ex.getMessage());
            }
        });

        row.getChildren().addAll(nameLabel, spacer, deleteButton);
        return row;
    }

    private Label buildEmptyLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("empty-state");
        return label;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
