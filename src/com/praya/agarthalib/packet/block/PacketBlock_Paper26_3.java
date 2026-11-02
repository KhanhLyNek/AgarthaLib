package com.praya.agarthalib.packet.block;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.modern.Paper26BlockDamage;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Implements the original block animation contract without versioned NMS.
 */
public final class PacketBlock_Paper26_3 extends HandlerPacket implements PacketBlockAnimation {
    public PacketBlock_Paper26_3(final AgarthaLib plugin) {
        super(plugin);
    }

    @Override
    public void blockBreakAnimation(final int id, final Collection<Player> players,
                                    final int x, final int y, final int z, final int stage) {
        Paper26BlockDamage.send(id, players, x, y, z, stage);
    }
}
