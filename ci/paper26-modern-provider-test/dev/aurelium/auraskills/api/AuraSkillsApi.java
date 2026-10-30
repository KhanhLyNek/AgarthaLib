package dev.aurelium.auraskills.api;
import java.util.UUID;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
/** CI-only public API shape, not packaged. */
public final class AuraSkillsApi {
    public static AuraSkillsApi get() { return new AuraSkillsApi(); }
    public SkillsUser getUser(final UUID uuid) { return uuid == null ? null : new SkillsUser(); }
    public static class SkillsUser {
        public int getSkillLevel(final Skill skill) { return skill == Skills.FARMING ? 9 : 5; }
    }
}
