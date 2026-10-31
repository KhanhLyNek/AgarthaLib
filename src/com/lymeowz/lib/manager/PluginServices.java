package com.lymeowz.lib.manager;

import java.util.Collection;
import java.util.List;
import com.lymeowz.lib.command.CommandRegistry;

/**
 * Canonical read-only view of live library command, language and placeholder
 * managers. Original manager types retain their public binary signatures.
 */
public interface PluginServices {
    CommandRegistry commands();
    Collection<String> languageIds();
    List<String> languageLines(String locale, String key);
    String placeholder(String identifier);
}
