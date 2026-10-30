// 
// Decompiled by Procyon v0.5.36
// 

package core.praya.agarthalib.bridge.unity;

import core.praya.agarthalib.enums.main.VersionNMS;
import com.praya.agarthalib.utility.ServerUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import core.praya.agarthalib.builder.bridge.MaterialTools;
import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.handler.HandlerBridge;

@Deprecated
public class BridgeMaterial extends HandlerBridge
{
    final HandlerPacket handler;
    
    protected BridgeMaterial(final AgarthaLib plugin, final HandlerPacket handler) {
        super(plugin);
        this.handler = handler;
    }
    
    protected static final BridgeMaterial getInstance() {
        return BridgeMaterialHelper.instance;
    }
    
    public final HandlerPacket getHandler() {
        return this.handler;
    }
    
    public final boolean isSet() {
        return this.getHandler() != null;
    }
    
    public final MaterialTools getMaterialTools() {
        return (this.isSet() && this.getHandler() instanceof MaterialTools) ? ((MaterialTools)this.getHandler()) : null;
    }
    
    public final boolean isImplementMaterialTools() {
        return this.getMaterialTools() != null;
    }
    
    public final boolean isSolid(final Material material) {
        return this.isImplementMaterialTools() && this.getMaterialTools().isSolid(material);
    }
    
    public final boolean isFluid(final Material material) {
        return this.isImplementMaterialTools() && this.getMaterialTools().isFluid(material);
    }
    
    private static class BridgeMaterialHelper
    {
        private static final BridgeMaterial instance;
        
        static {
            final AgarthaLib plugin = (AgarthaLib)JavaPlugin.getPlugin((Class)AgarthaLib.class);
            final HandlerPacket handler;
            switch (ServerUtil.getVersionNMS()) {
                case V26_3: {
                    handler = new PacketMaterial_Paper26_3(plugin);
                    break;
                }
                default: {
                    handler = null;
                    break;
                }
            }
            instance = new BridgeMaterial(plugin, handler);
        }
    }
}
