// 
// Decompiled by Procyon v0.5.36
// 

package core.praya.agarthalib.bridge.unity;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.AgarthaLibConfig;
import com.praya.agarthalib.handler.HandlerBridge;
import com.praya.agarthalib.handler.HandlerPacket;
import com.praya.agarthalib.packet.player.*;
import com.praya.agarthalib.utility.ServerUtil;
import core.praya.agarthalib.enums.main.VersionNMS;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class BridgePlayer extends HandlerBridge
{
    private final HandlerPacket handler;
    
    protected BridgePlayer(final AgarthaLib plugin, final HandlerPacket handler) {
        super(plugin);
        this.handler = handler;
    }
    
    public static final BridgePlayer getInstance() {
        return BridgePlayerSingleton.instance;
    }
    
    public final HandlerPacket getHandler() {
        return this.handler;
    }
    
    public final boolean isSet() {
        return this.getHandler() != null;
    }
    
    public final PacketPlayerProperties getPacketPlayerProperties() {
        return (this.isSet() && this.getHandler() instanceof PacketPlayerProperties) ? ((PacketPlayerProperties)this.getHandler()) : null;
    }
    
    public final PacketPlayerVisibility getPacketPlayerVisibility() {
        return (this.isSet() && this.getHandler() instanceof PacketPlayerVisibility) ? ((PacketPlayerVisibility)this.getHandler()) : null;
    }
    
    public final PacketPlayerAbsorption getPacketPlayerAbsorption() {
        return (this.isSet() && this.getHandler() instanceof PacketPlayerAbsorption) ? ((PacketPlayerAbsorption)this.getHandler()) : null;
    }
    
    public final PacketPlayerCombat getPacketPlayerCombat() {
        return (this.isSet() && this.getHandler() instanceof PacketPlayerCombat) ? ((PacketPlayerCombat)this.getHandler()) : null;
    }
    
    public final String getLocale(final Player player) {
        final PacketPlayerProperties packetPlayerProperties = this.getPacketPlayerProperties();
        final AgarthaLibConfig mainConfig = this.plugin.getMainConfig();
        if (player == null) {
            return mainConfig.getGeneralLocale();
        }
        return (packetPlayerProperties != null) ? packetPlayerProperties.getLocale(player) : mainConfig.getGeneralLocale();
    }
    
    public final void hideEntity(final Player player, final Entity entity) {
        final PacketPlayerVisibility packetPlayerVisibility = this.getPacketPlayerVisibility();
        if (packetPlayerVisibility != null) {
            packetPlayerVisibility.hideEntity(player, entity);
        }
    }
    
    public final void hideEntity(final Collection<Player> players, final Entity entity) {
        final PacketPlayerVisibility packetPlayerVisibility = this.getPacketPlayerVisibility();
        if (packetPlayerVisibility != null) {
            packetPlayerVisibility.hideEntity(players, entity);
        }
    }
    
    /**
     * Explicitly reveal an entity hidden by AgarthaLib on Paper 26.3.
     * This is additive; existing hideEntity call sites remain unchanged.
     * The original NMS protocols do not expose a matching operation here.
     */
    public final void showEntity(final Player viewer, final Entity entity) {
        if (ServerUtil.getVersionNMS() == VersionNMS.V26_3 && viewer != null && entity != null) {
            viewer.showEntity(this.plugin, entity);
        }
    }

    public final void showEntity(final Collection<Player> viewers, final Entity entity) {
        if (viewers != null && entity != null) {
            for (final Player viewer : viewers) {
                this.showEntity(viewer, entity);
            }
        }
    }

    public final float getAbsorptionHearts(final Player player) {
        final PacketPlayerAbsorption packetPlayerAbsorption = this.getPacketPlayerAbsorption();
        return (packetPlayerAbsorption != null) ? packetPlayerAbsorption.getAbsorptionHearts(player) : 0.0f;
    }
    
    public final void setAbsorptionHearts(final Player player, final float absorptionHearts) {
        final PacketPlayerAbsorption packetPlayerAbsorption = this.getPacketPlayerAbsorption();
        if (packetPlayerAbsorption != null) {
            packetPlayerAbsorption.setAbsorptionHearts(player, absorptionHearts);
        }
    }
    
    public final void playSwingMainHand(final Player player) {
        final PacketPlayerCombat packetPlayerCombat = this.getPacketPlayerCombat();
        if (packetPlayerCombat != null) {
            packetPlayerCombat.playSwingMainHand(player);
        }
    }
    
    public final float getSwingDuration(final Player player) {
        final PacketPlayerCombat packetPlayerCombat = this.getPacketPlayerCombat();
        if (packetPlayerCombat != null && packetPlayerCombat instanceof PacketPlayerCombatPresent) {
            final PacketPlayerCombatPresent packetPlayerCombatPresent = (PacketPlayerCombatPresent)packetPlayerCombat;
            return packetPlayerCombatPresent.getSwingDuration(player);
        }
        return 0.0f;
    }
    
    public final float getSwingProgress(final Player player) {
        final PacketPlayerCombat packetPlayerCombat = this.getPacketPlayerCombat();
        if (packetPlayerCombat != null && packetPlayerCombat instanceof PacketPlayerCombatPresent) {
            final PacketPlayerCombatPresent packetPlayerCombatPresent = (PacketPlayerCombatPresent)packetPlayerCombat;
            return packetPlayerCombatPresent.getSwingProgress(player);
        }
        return 0.0f;
    }
    
    public final void playSwingOffHand(final Player player) {
        if (ServerUtil.getVersionNMS() == VersionNMS.V26_3) {
            if (player != null) {
                player.swingOffHand();
            }
            return;
        }
        final PacketPlayerCombat packetPlayerCombat = this.getPacketPlayerCombat();
        if (packetPlayerCombat != null && packetPlayerCombat instanceof PacketPlayerCombatPresent) {
            final PacketPlayerCombatPresent packetPlayerCombatPresent = (PacketPlayerCombatPresent)packetPlayerCombat;
            packetPlayerCombatPresent.playSwingOffHand(player);
        }
    }
    
    private static class BridgePlayerSingleton
    {
        private static final BridgePlayer instance;
        
        static {
            final AgarthaLib plugin = (AgarthaLib)JavaPlugin.getPlugin((Class)AgarthaLib.class);
            final HandlerPacket handler;
            switch (ServerUtil.getVersionNMS()) {
                case V26_3: {
                    handler = new PacketPlayer_Paper26_3(plugin);
                    break;
                }
                default: {
                    handler = null;
                    break;
                }
            }
            instance = new BridgePlayer(plugin, handler);
        }
    }
}
