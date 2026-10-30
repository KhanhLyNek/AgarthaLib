package eu.decentsoftware.holograms.api;
import org.bukkit.Location;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
/** CI fixture: checks duplicate-id and persistence flags. */
public final class DHAPI {
    private static final Set<String> IDS = new HashSet<>();
    public static Object getHologram(String id) { return IDS.contains(id) ? id : null; }
    public static Object createHologram(String id, Location loc, boolean persist, List<String> lines) {
        if (persist || lines == null || lines.isEmpty()) throw new IllegalArgumentException();
        return IDS.add(id) ? id : null;
    }
}
