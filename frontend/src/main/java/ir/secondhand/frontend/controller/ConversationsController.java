package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.response.ConversationResponse;
import ir.secondhand.frontend.service.ConversationService;
import ir.secondhand.frontend.session.SessionManager;
import ir.secondhand.frontend.util.AlertHelper;
import ir.secondhand.frontend.util.Navigator;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConversationsController {

    @FXML private Label emptyLabel;
    @FXML private VBox listContainer;

    private final ConversationService conversationService = new ConversationService();

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            Navigator.switchTo("/fxml/login.fxml", "ورود به سامانه");
            return;
        }
        loadConversations();
    }

    private void loadConversations() {
        try {
            List<ConversationResponse> conversations = conversationService.getMyConversations();
            listContainer.getChildren().clear();
            emptyLabel.setVisible(conversations.isEmpty());
            emptyLabel.setManaged(conversations.isEmpty());
            for (ConversationResponse conversation : conversations) {
                listContainer.getChildren().add(buildRow(conversation));
            }
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت گفت‌وگوها با خطا مواجه شد: " + ex.getMessage());
        }
    }

    private HBox buildRow(ConversationResponse conversation) {
        HBox row = new HBox(12);
        row.getStyleClass().addAll("card", "list-row");
        row.setPadding(new Insets(14));
        row.setOnMouseClicked(e -> Navigator.switchTo("/fxml/conversation_detail.fxml", "گفت‌وگو", conversation.getId()));

        VBox textBox = new VBox(4);
        Label partyLabel = new Label(conversation.getOtherPartyName());
        partyLabel.getStyleClass().add("card-title");
        Label adLabel = new Label("درباره آگهی: " + conversation.getAdvertisementTitle());
        adLabel.getStyleClass().add("card-meta");
        Label lastMessageLabel = new Label(conversation.getLastMessage() == null
                ? "هنوز پیامی ارسال نشده است." : conversation.getLastMessage());
        lastMessageLabel.getStyleClass().add("card-meta");
        textBox.getChildren().addAll(partyLabel, adLabel, lastMessageLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLabel = new Label(conversation.getLastMessageAt() == null ? "" :
                conversation.getLastMessageAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
        timeLabel.getStyleClass().add("card-meta");

        row.getChildren().addAll(textBox, spacer, timeLabel);
        return row;
    }
}
