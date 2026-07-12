package ir.secondhand.frontend.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public final class PriceFormatter {

    private static final DecimalFormat FORMAT = new DecimalFormat("#,###");

    private PriceFormatter() {
    }

    public static String format(BigDecimal price) {
        if (price == null) {
            return "توافقی";
        }
        return FORMAT.format(price) + " تومان";
    }
}
