package com.praya.agarthalib.packet.player;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Paper 26.3 implementation of the original player packet contracts.
 *
 * Do not implement PacketPlayerCombatPresent until the old NMS swing-duration
 * and swing-progress semantics have been verified with fixtures. Returning
 * guessed values would change the original gameplay.
 *
 * Entity hiding uses the official plugin-scoped Paper API; unlike one legacy
 * destroy packet, the hidden state may persist until showEntity is called.
 * Review this difference before production deployment.
 */
public final class PacketPlayer_Paper26_3 extends HandlerPacket
        implements PacketPlayerProperties, PacketPlayerVisibility,
                   PacketPlayerAbsorption, PacketPlayerCombat {

    private final AgarthaLib owner;

    public PacketPlayer_Paper26_3(final AgarthaLib plugin) {
        super(plugin);
        this.owner = plugin;
    }

    @Override
    public String getLocale(final Player player) {
        return player == null ? null : player.getLocale();
    }

    @Override
    public void hideEntity(final Player player, final Entity entity) {
        if (player != null && entity != null && this.owner != null) {
            player.hideEntity(this.owner, entity);
        }
    }

    @Override
    public void hideEntity(final Collection<Player> players, final Entity entity) {
        if (players != null && entity != null) {
            for (final Player player : players) {
                this.hideEntity(player, entity);
            }
        }
    }

    @Override
    public float getAbsorptionHearts(final Player player) {
        return player == null ? 0.0f : (float) player.getAbsorptionAmount();
    }

    @Override
    public void setAbsorptionHearts(final Player player, final float amount) {
        if (player != null) {
            player.setAbsorptionAmount(amount);
        }
    }

    @Override
    public void playSwingMainHand(final Player player) {
        if (player != null) {
            player.swingMainHand();
        }
    }
}
