package com.praya.agarthalib.utility;

import java.util.Arrays;
import java.util.List;

/** CI-only signature fixture; never included in a production plugin JAR. */
public final class ListUtil {
    private ListUtil() {}
    public static List<String> convertStringToList(final String text, final String divider) {
        return Arrays.asList(text.split(java.util.regex.Pattern.quote(divider)));
    }
}
