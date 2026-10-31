package com.praya.agarthalib.modern;

import org.bukkit.plugin.java.JavaPlugin;

/** @deprecated Use the LyMeowzLib-owned migration API; kept for ABI compatibility. */
@Deprecated
public final class Paper26PluginFolderMigration {
    private Paper26PluginFolderMigration() { }

    public static void copyMissing(JavaPlugin plugin, String legacyName) {
        com.lymeowz.lib.migration.PluginFolderMigration.copyMissing(plugin, legacyName);
    }
}
