package com.lymeowz.lib.permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Locale;

/**
 * Canonical permission namespace for LyMeowz plugins.
 * Preserve old player permission nodes while operators migrate to new nodes.
 * No command IDs or item metadata are rewritten.
 */
public final class PermissionBridge {
    private PermissionBridge() { }

    public static String canonical(String node) {
        if (node == null) return null;
        String lower = node.toLowerCase(Locale.ROOT);
        if (lower.startsWith("agarthalib."))
            return "lymeowzlib." + node.substring("agarthalib.".length());
        if (lower.startsWith("myitems."))
            return "lymeowzitem." + node.substring("myitems.".length());
        return node;
    }

    public static boolean allowed(CommandSender sender, String permission) {
        // Maintain the historical AgarthaLib SenderUtil semantics: only
        // players are permission-gated, the console is not.
        if (permission == null || permission.isEmpty() || !(sender instanceof Player))
            return true;
        final String modern = canonical(permission);
        return sender.hasPermission(modern)
                || (!modern.equalsIgnoreCase(permission) && sender.hasPermission(permission));
    }
}
