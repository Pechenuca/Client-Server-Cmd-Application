package max.util;

import javafx.scene.paint.Color;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FxUtils {

    public static String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }
    private static String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public static String formatZonedDateTimeValue(ZonedDateTime time, Locale locale) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MMM HH:mm z").withLocale(locale);
        return dateFormatter.format(time);
    }
}