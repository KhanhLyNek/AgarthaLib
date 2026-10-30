package com.praya.agarthalib.modern;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

public final class Paper26ModernProvidersSelfTest {
    private static int count;
    private static void verify(boolean result, String label) {
        if (!result) throw new AssertionError(label);
        count++;
    }
    private static Plugin provider() {
        return (Plugin) Proxy.newProxyInstance(Plugin.class.getClassLoader(),
                new Class<?>[]{Plugin.class}, (proxy, method, args) -> {
                    if ("isEnabled".equals(method.getName())) return true;
                    if ("toString".equals(method.getName())) return "TestPlugin";
                    return null;
                });
    }
    public static void main(String[] args) {
        final ClassLoader loader = Paper26ModernProvidersSelfTest.class.getClassLoader();
        final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        verify(Integer.valueOf(9).equals(Paper26AuraSkillsProgress.level(loader,id,"farming")), "AuraSkills default skill");
        verify(Paper26AuraSkillsProgress.level(loader,id,"not_a_skill") == null, "unknown AuraSkills ID");
        verify(Paper26AuraSkillsProgress.level(loader,null,"farming") == null, "null AuraSkills player");
        verify(Integer.valueOf(17).equals(Paper26MMOCoreProgress.level(loader,id)), "MMOCore level");
        verify("mage".equals(Paper26MMOCoreProgress.classId(loader,id)), "MMOCore class");
        verify(Paper26MMOCoreProgress.level(loader,null) == null, "MMOCore null");
        verify(Boolean.TRUE.equals(Paper26LandsClaims.isClaimed(loader,provider(),new Location(null,1,64,0))), "Lands claim");
        verify(Boolean.FALSE.equals(Paper26LandsClaims.isClaimed(loader,provider(),new Location(null,0,64,0))), "Lands wilderness");
        verify(Paper26LandsClaims.isClaimed(loader,null,new Location(null,1,64,0)) == null, "Lands unavailable");
        verify(Paper26DecentHolograms.createTransient(loader,"myitems_fixture_01",
                new Location(null,0,64,0),List.of("Line 1","Line 2")), "DecentHolograms create");
        verify(!Paper26DecentHolograms.createTransient(loader,"myitems_fixture_01",
                new Location(null,0,64,0),List.of("Modified")), "Hologram ID collision");
        verify(!Paper26DecentHolograms.createTransient(loader,"bad name",
                new Location(null,0,64,0),List.of("Text")), "Hologram ID validation");
        verify(!Paper26DecentHolograms.createTransient(loader,"ok",
                new Location(null,0,64,0),List.of()), "Empty hologram");
        final ClassLoader absent = new ClassLoader(loader) {
            @Override public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (name.startsWith("dev.aurelium.auraskills.") || name.startsWith("net.Indyuce.mmocore.")
                    || name.startsWith("me.angeschossen.lands.") || name.startsWith("eu.decentsoftware.holograms."))
                    throw new ClassNotFoundException(name);
                return super.loadClass(name);
            }
        };
        verify(Paper26AuraSkillsProgress.level(absent,id,"farming") == null, "Missing AuraSkills");
        verify(Paper26MMOCoreProgress.level(absent,id) == null, "Missing MMOCore");
        verify(Paper26LandsClaims.isClaimed(absent,provider(),new Location(null,1,64,0)) == null, "Missing Lands");
        verify(!Paper26DecentHolograms.createTransient(absent,"different",
                new Location(null,1,64,0),List.of("x")), "Missing DecentHolograms");
        System.out.println("PASS: " + count + " modern provider adapter checks");
    }
}
