package org.greenbytes.http.jfv;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

public class Parser {

    public static JsonArray parse(String input) {
        JsonReader result = Json.createReader(new StringReader("[" + input + "]"));
        return result.readArray();
    }

    public static JsonArray parse(String... input) {
        JsonReader result = Json.createReader(new StringReader("[" + String.join(",", input) + "]"));
        return result.readArray();
    }
}
