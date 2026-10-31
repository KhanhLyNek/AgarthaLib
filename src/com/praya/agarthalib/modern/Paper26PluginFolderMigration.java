package com.praya.agarthalib.modern;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Non-destructive migration after the Bukkit plugin name (data-folder) rename.
 * Never follows links, deletes old data, or replaces any file already present
 * in the new data folder. Internal package, NBT and placeholders stay legacy.
 */
public final class Paper26PluginFolderMigration {
    private Paper26PluginFolderMigration() { }

    public static void copyMissing(final JavaPlugin plugin, final String legacyName) {
        final Path target = plugin.getDataFolder().toPath().toAbsolutePath().normalize();
        final Path parent = target.getParent();
        if (parent == null || legacyName == null ||
                !legacyName.matches("[A-Za-z0-9_-]{1,64}")) return;
        final Path source = parent.resolve(legacyName).normalize();
        if (source.equals(target) || !source.getParent().equals(parent) ||
                Files.isSymbolicLink(source) || Files.isSymbolicLink(target) ||
                !Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) return;
        final int[] copied = { 0 };
        try {
            Files.createDirectories(target);
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) throws IOException {
                    final Path destination = target.resolve(source.relativize(directory));
                    if (Files.isSymbolicLink(destination)) return FileVisitResult.SKIP_SUBTREE;
                    if (Files.exists(destination, LinkOption.NOFOLLOW_LINKS) &&
                            !Files.isDirectory(destination, LinkOption.NOFOLLOW_LINKS))
                        return FileVisitResult.SKIP_SUBTREE;
                    Files.createDirectories(destination);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!attrs.isRegularFile() || Files.isSymbolicLink(file)) return FileVisitResult.CONTINUE;
                    final Path destination = target.resolve(source.relativize(file));
                    if (!Files.exists(destination, LinkOption.NOFOLLOW_LINKS)) {
                        try {
                            Files.copy(file, destination);
                            copied[0]++;
                        } catch (java.nio.file.FileAlreadyExistsException ignored) {
                            // Another actor populated the destination. Keep their copy.
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            if (copied[0] > 0) {
                plugin.getLogger().info("Preserved " + copied[0] +
                        " existing data file(s) from plugins/" + legacyName +
                        " to plugins/" + plugin.getName() +
                        ". The original folder was not changed.");
            }
        } catch (IOException ex) {
            plugin.getLogger().warning("Could not copy all legacy " + legacyName +
                    " data files: " + ex.getMessage() +
                    ". The old folder remains untouched.");
        }
    }
}
