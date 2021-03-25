package org.greenbytes.http.jfv;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParsingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Tests {

    private final JsonProvider j;
    private final Parser p;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        List<Object[]> result = new ArrayList<>();
        result.add(new Object[] { "default", JsonProvider.provider() });
        result.add(new Object[] { "glassfish", org.glassfish.json.JsonProviderImpl.provider() });
        result.add(new Object[] { "johnzon", org.apache.johnzon.core.JsonProviderImpl.provider() });
        return result;
    }

    public Tests(String name, JsonProvider provider) {
        this.j = provider;
        this.p = new Parser(provider);
    }

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
        JsonArray value = j.createArrayBuilder().add(j.createObjectBuilder().add("type", "home").add("number", "212 555-1234"))
                .add(j.createObjectBuilder().add("type", "fax").add("number", "646 555-4567")).build();
        assertEquals("{\"type\":\"home\",\"number\":\"212 555-1234\"}, {\"type\":\"fax\",\"number\":\"646 555-4567\"}",
                Serializer.list(value));
    }

    @Test
    public void sobject() {
        String o = "{\n" + "   \"foo\": \"bar\"\n" + "}";
        assertEquals("{\"foo\": \"bar\"}", Serializer.single(o));
    }

    @Test
    public void generator() {
        StringWriter s = new StringWriter();
        Map<String, Boolean> m = Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true);
        JsonGenerator g = j.createGeneratorFactory(m).createGenerator(s);
        g.writeStartObject().write("firstName", "John").write("lastName", "Smith").write("age", 25).writeStartObject("address")
                .write("streetAddress", "21 2nd Street").write("city", "New York").write("state", "NY").write("postalCode", "10021")
                .writeEnd().writeStartArray("phoneNumber").writeStartObject().write("type", "home").write("number", "212 555-1234")
                .writeEnd().writeStartObject().write("type", "fax").write("number", "646 555-4567").writeEnd().writeEnd()
                .writeEnd();
        g.close();
        assertEquals(
                "{\"firstName\":\"John\",\"lastName\":\"Smith\",\"age\":25,\"address\":{\"streetAddress\":\"21 2nd Street\",\"city\":\"New York\",\"state\":\"NY\",\"postalCode\":\"10021\"},\"phoneNumber\":[{\"type\":\"home\",\"number\":\"212 555-1234\"},{\"type\":\"fax\",\"number\":\"646 555-4567\"}]}",
                Serializer.single(s.toString()));
    }

    @Test
    public void readSingle() {
        JsonArray a = p.parse("1");
        assertEquals("[1]", a.toString());
    }

    @Test
    public void readMulti() {
        JsonArray a = p.parse("1", "2");
        assertEquals("[1,2]", a.toString());
    }

    @Test(expected = JsonParsingException.class)
    public void readError() {
        p.parse("a");
    }

    @Test(expected = JsonParsingException.class)
    public void readError2() {
        p.parse("{\"foo\":\"bar\"");
    }

    @Test
    public void duplicateFieldNames() {
        // TODO: might also throw
        JsonArray a = p.parse("{\"foo\":\"bar\", \"foo\":\"qux\"}");
        assertEquals("[{\"foo\":\"qux\"}]", a.toString());
    }

    @Test(expected = JsonParsingException.class)
    public void readError4() {
        p.parse("1", ",");
    }
}