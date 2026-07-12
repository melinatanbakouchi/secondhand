package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.frontend.service.AdvertisementService;
import ir.secondhand.frontend.session.SessionManager;
import ir.secondhand.frontend.util.AdCardFactory;
import ir.secondhand.frontend.util.AlertHelper;
import ir.secondhand.frontend.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class MyAdsController {

    @FXML private Label emptyLabel;
    @FXML private FlowPane adsContainer;

    private final AdvertisementService advertisementService = new AdvertisementService();

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            Navigator.switchTo("/fxml/login.fxml", "ورود به سامانه");
            return;
        }
        loadMyAdvertisements();
    }

    private void loadMyAdvertisements() {
        try {
            List<AdvertisementSummaryResponse> ads = advertisementService.getMyAdvertisements();
            adsContainer.getChildren().clear();
            emptyLabel.setVisible(ads.isEmpty());
            emptyLabel.setManaged(ads.isEmpty());
            for (AdvertisementSummaryResponse ad : ads) {
                adsContainer.getChildren().add(AdCardFactory.create(ad,
                        () -> Navigator.switchTo("/fxml/ad_detail.fxml", ad.getTitle(), ad.getId())));
            }
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت آگهی‌های شما با خطا مواجه شد: " + ex.getMessage());
        }
    }
}
