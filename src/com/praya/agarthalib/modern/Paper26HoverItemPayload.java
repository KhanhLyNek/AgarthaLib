package com.praya.agarthalib.modern;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.json.simple.parser.JSONParser;

/**
 * Versioned handoff between legacy JsonUtil.generateJsonItem() markup and a
 * modern Adventure show_item event. Encodes JSON as URL-safe Base64 so the
 * original || and colon-separated text DSL cannot split an item component.
 *
 * This is NOT a decoder for old raw SNBT or unversioned NBT.
 */
public final class Paper26HoverItemPayload {
    private static final String PREFIX = "AgarthaItem26:";
    private static final int MAX_JSON_BYTES = 262144;

    private Paper26HoverItemPayload() {}

    public static boolean isMarker(final String value) {
        return value != null && value.startsWith(PREFIX);
    }

    public static String encode(final String componentJson) {
        if (componentJson == null || componentJson.isBlank()) return null;
        final byte[] utf8 = componentJson.getBytes(StandardCharsets.UTF_8);
        if (utf8.length == 0 || utf8.length > MAX_JSON_BYTES) return null;
        // Only accept a valid show_item payload; never pass arbitrary JSON
        // through the original click/hover event parser.
        final String candidate = PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(utf8);
        return contents(candidate) == null ? null : candidate;
    }

    /** Return JSON-simple compatible nested map for Adventure hoverEvent.contents. */
    public static Object contents(final String token) {
        if (!isMarker(token)) return null;
        final String encoded = token.substring(PREFIX.length());
        if (encoded.isEmpty() || encoded.length() > ((MAX_JSON_BYTES + 2) / 3) * 4) return null;
        try {
            final byte[] utf8 = Base64.getUrlDecoder().decode(encoded);
            if (utf8.length == 0 || utf8.length > MAX_JSON_BYTES) return null;
            final Object component = new JSONParser().parse(
                    new String(utf8, StandardCharsets.UTF_8));
            if (!(component instanceof Map<?, ?> root)) return null;
            final Object hover = root.containsKey("hoverEvent")
                    ? root.get("hoverEvent") : root.get("hover_event");
            if (!(hover instanceof Map<?, ?> event) ||
                    !"show_item".equals(event.get("action"))) return null;
            final Object contents = event.get("contents");
            if (!(contents instanceof Map<?, ?> data)) return null;
            final Object id = data.get("id");
            if (!(id instanceof String name) || name.isBlank() || name.length() > 256) return null;
            if (data.containsKey("count")) {
                final Object amount = data.get("count");
                if (!(amount instanceof Number n) || n.intValue() <= 0) return null;
            }
            return contents;
        } catch (RuntimeException | org.json.simple.parser.ParseException ex) {
            // Malformed data cannot inject a synthetic click or item event.
            return null;
        }
    }
}
