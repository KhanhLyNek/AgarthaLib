package com.praya.agarthalib.placeholder;

import com.praya.agarthalib.AgarthaLib;
import java.util.HashMap;

/** Test-only config holder. The real PlaceholderConfig loads protected YAML. */
public final class PlaceholderConfig {
    protected final HashMap<String, String> mapPlaceholder = new HashMap<>();
    public PlaceholderConfig(final AgarthaLib plugin) {}
    public void setFixtureValue(final String key, final String value) {
        mapPlaceholder.put(key, value);
    }
}
