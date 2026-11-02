// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.main;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.lymeowz.lib.projectile.ProjectileProperties;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import com.lymeowz.lib.manager.game.ProjectilePropertiesManager;
import com.lymeowz.lib.manager.game.GameManager;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.lib.core.enums.main.Slot;
import com.lymeowz.lib.utility.EquipmentUtil;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.event.Listener;
import com.lymeowz.lib.handler.HandlerListener;

public class ListenerEntityShootBow extends HandlerListener implements Listener
{
    public ListenerEntityShootBow(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void entityShootBowEvent(final EntityShootBowEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final ProjectilePropertiesManager projectilePropertiesManager = gameManager.getProjectilePropertiesManager();
        if (!event.isCancelled()) {
            final Entity entityProjectile = event.getProjectile();
            if (entityProjectile instanceof Projectile) {
                final LivingEntity shooter = event.getEntity();
                final Projectile projectile = (Projectile)entityProjectile;
                final Slot slotMain = EquipmentUtil.getActiveSlotBow(shooter);
                final Slot slotDeputy = (slotMain != null && slotMain.equals(Slot.OFFHAND)) ? Slot.MAINHAND : Slot.OFFHAND;
                final ItemStack itemMain = event.getBow();
                final ItemStack itemDeputy = Bridge.getBridgeEquipment().getEquipment(shooter, slotDeputy);
                final ProjectileProperties projectileProperties = projectilePropertiesManager.getProjectileProperties(projectile, true);
                projectileProperties.setItemMain(itemMain);
                projectileProperties.setItemDeputy(itemDeputy);
            }
        }
    }
}
