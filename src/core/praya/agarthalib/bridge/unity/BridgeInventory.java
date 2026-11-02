// 
// Decompiled by Procyon v0.5.36
// 

package core.praya.agarthalib.bridge.unity;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.Handler;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.packet.inventory.*;
import com.praya.agarthalib.utility.ServerUtil;
import core.praya.agarthalib.builder.bridge.WindowTools;
import core.praya.agarthalib.enums.main.VersionNMS;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class BridgeInventory extends Handler
{
    private final HandlerPacket handler;
    
    protected BridgeInventory(final AgarthaLib plugin, final HandlerPacket handler) {
        super(plugin);
        this.handler = handler;
    }
    
    protected static final BridgeInventory getInstance() {
        return BridgeInventoryHelper.instance;
    }
    
    public final HandlerPacket getHandler() {
        return this.handler;
    }
    
    public final boolean isSet() {
        return this.getHandler() != null;
    }
    
    public final WindowTools getWindowTools() {
        return (this.isSet() && this.getHandler() instanceof WindowTools) ? ((WindowTools)this.getHandler()) : null;
    }
    
    public final boolean isImplementWindowTools() {
        return this.getWindowTools() != null;
    }
    
    public final void setWindowTitle(final Player player, final String title) {
        final WindowTools windowTools = this.getWindowTools();
        if (windowTools != null) {
            windowTools.setTitleWindow(player, title);
        }
    }
    
    public final void setWindowTitle(final Inventory inventory, final String title) {
        final WindowTools windowTools = this.getWindowTools();
        if (windowTools != null) {
            windowTools.setTitleWindow(inventory, title);
        }
    }
    
    private static class BridgeInventoryHelper
    {
        private static final BridgeInventory instance;
        
        static {
            final AgarthaLib plugin = (AgarthaLib)JavaPlugin.getPlugin((Class)AgarthaLib.class);
            final HandlerPacket handler;
            switch (ServerUtil.getVersionNMS()) {
                case V26_3: {
                    handler = new PacketInventory_Paper26_3(plugin);
                    break;
                }
                default: {
                    handler = null;
                    break;
                }
            }
            instance = new BridgeInventory(plugin, handler);
        }
    }
}
