package com.praya.agarthalib.modern;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Executable, dependency-free checks for the real Paper26StringListCodec. */
public final class Paper26StringListCodecSelfTest {
    private static int assertions;

    private static void roundTrip(final List<String> strings) {
        final List<String> actual = Paper26StringListCodec.decode(Paper26StringListCodec.encode(strings));
        if (!strings.equals(actual)) {
            throw new AssertionError("UTF-8 item-data list changed across round trip");
        }
        assertions++;
    }

    private static void expectInvalid(final byte[] encoded) {
        try {
            Paper26StringListCodec.decode(encoded);
            throw new AssertionError("Corrupted list was accepted");
        } catch (final IllegalStateException expected) {
            assertions++;
        }
    }

    public static void main(final String[] args) throws Exception {
        roundTrip(List.of());
        roundTrip(List.of(""));
        roundTrip(List.of("a", "b", "a", ""));
        roundTrip(List.of("comma, newline\n and \r quotes \"", "a=b;c:d"));
        roundTrip(List.of("Tiếng Việt", "한국어", "漢字", "☃", "😊", "a\u0000b"));
        // DataOutputStream.writeUTF() cannot encode this, hence the new codec.
        roundTrip(List.of("🌼".repeat(30_000)));
        roundTrip(new ArrayList<>(java.util.Collections.nCopies(300, "nested_socket=gem")));

        final ByteArrayOutputStream old = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(old)) {
            output.writeInt(3);
            output.writeUTF("old");
            output.writeUTF("Tiếng Việt");
            output.writeUTF("a\u0000b");
        }
        if (!Paper26StringListCodec.decode(old.toByteArray())
                .equals(List.of("old", "Tiếng Việt", "a\u0000b"))) {
            throw new AssertionError("Preview-format backward read failed");
        }
        assertions++;

        expectInvalid(new byte[0]);
        expectInvalid(new byte[] {0, 0, 0, -1});
        expectInvalid(new byte[] {0, 0, 0, 1, 0, 8, 65});
        final byte[] valid = Paper26StringListCodec.encode(List.of("example"));
        expectInvalid(Arrays.copyOf(valid, valid.length - 1));
        final byte[] trailing = Arrays.copyOf(valid, valid.length + 1);
        expectInvalid(trailing);

        // A valid length prefix can still contain invalid UTF-8. Never
        // silently convert saved item data into Unicode replacement chars.
        final byte[] malformedLead = Paper26StringListCodec.encode(List.of("x"));
        malformedLead[malformedLead.length - 1] = (byte) 0xC3;
        expectInvalid(malformedLead);
        final byte[] unexpectedContinuation = Paper26StringListCodec.encode(List.of("x"));
        unexpectedContinuation[unexpectedContinuation.length - 1] = (byte) 0x80;
        expectInvalid(unexpectedContinuation);
        final byte[] overlongEncoding = Paper26StringListCodec.encode(List.of("xx"));
        overlongEncoding[overlongEncoding.length - 2] = (byte) 0xC0;
        overlongEncoding[overlongEncoding.length - 1] = (byte) 0xAF;
        expectInvalid(overlongEncoding);

        if (!Paper26StringListCodec.decode(null).isEmpty()) {
            throw new AssertionError("Null custom list should read as empty");
        }
        assertions++;
        System.out.println("PASS: " + assertions + " Paper26StringListCodec assertions");
    }
}
