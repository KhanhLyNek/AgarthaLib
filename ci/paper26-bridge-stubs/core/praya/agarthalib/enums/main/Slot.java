package core.praya.agarthalib.enums.main;
/** CI-only enum matching the legacy enum constants. */
public enum Slot {
    MAINHAND, OFFHAND, HELMET, CHESTPLATE, LEGGINGS, BOOTS;
    public static Slot get(final int id) {
        return id < 0 || id >= values().length ? null : values()[id];
    }
}
