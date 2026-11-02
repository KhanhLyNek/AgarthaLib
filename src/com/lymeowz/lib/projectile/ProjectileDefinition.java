package com.lymeowz.lib.projectile;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/** The live legacy projectile metadata object with clone-safe item access. */
public interface ProjectileDefinition {
    UUID id();
    ItemStack mainItemSnapshot();
    ItemStack deputyItemSnapshot();
}
