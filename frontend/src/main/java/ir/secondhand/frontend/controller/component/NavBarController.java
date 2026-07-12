package ir.secondhand.frontend.controller.component;

import ir.secondhand.frontend.service.AuthService;
import ir.secondhand.frontend.session.SessionManager;
import ir.secondhand.frontend.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * نوار ناوبری مشترک بین تمام صفحات؛ بر اساس وضعیت ورود کاربر، دکمه‌های
 * مناسب را نمایش یا پنهان می‌کند.
 */
public class NavBarController {

    @FXML private Button homeButton;
    @FXML private Button createAdButton;
    @FXML private Button myAdsButton;
    @FXML private Button favoritesButton;
    @FXML private Button conversationsButton;
    @FXML private Button adminButton;
    @FXML private Button loginButton;
    @FXML private Button logoutButton;
    @FXML private Label welcomeLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        refresh();
    }

    public void refresh() {
        boolean loggedIn = SessionManager.getInstance().isLoggedIn();
        boolean isAdmin = SessionManager.getInstance().isAdmin();

        createAdButton.setVisible(loggedIn);
        createAdButton.setManaged(loggedIn);
        myAdsButton.setVisible(loggedIn);
        myAdsButton.setManaged(loggedIn);
        favoritesButton.setVisible(loggedIn);
        favoritesButton.setManaged(loggedIn);
        conversationsButton.setVisible(loggedIn);
        conversationsButton.setManaged(loggedIn);
        adminButton.setVisible(loggedIn && isAdmin);
        adminButton.setManaged(loggedIn && isAdmin);

        loginButton.setVisible(!loggedIn);
        loginButton.setManaged(!loggedIn);
        logoutButton.setVisible(loggedIn);
        logoutButton.setManaged(loggedIn);

        welcomeLabel.setText(loggedIn ? "خوش آمدید، " + SessionManager.getInstance().getFullName() : "");
    }

    @FXML
    private void onHome() {
        Navigator.switchTo("/fxml/home.fxml", "بازار دست دوم");
    }

    @FXML
    private void onCreateAd() {
        Navigator.switchTo("/fxml/ad_form.fxml", "ثبت آگهی جدید");
    }

    @FXML
    private void onMyAds() {
        Navigator.switchTo("/fxml/my_ads.fxml", "آگهی‌های من");
    }

    @FXML
    private void onFavorites() {
        Navigator.switchTo("/fxml/favorites.fxml", "علاقه‌مندی‌های من");
    }

    @FXML
    private void onConversations() {
        Navigator.switchTo("/fxml/conversations.fxml", "گفت‌وگوهای من");
    }

    @FXML
    private void onAdmin() {
        Navigator.switchTo("/fxml/admin_dashboard.fxml", "پنل مدیریت");
    }

    @FXML
    private void onLogin() {
        Navigator.switchTo("/fxml/login.fxml", "ورود به سامانه");
    }

    @FXML
    private void onLogout() {
        authService.logout();
        Navigator.switchTo("/fxml/home.fxml", "بازار دست دوم");
    }
}
