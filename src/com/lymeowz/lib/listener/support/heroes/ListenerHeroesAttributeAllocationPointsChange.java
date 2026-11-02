// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.support.heroes;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.herocraftonline.heroes.characters.Hero;
import org.bukkit.plugin.Plugin;
import com.lymeowz.lib.utility.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.herocraftonline.heroes.api.events.AttributeAllocationPointsChangeEvent;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.event.Listener;
import com.lymeowz.lib.handler.HandlerListener;

public class ListenerHeroesAttributeAllocationPointsChange extends HandlerListener implements Listener
{
    public ListenerHeroesAttributeAllocationPointsChange(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void eventAttributeChange(final AttributeAllocationPointsChangeEvent event) {
        if (!event.isCancelled()) {
            final Hero hero = event.getHero();
            if (hero != null) {
                final Player player = hero.getPlayer();
                new BukkitRunnable() {
                    public void run() {
                        PlayerUtil.setMaxHealth(player);
                    }
                }.runTaskLater((Plugin)this.plugin, 2L);
            }
        }
    }
}
