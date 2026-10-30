package com.praya.agarthalib.modern;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * String-list codec for new PDC item tags. Does not interpret older raw NBT.
 *
 * v1 uses explicit UTF-8 byte lengths (unlike DataOutputStream.writeUTF,
 * which rejects strings above 65535 encoded bytes). It can also decode the
 * earlier preview PDC format produced by the first 26.3 adapter.
 */
public final class Paper26StringListCodec {
    private static final byte[] MAGIC = { 0x41, 0x47, 0x4c, 0x53, 0x01 };

    private Paper26StringListCodec() {
    }

    public static byte[] encode(final List<String> lines) {
        if (lines == null) {
            throw new IllegalArgumentException("list cannot be null");
        }
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (DataOutputStream output = new DataOutputStream(buffer)) {
                output.write(MAGIC);
                output.writeInt(lines.size());
                for (final String line : lines) {
                    final byte[] utf8 = (line == null ? "" : line).getBytes(StandardCharsets.UTF_8);
                    output.writeInt(utf8.length);
                    output.write(utf8);
                }
            }
            return buffer.toByteArray();
        } catch (final IOException ex) {
            throw new IllegalStateException("Unexpected in-memory encoding failure", ex);
        }
    }

    public static List<String> decode(final byte[] bytes) {
        if (bytes == null) {
            return new ArrayList<>();
        }
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes))) {
            final boolean newFormat = hasMagic(bytes);
            if (newFormat) {
                input.skipNBytes(MAGIC.length);
            }
            final int count = input.readInt();
            final int minimumBytesPerElement = newFormat ? 4 : 2;
            final int maxElements = input.available() / minimumBytesPerElement;
            if (count < 0 || count > maxElements) {
                throw new IOException("Invalid list size " + count);
            }
            final List<String> result = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                if (newFormat) {
                    final int length = input.readInt();
                    if (length < 0 || length > input.available()) {
                        throw new IOException("Invalid UTF-8 byte length");
                    }
                    final byte[] data = new byte[length];
                    input.readFully(data);
                    result.add(new String(data, StandardCharsets.UTF_8));
                } else {
                    // Preview builds used Java modified UTF-8, not standard UTF-8.
                    result.add(input.readUTF());
                }
            }
            if (input.available() != 0) {
                throw new IOException("Unexpected trailing bytes");
            }
            return result;
        } catch (final IOException | IllegalArgumentException ex) {
            throw new IllegalStateException("Malformed custom item string-list data", ex);
        }
    }

    private static boolean hasMagic(final byte[] bytes) {
        if (bytes.length < MAGIC.length + 4) {
            return false;
        }
        for (int i = 0; i < MAGIC.length; i++) {
            if (bytes[i] != MAGIC[i]) {
                return false;
            }
        }
        return true;
    }
}
