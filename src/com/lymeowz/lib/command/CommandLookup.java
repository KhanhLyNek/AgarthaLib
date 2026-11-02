package com.lymeowz.lib.command;

import com.lymeowz.lib.core.builder.command.CommandBuild;

/** Legacy command-main and alias matching, moved without rewriting any IDs. */
public final class CommandLookup {
    private CommandLookup() { }

    public static boolean matches(final CommandBuild commandBuild, final String arg) {
        if (commandBuild != null) {
            final String main = commandBuild.getMain();
            if (main.equalsIgnoreCase(arg)) {
                return true;
            }
            for (final String aliases : commandBuild.getAliases()) {
                if (aliases.equalsIgnoreCase(arg)) {
                    return true;
                }
            }
        }
        return false;
    }
}
