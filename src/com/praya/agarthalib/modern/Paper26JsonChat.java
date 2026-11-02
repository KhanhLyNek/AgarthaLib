package com.praya.agarthalib.modern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import java.util.Collection;

/** Modern JSON chat transport; legacy DSL -> JSON remains in original JsonUtil. */
public final class Paper26JsonChat {
    private Paper26JsonChat() {}

    /** Never turn malformed JSON into a plain-text message with hidden commands. */
    public static Component parse(final String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return GsonComponentSerializer.gson().deserialize(json);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public static void send(final Collection<Player> recipients, final String json) {
        if (recipients == null) return;
        final Component component = parse(json);
        if (component == null) return;
        for (final Player player : recipients) {
            if (player != null && player.isOnline()) player.sendMessage(component);
        }
    }
}
