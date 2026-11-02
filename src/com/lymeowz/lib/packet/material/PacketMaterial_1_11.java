// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.packet.material;

import com.lymeowz.lib.core.enums.branch.MaterialEnum;
import org.bukkit.Material;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.core.builder.bridge.MaterialTools;
import com.lymeowz.lib.handler.HandlerPacket;

public class PacketMaterial_1_11 extends HandlerPacket implements MaterialTools
{
    public PacketMaterial_1_11(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @Override
    public boolean isSolid(final Material material) {
        if (material != null) {
            final MaterialEnum materialEnum = MaterialEnum.getMaterialEnum(material);
            if (materialEnum != null) {
                return materialEnum.isSolid();
            }
        }
        return false;
    }
    
    @Override
    public boolean isFluid(final Material material) {
        if (material != null) {
            final MaterialEnum materialEnum = MaterialEnum.getMaterialEnum(material);
            if (materialEnum != null) {
                return materialEnum.isFluid();
            }
        }
        return false;
    }
}
