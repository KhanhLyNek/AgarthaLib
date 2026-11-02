// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.task;

import com.lymeowz.lib.core.builder.message.BossBar;
import java.util.Iterator;
import com.lymeowz.lib.manager.player.PlayerBossBarManager;
import com.lymeowz.lib.manager.player.PlayerManager;
import org.bukkit.entity.Player;
import com.lymeowz.lib.utility.PlayerUtil;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerTask;

public class TaskBossBarPlayer extends HandlerTask implements Runnable
{
    public TaskBossBarPlayer(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @Override
    public void run() {
        final PlayerManager playerManager = this.plugin.getPlayerManager();
        final PlayerBossBarManager playerBossBarManager = playerManager.getPlayerBossBarManager();
        for (final Player player : PlayerUtil.getOnlinePlayers()) {
            final BossBar bar = playerBossBarManager.getBossBar(player);
            if (bar != null) {
                bar.updateMovement();
            }
        }
    }
}
