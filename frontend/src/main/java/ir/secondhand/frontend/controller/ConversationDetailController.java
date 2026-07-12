package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.response.ChatMessageResponse;
import ir.secondhand.frontend.dto.response.ConversationResponse;
import ir.secondhand.frontend.service.ConversationService;
import ir.secondhand.frontend.session.SessionManager;
import ir.secondhand.frontend.util.AlertHelper;
import ir.secondhand.frontend.util.Navigator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConversationDetailController {

    @FXML private Label headerLabel;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageField;

    private final ConversationService conversationService = new ConversationService();
    private Long conversationId;

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            Navigator.switchTo("/fxml/login.fxml", "ورود به سامانه");
            return;
        }
        conversationId = Navigator.consumePayload();
        if (conversationId == null) {
            AlertHelper.showError("گفت‌وگوی مورد نظر پیدا نشد.");
            Navigator.switchTo("/fxml/conversations.fxml", "گفت‌وگوهای من");
            return;
        }
        loadHeader();
        loadMessages();
    }

    private void loadHeader() {
        try {
            List<ConversationResponse> conversations = conversationService.getMyConversations();
            conversations.stream()
                    .filter(c -> c.getId().equals(conversationId))
                    .findFirst()
                    .ifPresent(c -> headerLabel.setText(
                            "گفت‌وگو با " + c.getOtherPartyName() + " درباره آگهی «" + c.getAdvertisementTitle() + "»"));
        } catch (ApiException ex) {
            headerLabel.setText("گفت‌وگو");
        }
    }

    private void loadMessages() {
        try {
            List<ChatMessageResponse> messages = conversationService.getMessages(conversationId);
            messagesContainer.getChildren().clear();
            for (ChatMessageResponse message : messages) {
                messagesContainer.getChildren().add(buildBubble(message));
            }
            scrollToBottom();
        } catch (ApiException ex) {
            AlertHelper.showError("دریافت پیام‌ها با خطا مواجه شد: " + ex.getMessage());
        }
    }

    private HBox buildBubble(ChatMessageResponse message) {
        boolean isMine = message.getSenderId().equals(SessionManager.getInstance().getUserId());

        VBox bubble = new VBox(4);
        bubble.getStyleClass().add(isMine ? "chat-bubble-mine" : "chat-bubble-other");
        bubble.setMaxWidth(420);

        Label contentLabel = new Label(message.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle(isMine ? "-fx-text-fill: white;" : "-fx-text-fill: #222222;");

        Label timeLabel = new Label(message.getCreatedAt() == null ? "" :
                message.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle(isMine ? "-fx-text-fill: #e5f6ee; -fx-font-size: 10px;" : "-fx-text-fill: #999999; -fx-font-size: 10px;");

        bubble.getChildren().addAll(contentLabel, timeLabel);

        HBox row = new HBox(bubble);
        row.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.setPadding(new Insets(0));
        HBox.setHgrow(bubble, javafx.scene.layout.Priority.NEVER);
        return row;
    }

    @FXML
    private void onSendMessage() {
        String content = messageField.getText() == null ? "" : messageField.getText().trim();
        if (content.isEmpty()) {
            return;
        }
        try {
            conversationService.sendMessage(conversationId, content);
            messageField.clear();
            loadMessages();
        } catch (ApiException ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
