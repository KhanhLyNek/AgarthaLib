// 
// Decompiled by Procyon v0.5.36
// 

package core.praya.agarthalib.bridge.unity;

import com.praya.agarthalib.packet.arrow.PacketArrow_Paper26_3;

import com.praya.agarthalib.utility.ServerUtil;
import core.praya.agarthalib.enums.main.VersionNMS;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import com.praya.agarthalib.packet.arrow.PacketArrowModification;
import com.praya.agarthalib.packet.arrow.PacketArrowNormal;
import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.handler.HandlerBridge;

public class BridgeArrow extends HandlerBridge
{
    private final HandlerPacket handler;
    
    protected BridgeArrow(final AgarthaLib plugin, final HandlerPacket handler) {
        super(plugin);
        this.handler = handler;
    }
    
    protected static final BridgeArrow getInstance() {
        return BridgeArrowHelper.instance;
    }
    
    public final HandlerPacket getHandler() {
        return this.handler;
    }
    
    public final boolean isSet() {
        return this.getHandler() != null;
    }
    
    public final PacketArrowNormal getArrowNormal() {
        return (this.isSet() && this.getHandler() instanceof PacketArrowNormal) ? ((PacketArrowNormal)this.getHandler()) : null;
    }
    
    public final PacketArrowModification getArrowModification() {
        return (this.isSet() && this.getHandler() instanceof PacketArrowModification) ? ((PacketArrowModification)this.getHandler()) : null;
    }
    
    public final boolean isImplementArrowNormal() {
        return this.getArrowNormal() != null;
    }
    
    public final boolean isImplementArrowModification() {
        return this.getArrowModification() != null;
    }
    
    public final ItemStack createNormalArrow() {
        return this.isImplementArrowNormal() ? this.getArrowNormal().createNormalArrow() : null;
    }
    
    public final ItemStack createTippedArrow(final Color color) {
        return this.isImplementArrowModification() ? this.getArrowModification().createTippedArrow(color) : this.createNormalArrow();
    }
    
    public final ItemStack createTippedArrow(final int red, final int green, final int blue) {
        return this.isImplementArrowModification() ? this.getArrowModification().createTippedArrow(red, green, blue) : this.createNormalArrow();
    }
    
    public final ItemStack createSpectralArrow() {
        return this.isImplementArrowModification() ? this.getArrowModification().createSpectralArrow() : this.createNormalArrow();
    }
    
    @Deprecated
    public final ItemStack createDefault() {
        return this.createNormalArrow();
    }
    
    @Deprecated
    public final ItemStack createTipped(final Color color) {
        return this.createTippedArrow(color);
    }
    
    @Deprecated
    public final ItemStack createTipped(final int red, final int green, final int blue) {
        return this.createTippedArrow(red, green, blue);
    }
    
    @Deprecated
    public final ItemStack createSpectral() {
        return this.createSpectralArrow();
    }
    
    private static class BridgeArrowHelper
    {
        private static final BridgeArrow instance;
        
        static {
            final AgarthaLib plugin = (AgarthaLib)JavaPlugin.getPlugin((Class)AgarthaLib.class);
            final HandlerPacket handler;
            switch (ServerUtil.getVersionNMS()) {
                case V26_3: {
                    handler = new PacketArrow_Paper26_3(plugin);
                    break;
                }
                default: {
                    handler = null;
                    break;
                }
            }
            instance = new BridgeArrow(plugin, handler);
        }
    }
}
