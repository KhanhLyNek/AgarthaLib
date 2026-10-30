package com.praya.agarthalib.modern;
/** Comparison with legacy 1.15 packet rounding/clamping behavior. */
public final class Paper26ServerTPSSelfTest {
    private static void check(double input, double expected) {
        double actual = Paper26ServerTPS.normalize(input);
        if (actual != expected)
            throw new AssertionError(input + " -> " + actual + " (expected " + expected + ")");
    }
    public static void main(String[] args) {
        check(20.57,20.0);
        check(19.987,19.99);
        check(0.001,0.0);
        check(-5.0,0.0);
        check(Double.NaN,20.0);
        check(Double.POSITIVE_INFINITY,20.0);
        check(Double.NEGATIVE_INFINITY,0.0);
        System.out.println("PASS: 7 legacy server TPS normalization checks");
    }
}
