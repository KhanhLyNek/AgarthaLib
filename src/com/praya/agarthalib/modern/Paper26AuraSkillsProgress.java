package com.praya.agarthalib.modern;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import java.util.Locale;
import java.util.UUID;

/** Read-only AuraSkills progression; no economy/JobsReborn semantic substitution. */
public final class Paper26AuraSkillsProgress {
    private Paper26AuraSkillsProgress() {}

    public static Integer level(final Plugin provider, final OfflinePlayer player, final String skillName) {
        if (provider == null || !provider.isEnabled() || player == null || !player.isOnline()) return null;
        return level(provider.getClass().getClassLoader(), player.getUniqueId(), skillName);
    }

    static Integer level(final ClassLoader loader, final UUID player, final String skillName) {
        if (loader == null || player == null || skillName == null || skillName.isBlank()) return null;
        try {
            final Class<?> apiType = Class.forName("dev.aurelium.auraskills.api.AuraSkillsApi", false, loader);
            final Object api = apiType.getMethod("get").invoke(null);
            if (api == null) return null;
            final Object user = apiType.getMethod("getUser", UUID.class).invoke(api, player);
            if (user == null) return null;
            final Class<?> skillType = Class.forName("dev.aurelium.auraskills.api.skill.Skill", false, loader);
            final Class<?> skills = Class.forName("dev.aurelium.auraskills.api.skill.Skills", false, loader);
            @SuppressWarnings({"unchecked", "rawtypes"})
            final Object skill = Enum.valueOf((Class<? extends Enum>) skills.asSubclass(Enum.class),
                    skillName.toUpperCase(Locale.ROOT));
            final Object value = user.getClass().getMethod("getSkillLevel", skillType).invoke(user, skill);
            return value instanceof Number n && n.intValue() >= 0 ? n.intValue() : null;
        } catch (ReflectiveOperationException | IllegalArgumentException | LinkageError | SecurityException ex) {
            return null;
        }
    }
}
