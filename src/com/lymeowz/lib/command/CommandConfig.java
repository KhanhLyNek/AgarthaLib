// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.command;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Iterator;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Collection;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.utility.FileUtil;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.core.builder.command.CommandBuild;
import java.util.HashMap;
import com.lymeowz.lib.handler.HandlerConfig;

public class CommandConfig extends HandlerConfig
{
    private static final String PATH_FILE = "Resources/command.yml";
    protected final HashMap<String, CommandBuild> mapCommandBuild;
    
    public CommandConfig(final AgarthaLib plugin) {
        super(plugin);
        this.mapCommandBuild = new HashMap<String, CommandBuild>();
        this.setup();
    }
    
    @Override
    public final void setup() {
        this.reset();
        this.loadConfig();
    }
    
    private final void reset() {
        this.mapCommandBuild.clear();
    }
    
    private final void loadConfig() {
        this.mapCommandBuild.putAll(com.lymeowz.lib.command.CommandDefinitions.load(this.plugin));
    }
}
