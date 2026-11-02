// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.agarthalib.command;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Iterator;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Collection;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import com.praya.agarthalib.utility.FileUtil;
import com.praya.agarthalib.AgarthaLib;
import core.praya.agarthalib.builder.command.CommandBuild;
import java.util.HashMap;
import com.praya.agarthalib.handler.HandlerConfig;

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
