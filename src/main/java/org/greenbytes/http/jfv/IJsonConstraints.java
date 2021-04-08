package org.greenbytes.http.jfv;

import java.math.BigDecimal;

import javax.json.JsonException;
import javax.json.JsonNumber;

/**
 * Implements checker methods for the contraints defined in IETF RFC 7493 ("The I-JSON Message Format")
 * 
 * @see <a href="https://greenbytes.de/tech/webdav/rfc7493.html">RFC 7493: The I-JSON Message Format</a>
 */
public class IJsonConstraints {

    /**
     * Check string for invalid characters, throws in case of error.
     * 
     * @see <a href="https://greenbytes.de/tech/webdav/rfc7493.html#rfc.section.2.1">RFC 7493, Section 2.1</a>
     */
    public static String check(String value) throws IJsonConstraintViolationException {
        boolean expectLow = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isSurrogate(c)) {
                if (expectLow) {
                    if (!Character.isLowSurrogate(c)) {
                        throw new IJsonConstraintViolationException("Invalid surrogate at position " + i + " of '" + value + "'");
                    }
                    expectLow = false;
                } else {
                    if (!Character.isHighSurrogate(c)) {
                        throw new IJsonConstraintViolationException("Invalid surrogate at position " + i + " of '" + value + "'");
                    }
                    expectLow = true;
                }
            }
        }

        if (expectLow) {
            throw new IJsonConstraintViolationException("Invalid surrogate at end of '" + value + "'");
        }

        return value;
    }

    /**
     * Check number for compatibility with IEEE 754-2008 binary64.
     * 
     * @see <a href="https://greenbytes.de/tech/webdav/rfc7493.html#rfc.section.2.2">RFC 7493, Section 2.2</a>
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
