// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.core.builder.bridge;

import com.lymeowz.lib.core.enums.branch.SoundEnum;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Collection;

public interface SoundTools
{
    void playSoundEffect(final Collection<Player> p0, final Location p1, final SoundEnum p2, final float p3, final float p4);
}
