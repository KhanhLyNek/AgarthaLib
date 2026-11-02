package com.praya.agarthalib.modern;

/**
 * @deprecated Use com.lymeowz.lib.text.HexColors.
 * Preserves the historic public method's JVM signature for other plugins.
 */
@Deprecated
public final class Paper26HexColors {
    private Paper26HexColors() { }

    public static String translate(String text) {
        return com.lymeowz.lib.text.HexColors.translate(text);
    }
}
