package com.praya.agarthalib.modern;

/** JVM regression tests independent of the running Paper server. */
public final class Paper26HexColorsSelfTest {
    private static int checks;
    private static void check(boolean okay, String label) {
        if (!okay) throw new AssertionError(label);
        checks++;
    }
    public static void main(String[] args) {
        final String rgb = "§x§f§f§0§0§a§a";
        for (String candidate : new String[] {
                "&#FF00Aa", "<#fF00aA>", "{#ff00aa}", "#FF00AA",
                "&x&F&F&0&0&A&A", "&aLegacy", "§x§0§0§f§f§0§0",
                "#1234567", "invalid", "", "A &#FEEDBBSword"}) {
            check(java.util.Objects.equals(Paper26HexColors.translate(candidate),
                    com.lymeowz.lib.text.HexColors.translate(candidate)),
                    "old/new HEX namespaces agree: " + candidate);
        }
        check(com.lymeowz.lib.text.HexColors.translate(null) == null,
                "new HEX namespace preserves null");
        check(rgb.equals(Paper26HexColors.translate("&#FF00Aa")), "ampersand HEX");
        check(rgb.equals(Paper26HexColors.translate("<#fF00aA>")), "angle HEX");
        check(rgb.equals(Paper26HexColors.translate("{#ff00aa}")), "brace HEX");
        check(rgb.equals(Paper26HexColors.translate("#FF00AA")), "bare HEX");
        check(rgb.equals(Paper26HexColors.translate("&x&F&F&0&0&A&A")), "spaced X HEX");
        check(("Sword " + rgb + "Soul").equals(Paper26HexColors.translate(
                "Sword &#FF00AASoul")), "inline RGB before words");
        check(("§x§1§2§a§b§c§d" + "Blade").equals(
                Paper26HexColors.translate("<#12ABCD>Blade")), "mixed letter-case");
        check("&a&lHello".equals(Paper26HexColors.translate("&a&lHello")),
                "legacy formatting preserved for Bukkit translation");
        check("§aAlready".equals(Paper26HexColors.translate("§aAlready")),
                "stored legacy section formatting preserved");
        check(Paper26HexColors.translate(null) == null, "null preserved");
        check("".equals(Paper26HexColors.translate("")), "empty text preserved");
        for (String invalid : new String[] {"&#FFF", "&#GH1234", "<#FF00A>", "{#FF00AA",
                "#1234567", "foo#123456", "id/#123456", "&x&1&2&3&4&5"}) {
            check(invalid.equals(Paper26HexColors.translate(invalid)),
                    "invalid text left literal: " + invalid);
        }
        check(("§x§a§1§b§2§c§3&lY §x§0§0§f§f§0§0Z")
                        .equals(Paper26HexColors.translate("&#A1B2C3&lY <#00ff00>Z")),
                "multiple HEX runs with legacy bold preserved");
        System.out.println("PASS: " + checks + " MyItems/Paper26 HEX color cases");
    }
}
