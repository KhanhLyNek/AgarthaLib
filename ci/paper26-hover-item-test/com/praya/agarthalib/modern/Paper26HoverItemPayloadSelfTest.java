package com.praya.agarthalib.modern;

import java.util.Map;

public final class Paper26HoverItemPayloadSelfTest {
    private static int checks;
    private static void expect(boolean condition, String detail) {
        checks++;
        if (!condition) throw new AssertionError(detail);
    }
    private static String json(String count) {
        return "{\"text\":\"Kỹ năng\",\"hoverEvent\":{\"action\":\"show_item\","
                + "\"contents\":{\"id\":\"minecraft:diamond\",\"count\":" + count + "}}}";
    }
    public static void main(String[] args) {
        for (String valid : new String[]{"1", "64", "99"}) {
            String marker = Paper26HoverItemPayload.encode(json(valid));
            expect(marker != null && Paper26HoverItemPayload.isMarker(marker), "valid marker " + valid);
            Object contents = Paper26HoverItemPayload.contents(marker);
            expect(contents instanceof Map<?, ?>, "valid nested map " + valid);
            expect("minecraft:diamond".equals(((Map<?, ?>) contents).get("id")), "item ID " + valid);
        }
        for (String invalid : new String[]{"0", "-1", "100", "1.5", "99.1",
                "4294967297", "9223372036854775807", "null", "\"3\""}) {
            expect(Paper26HoverItemPayload.encode(json(invalid)) == null,
                    "reject invalid stack count " + invalid);
        }
        expect(Paper26HoverItemPayload.encode(json("1").replace("show_item", "run_command")) == null,
                "must reject non-item actions");
        expect(Paper26HoverItemPayload.encode(json("1").replace("minecraft:diamond", "")) == null,
                "must reject empty item IDs");
        expect(Paper26HoverItemPayload.contents("AgarthaItem26:not-base64!?") == null,
                "must reject malformed encoded payload");
        expect(Paper26HoverItemPayload.contents("ordinary text") == null,
                "ordinary chat text is not a marker");
        expect(Paper26HoverItemPayload.encode(null) == null, "null must be rejected");
        expect(Paper26HoverItemPayload.encode(" ".repeat(262145)) == null,
                "oversized JSON must be rejected before decoding");
        expect(Paper26HoverItemPayload.encode(
                "{\"hover_event\":{\"action\":\"show_item\",\"contents\":{\"id\":\"minecraft:book\"}}}")
                != null, "legacy snake_case with omitted count");
        System.out.println("PASS: " + checks + " hover-item payload regression checks");
    }
}
