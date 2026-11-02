package com.praya.agarthalib.modern;
import java.util.UUID;

public final class Paper26MythicLibStatsSelfTest {
    private static void check(final boolean result) {
        if (!result) throw new AssertionError("MythicLib read-only bridge check failed");
    }
    public static void main(final String[] args) {
        final ClassLoader loader = Paper26MythicLibStatsSelfTest.class.getClassLoader();
        final UUID playerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        check(Double.valueOf(13.5).equals(Paper26MythicLibStats.read(loader, playerId, "ATTACK_DAMAGE")));
        check(Paper26MythicLibStats.read(loader, playerId, "UNKNOWN_STAT") == null);
        check(Paper26MythicLibStats.read(loader, null, "ATTACK_DAMAGE") == null);
        check(Paper26MythicLibStats.read(loader, playerId, "") == null);
        check(Paper26MythicLibStats.read(null, playerId, "ATTACK_DAMAGE") == null);
        final ClassLoader absent = new ClassLoader(loader) {
            @Override
            public Class<?> loadClass(final String name) throws ClassNotFoundException {
                if (name.startsWith("io.lumine.mythic.lib.")) throw new ClassNotFoundException(name);
                return super.loadClass(name);
            }
        };
        check(Paper26MythicLibStats.read(absent, playerId, "ATTACK_DAMAGE") == null);
        System.out.println("PASS: 6 read-only optional MythicLib stats assertions");
    }
}
