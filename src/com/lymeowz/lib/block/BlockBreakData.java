// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.block;

import org.bukkit.Location;

public class BlockBreakData extends com.lymeowz.lib.core.builder.block.BlockBreakData
{
    public BlockBreakData(final int id, final Location location) {
        this(id, location, 200);
    }
    
    public BlockBreakData(final int id, final Location location, final int cooldown) {
        super(id, location, cooldown);
    }
}
