package com.praya.agarthalib.modern;

import org.bukkit.Bukkit;

/** Preserve the former packet server contract: first-minute TPS, 2 decimals, [0,20]. */
public final class Paper26ServerTPS {
    private Paper26ServerTPS() {}

    public static double current() {
        final double[] tps = Bukkit.getTPS();
        return tps.length == 0 ? 20.0 : normalize(tps[0]);
    }

    public static double normalize(final double tps) {
        if (Double.isNaN(tps)) return 20.0;
        if (tps == Double.NEGATIVE_INFINITY) return 0.0;
        if (tps == Double.POSITIVE_INFINITY) return 20.0;
        return Math.round(Math.max(0.0, Math.min(20.0, tps)) * 100.0) / 100.0;
    }
}
