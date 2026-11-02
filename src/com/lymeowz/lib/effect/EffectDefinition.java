package com.lymeowz.lib.effect;

/** Read-only view implemented by each original, live custom effect type. */
public interface EffectDefinition {
    String id();
    String pluginName();
}
