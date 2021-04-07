package org.greenbytes.http.jfv;

import java.math.BigDecimal;

import javax.json.JsonException;
import javax.json.JsonNumber;

public class IJsonConstraints {

    public static JsonNumber check(JsonNumber number) {
        double d = number.doubleValue();
        if (!Double.isFinite(d)) {
            throw new IJsonConstraintViolationException("Not a number: " + number);
        } else if (!number.bigDecimalValue().stripTrailingZeros().equals(BigDecimal.valueOf(d).stripTrailingZeros())) {
            throw new IJsonConstraintViolationException("Number exceeds size/precision: " + number);
        }
        return number;
    }

    public static String check(String value) {
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

    public static class IJsonConstraintViolationException extends JsonException {

        private static final long serialVersionUID = -2157406642651467141L;

        public IJsonConstraintViolationException(String message) {
            super(message);
        }
    }
}
