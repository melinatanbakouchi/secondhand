package ir.secondhand.frontend.util;

import ir.secondhand.frontend.config.ApiConfig;
import ir.secondhand.frontend.dto.response.AdvertisementSummaryResponse;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * ساخت کارت نمایش آگهی (مورد استفاده در صفحه خانه، آگهی‌های من و علاقه‌مندی‌ها).
 */
public final class AdCardFactory {

    private static final double CARD_WIDTH = 250;
    private static final double IMAGE_HEIGHT = 150;

    private AdCardFactory() {
    }

    public static VBox create(AdvertisementSummaryResponse ad, Runnable onClick) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setPrefWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);
        card.setOnMouseClicked(event -> onClick.run());

        StackPane imagePane = buildImage(ad.getCoverImagePath());
        imagePane.setPrefHeight(IMAGE_HEIGHT);
        imagePane.setMaxWidth(Double.MAX_VALUE);

        VBox content = new VBox(6);
        content.setPadding(new Insets(12));

        Label title = new Label(ad.getTitle());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);
        title.setMaxWidth(CARD_WIDTH - 24);

        Label price = new Label(PriceFormatter.format(ad.getPrice()));
        price.getStyleClass().add("card-price");

        HBox metaRow = new HBox(6);
        Label meta = new Label(safe(ad.getCityName()) + " • " + safe(ad.getCategoryTitle()));
        meta.getStyleClass().add("card-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        metaRow.getChildren().addAll(meta, spacer);

        HBox bottomRow = new HBox(8);
        bottomRow.getChildren().add(buildRating(ad.getAverageRating(), ad.getRatingCount()));
        if (!"ACTIVE".equals(ad.getStatus())) {
            Region bottomSpacer = new Region();
            HBox.setHgrow(bottomSpacer, Priority.ALWAYS);
            bottomRow.getChildren().addAll(bottomSpacer, StatusLabelFactory.create(ad.getStatus()));
        }

        content.getChildren().addAll(title, price, metaRow, bottomRow);
        card.getChildren().addAll(imagePane, content);
        return card;
    }

    private static StackPane buildImage(String coverImagePath) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("card-image-placeholder");
        if (coverImagePath != null && !coverImagePath.isBlank()) {
            try {
                Image image = new Image(ApiConfig.FILES_BASE_URL + "/uploads/" + coverImagePath, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(CARD_WIDTH);
                imageView.setFitHeight(IMAGE_HEIGHT);
                imageView.setPreserveRatio(false);
                pane.getChildren().add(imageView);
                return pane;
            } catch (Exception ignored) {
                // در صورت خطا در بارگذاری تصویر، جای‌خالی نمایش داده می‌شود.
            }
        }
        Label placeholder = new Label("بدون تصویر");
        placeholder.getStyleClass().add("card-meta");
        pane.getChildren().add(placeholder);
        return pane;
    }

    private static Label buildRating(double averageRating, long ratingCount) {
        String text = ratingCount == 0 ? "بدون امتیاز" : String.format("★ %.1f (%d)", averageRating, ratingCount);
        Label label = new Label(text);
        label.getStyleClass().add("card-meta");
        return label;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
