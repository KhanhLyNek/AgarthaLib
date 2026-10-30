// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.agarthalib.utility;

import core.praya.agarthalib.enums.branch.BiomeEnum;
import org.bukkit.block.Biome;

public class BiomeUtil
{
    public static final Biome getBiome(final String biome) {
        if (biome != null) {
            final Biome biomePrimary = getBiomeByOriginalName(biome);
            if (biomePrimary != null) {
                return biomePrimary;
            }
            final BiomeEnum biomeEnum = BiomeEnum.getBiomeEnum(biome);
            if (biomeEnum != null) {
                return biomeEnum.getBiome();
            }
        }
        return null;
    }
    
    public static final Biome getBiomeByOriginalName(final String biome) {
        if (biome == null || biome.isBlank()) return null;
        // Biome.values() is marked for removal: biomes are a dynamic registry
        // on Paper 26.3 and may be supplied by datapacks. The historical
        // names ("PLAINS") still resolve as minecraft:plains.
        final String canonical = biome.trim().toLowerCase(java.util.Locale.ROOT);
        final org.bukkit.NamespacedKey key;
        try {
            key = org.bukkit.NamespacedKey.fromString(canonical);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return key == null ? null : io.papermc.paper.registry.RegistryAccess.registryAccess()
                .getRegistry(io.papermc.paper.registry.RegistryKey.BIOME).get(key);
    }
    
    public static final boolean isBiomeExists(final String biome) {
        return getBiome(biome) != null;
    }
    
    @Deprecated
    public static final boolean isExists(final String biome) {
        return getBiome(biome) != null;
    }
}
