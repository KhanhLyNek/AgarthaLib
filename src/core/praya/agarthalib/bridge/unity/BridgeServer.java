// 
// Decompiled by Procyon v0.5.36
// 

package core.praya.agarthalib.bridge.unity;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerBridge;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.packet.server.*;
import com.praya.agarthalib.utility.ServerUtil;
import core.praya.agarthalib.builder.bridge.ServerTools;
import core.praya.agarthalib.enums.main.VersionNMS;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

public class BridgeServer extends HandlerBridge
{
    final HandlerPacket handler;
    
    protected BridgeServer(final AgarthaLib plugin, final HandlerPacket handler) {
        super(plugin);
        this.handler = handler;
    }
    
    protected static final BridgeServer getInstance() {
        return BridgeServerHelper.instance;
    }
    
    public final HandlerPacket getHandler() {
        return this.handler;
    }
    
    public final boolean isSet() {
        return this.getHandler() != null;
    }
    
    public final ServerTools getServerTools() {
        return (this.isSet() && this.getHandler() instanceof ServerTools) ? ((ServerTools)this.getHandler()) : null;
    }
    
    public final boolean isImplementServerTools() {
        return this.getServerTools() != null;
    }
    
    public final double getTPS() {
        if (ServerUtil.getVersionNMS() == VersionNMS.V26_3) {
            return com.praya.agarthalib.modern.Paper26ServerTPS.current();
        }
        return this.isImplementServerTools() ? this.getServerTools().getTPS() : 20.0;
    }
    
    private static class BridgeServerHelper
    {
        private static final BridgeServer instance;
        
        static {
            final AgarthaLib plugin = (AgarthaLib)JavaPlugin.getPlugin((Class)AgarthaLib.class);
            final HandlerPacket handler;
            switch (ServerUtil.getVersionNMS()) {
                case V26_3: {
                    handler = null;
                    break;
                }
                default: {
                    handler = null;
                    break;
                }
            }
            instance = new BridgeServer(plugin, handler);
        }
    }
}
