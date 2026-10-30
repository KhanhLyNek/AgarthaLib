package com.praya.agarthalib;

import com.praya.agarthalib.manager.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * CI-only stand-in for the original JavaPlugin-backed hierarchy.
 * Never part of a packaged production plugin.
 */
public class AgarthaLib extends JavaPlugin {
    public String getPluginPlaceholder() {
        return "agarthalib";
    }

    public PluginManager getPluginManager() {
        return null;
    }
}
