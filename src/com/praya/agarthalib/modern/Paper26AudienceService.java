package com.praya.agarthalib.modern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collection;

/**
 * Modern Paper/Adventure replacement for action-bar, title and subtitle NMS
 * packets. The old public BridgeMessage signatures and timing units remain.
 *
 * The legacy BridgeMessage already translates '&' to section signs for
 * action bars. For titles it historically forwards the string unchanged,
 * so we deliberately parse section signs but do not introduce '&' handling.
 */
public final class Paper26AudienceService {
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacySection();

    private Paper26AudienceService() {
    }

    public static Component legacy(final String text) {
        return LEGACY.deserialize(text == null ? "" : text);
    }

    public static void actionBar(final Collection<Player> players, final String legacyText) {
        if (players == null || legacyText == null) {
            return;
        }
        final Component content = legacy(legacyText);
        for (final Player player : players) {
            if (player != null && player.isOnline()) {
                player.sendActionBar(content);
            }
        }
    }

    /**
     * Send only the title portion; do not erase the existing subtitle.
     * The original packet supplied its timing values on every call.
     */
    public static void title(final Collection<Player> players, final String text,
                             final int fadeIn, final int stay, final int fadeOut) {
        sendPart(players, text, fadeIn, stay, fadeOut, false);
    }

    public static void subtitle(final Collection<Player> players, final String text,
                                final int fadeIn, final int stay, final int fadeOut) {
        sendPart(players, text, fadeIn, stay, fadeOut, true);
    }

    private static void sendPart(final Collection<Player> players, final String text,
                                 final int fadeIn, final int stay, final int fadeOut,
                                 final boolean subtitle) {
        if (players == null || text == null) {
            return;
        }
        final Component content = legacy(text);
        // Minecraft sends title timings as ticks. A negative value used to
        // denote reuse/default in older APIs, so only send TIMES when all
        // values are concrete; otherwise retain the viewer's current timings.
        final Title.Times times = (fadeIn >= 0 && stay >= 0 && fadeOut >= 0)
                ? Title.Times.times(ticks(fadeIn), ticks(stay), ticks(fadeOut))
                : null;
        for (final Player player : players) {
            if (player == null || !player.isOnline()) {
                continue;
            }
            if (times != null) {
                player.sendTitlePart(TitlePart.TIMES, times);
            }
            if (subtitle) {
                player.sendTitlePart(TitlePart.SUBTITLE, content);
            } else {
                player.sendTitlePart(TitlePart.TITLE, content);
            }
        }
    }

    private static Duration ticks(final int ticks) {
        return Duration.ofMillis(ticks * 50L);
    }
}
