// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.packet.server;

import com.lymeowz.lib.utility.MathUtil;
import net.minecraft.server.v1_9_R2.MinecraftServer;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.core.builder.bridge.ServerTools;
import com.lymeowz.lib.handler.HandlerPacket;

public class PacketServer_1_9_R2 extends HandlerPacket implements ServerTools
{
    public PacketServer_1_9_R2(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @Override
    public double getTPS() {
        final double tps = MathUtil.roundNumber(MinecraftServer.getServer().recentTps[0], 2);
        return MathUtil.limitDouble(tps, 0.0, 20.0);
    }
}
