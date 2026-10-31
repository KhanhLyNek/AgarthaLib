package com.lymeowz.lib.command;

import com.praya.agarthalib.AgarthaLib;
import com.praya.agarthalib.utility.FileUtil;
import core.praya.agarthalib.builder.command.CommandBuild;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/** Canonical command YAML reader. Keeps original AgarthaLib command IDs and aliases unchanged. */
public final class CommandDefinitions {
    private CommandDefinitions() { }

    public static HashMap<String, CommandBuild> load(final AgarthaLib plugin) {
        final HashMap<String, CommandBuild> map = new HashMap<>();
        final FileConfiguration config = FileUtil.getFileConfigurationResource(plugin, "Resources/command.yml");
        for (final String key : config.getKeys(false)) {
            final ConfigurationSection keyDataSection = config.getConfigurationSection(key);
            for (final String keyData : keyDataSection.getKeys(false)) {
                if (keyDataSection.isConfigurationSection(keyData)) {
                    final ConfigurationSection mainDataSection = keyDataSection.getConfigurationSection(keyData);
                    final String id = String.valueOf(key) + "_" + keyData;
                    final List<String> aliases = new ArrayList<String>();
                    String main = null;
                    String permission = null;
                    for (final String mainData : mainDataSection.getKeys(false)) {
                        if (mainData.equalsIgnoreCase("Main")) {
                            main = mainDataSection.getString(mainData);
                        }
                        else if (mainData.equalsIgnoreCase("Permission")) {
                            permission = mainDataSection.getString(mainData);
                        }
                        else {
                            if (!mainData.equalsIgnoreCase("Aliases")) {
                                continue;
                            }
                            if (mainDataSection.isString(mainData)) {
                                aliases.add(mainDataSection.getString(mainData));
                            }
                            else {
                                if (!mainDataSection.isList(mainData)) {
                                    continue;
                                }
                                aliases.addAll(mainDataSection.getStringList(mainData));
                            }
                        }
                    }
                    if (main == null) {
                        continue;
                    }
                    final CommandBuild commandBuild = new CommandBuild(id, main, permission, aliases);
                    map.put(id, commandBuild);
                }
            }
        }
        return map;
    }
