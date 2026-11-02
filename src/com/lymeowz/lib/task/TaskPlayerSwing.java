// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.task;

import java.util.Iterator;
import com.lymeowz.lib.manager.player.PlayerSwingManager;
import com.lymeowz.lib.manager.player.PlayerManager;
import org.bukkit.entity.Player;
import com.lymeowz.lib.utility.PlayerUtil;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerTask;

public class TaskPlayerSwing extends HandlerTask implements Runnable
{
    public TaskPlayerSwing(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @Override
    public void run() {
        final PlayerManager playerManager = this.plugin.getPlayerManager();
        final PlayerSwingManager playerSwingManager = playerManager.getPlayerSwingManager();
        for (final Player player : PlayerUtil.getOnlinePlayers()) {
            playerSwingManager.updatePlayerSwing(player);
        }
    }
}
