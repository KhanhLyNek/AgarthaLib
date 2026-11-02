package com.praya.agarthalib.modern;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Paper 26.3 replacement for the old client-only block-break animation packet.
 *
 * Keeps the legacy source id and x/y/z for each viewer. This only draws the
 * cracking overlay; it never mines or edits the real block.
 */
public final class Paper26BlockDamage {
    private Paper26BlockDamage() {
    }

    /**
     * Legacy packet stages are 0..9; out-of-range values remove the overlay.
     * Paper accepts progress in 0..1. We preserve the full range and use 0 for
     * a cleared animation. Exact visual quantization requires a server test.
     */
    public static float progressForLegacyStage(final int stage) {
        if (stage < 0 || stage > 9) {
            return 0.0f;
        }
        return (stage + 1) / 10.0f;
    }

    public static void send(final int sourceId, final Collection<Player> viewers,
                            final int x, final int y, final int z, final int stage) {
        if (viewers == null) {
            return;
        }
        final float progress = progressForLegacyStage(stage);
        for (final Player viewer : viewers) {
            if (viewer != null && viewer.isOnline() && viewer.getWorld() != null) {
                // Old packet carried only coordinates, not a world identifier.
                // Use the receiver's world to match the original packet scope.
                final Location block = new Location(viewer.getWorld(), x, y, z);
                viewer.sendBlockDamage(block, progress, sourceId);
            }
        }
    }
}
