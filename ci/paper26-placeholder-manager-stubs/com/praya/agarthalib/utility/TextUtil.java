package com.praya.agarthalib.utility;

import java.util.HashMap;
import java.util.List;

/** CI-only signature fixture; never included in a production plugin JAR. */
public final class TextUtil {
    private TextUtil() {}
    public static String convertListToString(final List<String> list, final String divider) {
        return String.join(divider, list);
    }
    public static String placeholder(final HashMap<String, String> values, final String text) {
        return text;
    }
}
