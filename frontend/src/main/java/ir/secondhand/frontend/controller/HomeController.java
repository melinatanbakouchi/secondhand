package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.frontend.dto.response.CategoryResponse;
import ir.secondhand.frontend.dto.response.CityResponse;
import ir.secondhand.frontend.service.AdvertisementService;
import ir.secondhand.frontend.service.CategoryService;
import ir.secondhand.frontend.service.CityService;
import ir.secondhand.frontend.util.AdCardFactory;
import ir.secondhand.frontend.util.AlertHelper;
import ir.secondhand.frontend.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.math.BigDecimal;
import java.util.List;

public class HomeController {

    @FXML private TextField keywordField;
    @FXML private ComboBox<CategoryResponse> categoryCombo;
    @FXML private ComboBox<CityResponse> cityCombo;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private Label resultCountLabel;
    @FXML private Label emptyLabel;
    @FXML private FlowPane adsContainer;

    private final AdvertisementService advertisementService = new AdvertisementService();
    private final CategoryService categoryService = new CategoryService();
    private final CityService cityService = new CityService();

    @FXML
    public void initialize() {
        loadFilters();
        performSearch();
    }

    private void loadFilters() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            categoryCombo.getItems().addAll(categories);
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت دسته‌بندی‌ها با خطا مواجه شد: " + ex.getMessage());
        }
        try {
            List<CityResponse> cities = cityService.getAllCities();
            cityCombo.getItems().addAll(cities);
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت شهرها با خطا مواجه شد: " + ex.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        performSearch();
    }

    @FXML
    private void onClearFilters() {
        keywordField.clear();
        categoryCombo.getSelectionModel().clearSelection();
        cityCombo.getSelectionModel().clearSelection();
        minPriceField.clear();
        maxPriceField.clear();
        performSearch();
    }

    private void performSearch() {
        try {
            String keyword = keywordField.getText();
            Long categoryId = categoryCombo.getValue() == null ? null : categoryCombo.getValue().getId();
            Long cityId = cityCombo.getValue() == null ? null : cityCombo.getValue().getId();
            BigDecimal minPrice = parsePrice(minPriceField.getText());
            BigDecimal maxPrice = parsePrice(maxPriceField.getText());

            List<AdvertisementSummaryResponse> results =
                    advertisementService.search(keyword, categoryId, cityId, minPrice, maxPrice);

            renderResults(results);
        } catch (ApiException ex) {
            AlertHelper.showError("جست‌وجوی آگهی با خطا مواجه شد: " + ex.getMessage());
        }
    }

    private void renderResults(List<AdvertisementSummaryResponse> results) {
        adsContainer.getChildren().clear();
        resultCountLabel.setText(results.isEmpty() ? "" : results.size() + " آگهی یافت شد");
        emptyLabel.setVisible(results.isEmpty());
        emptyLabel.setManaged(results.isEmpty());

        for (AdvertisementSummaryResponse ad : results) {
            adsContainer.getChildren().add(AdCardFactory.create(ad,
                    () -> Navigator.switchTo("/fxml/ad_detail.fxml", ad.getTitle(), ad.getId())));
        }
    }

    private BigDecimal parsePrice(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
