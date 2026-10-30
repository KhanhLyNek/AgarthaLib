package core.praya.agarthalib.enums.main;
/** CI-only enum contract, not included in the plugin build. */
public enum TagsAttribute {
    HEALTH, FOLLOW_RANGE, KNOCKBACK_RESISTANCE, SPEED,
    ATTACK_DAMAGE, DEFENSE, SHIELD, ATTACK_RATE, LUCK,
    HORSE_JUMP, FLYING_SPEED, ZOMBIE_REINFORCEMENT;

    public String getValueType() { return "Double"; }
    public double getMinValue() { return -2048.0; }
    public double getMaxValue() { return 2048.0; }
}
