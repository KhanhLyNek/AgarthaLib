package net.ess3.api;

import java.util.Collection;
import org.bukkit.Location;

/**
 * CI-only public EssentialsX 2.22 API shape. This Java source is never part
 * of AgarthaLib's production JAR and does NOT replace the provider itself.
 */
public interface IWarps {
    Collection<String> getList();
    Location getWarp(String name) throws Exception;
}
