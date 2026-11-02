package com.praya.agarthalib.modern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.List;

/** Stable AgarthaLib entry point for four optional, separate provider contracts. */
public final class Paper26OptionalProviders {
    private Paper26OptionalProviders() {}

    private static Plugin enabled(final String name) {
        final Plugin provider = Bukkit.getPluginManager().getPlugin(name);
        return provider != null && provider.isEnabled() ? provider : null;
    }

    public static Integer auraSkillLevel(final OfflinePlayer player, final String skill) {
        return Paper26AuraSkillsProgress.level(enabled("AuraSkills"), player, skill);
    }

    public static Integer mmoCoreLevel(final OfflinePlayer player) {
        return Paper26MMOCoreProgress.level(enabled("MMOCore"), player);
    }

    public static String mmoCoreClassId(final OfflinePlayer player) {
        return Paper26MMOCoreProgress.classId(enabled("MMOCore"), player);
    }

    /** Null means Lands integration unavailable, not an unclaimed area. */
    public static Boolean landsClaimed(final Plugin owner, final Location where) {
        return Paper26LandsClaims.isClaimed(enabled("Lands"), owner, where);
    }

    /** Transient only; does not touch saved or imported hologram YAML. */
    public static boolean createTransientHologram(final String name, final Location where,
                                                  final List<String> lines) {
        return Paper26DecentHolograms.createTransient(
                enabled("DecentHolograms"), name, where, lines);
    }
}
