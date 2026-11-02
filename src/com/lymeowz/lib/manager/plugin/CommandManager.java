// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.manager.plugin;

import java.util.Iterator;
import com.lymeowz.lib.permission.PermissionBridge;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;
import com.lymeowz.lib.core.builder.command.CommandBuild;
import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerManager;

public abstract class CommandManager extends HandlerManager implements com.lymeowz.lib.command.CommandRegistry
{
    protected CommandManager(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public abstract Collection<String> getCommandIds();
    
    public abstract Collection<CommandBuild> getAllCommandBuild();
    
    public abstract CommandBuild getCommandBuild(final String p0);
    
    public final boolean isCommandExists(final String id) {
        return this.getCommandBuild(id) != null;
    }
    
    public final String getMain(final String id) {
        final CommandBuild commandBuild = this.getCommandBuild(id);
        return (commandBuild != null) ? commandBuild.getMain() : null;
    }
    
    public final String getPermission(final String id) {
        final CommandBuild commandBuild = this.getCommandBuild(id);
        return (commandBuild != null) ? PermissionBridge.canonical(commandBuild.getPermission()) : null;
    }
    
    public final List<String> getAliases(final String id) {
        final CommandBuild commandBuild = this.getCommandBuild(id);
        return (commandBuild != null) ? commandBuild.getAliases() : new ArrayList<String>();
    }
    
    public final boolean checkPermission(final CommandSender sender, final String id) {
        final CommandBuild commandBuild = this.getCommandBuild(id);
        if (commandBuild != null) {
            final String permission = commandBuild.getPermission();
            return PermissionBridge.allowed(sender, permission);
        }
        return false;
    }
    
    public final boolean checkCommand(final String arg, final String id) {
        return com.lymeowz.lib.command.CommandLookup.matches(getCommandBuild(id), arg);
    }

}
