package com.praya.agarthalib.modern;

import api.praya.agarthalib.builder.support.SupportLangUtils;
import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Proxy;

/** Test real SupportLangUtils, with the external API fake kept exclusively in CI. */
public final class Paper26LangUtilsSelfTest {
    private static int assertions;

    private static void check(final boolean condition, final String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        assertions++;
    }

    public static void main(final String[] args) {
        final boolean[] enabled = {true};
        final Plugin pluginSource = (Plugin) Proxy.newProxyInstance(
                Plugin.class.getClassLoader(), new Class<?>[]{Plugin.class},
                (proxy, method, arguments) -> {
                    if ("isEnabled".equals(method.getName())) {
                        return enabled[0];
                    }
                    throw new AssertionError("Unexpected Plugin method: " + method.getName());
                });
        final SupportLangUtils hook = new SupportLangUtils(null, pluginSource);

        check("display:vi_vn".equals(hook.getItemDisplayName(null, "vi_vn")),
                "Display-name locale forwarded unchanged");
        check("item:offline".equals(hook.getItemName(null, (org.bukkit.entity.Player) null)),
                "Player overload retained");
        check("ZOMBIE:en_us".equals(hook.getEntityName(EntityType.ZOMBIE, "en_us")),
                "EntityType overload retained");
        check("7:vi_vn".equals(hook.getEnchantmentLevelName(7, "vi_vn")),
                "Numeric enchantment level retained");
        check("4:ench:en_us".equals(hook.getEnchantmentDisplayName(null, 4, "en_us")),
                "Enchantment and level overload retained");
        check("item.minecraft.stone:vi_vn".equals(
                hook.translateToLocal("item.minecraft.stone", "vi_vn")),
                "Translation key retained");

        // The CI-only fake implements only some LangUtils methods. Missing
        // overloads must not disable unrelated, still-working translations.
        check(hook.getItemUnlocalizedName(null) == null, "Unknown overload returns null");
        check("display:vi_vn".equals(hook.getItemDisplayName(null, "vi_vn")),
                "Missing overload does not disable working overload");

        final int beforeDisabled = LanguageHelper.calls;
        enabled[0] = false;
        check(hook.getItemDisplayName(null, "vi_vn") == null,
                "Disabled optional plugin returns no fabricated translation");
        check(LanguageHelper.calls == beforeDisabled, "No call to disabled plugin");

        enabled[0] = true;
        check("display:vi_vn".equals(hook.getItemDisplayName(null, "vi_vn")),
                "Plugin re-enable restores translation");

        final ClassLoader withoutLangUtils = new ClassLoader(Plugin.class.getClassLoader()) {
            @Override
            protected Class<?> loadClass(final String name, final boolean resolve)
                    throws ClassNotFoundException {
                if ("com.meowj.langutils.lang.LanguageHelper".equals(name)) {
                    throw new ClassNotFoundException(name);
                }
                return super.loadClass(name, resolve);
            }
        };
        final Plugin missing = (Plugin) Proxy.newProxyInstance(
                withoutLangUtils, new Class<?>[]{Plugin.class},
                (proxy, method, arguments) -> {
                    if ("isEnabled".equals(method.getName())) {
                        return true;
                    }
                    throw new AssertionError("Unexpected optional Plugin method");
                });
        final SupportLangUtils missingHook = new SupportLangUtils(null, missing);
        check(missingHook.getItemDisplayName(null, "vi_vn") == null,
                "Missing external LanguageHelper must not fake translations");

        System.out.println("PASS: " + assertions + " LangUtils language-API forwarding assertions");
    }
}
