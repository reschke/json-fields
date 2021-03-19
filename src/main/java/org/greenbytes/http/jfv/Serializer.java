package org.greenbytes.http.jfv;

import javax.json.JsonValue;

public class Serializer {

    private static String convert(String input) {
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c >= 0x0 && c < 0x20) {
                // Control characters
                if (c == 0x9 || c == 0xa || c == 0xd) {
                    result.append(' ');
                } else {
                    throw new RuntimeException("unexpected character: " + (int) c);
                }
            } else if (c >= 0x20 && c <= 0x7e) {
                // VCHAR
                result.append(c);
            } else {
                // non-ASCII
                result.append(String.format("\\u%04x", (int) c));
            }
        }

        return result.toString();
    }

    public static String serialize(JsonValue value) {
        return convert(value.toString());
    }
}
