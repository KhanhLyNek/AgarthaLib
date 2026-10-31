package com.lymeowz.lib.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rename only user-facing help command roots; existing resource YAML, item
 * metadata, permissions, subcommand IDs and placeholder expansion IDs stay
 * byte-identical. Works for old migrated language files as well as defaults.
 */
public final class CommandText {
    private static final Pattern ROOT_COMMAND =
            Pattern.compile("/(myitems|agarthalib)(?![A-Za-z0-9_:-])", Pattern.CASE_INSENSITIVE);

    private CommandText() { }

    public static String rewrite(String text) {
        if (text == null || text.isEmpty()) return text;
        final Matcher matcher = ROOT_COMMAND.matcher(text);
        final StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            final String oldName = matcher.group(1).toLowerCase(Locale.ROOT);
            final String newRoot = "myitems".equals(oldName) ? "/lymeowzitem" : "/lymeowzlib";
            matcher.appendReplacement(result, Matcher.quoteReplacement(newRoot));
        }
        matcher.appendTail(result);
        return result.toString()
                .replace("&aMyItems&8", "&aLyMeowzItem&8")
                .replace("§aMyItems§8", "§aLyMeowzItem§8")
                .replace("plugin MyItems", "plugin LyMeowzItem");
    }

    public static List<String> rewrite(List<String> lines) {
        if (lines == null) return new ArrayList<>();
        final List<String> result = new ArrayList<>(lines.size());
        for (final String line : lines) result.add(rewrite(line));
        return result;
    }
}
