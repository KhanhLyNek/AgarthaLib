// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.support.battlelevels;

import com.sucy.skill.api.event.PlayerLevelUpEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import com.lymeowz.lib.utility.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.event.Listener;
import com.lymeowz.lib.handler.HandlerListener;

public class ListenerBattleLevelsPlayerLevelUp extends HandlerListener implements Listener
{
    public ListenerBattleLevelsPlayerLevelUp(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void eventPlayerLevelUp(final PlayerLevelUpEvent event) {
        if (event.getPlayerData().getPlayer() != null) {
            final Player player = event.getPlayerData().getPlayer();
            new BukkitRunnable() {
                public void run() {
                    PlayerUtil.setMaxHealth(player);
                }
            }.runTaskLater((Plugin)this.plugin, 1L);
        }
    }
}
