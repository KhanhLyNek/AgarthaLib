// 
// Decompiled by Procyon v0.5.36
// 

package api.praya.agarthalib.manager.plugin;

import api.praya.agarthalib.builder.support.main.SupportClass;
import api.praya.agarthalib.builder.support.main.SupportLevel;
import api.praya.agarthalib.builder.support.main.SupportTerritory;
import api.praya.agarthalib.builder.support.main.SupportGroup;
import org.bukkit.plugin.Plugin;
import com.praya.agarthalib.utility.PluginUtil;
import com.praya.agarthalib.AgarthaLib;
import api.praya.agarthalib.builder.support.SupportTowny;
import api.praya.agarthalib.builder.support.SupportLangUtils;
import api.praya.agarthalib.builder.support.SupportMythicLib;
import api.praya.agarthalib.builder.support.SupportMythicMobs;
import api.praya.agarthalib.builder.support.SupportFeatherBoard;
import api.praya.agarthalib.builder.support.SupportEssentials;
import api.praya.agarthalib.builder.support.SupportWorldGuard;
import api.praya.agarthalib.builder.support.SupportMVdWPlaceholderAPI;
import api.praya.agarthalib.builder.support.SupportPlaceholderAPI;
import api.praya.agarthalib.builder.support.SupportVault;
import com.praya.agarthalib.handler.HandlerManager;

/** Paper-only manager: obsolete typed plugin provider handles are retired. */
public class SupportManagerAPI extends HandlerManager
{
    private SupportVault supportVault;
    private SupportPlaceholderAPI supportPlaceholderAPI;
    private SupportMVdWPlaceholderAPI supportMVdWPlaceholderAPI;
    private SupportWorldGuard supportWorldGuard;
    private SupportEssentials supportEssentials;
    private SupportFeatherBoard supportFeatherBoard;
    private SupportMythicMobs supportMythicMobs;
    private SupportMythicLib supportMythicLib;
    private SupportLangUtils supportLangUtils;
    private SupportTowny supportTowny;
    
    public SupportManagerAPI(final AgarthaLib plugin) {
        super(plugin);
    }

    // Replacement provider APIs deliberately use separate contracts and return null
    // when unavailable; they must not emulate old Jobs/Heroes level/XP semantics.
    public final Integer getAuraSkillsLevel(final org.bukkit.OfflinePlayer player, final String skill) {
        return com.praya.agarthalib.modern.Paper26OptionalProviders.auraSkillLevel(player, skill);
    }
    public final Integer getMMOCoreLevel(final org.bukkit.OfflinePlayer player) {
        return com.praya.agarthalib.modern.Paper26OptionalProviders.mmoCoreLevel(player);
    }
    public final String getMMOCoreClassId(final org.bukkit.OfflinePlayer player) {
        return com.praya.agarthalib.modern.Paper26OptionalProviders.mmoCoreClassId(player);
    }
    public final Boolean isLandsClaimed(final Plugin owner, final org.bukkit.Location where) {
        return com.praya.agarthalib.modern.Paper26OptionalProviders.landsClaimed(owner, where);
    }
    public final boolean createTransientDecentHologram(final String id, final org.bukkit.Location where,
                                                      final java.util.List<String> lines) {
        return com.praya.agarthalib.modern.Paper26OptionalProviders.createTransientHologram(id, where, lines);
    }
    
    /** Optional provider; never caches a disabled plugin instance. */
    public final SupportMythicLib getSupportMythicLib() {
        final Plugin provider = PluginUtil.getPlugin("MythicLib");
        if (provider == null || !provider.isEnabled()) {
            this.supportMythicLib = null;
            return null;
        }
        if (this.supportMythicLib == null || this.supportMythicLib.getSource() != provider) {
            this.supportMythicLib = new SupportMythicLib(this.plugin, provider);
        }
        return this.supportMythicLib;
    }

    public final SupportVault getSupportVault() {
        if (this.supportVault == null) {
            final Plugin source = PluginUtil.getPlugin("Vault");
            if (source != null && source.isEnabled()) {
                this.supportVault = new SupportVault(this.plugin, source);
            }
        }
        else {
            final Plugin source = this.supportVault.getSource();
            if (!source.isEnabled()) {
                this.supportVault = null;
            }
        }
        return this.supportVault;
    }
    
    public final SupportPlaceholderAPI getSupportPlaceholderAPI() {
        if (this.supportPlaceholderAPI == null) {
            final Plugin source = PluginUtil.getPlugin("PlaceholderAPI");
            if (source != null && source.isEnabled()) {
                this.supportPlaceholderAPI = new SupportPlaceholderAPI(this.plugin, source);
            }
        }
        else {
            final Plugin source = this.supportPlaceholderAPI.getSource();
            if (!source.isEnabled()) {
                this.supportPlaceholderAPI = null;
            }
        }
        return this.supportPlaceholderAPI;
    }
    
    public final SupportMVdWPlaceholderAPI getSupportMVdWPlaceholderAPI() {
        if (this.supportMVdWPlaceholderAPI == null) {
            final Plugin source = PluginUtil.getPlugin("MVdWPlaceholderAPI");
            if (source != null && source.isEnabled()) {
                this.supportMVdWPlaceholderAPI = new SupportMVdWPlaceholderAPI(this.plugin, source);
            }
        }
        else {
            final Plugin source = this.supportMVdWPlaceholderAPI.getSource();
            if (!source.isEnabled()) {
                this.supportMVdWPlaceholderAPI = null;
            }
        }
        return this.supportMVdWPlaceholderAPI;
    }
    
    public final SupportWorldGuard getSupportWorldGuard() {
        if (this.supportWorldGuard == null) {
            final Plugin source = PluginUtil.getPlugin("WorldGuard");
            if (source != null && source.isEnabled()) {
                this.supportWorldGuard = new SupportWorldGuard(this.plugin, source);
            }
        }
        else {
            final Plugin source = this.supportWorldGuard.getSource();
            if (!source.isEnabled()) {
                this.supportWorldGuard = null;
            }
        }
        return this.supportWorldGuard;
    }
    
    public final SupportEssentials getSupportEssentials() {
        if (this.supportEssentials == null) {
            final Plugin source = PluginUtil.getPlugin("Essentials");
            if (source != null && source.isEnabled()) {
                this.supportEssentials = new SupportEssentials(this.plugin, source);
            }
        }
        else {
            final Plugin source = this.supportEssentials.getSource();
            if (!source.isEnabled()) {
                this.supportEssentials = null;
            }
        }
        return this.supportEssentials;
    }
    
    
    
    
    
    public final SupportFeatherBoard getSupportFeatherBoard() {
        if (this.supportFeatherBoard == null) {
            final Plugin source = PluginUtil.getPlugin("FeatherBoard");
            if (source != null && source.isEnabled()) {
                this.supportFeatherBoard = new SupportFeatherBoard(this.plugin, source);
            }
        }
        else {
            final Plugin source = this.supportFeatherBoard.getSource();
            if (!source.isEnabled()) {
                this.supportFeatherBoard = null;
            }
        }
        return this.supportFeatherBoard;
    }
    
    
    
    
    
    
    
    public final SupportMythicMobs getSupportMythicMobs() {
        if (this.supportMythicMobs == null) {
            final Plugin source = PluginUtil.getPlugin("MythicMobs");
            if (source != null && source.isEnabled()) {
                this.supportMythicMobs = new SupportMythicMobs(this.plugin, source);
            }
        }
        else {
            final Plugin source = this.supportMythicMobs.getSource();
            if (!source.isEnabled()) {
                this.supportMythicMobs = null;
            }
        }
        return this.supportMythicMobs;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    public final SupportLangUtils getSupportLangUtils() {
        if (this.supportLangUtils == null) {
            final Plugin source = PluginUtil.getPlugin("LangUtils");
            if (source != null && source.isEnabled()) {
                this.supportLangUtils = new SupportLangUtils(this.plugin, source);
            }
        }
        else {
            final Plugin source = this.supportLangUtils.getSource();
            if (!source.isEnabled()) {
                this.supportLangUtils = null;
            }
        }
        return this.supportLangUtils;
    }
    
    
    
    
    
    public final SupportTowny getSupportTowny() {
        if (this.supportTowny == null) {
            final Plugin source = PluginUtil.getPlugin("Towny");
            if (source != null && source.isEnabled()) {
                this.supportTowny = new SupportTowny(this.plugin, source);
            }
        }
        else {
            final Plugin source = this.supportTowny.getSource();
            if (!source.isEnabled()) {
                this.supportTowny = null;
            }
        }
        return this.supportTowny;
    }
    
    
    
    
    
    
    
    
    public final SupportGroup getSupportGroup() {
        SupportGroup.SupportGroupType[] values;
        for (int length = (values = SupportGroup.SupportGroupType.values()).length, i = 0; i < length; ++i) {
            final SupportGroup.SupportGroupType groupType = values[i];
            final SupportGroup supportGroup = this.getSupportGroup(groupType);
            if (supportGroup != null) {
                return supportGroup;
            }
        }
        return null;
    }
    
    public final SupportGroup getSupportGroup(final SupportGroup.SupportGroupType groupType) {
        switch (groupType) {
            case KINGDOMS: {
                return null;
            }
            case GANGS: {
                return null;
            }
            case TOWNY: {
                return this.getSupportTowny();
            }
            case SIMPLE_CLANS: {
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    public final SupportTerritory getSupportTerritory() {
        SupportTerritory.SupportTerritoryType[] values;
        for (int length = (values = SupportTerritory.SupportTerritoryType.values()).length, i = 0; i < length; ++i) {
            final SupportTerritory.SupportTerritoryType groupType = values[i];
            final SupportTerritory supportGroup = this.getSupportTerritory(groupType);
            if (supportGroup != null) {
                return supportGroup;
            }
        }
        return null;
    }
    
    public final SupportTerritory getSupportTerritory(final SupportTerritory.SupportTerritoryType groupType) {
        switch (groupType) {
            default: {
                return null;
            }
        }
    }
    
    public final SupportLevel getSupportLevel() {
        SupportLevel.SupportLevelType[] values;
        for (int length = (values = SupportLevel.SupportLevelType.values()).length, i = 0; i < length; ++i) {
            final SupportLevel.SupportLevelType levelType = values[i];
            final SupportLevel supportLevel = this.getSupportLevel(levelType);
            if (supportLevel != null) {
                return supportLevel;
            }
        }
        return null;
    }
    
    public final SupportLevel getSupportLevel(final SupportLevel.SupportLevelType levelType) {
        switch (levelType) {
            case BATTLE_LEVELS: {
                return null;
            }
            case SKILL_API: {
                return null;
            }
            case SKILLS_PRO: {
                return null;
            }
            case HEROES: {
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    public final SupportClass getSupportClass() {
        SupportClass.SupportClassType[] values;
        for (int length = (values = SupportClass.SupportClassType.values()).length, i = 0; i < length; ++i) {
            final SupportClass.SupportClassType classType = values[i];
            final SupportClass supportClass = this.getSupportClass(classType);
            if (supportClass != null) {
                return supportClass;
            }
        }
        return null;
    }
    
    public final SupportClass getSupportClass(final SupportClass.SupportClassType classType) {
        switch (classType) {
            case SKILL_API: {
                return null;
            }
            case SKILLS_PRO: {
                return null;
            }
            case HEROES: {
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    public final boolean isSupportVault() {
        return this.getSupportVault() != null;
    }
    
    public final boolean isSupportPlaceholderAPI() {
        return this.getSupportPlaceholderAPI() != null;
    }
    
    public final boolean isSupportMVdWPlaceholder() {
        return this.getSupportMVdWPlaceholderAPI() != null;
    }
    
    public final boolean isSupportWorldGuard() {
        return this.getSupportWorldGuard() != null;
    }
    
    public final boolean isSupportEssentials() {
        return this.getSupportEssentials() != null;
    }
    
    public final boolean isSupportCitizens() {
        return false;
    }
    
    public final boolean isSupportHolographicDisplay() {
        return false;
    }
    
    public final boolean isSupportFeatherBoard() {
        return this.getSupportFeatherBoard() != null;
    }
    
    public final boolean isSupportSkillAPI() {
        return false;
    }
    
    public final boolean isSupportSkillsPro() {
        return false;
    }
    
    public final boolean isSupportHeroes() {
        return false;
    }
    
    public final boolean isSupportMythicMobs() {
        return this.getSupportMythicMobs() != null;
    }
    
    public final boolean isSupportMcMMO() {
        return false;
    }
    
    public final boolean isSupportJobsReborn() {
        return false;
    }
    
    public final boolean isSupportMyItems() {
        return false;
    }
    
    public final boolean isSupportCombatStamina() {
        return false;
    }
    
    public final boolean isSupportLifeEssence() {
        return false;
    }
    
    public final boolean isSupportBattleLevels() {
        return false;
    }
    
    public final boolean isSupportLangUtils() {
        return this.getSupportLangUtils() != null;
    }
    
    public final boolean isSupportKingdoms() {
        return false;
    }
    
    public final boolean isSupportGangs() {
        return false;
    }
    
    public final boolean isSupportTowny() {
        return this.getSupportTowny() != null;
    }
    
    public final boolean isSupportSimpleClans() {
        return false;
    }
    
    
    
    
    
    public final boolean isSupportGroups() {
        return this.getSupportGroup() != null;
    }
    
    public final boolean isSupportLevel() {
        return this.getSupportLevel() != null;
    }
    
    public final boolean isSupportClass() {
        return this.getSupportClass() != null;
    }
    
    @Deprecated
    public final boolean isHookVault() {
        return this.getSupportVault() != null;
    }
    
    @Deprecated
    public final boolean isHookPlaceholderAPI() {
        return this.getSupportPlaceholderAPI() != null;
    }
    
    @Deprecated
    public final boolean isHookMVDWPlaceholder() {
        return this.getSupportMVdWPlaceholderAPI() != null;
    }
    
    @Deprecated
    public final boolean isHookWorldGuard() {
        return this.getSupportWorldGuard() != null;
    }
    
    @Deprecated
    public final boolean isHookHolographicDisplay() {
        return false;
    }
    
    @Deprecated
    public final boolean isHookSkillAPI() {
        return false;
    }
    
    @Deprecated
    public final boolean isHookMythicMobs() {
        return this.getSupportMythicMobs() != null;
    }
    
    @Deprecated
    public final boolean isHookMcMMO() {
        return false;
    }
    
    @Deprecated
    public final boolean isHookJobsReborn() {
        return false;
    }
    
    @Deprecated
    public final boolean isHookKingdoms() {
        return false;
    }
    
    
    
    
    
    @Deprecated
    public final boolean isHookGroups() {
        return this.getSupportGroup() != null;
    }
}
