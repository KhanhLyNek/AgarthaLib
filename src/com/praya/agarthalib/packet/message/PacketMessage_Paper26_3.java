package com.praya.agarthalib.packet.message;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.modern.Paper26AudienceService;
import core.praya.agarthalib.builder.bridge.MessageActionbar;
import core.praya.agarthalib.builder.bridge.MessageTitle;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Paper 26.3 replacement for the old action bar/title/subtitle packet paths.
 * Intentionally does not implement MessageJson: JSON click/hover and item
 * payload migration require a separate lossless adapter and server fixtures.
 */
public final class PacketMessage_Paper26_3 extends HandlerPacket
        implements MessageActionbar, MessageTitle {

    public PacketMessage_Paper26_3(final AgarthaLib plugin) {
        super(plugin);
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
