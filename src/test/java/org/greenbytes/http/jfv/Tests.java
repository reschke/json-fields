package org.greenbytes.http.jfv;

import static org.junit.Assert.assertEquals;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonValue;

import org.junit.Test;

public class Tests {

    @Test
    public void booleans() {
        assertEquals("false", Serializer.single(JsonValue.FALSE));
        assertEquals("true", Serializer.single(JsonValue.TRUE));
        assertEquals("true", Serializer.single(JsonValue.TRUE));
    }

    @Test
    public void nulls() {
        assertEquals("null", Serializer.single(JsonValue.NULL));
    }

    @Test
    public void strings() {
        assertEquals("\"foo\"", Serializer.single(Json.createValue("foo")));
        assertEquals("\"Euro \\u20ac\"", Serializer.single(Json.createValue("Euro \u20ac")));
        assertEquals("\"DEL \\u007f\"", Serializer.single(Json.createValue("DEL \u007f")));
        assertEquals("\"foo\\nbar\"", Serializer.single(Json.createValue("foo\nbar")));
        assertEquals("\"NUL \\u0000\"", Serializer.single(Json.createValue("NUL \u0000")));
        assertEquals("\"HTAB \\t\"", Serializer.single(Json.createValue("HTAB \t")));
        assertEquals("\"pile of poo: \\ud83d\\udca9\"", Serializer.single(Json.createValue("pile of poo: \uD83D\uDCA9")));
    }

    @Test
    public void numbers() {
        assertEquals("1", Serializer.single(Json.createValue(1)));
        assertEquals("-1", Serializer.single(Json.createValue(-1)));
    }

    @Test
    public void combined() {
        assertEquals("false, true, null, 1, \"foo bar\"",
                Serializer.list(JsonValue.FALSE, JsonValue.TRUE, JsonValue.NULL, Json.createValue(1), Json.createValue("foo bar")));
    }

    @Test
    public void list() {
        JsonArray value = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("type", "home").add("number", "212 555-1234"))
                .add(Json.createObjectBuilder().add("type", "fax").add("number", "646 555-4567")).build();
        assertEquals("{\"type\":\"home\",\"number\":\"212 555-1234\"}, {\"type\":\"fax\",\"number\":\"646 555-4567\"}",
                Serializer.list(value));
    }

    @Test
    public void sobject() {
        String o = "{\n" + "   \"foo\": \"bar\"\n" + "}";
        assertEquals("{\"foo\": \"bar\"}", Serializer.single(o));
    }
}