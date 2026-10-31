package com.lymeowz.lib.command;

import java.util.Collection;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * Canonical view over the existing legacy command registry. Existing commands,
 * aliases, permission semantics and return types are not altered.
 */
public interface CommandRegistry {
    Collection<String> getCommandIds();
    String getMain(String id);
    String getPermission(String id);
    List<String> getAliases(String id);
    boolean isCommandExists(String id);
    boolean checkPermission(CommandSender sender, String id);
    boolean checkCommand(String arg, String id);
}
