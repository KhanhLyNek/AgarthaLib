// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.task;

import org.bukkit.inventory.Inventory;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.lib.core.builder.text.TextList;
import com.lymeowz.lib.core.builder.menu.Menu;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskMenuTitle extends BukkitRunnable
{
    private final Menu menu;
    private final TextList titles;
    
    public TaskMenuTitle(final Menu menu, final TextList titles) {
        this.menu = menu;
        this.titles = titles;
    }
    
    public void run() {
        if (!this.menu.hasViewer()) {
            this.cancel();
        }
        else {
            final Inventory inventory = this.menu.getInventory();
            final String title = this.titles.next();
            Bridge.getBridgeInventory().setWindowTitle(inventory, title);
        }
    }
}
