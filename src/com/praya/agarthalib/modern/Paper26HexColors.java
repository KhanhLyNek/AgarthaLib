package com.praya.agarthalib.modern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert modern Minecraft RGB notation into the standard section-sign
 * sequence §x§R§R§G§G§B§B. Bukkit's pre-existing alternate & color
 * translation still runs in TextUtil, preserving legacy color/format codes.
 *
 * Supported input: &#RRGGBB, <#RRGGBB>, {#RRGGBB}, #RRGGBB
 * (standalone/boundary), and &x&R&R&G&G&B&B.
 * An invalid or truncated color sequence remains literal.
 */
public final class Paper26HexColors {
    private static final Pattern RGB = Pattern.compile(
            "(?i)&#([0-9a-f]{6})(?![0-9a-f])"
            + "|<#([0-9a-f]{6})>"
            + "|\\{#([0-9a-f]{6})\\}"
            + "|(?<![a-z0-9_/#<&{])#([0-9a-f]{6})(?![0-9a-f])");
    private static final Pattern AMP_X = Pattern.compile(
            "(?i)&x(?:&[0-9a-f]){6}");

    private Paper26HexColors() { }

    public static String translate(final String text) {
        if (text == null || text.isEmpty()) return text;
        String result = replaceRgb(text);
        final Matcher matcher = AMP_X.matcher(result);
        final StringBuilder output = new StringBuilder(result.length());
        while (matcher.find()) {
            final String value = matcher.group();
            final StringBuilder hex = new StringBuilder(6);
            for (int i = 3; i < value.length(); i += 2) hex.append(value.charAt(i));
            matcher.appendReplacement(output, Matcher.quoteReplacement(toLegacyHex(hex.toString())));
        }
        matcher.appendTail(output);
        return output.toString();
    }

    private static String replaceRgb(final String text) {
        final Matcher matcher = RGB.matcher(text);
        final StringBuilder output = new StringBuilder(text.length());
        while (matcher.find()) {
            String rgb = null;
            for (int group = 1; group <= 4; group++) {
                if (matcher.group(group) != null) {
                    rgb = matcher.group(group);
                    break;
                }
            }
            matcher.appendReplacement(output, Matcher.quoteReplacement(toLegacyHex(rgb)));
        }
        matcher.appendTail(output);
        return output.toString();
    }

    private static String toLegacyHex(final String rgb) {
        final StringBuilder output = new StringBuilder(14).append('§').append('x');
        for (int i = 0; i < 6; i++) {
            output.append('§').append(Character.toLowerCase(rgb.charAt(i)));
        }
        return output.toString();
    }
}
