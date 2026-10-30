package me.angeschossen.lands.api.integration;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
public interface LandsIntegration {
    static LandsIntegration of(final Plugin owner) { return owner == null ? null : new Fixture(); }
    Object getArea(Location at);
    final class Fixture implements LandsIntegration {
        @Override public Object getArea(final Location at) {
            return at.getBlockX() > 0 ? new Object() : null;
        }
    }
}
