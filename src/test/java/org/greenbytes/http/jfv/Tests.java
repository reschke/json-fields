package org.greenbytes.http.jfv;

import static org.junit.Assert.assertEquals;

import javax.json.Json;
import javax.json.JsonValue;

import org.junit.Test;

public class Tests {

    @Test
    public void booleans() {
        assertEquals("false", Serializer.serialize(JsonValue.FALSE));
        assertEquals("true", Serializer.serialize(JsonValue.TRUE));
    }

    @Test
    public void nulls() {
        assertEquals("null", Serializer.serialize(JsonValue.NULL));
    }

    @Test
    public void strings() {
        assertEquals("\"foo\"", Serializer.serialize(Json.createValue("foo")));
        assertEquals("\"Euro \\u20ac\"", Serializer.serialize(Json.createValue("Euro \u20ac")));
        assertEquals("\"DEL \\u007f\"", Serializer.serialize(Json.createValue("DEL \u007f")));
        assertEquals("\"foo\\nbar\"", Serializer.serialize(Json.createValue("foo\nbar")));
        assertEquals("\"NUL \\u0000\"", Serializer.serialize(Json.createValue("NUL \u0000")));
        assertEquals("\"HTAB \\t\"", Serializer.serialize(Json.createValue("HTAB \t")));
        assertEquals("\"pile of poo: \\ud83d\\udca9\"", Serializer.serialize(Json.createValue("pile of poo: \uD83D\uDCA9")));
    }

    @Test
    public void numbers() {
        assertEquals("1", Serializer.serialize(Json.createValue(1)));
        assertEquals("-1", Serializer.serialize(Json.createValue(-1)));
    }
}