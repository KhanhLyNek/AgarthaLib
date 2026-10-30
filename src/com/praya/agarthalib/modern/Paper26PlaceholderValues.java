package com.praya.agarthalib.modern;

import java.util.Map;

/**
 * Preserves the exact YAML values and legacy case-insensitive identifier
 * matching. It does not rewrite, reformat or recursively parse placeholders.
 */
public final class Paper26PlaceholderValues {
    private Paper26PlaceholderValues() {
    }

    public static String get(final Map<String, String> configured, final String identifier) {
        if (configured == null || identifier == null) {
            return null;
        }
        for (final Map.Entry<String, String> entry : configured.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(identifier)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
