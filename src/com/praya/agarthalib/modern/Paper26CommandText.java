package com.praya.agarthalib.modern;

import java.util.List;

/** @deprecated The command-text implementation now belongs to LyMeowzLib. */
@Deprecated
public final class Paper26CommandText {
    private Paper26CommandText() { }

    public static String rewrite(String text) {
        return com.lymeowz.lib.command.CommandText.rewrite(text);
    }

    public static List<String> rewrite(List<String> lines) {
        return com.lymeowz.lib.command.CommandText.rewrite(lines);
    }
}
