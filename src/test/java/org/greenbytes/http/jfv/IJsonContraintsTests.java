package org.greenbytes.http.jfv;

import static org.junit.Assert.fail;

import java.math.BigDecimal;

import javax.json.Json;

import org.greenbytes.http.jfv.IJsonConstraints.IJsonConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;

public class IJsonContraintsTests {

    @Test
    public void nonDoubles() {
        double tests[] = new double[] { Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };

        for (double test : tests) {
            try {
                IJsonConstraints.check(Json.createValue(test));
                fail("invalid number");
            } catch (IJsonConstraintViolationException expected) {
            } catch (NumberFormatException expected) {
            }
        }
    }

    @Test
    public void nonBigIntegers() {
        BigDecimal tests[] = new BigDecimal[] { new BigDecimal("1e400"), new BigDecimal("3.141592653589793238462643383279") };

        for (BigDecimal test : tests) {
            try {
                IJsonConstraints.check(Json.createValue(test));
                fail("should fail");
            } catch (IJsonConstraintViolationException expected) {
            }
        }
    }

    @Test
    public void doubles() {
        double tests[] = new double[] { 0, 1, -1, 3.1415, Double.MIN_VALUE, Double.MAX_VALUE };

        for (double test : tests) {
            IJsonConstraints.check(Json.createValue(test));
        }
    }

    @Test
    public void brokenStrings() {
        String tests[] = new String[] { "foo\ud800", "\ud800foo", "bar\ud800foo", "\ud800\ud800", "\udead\ud800" };
        for (String test : tests) {
            try {
                IJsonConstraints.check(test);
                Assert.fail("exception expected");
            } catch (IJsonConstraintViolationException expected) {
            }
        }
    }

    @Test
    public void brokenStringsEscaped() {
        String tests[] = new String[] { "foo\\ud800", "\\ud800foo", "bar\\ud800foo", "\\ud800\\ud800", "\\udead\\ud800",
                "incomplete\\" };
        for (String test : tests) {
            try {
                IJsonConstraints.check(test);
                Assert.fail("exception expected for: " + test);
            } catch (IJsonConstraintViolationException expected) {
            }
        }
    }
}
