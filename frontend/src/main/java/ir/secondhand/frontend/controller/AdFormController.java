package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.AdvertisementRequest;
import ir.secondhand.frontend.dto.response.AdvertisementResponse;
import ir.secondhand.frontend.dto.response.CategoryResponse;
import ir.secondhand.frontend.dto.response.CityResponse;
import ir.secondhand.frontend.service.AdvertisementService;
import ir.secondhand.frontend.service.CategoryService;
import ir.secondhand.frontend.service.CityService;
import ir.secondhand.frontend.util.AlertHelper;
import ir.secondhand.frontend.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

/**
 * فرم یکسان برای ثبت آگهی جدید و ویرایش آگهی موجود. اگر شناسه آگهی از طریق
 * Navigator منتقل شده باشد، فرم در حالت ویرایش قرار می‌گیرد.
 */
public class AdFormController {

    @FXML private Label formTitleLabel;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private ComboBox<CategoryResponse> categoryCombo;
    @FXML private ComboBox<CityResponse> cityCombo;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;

    private final AdvertisementService advertisementService = new AdvertisementService();
    private final CategoryService categoryService = new CategoryService();
    private final CityService cityService = new CityService();

    private Long editingAdId;

    @FXML
    public void initialize() {
        editingAdId = Navigator.consumePayload();
        loadCombos();

        if (editingAdId != null) {
            formTitleLabel.setText("ویرایش آگهی");
            submitButton.setText("ذخیره تغییرات");
            loadExistingAdvertisement();
        }
    }

    private void loadCombos() {
        try {
            categoryCombo.getItems().addAll(categoryService.getAllCategories());
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت دسته‌بندی‌ها با خطا مواجه شد: " + ex.getMessage());
        }
        try {
            cityCombo.getItems().addAll(cityService.getAllCities());
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت شهرها با خطا مواجه شد: " + ex.getMessage());
        }
    }

    private void loadExistingAdvertisement() {
        try {
            AdvertisementResponse ad = advertisementService.getById(editingAdId);
            titleField.setText(ad.getTitle());
            descriptionField.setText(ad.getDescription());
            priceField.setText(ad.getPrice().toBigInteger().toString());

            categoryCombo.getItems().stream()
                    .filter(item -> item.getId().equals(ad.getCategoryId()))
                    .findFirst()
                    .ifPresent(categoryCombo::setValue);

            cityCombo.getItems().stream()
                    .filter(item -> item.getId().equals(ad.getCityId()))
                    .findFirst()
                    .ifPresent(cityCombo::setValue);
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت اطلاعات آگهی با خطا مواجه شد: " + ex.getMessage());
        }
    }

    @FXML
    private void onSubmit() {
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();
        String priceText = priceField.getText() == null ? "" : priceField.getText().trim();
        CategoryResponse category = categoryCombo.getValue();
        CityResponse city = cityCombo.getValue();

        if (title.isEmpty() || description.isEmpty() || priceText.isEmpty() || category == null || city == null) {
            showError("لطفا تمام فیلدهای فرم را تکمیل کنید.");
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException ex) {
            showError("قیمت وارد شده معتبر نیست.");
            return;
        }

        AdvertisementRequest request = new AdvertisementRequest(title, description, price, category.getId(), city.getId());

        try {
            AdvertisementResponse response = editingAdId == null
                    ? advertisementService.create(request)
                    : advertisementService.update(editingAdId, request);

            AlertHelper.showInfo(editingAdId == null
                    ? "آگهی با موفقیت ثبت شد و در انتظار تایید مدیر است."
                    : "آگهی با موفقیت ویرایش شد و در انتظار تایید مجدد است.");
            Navigator.switchTo("/fxml/ad_detail.fxml", response.getTitle(), response.getId());
        } catch (ApiException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        Navigator.switchTo("/fxml/home.fxml", "بازار دست دوم");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
