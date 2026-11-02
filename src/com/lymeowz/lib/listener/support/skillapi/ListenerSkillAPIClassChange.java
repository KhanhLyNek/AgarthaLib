// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.support.skillapi;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.plugin.Plugin;
import com.lymeowz.lib.utility.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.event.Listener;
import com.lymeowz.lib.handler.HandlerListener;

public class ListenerSkillAPIClassChange extends HandlerListener implements Listener
{
    public ListenerSkillAPIClassChange(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void eventPlayerClassChange(final PlayerClassChangeEvent event) {
        final PlayerData playerData = event.getPlayerData();
        if (playerData != null) {
            final Player player = playerData.getPlayer();
            new BukkitRunnable() {
                public void run() {
                    PlayerUtil.setMaxHealth(player);
                }
            }.runTaskLater((Plugin)this.plugin, 1L);
        }
    }
}
