// 
// Decompiled by Procyon v0.5.36
// 

package api.praya.agarthalib.builder.support;

import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.praya.agarthalib.AgarthaLib;
import api.praya.agarthalib.builder.support.main.Support;

public class SupportLangUtils extends Support
{
    // The external LangUtils plugin is optional. Cache only the original
    // LanguageHelper method signatures and invoke its real implementation.
    private final ConcurrentHashMap<String, Method> methods = new ConcurrentHashMap<>();
    private final Set<String> missingMethods = ConcurrentHashMap.newKeySet();
    private volatile Class<?> languageHelper;

    private String resolve(final String methodName, final Class<?>[] signature, final Object... args) {
        if (source == null || !source.isEnabled()) {
            return null;
        }
        final String key = methodName + Arrays.toString(signature);
        if (missingMethods.contains(key)) {
            return null;
        }
        try {
            Method method = methods.get(key);
            if (method == null) {
                synchronized (methods) {
                    method = methods.get(key);
                    if (method == null) {
                        Class<?> api = languageHelper;
                        if (api == null) {
                            api = Class.forName("com.meowj.langutils.lang.LanguageHelper",
                                    false, source.getClass().getClassLoader());
                            languageHelper = api;
                        }
                        method = api.getMethod(methodName, signature);
                        methods.put(key, method);
                    }
                }
            }
            final Object value = method.invoke(null, args);
            return value instanceof String ? (String) value : null;
        } catch (final ClassNotFoundException | NoSuchMethodException | LinkageError ex) {
            if (missingMethods.add(key) && plugin != null) {
                plugin.getLogger().warning("LangUtils method missing: " + key);
            }
            return null;
        } catch (final InvocationTargetException | IllegalAccessException ex) {
            if (plugin != null) {
                plugin.getLogger().fine("LangUtils invocation error in " + key + ": "
                        + ex.getClass().getSimpleName());
            }
            return null;
        }
    }

    public SupportLangUtils(final AgarthaLib plugin, final Plugin source) {
        super(plugin, source);
    }
    
    public String getItemDisplayName(final ItemStack item, final String locale) {
        return resolve("getItemDisplayName", new Class<?>[]{ItemStack.class, String.class}, item, locale);
    }
    
    public String getItemDisplayName(final ItemStack item, final Player player) {
        return resolve("getItemDisplayName", new Class<?>[]{ItemStack.class, Player.class}, item, player);
    }
    
    public String getItemName(final ItemStack item, final String locale) {
        return resolve("getItemName", new Class<?>[]{ItemStack.class, String.class}, item, locale);
    }
    
    public String getItemName(final ItemStack item, final Player player) {
        return resolve("getItemName", new Class<?>[]{ItemStack.class, Player.class}, item, player);
    }
    
    public String getItemUnlocalizedName(final ItemStack item) {
        return resolve("getItemUnlocalizedName", new Class<?>[]{ItemStack.class}, item);
    }
    
    public String getEntityUnlocalizedName(final Entity entity) {
        return resolve("getEntityUnlocalizedName", new Class<?>[]{Entity.class}, entity);
    }
    
    public String getEntityUnlocalizedName(final EntityType entityType) {
        return resolve("getEntityUnlocalizedName", new Class<?>[]{EntityType.class}, entityType);
    }
    
    public String getEntityDisplayName(final Entity entity, final String locale) {
        return resolve("getEntityDisplayName", new Class<?>[]{Entity.class, String.class}, entity, locale);
    }
    
    public String getEntityDisplayName(final Entity entity, final Player player) {
        return resolve("getEntityDisplayName", new Class<?>[]{Entity.class, Player.class}, entity, player);
    }
    
    public String getEntityName(final Entity entity, final String locale) {
        return resolve("getEntityName", new Class<?>[]{Entity.class, String.class}, entity, locale);
    }
    
    public String getEntityName(final Entity entity, final Player player) {
        return resolve("getEntityName", new Class<?>[]{Entity.class, Player.class}, entity, player);
    }
    
    public String getEntityName(final EntityType entityType, final String locale) {
        return resolve("getEntityName", new Class<?>[]{EntityType.class, String.class}, entityType, locale);
    }
    
    public String getEntityName(final EntityType entityType, final Player player) {
        return resolve("getEntityName", new Class<?>[]{EntityType.class, Player.class}, entityType, player);
    }
    
    public String getEnchantmentLevelUnlocalizedName(final int level) {
        return resolve("getEnchantmentLevelUnlocalizedName", new Class<?>[]{int.class}, level);
    }
    
    public String getEnchantmentLevelName(final int level, final Player player) {
        return resolve("getEnchantmentLevelName", new Class<?>[]{int.class, Player.class}, level, player);
    }
    
    public String getEnchantmentLevelName(final int level, final String locale) {
        return resolve("getEnchantmentLevelName", new Class<?>[]{int.class, String.class}, level, locale);
    }
    
    public String getEnchantmentUnlocalizedName(final Enchantment enchantment) {
        return resolve("getEnchantmentUnlocalizedName", new Class<?>[]{Enchantment.class}, enchantment);
    }
    
    public String getEnchantmentName(final Enchantment enchantment, final Player player) {
        return resolve("getEnchantmentName", new Class<?>[]{Enchantment.class, Player.class}, enchantment, player);
    }
    
    public String getEnchantmentName(final Enchantment enchantment, final String locale) {
        return resolve("getEnchantmentName", new Class<?>[]{Enchantment.class, String.class}, enchantment, locale);
    }
    
    public String getEnchantmentDisplayName(final Enchantment enchantment, final int level, final Player player) {
        return resolve("getEnchantmentDisplayName", new Class<?>[]{Enchantment.class, int.class, Player.class}, enchantment, level, player);
    }
    
    public String getEnchantmentDisplayName(final Enchantment enchantment, final int level, final String locale) {
        return resolve("getEnchantmentDisplayName", new Class<?>[]{Enchantment.class, int.class, String.class}, enchantment, level, locale);
    }
    
    public String getEnchantmentDisplayName(final Map.Entry<Enchantment, Integer> entry, final String locale) {
        return resolve("getEnchantmentDisplayName", new Class<?>[]{Map.Entry.class, String.class}, entry, locale);
    }
    
    public String getEnchantmentDisplayName(final Map.Entry<Enchantment, Integer> entry, final Player player) {
        return resolve("getEnchantmentDisplayName", new Class<?>[]{Map.Entry.class, Player.class}, entry, player);
    }
    
    public String translateToLocal(final String unlocalizedName, final String locale) {
        return resolve("translateToLocal", new Class<?>[]{String.class, String.class}, unlocalizedName, locale);
    }
}
