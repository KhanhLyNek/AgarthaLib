package com.praya.agarthalib.packet.message;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.modern.Paper26AudienceService;
import com.praya.agarthalib.modern.Paper26JsonChat;
import com.praya.agarthalib.utility.JsonUtil;
import core.praya.agarthalib.builder.bridge.MessageActionbar;
import core.praya.agarthalib.builder.bridge.MessageJson;
import core.praya.agarthalib.builder.bridge.MessageTitle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * Paper 26.3 replacement for the old action bar/title/subtitle packet paths.
 * Supports text/click/hover chat through Adventure. The historical item-hover
 * SNBT format cannot be generated safely from modern data components yet.
 */
public final class PacketMessage_Paper26_3 extends HandlerPacket
        implements MessageActionbar, MessageTitle, MessageJson {

    public PacketMessage_Paper26_3(final AgarthaLib plugin) {
        super(plugin);
    }

    /**
     * Reuse the *original* JsonUtil rich-text DSL converter instead of
     * renaming commands or silently interpreting markup as plain chat.
     */
    @Override
    public void packetSendJson(final Collection<Player> players, final String message) {
        if (message == null) return;
        try {
            Paper26JsonChat.send(players, JsonUtil.getJsonText(message));
        } catch (RuntimeException ex) {
            // Malformed legacy DSL must not crash the scheduler or send a
            // lossy fallback with accidentally executable click actions.
        }
    }

    @Override
    public String packetGetJsonItem(final ItemStack item) {
        // Pre-1.20 SNBT hover-item tags are not interchangeable with modern
        // data components. No inaccurate conversion is fabricated.
        return null;
    }

    @Override
    public void packetSendActionbar(final Collection<Player> players, final String message) {
        Paper26AudienceService.actionBar(players, message);
    }

    @Override
    public void packetSendTitle(final Collection<Player> players, final String message,
                                final int fadeIn, final int stay, final int fadeOut) {
        Paper26AudienceService.title(players, message, fadeIn, stay, fadeOut);
    }

    @Override
    public void packetSendSubtitle(final Collection<Player> players, final String message,
                                   final int fadeIn, final int stay, final int fadeOut) {
        Paper26AudienceService.subtitle(players, message, fadeIn, stay, fadeOut);
    }
}
