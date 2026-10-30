package com.praya.agarthalib.modern;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Paper 26.3 sound registry resolver.
 *
 * Accepts the *original* SoundEnum identifier as a String, so gameplay classes
 * and YAML aliases keep their existing keys. Returns null for removed sounds;
 * call sites should not replace a missing sound with a different effect.
 */
public final class Paper26SoundRegistry {
    private static final Map<String, Optional<Sound>> CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> RENAMED = Map.ofEntries(
            Map.entry("block.cloth.break", "block.wool.break"),
            Map.entry("block.cloth.fall", "block.wool.fall"),
            Map.entry("block.cloth.hit", "block.wool.hit"),
            Map.entry("block.cloth.place", "block.wool.place"),
            Map.entry("block.cloth.step", "block.wool.step"),
            Map.entry("block.note.bass", "block.note_block.bass"),
            Map.entry("block.note.basedrum", "block.note_block.basedrum"),
            Map.entry("block.note.harp", "block.note_block.harp"),
            Map.entry("block.note.hat", "block.note_block.hat"),
            Map.entry("block.note.pling", "block.note_block.pling"),
            Map.entry("block.note.snare", "block.note_block.snare"),
            Map.entry("entity.enderdragon.death", "entity.ender_dragon.death"),
            Map.entry("entity.enderdragon.hurt", "entity.ender_dragon.hurt"),
            Map.entry("entity.enderdragon.flap", "entity.ender_dragon.flap"),
            Map.entry("entity.zombie.pig.ambient", "entity.zombified_piglin.ambient"),
            Map.entry("entity.zombie.pig.angry", "entity.zombified_piglin.angry"),
            Map.entry("entity.zombie.pig.death", "entity.zombified_piglin.death"),
            Map.entry("entity.zombie.pig.hurt", "entity.zombified_piglin.hurt")
    );

    private Paper26SoundRegistry() {
    }

    public static Sound resolve(final String originalEnumName) {
        if (originalEnumName == null || originalEnumName.isBlank()) {
            return null;
        }
        final String normalized = originalEnumName.toLowerCase(Locale.ROOT);
        return CACHE.computeIfAbsent(normalized, Paper26SoundRegistry::resolveUncached).orElse(null);
    }

    private static Optional<Sound> resolveUncached(final String originalEnumName) {
        final String naturalKey = originalEnumName.replace('_', '.');
        final Sound original = Registry.SOUND_EVENT.get(NamespacedKey.minecraft(naturalKey));
        if (original != null) {
            return Optional.of(original);
        }
        final String replacement = RENAMED.get(naturalKey);
        if (replacement == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(Registry.SOUND_EVENT.get(NamespacedKey.minecraft(replacement)));
    }
}
