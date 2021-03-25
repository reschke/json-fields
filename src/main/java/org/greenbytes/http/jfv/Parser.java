package org.greenbytes.http.jfv;

import java.io.StringReader;

import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;

public class Parser {

    private final JsonProvider jp;

    private static final Parser INSTANCE = new Parser();

    private Parser() {
        this.jp = JsonProvider.provider();
    }

    public Parser(JsonProvider provider) {
        this.jp = provider;
    }

    public static Parser getInstance() {
        return INSTANCE;
    }

    public JsonArray parse(String input) {
        JsonReader result = jp.createReader(new StringReader("[" + input + "]"));
        return result.readArray();
    }

    public JsonArray parse(String... input) {
        JsonReader result = jp.createReader(new StringReader("[" + String.join(",", input) + "]"));
        return result.readArray();
    }
}
