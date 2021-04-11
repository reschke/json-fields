package org.greenbytes.http.jfv;

import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class Serializer {

    private static String convert(String input) {
        return IJsonConstraints.check(input, true);
    }

    public static String single(JsonValue value) {
        return convert(value.toString()).toString();
    }

    public static String single(String value) {
        return convert(value).toString();
    }

    public static String list(JsonValue... values) {
        if (values.length == 1) {
            return single(values[0]);
        } else {
            StringBuilder sb = new StringBuilder();
            String delim = "";
            for (JsonValue value : values) {
                sb.append(delim);
                sb.append(convert(value.toString()));
                delim = ", ";
            }
            return sb.toString();
        }
    }

    public static String list(JsonArray array) {
        if (array.size() == 1) {
            return single(array.getValuesAs(JsonValue.class).get(0));
        } else {
            StringBuilder sb = new StringBuilder();
            String delim = "";
            for (JsonValue value : array.getValuesAs(JsonValue.class)) {
                sb.append(delim);
                sb.append(convert(value.toString()));
                delim = ", ";
            }
            return sb.toString();
        }
    }

    public static JsonValue check(JsonValue value) {

        if (value instanceof JsonNumber) {
            IJsonConstraints.check((JsonNumber) value);
        } else if (value instanceof JsonString) {
            IJsonConstraints.check(((JsonString) value).getString());
        } else if (value instanceof JsonArray) {
            for (JsonValue v : (JsonArray) value) {
                check(v);
            }
        } else if (value instanceof JsonObject) {
            for (Map.Entry<String, JsonValue> entry : ((JsonObject) value).entrySet()) {
                IJsonConstraints.check(entry.getKey());
                check(entry.getValue());
            }
        }

        return value;
    }
}
