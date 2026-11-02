package com.lymeowz.lib.effect;

import java.util.Collection;

/** Immutable ID snapshots and direct lookups on the original effect manager. */
public interface EffectRegistry {
    Collection<String> ids();
    EffectDefinition find(String id);
}
