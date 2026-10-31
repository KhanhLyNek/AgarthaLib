package com.praya.agarthalib.modern;

import java.util.List;

/**
 * @deprecated The stable persisted item string-list format now belongs to
 * LyMeowzLib. This facade retains both exact JVM signatures; NEVER change
 * the saved AGLS magic header or the historical read/write grammar.
 */
@Deprecated
public final class Paper26StringListCodec {
    private Paper26StringListCodec() { }

    public static byte[] encode(final List<String> lines) {
        return com.lymeowz.lib.item.StringListCodec.encode(lines);
    }

    public static List<String> decode(final byte[] bytes) {
        return com.lymeowz.lib.item.StringListCodec.decode(bytes);
    }
}
