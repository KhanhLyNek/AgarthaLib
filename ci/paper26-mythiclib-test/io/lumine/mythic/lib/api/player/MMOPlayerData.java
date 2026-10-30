package io.lumine.mythic.lib.api.player;
import java.util.UUID;

/** CI-only shape from MythicLib's documented stats API; never shipped. */
public final class MMOPlayerData {
    public static MMOPlayerData get(final UUID id) { return id == null ? null : new MMOPlayerData(); }
    public StatMap getStatMap() { return new StatMap(); }
    public static final class StatMap {
        public double getStat(final String name) {
            return "ATTACK_DAMAGE".equals(name) ? 13.5 : Double.NaN;
        }
    }
}
