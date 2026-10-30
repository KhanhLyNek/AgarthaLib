package com.praya.agarthalib.modern;

/** Tests rejection paths without spinning up the Paper server internals. */
public final class Paper26ItemSnapshotsSelfTest {
    private static void invalid(final String s) {
        try { Paper26ItemSnapshots.restore(s); throw new AssertionError("Accepted invalid backup"); }
        catch (IllegalArgumentException expected) { }
    }
    public static void main(String[] args) {
        invalid(null);
        invalid("");
        invalid("not base64");
        invalid("%%%");
        invalid("A".repeat(3 * 1024 * 1024));
        if (Paper26ItemSnapshots.snapshot(null) != null) throw new AssertionError("null snapshot");
        if (Paper26HoverItems.component("description", null) != null) throw new AssertionError("null item");
        if (Paper26HoverItems.json("description", null) != null) throw new AssertionError("null item JSON");
        System.out.println("PASS: 8 snapshot/hover fail-closed checks; roundtrip needs Paper fixture");
    }
}
