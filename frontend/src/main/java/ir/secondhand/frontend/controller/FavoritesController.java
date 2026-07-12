package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.frontend.service.FavoriteService;
import ir.secondhand.frontend.session.SessionManager;
import ir.secondhand.frontend.util.AdCardFactory;
import ir.secondhand.frontend.util.AlertHelper;
import ir.secondhand.frontend.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class FavoritesController {

    @FXML private Label emptyLabel;
    @FXML private FlowPane adsContainer;

    private final FavoriteService favoriteService = new FavoriteService();

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            Navigator.switchTo("/fxml/login.fxml", "ورود به سامانه");
            return;
        }
        loadFavorites();
    }

    private void loadFavorites() {
        try {
            List<AdvertisementSummaryResponse> ads = favoriteService.getMyFavorites();
            adsContainer.getChildren().clear();
            emptyLabel.setVisible(ads.isEmpty());
            emptyLabel.setManaged(ads.isEmpty());
            for (AdvertisementSummaryResponse ad : ads) {
                adsContainer.getChildren().add(AdCardFactory.create(ad,
                        () -> Navigator.switchTo("/fxml/ad_detail.fxml", ad.getTitle(), ad.getId())));
            }
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت علاقه‌مندی‌ها با خطا مواجه شد: " + ex.getMessage());
        }
    }
}
