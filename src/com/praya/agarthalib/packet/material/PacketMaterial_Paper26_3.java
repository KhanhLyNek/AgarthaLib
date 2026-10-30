package com.praya.agarthalib.packet.material;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import core.praya.agarthalib.builder.bridge.MaterialTools;
import org.bukkit.Material;

/** Current Material properties rather than the frozen 1.12 material registry. */
public final class PacketMaterial_Paper26_3 extends HandlerPacket implements MaterialTools {
    public PacketMaterial_Paper26_3(final AgarthaLib plugin) { super(plugin); }
    @Override public boolean isSolid(final Material material) {
        return material != null && material.isSolid();
    }
    @Override public boolean isFluid(final Material material) {
        return material != null && material.isLiquid();
    }
}
