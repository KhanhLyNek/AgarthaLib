package com.praya.agarthalib.modern;

/**
 * Tests only deterministic stage mapping. Actual crack overlay / clearing
 * semantics must be checked on Paper 26.3 with a connected client.
 */
public final class Paper26BlockDamageSelfTest {
    public static void main(final String[] args) {
        final int[] stages = {-3, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 99};
        final float[] expected = {0.0f, 0.0f, 0.1f, 0.2f, 0.3f, 0.4f,
                0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 0.0f, 0.0f};
        for (int i = 0; i < stages.length; i++) {
            final float actual = Paper26BlockDamage.progressForLegacyStage(stages[i]);
            if (Math.abs(actual - expected[i]) > 0.00001f) {
                throw new AssertionError("Legacy stage " + stages[i]
                        + ": expected " + expected[i] + ", got " + actual);
            }
        }
        System.out.println("PASS: " + stages.length + " legacy block-damage stage mapping cases");
    }
}
