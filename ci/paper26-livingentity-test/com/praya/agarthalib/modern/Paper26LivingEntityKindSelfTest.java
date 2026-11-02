package com.praya.agarthalib.modern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Proxy;

/** Exercises the *actual* Paper API entity metadata and our new classifier. */
public final class Paper26LivingEntityKindSelfTest {
    private static int checks;

    private static void expect(final boolean expected, final boolean actual, final String reason) {
        if (expected != actual) {
            throw new AssertionError(reason + ": expected " + expected + ", got " + actual);
        }
        checks++;
    }

    private static Entity entity(final Class<? extends Entity> contract, final EntityType type) {
        return (Entity) Proxy.newProxyInstance(
                contract.getClassLoader(),
                new Class<?>[] {contract},
                (proxy, method, args) -> {
                    if ("getType".equals(method.getName())) {
                        return type;
                    }
                    if ("toString".equals(method.getName())) {
                        return "FakeEntity(" + type + ")";
                    }
                    return null;
                });
    }

    public static void main(final String[] args) {
        expect(true, Paper26LivingEntityKind.isLivingType(EntityType.PLAYER), "player");
        expect(true, Paper26LivingEntityKind.isLivingType(EntityType.ZOMBIE), "zombie");
        expect(true, Paper26LivingEntityKind.isLivingType(EntityType.VILLAGER), "villager");
        expect(false, Paper26LivingEntityKind.isLivingType(EntityType.ARMOR_STAND), "legacy exclusion");
        expect(false, Paper26LivingEntityKind.isLivingType(EntityType.ARROW), "projectile");
        expect(false, Paper26LivingEntityKind.isLivingType(EntityType.TEXT_DISPLAY), "text hologram");
        expect(false, Paper26LivingEntityKind.isLivingType(EntityType.ITEM_DISPLAY), "item hologram");
        expect(false, Paper26LivingEntityKind.isLivingType(EntityType.UNKNOWN), "unknown");
        expect(false, Paper26LivingEntityKind.isLivingType(null), "null type");
        expect(true, Paper26LivingEntityKind.isLivingEntity(entity(LivingEntity.class, EntityType.ZOMBIE)),
                "living entity instance");
        expect(false, Paper26LivingEntityKind.isLivingEntity(entity(LivingEntity.class, EntityType.ARMOR_STAND)),
                "armor stand entity instance");
        expect(false, Paper26LivingEntityKind.isLivingEntity(entity(Entity.class, EntityType.ARROW)),
                "nonliving entity instance");
        expect(false, Paper26LivingEntityKind.isLivingEntity(null), "null entity");
        System.out.println("PASS: " + checks + " Paper 26.3 living-entity classification checks");
    }
}
