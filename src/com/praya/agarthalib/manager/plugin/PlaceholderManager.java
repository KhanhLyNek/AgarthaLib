// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.agarthalib.manager.plugin;

import org.bukkit.entity.Player;
import com.praya.agarthalib.utility.ListUtil;
import com.praya.agarthalib.utility.TextUtil;
import java.util.List;
import com.praya.agarthalib.placeholder.replacer.ReplacerMVDWPlaceholderAPI;
import com.praya.agarthalib.placeholder.replacer.ReplacerPlaceholderAPI;
import com.praya.agarthalib.utility.PluginUtil;
import java.util.Collection;
import java.util.HashMap;
import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerManager;

public abstract class PlaceholderManager extends HandlerManager
{
    protected PlaceholderManager(final AgarthaLib plugin) {
        super(plugin);
    }
    
    protected abstract HashMap<String, String> getMapPlaceholder();
    
    public abstract Collection<String> getPlaceholderIds();
    
    public abstract Collection<String> getPlaceholders();
    
    public abstract String getPlaceholder(final String p0);
    
    public final boolean isPlaceholderExists(final String id) {
        return this.getPlaceholder(id) != null;
    }
    
    public final void registerAll() {
        final String placeholder = this.plugin.getPluginPlaceholder();
        final org.bukkit.plugin.Plugin papi = PluginUtil.getPlugin("PlaceholderAPI");
        if (papi != null && papi.isEnabled()) {
            new ReplacerPlaceholderAPI(this.plugin, placeholder).hook();
        }
        final org.bukkit.plugin.Plugin mvdw = PluginUtil.getPlugin("MVdWPlaceholderAPI");
        if (mvdw != null && mvdw.isEnabled()) {
            new ReplacerMVDWPlaceholderAPI(this.plugin, placeholder).register();
        }
    }
    
    public final List<String> localPlaceholder(final List<String> list) {
        final String divider = "\n";
        final String builder = TextUtil.convertListToString(list, "\n");
        final String text = this.localPlaceholder(builder);
        return ListUtil.convertStringToList(text, "\n");
    }
    
    public final String localPlaceholder(final String text) {
        return TextUtil.placeholder(this.getMapPlaceholder(), text);
    }
    
    public final String getReplacement(final Player player, final String identifier) {
        // The original decompiled body unconditionally returned null, making
        // both PAPI and MVdW expansions inert. Configured values are returned
        // byte-for-byte: no new color, recursion or player-dependent behavior.
        return this.getPlaceholder(identifier);
    }
}
