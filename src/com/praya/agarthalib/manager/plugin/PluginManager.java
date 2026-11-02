// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.agarthalib.manager.plugin;

import com.praya.agarthalib.metrics.MetricsMemory;
import com.praya.agarthalib.plugin.PluginPropertiesMemory;
import com.praya.agarthalib.command.CommandMemory;
import com.praya.agarthalib.language.LanguageMemory;
import com.praya.agarthalib.placeholder.PlaceholderMemory;
import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerManager;

public class PluginManager extends HandlerManager implements com.lymeowz.lib.manager.PluginServices
{
    private LanguageManager languageManager;
    private CommandManager commandManager;
    private PlaceholderManager placeholderManager;
    private PluginPropertiesManager pluginPropertiesManager;
    private MetricsManager metricsManager;
    
    public PluginManager(final AgarthaLib plugin) {
        super(plugin);
        this.placeholderManager = PlaceholderMemory.getInstance();
        this.languageManager = LanguageMemory.getInstance();
        this.commandManager = CommandMemory.getInstance();
        this.pluginPropertiesManager = PluginPropertiesMemory.getInstance();
        this.metricsManager = MetricsMemory.getInstance();
    }
    
    public final CommandManager getCommandManager() {
        return this.commandManager;
    }
    
    public final LanguageManager getLanguageManager() {
        return this.languageManager;
    }
    
    public final PlaceholderManager getPlaceholderManager() {
        return this.placeholderManager;
    }
    
    public final PluginPropertiesManager getPluginPropertiesManager() {
        return this.pluginPropertiesManager;
    }
    
    public final MetricsManager getMetricsManager() {
        return this.metricsManager;
    }

    @Override
    public final com.lymeowz.lib.command.CommandRegistry commands() {
        return this.commandManager;
    }

    @Override
    public final java.util.Collection<String> languageIds() {
        return java.util.Collections.unmodifiableList(
                new java.util.ArrayList<>(this.languageManager.getLanguageIds()));
    }

    @Override
    public final java.util.List<String> languageLines(final String locale, final String key) {
        return java.util.Collections.unmodifiableList(
                new java.util.ArrayList<>(this.languageManager.getListText(locale, key)));
    }

    @Override
    public final String placeholder(final String identifier) {
        return this.placeholderManager.getPlaceholder(identifier);
    }
}
