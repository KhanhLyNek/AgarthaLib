// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.packet.arrow;

import com.lymeowz.lib.utility.EquipmentUtil;
import com.lymeowz.lib.core.enums.branch.MaterialEnum;
import org.bukkit.inventory.ItemStack;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerPacket;

public class PacketArrow_1_7 extends HandlerPacket implements PacketArrowNormal
{
    public PacketArrow_1_7(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @Override
    public ItemStack createNormalArrow() {
        return EquipmentUtil.createItem(MaterialEnum.ARROW);
    }
}
