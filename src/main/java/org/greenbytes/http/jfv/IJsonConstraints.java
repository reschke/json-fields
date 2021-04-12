package org.greenbytes.http.jfv;

import java.math.BigDecimal;

import javax.json.JsonException;
import javax.json.JsonNumber;

/**
 * Implements checker methods for the constraints defined in IETF RFC 7493 ("The
 * I-JSON Message Format")
 * 
 * @see <a href="https://greenbytes.de/tech/webdav/rfc7493.html">RFC 7493: The
 *      I-JSON Message Format</a>
 */
public class IJsonConstraints {

    /**
     * Check string for invalid characters, throws in case of error.
     * 
     * @param value
     *            string to check
     * @return checked string value
     * 
     * @see <a href=
     *      "https://greenbytes.de/tech/webdav/rfc7493.html#rfc.section.2.1">RFC
     *      7493, Section 2.1</a>
     */
    public static String check(String value) throws IJsonConstraintViolationException {
        boolean expectLowSurrogate = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isSurrogate(c)) {
                if (expectLowSurrogate) {
                    if (!Character.isLowSurrogate(c)) {
                        throw new IJsonConstraintViolationException("Invalid surrogate at position " + i + " of '" + value + "'");
                    }
                    expectLowSurrogate = false;
                    checkForNonCharacter(Character.codePointAt(value.toCharArray(), i - 1), i - 1);
                } else {
                    if (!Character.isHighSurrogate(c)) {
                        throw new IJsonConstraintViolationException("Invalid surrogate at position " + i + " of '" + value + "'");
                    }
                    expectLowSurrogate = true;
                }
            } else {
                checkForNonCharacter(c, i);
            }
        }

        if (expectLowSurrogate) {
            throw new IJsonConstraintViolationException("Invalid surrogate at end of '" + value + "'");
        }

        return value;
    }

    private static void checkForNonCharacter(int c, int pos) {
        int l = c & 0xffff;
        if ((c >= 0xfdd0 && c <= 0xfdef) || l == 0xfffe || l == 0xffff) {
            throw new IJsonConstraintViolationException(String.format("Noncharacter \\u%04x at position %d", c, pos));
        }
        System.err.println(String.format("Noncharacter \\u%04x at position %d", c, pos));
    }

    /**
     * Check number for compatibility with IEEE 754-2008 binary64.
     * 
     * @param number
     *            number to check
     * @return checked number
     * 
     * @see <a href=
     *      "https://greenbytes.de/tech/webdav/rfc7493.html#rfc.section.2.2">RFC
     *      7493, Section 2.2</a>
     */
    public static JsonNumber check(JsonNumber number) {
        double d = number.doubleValue();
        if (!Double.isFinite(d)) {
            throw new IJsonConstraintViolationException("Not a number: " + number);
        } else if (!number.bigDecimalValue().stripTrailingZeros().equals(BigDecimal.valueOf(d).stripTrailingZeros())) {
            throw new IJsonConstraintViolationException("Number exceeds size/precision: " + number);
        }
        return number;
    }

    public static class IJsonConstraintViolationException extends JsonException {

        private static final long serialVersionUID = -2157406642651467141L;

        public IJsonConstraintViolationException(String message) {
            super(message);
        }
    }
}
