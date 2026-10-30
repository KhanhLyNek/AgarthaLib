package com.praya.agarthalib.modern;

import com.praya.agarthalib.utility.JsonUtil;
import core.praya.agarthalib.builder.message.JsonBuild;
import core.praya.agarthalib.enums.main.JsonOption;
import java.util.List;
import java.util.Map;
import org.json.simple.parser.JSONParser;

/**
 * Exercises the actual historic DSL and JsonBuild, not just the standalone
 * item serializer. No Bukkit server or fake MythicMobs jars are required.
 */
public final class Paper26HoverPipelineSelfTest {
    private static int checks;
    private static void verify(final boolean condition, final String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static Map<?, ?> object(final String json) throws Exception {
        return (Map<?, ?>) new JSONParser().parse(json);
    }
    private static Map<?, ?> extra(final String json) throws Exception {
        List<?> parts = (List<?>) object(json).get("extra");
        verify(parts != null && !parts.isEmpty(), "JSON contains extra message segment");
        return (Map<?, ?>) parts.get(0);
    }
    public static void main(String[] args) throws Exception {
        String itemJson = "{\"text\":\"Đồ & vật\",\"hoverEvent\":{\"action\":\"show_item\","
                + "\"contents\":{\"id\":\"minecraft:diamond_sword\",\"count\":1,"
                + "\"components\":{\"minecraft:custom_name\":\"Kiếm: hiếm || đặc biệt\"}}}}";
        String marker = Paper26HoverItemPayload.encode(itemJson);
        verify(marker != null, "valid modern data components encode");
        verify(!marker.contains(":") || marker.indexOf(':') == "AgarthaItem26".length(),
                "marker contains no DSL separator in encoded data");
        verify(!marker.contains("||"), "marker cannot split old DSL");
        String fromDsl = JsonUtil.getJsonText("Kiếm||item: " + marker);
        Map<?, ?> segment = extra(fromDsl);
        verify("Kiếm".equals(segment.get("text")), "legacy item label unchanged");
        Map<?, ?> hover = (Map<?, ?>) segment.get("hoverEvent");
        verify(hover != null && "show_item".equals(hover.get("action")), "show_item emitted");
        verify(!hover.containsKey("value"), "modern item must not emit obsolete value");
        Map<?, ?> contents = (Map<?, ?>) hover.get("contents");
        verify("minecraft:diamond_sword".equals(contents.get("id")), "item ID unchanged");
        verify(Long.valueOf(1).equals(contents.get("count")), "item count unchanged");
        Map<?, ?> components = (Map<?, ?>) contents.get("components");
        verify("Kiếm: hiếm || đặc biệt".equals(components.get("minecraft:custom_name")),
                "colons and double pipes survive JSON roundtrip");

        Map<?, ?> textHover = (Map<?, ?>) extra(JsonUtil.getJsonText("Đọc||ttp: Mô tả"))
                .get("hoverEvent");
        verify("show_text".equals(textHover.get("action")), "original text hover action");
        verify("Mô tả".equals(textHover.get("value")), "original text hover value");

        Map<?, ?> oldHover = (Map<?, ?>) extra(JsonUtil.getJsonText("Old||item: legacy-snbt"))
                .get("hoverEvent");
        verify("legacy-snbt".equals(oldHover.get("value")), "original item text remains unchanged");
        verify(!oldHover.containsKey("contents"), "legacy string never guessed as components");

        Map<?, ?> bad = extra(JsonUtil.getJsonText("Safe||item: AgarthaItem26:INVALID!!"));
        verify(!bad.containsKey("hoverEvent"), "bad versioned marker is ignored");
        Map<?, ?> click = (Map<?, ?>) extra(JsonUtil.getJsonText("Execute||cmd: /myitems list"))
                .get("clickEvent");
        verify("run_command".equals(click.get("action")), "historic command action preserved");
        verify("/myitems list".equals(click.get("value")), "historic command preserved");

        verify(new JsonBuild().addHoverEvent("Sword", JsonOption.HOVER_SHOW_ITEM, marker)
                .read().contains("\"contents\""), "direct JsonBuild API keeps new hover");
        System.out.println("PASS: " + checks + " real JSON/hover DSL regression checks");
    }
}
