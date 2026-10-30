// 
// Decompiled by Procyon v0.5.36
// 

package core.praya.agarthalib.bridge.unity;

import core.praya.agarthalib.enums.main.VersionNMS;
import com.praya.agarthalib.packet.livingentity.PacketLivingEntity_Paper26_3;
import com.praya.agarthalib.utility.ServerUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import core.praya.agarthalib.builder.bridge.LivingEntityTools;
import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.handler.HandlerBridge;

public class BridgeLivingEntity extends HandlerBridge
{
    final HandlerPacket handler;
    
    protected BridgeLivingEntity(final AgarthaLib plugin, final HandlerPacket handler) {
        super(plugin);
        this.handler = handler;
    }
    
    protected static final BridgeLivingEntity getInstance() {
        return BridgeLivingEntityHelper.instance;
    }
    
    public final HandlerPacket getHandler() {
        return this.handler;
    }
    
    public final boolean isSet() {
        return this.getHandler() != null;
    }
    
    public final LivingEntityTools getLivingEntityTools() {
        return (this.isSet() && this.getHandler() instanceof LivingEntityTools) ? ((LivingEntityTools)this.getHandler()) : null;
    }
    
    public final boolean isImplementLivingEntityTools() {
        return this.getLivingEntityTools() != null;
    }
    
    public final boolean isLivingEntity(final Entity entity) {
        return this.isImplementLivingEntityTools() && this.getLivingEntityTools().isLivingEntity(entity);
    }
    
    public final boolean isLivingEntity(final EntityType type) {
        return this.isImplementLivingEntityTools() && this.getLivingEntityTools().isLivingEntity(type);
    }
    
    private static class BridgeLivingEntityHelper
    {
        private static final BridgeLivingEntity instance;
        
        static {
            final AgarthaLib plugin = (AgarthaLib)JavaPlugin.getPlugin((Class)AgarthaLib.class);
            final HandlerPacket handler;
            switch (ServerUtil.getVersionNMS()) {
                case V26_3: {
                    handler = new PacketLivingEntity_Paper26_3(plugin);
                    break;
                }
                default: {
                    handler = null;
                    break;
                }
            }
            instance = new BridgeLivingEntity(plugin, handler);
        }
    }
}
