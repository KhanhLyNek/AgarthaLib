package net.Indyuce.mmocore.api.player;
import java.util.UUID;
/** CI-only public MMOCore API shape. */
public final class PlayerData {
    public static PlayerData get(final UUID player) { return player == null ? null : new PlayerData(); }
    public int getLevel() { return 17; }
    public Prof getProfess() { return new Prof(); }
    public static final class Prof { public String getId() { return "mage"; } }
}
