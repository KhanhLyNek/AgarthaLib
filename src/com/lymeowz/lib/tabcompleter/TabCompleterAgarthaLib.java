// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.tabcompleter;

import com.lymeowz.lib.manager.plugin.CommandManager;
import com.lymeowz.lib.manager.plugin.PluginManager;
import com.lymeowz.lib.utility.TabCompleterUtil;
import com.lymeowz.lib.utility.SenderUtil;
import com.lymeowz.lib.core.enums.branch.SoundEnum;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.command.TabCompleter;
import com.lymeowz.lib.handler.HandlerTabCompleter;

public class TabCompleterAgarthaLib extends HandlerTabCompleter implements TabCompleter
{
    public TabCompleterAgarthaLib(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final CommandManager commandManager = pluginManager.getCommandManager();
        final List<String> tabList = new ArrayList<String>();
        SenderUtil.playSound(sender, SoundEnum.BLOCK_WOOD_BUTTON_CLICK_ON);
        if (args.length == 1) {
            if (commandManager.checkPermission(sender, "AgarthaLib_Help")) {
                tabList.add("Help");
            }
            if (commandManager.checkPermission(sender, "AgarthaLib_About")) {
                tabList.add("About");
            }
            if (commandManager.checkPermission(sender, "AgarthaLib_Reload")) {
                tabList.add("Reload");
            }
        }
        return TabCompleterUtil.returnList(tabList, args);
    }
}
