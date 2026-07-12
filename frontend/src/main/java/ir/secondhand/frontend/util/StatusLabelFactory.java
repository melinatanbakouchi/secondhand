package ir.secondhand.frontend.util;

import javafx.scene.control.Label;

/**
 * تبدیل مقدار enum وضعیت آگهی به برچسب فارسی همراه با کلاس CSS مناسب.
 */
public final class StatusLabelFactory {

    private StatusLabelFactory() {
    }

    public static Label create(String status) {
        Label label = new Label(translate(status));
        label.getStyleClass().addAll("status-badge", cssClass(status));
        return label;
    }

    public static String translate(String status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case "PENDING" -> "در انتظار بررسی";
            case "ACTIVE" -> "فعال";
            case "REJECTED" -> "رد شده";
            case "SOLD" -> "فروخته شده";
            case "DELETED" -> "حذف شده";
            default -> status;
        };
    }

    private static String cssClass(String status) {
        if (status == null) {
            return "status-pending";
        }
        return switch (status) {
            case "ACTIVE" -> "status-active";
            case "REJECTED" -> "status-rejected";
            case "SOLD" -> "status-sold";
            case "DELETED" -> "status-deleted";
            default -> "status-pending";
        };
    }
}
