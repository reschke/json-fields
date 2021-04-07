package org.greenbytes.http.jfv;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class Parser {

    private final JsonProvider jp;
    private final boolean strict;

    private static final Parser INSTANCE = new Parser();

    private Parser() {
        this.jp = JsonProvider.provider();
        this.strict = true;
    }

    public Parser(JsonProvider provider, boolean strict) {
        this.jp = provider;
        this.strict = strict;
    }

    public static Parser getInstance() {
        return INSTANCE;
    }

    public JsonArray parse(String... input) {
        JsonArrayBuilder ab = jp.createArrayBuilder();
        for (String s : input) {
            readIntoArray(ab, s);
        }
        return ab.build();
    }

    public JsonArray parse(String input) {
        JsonArrayBuilder ab = jp.createArrayBuilder();
        readIntoArray(ab, input);
        return ab.build();
    }

    private void readIntoArray(JsonArrayBuilder ab, String input) {
        try (JsonParser parser = jp.createParser(new StringReader(input))) {
            while (parser.hasNext()) {
                ab.add(readItem(parser, parser.next()));
            }
        }
    }

    private JsonObject readObject(JsonParser parser) {
        Map<String, Object> ret = new HashMap<>();
        boolean done = false;
        while (parser.hasNext() && !done) {
            Event event = parser.next();
            switch (event) {
                case KEY_NAME:
                    String name = parser.getString();
                    if (this.strict && ret.containsKey(check(name))) {
                        throw new JsonException("duplicate key: '" + name + "'");
                    }
                    JsonValue value = readItem(parser, parser.next());
                    ret.put(name, value);
                    break;
                case END_OBJECT:
                    done = true;
                    break;
                default:
                    throw new JsonException("Unexpected parse event: " + event);
            }
        }
        return jp.createObjectBuilder(ret).build();
    }

    private JsonArray readArray(JsonParser parser) {
        JsonArrayBuilder ab = jp.createArrayBuilder();
        boolean done = false;
        while (parser.hasNext() && !done) {
            Event event = parser.next();
            switch (event) {
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_NUMBER:
                case VALUE_STRING:
                case START_OBJECT:
                case START_ARRAY:
                    ab.add(readItem(parser, event));
                    break;
                case END_ARRAY:
                    done = true;
                    break;
                default:
                    throw new JsonException("Unexpected parse event: " + event);
            }
        }
        return ab.build();
    }

    private JsonValue readItem(JsonParser parser, Event event) {
        switch (event) {
            case VALUE_TRUE:
            case VALUE_FALSE:
            case VALUE_NULL:
            case VALUE_NUMBER:
            case VALUE_STRING:
                return check(parser.getValue());
            case START_OBJECT:
                return readObject(parser);
            case START_ARRAY:
                return readArray(parser);
            default:
                throw new JsonException("Unexpected parse event: " + event);
        }
    }

    private JsonValue check(JsonValue value) {
        if (this.strict) {
            if (value instanceof JsonNumber) {
                check((JsonNumber) value);
            } else if (value instanceof JsonString) {
                check(((JsonString) value).getString());
            }
        }

        return value;
    }

    private static String check(String value) {
        boolean expectLow = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isSurrogate(c)) {
                if (expectLow) {
                    if (!Character.isLowSurrogate(c)) {
                        throw new JsonException("Invalid surrogate at position " + i + " of '" + value + "'");
                    }
                    expectLow = false;
                } else {
                    if (!Character.isHighSurrogate(c)) {
                        throw new JsonException("Invalid surrogate at position " + i + " of '" + value + "'");
                    }
                    expectLow = true;
                }
            }
        }

        if (expectLow) {
            throw new JsonException("Invalid surrogate at end of '" + value + "'");
        }

        return value;
    }

    private JsonNumber check(JsonNumber number) {
        double d = number.doubleValue();
        if (!(Double.isFinite(d)) || !number.bigDecimalValue().equals(BigDecimal.valueOf(number.doubleValue()))) {
            throw new JsonException("Number exceeds size/precision: " + number);
        }
        return number;
    }
}
