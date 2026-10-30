package com.praya.agarthalib.modern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public final class Paper26JsonChatSelfTest {
    private static void verify(final boolean ok, final String message) {
        if (!ok) throw new AssertionError(message);
    }
    public static void main(final String[] args) {
        final Component rich = Component.text("Click")
            .clickEvent(ClickEvent.suggestCommand("/myitems list"))
            .hoverEvent(HoverEvent.showText(Component.text("Thông tin kỹ năng")));
        final String json = GsonComponentSerializer.gson().serialize(rich);
        final Component parsed = Paper26JsonChat.parse(json);
        verify(parsed != null, "rich text must parse");
        verify(parsed.clickEvent() != null, "click action must survive");
        verify(parsed.hoverEvent() != null, "hover text must survive");
        verify(Paper26JsonChat.parse("{broken") == null, "malformed JSON rejected");
        verify(Paper26JsonChat.parse(null) == null, "null JSON rejected");
        verify(Paper26JsonChat.parse("") == null, "empty JSON rejected");
        System.out.println("PASS: 6 rich JSON message checks");
    }
}
