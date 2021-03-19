package org.greenbytes.http.jfv;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

public class Parser {

    // naive impl for now; use streaming API to avoid the string operations

    public static JsonArray parse(String input) {
        JsonReader result = Json.createReader(new StringReader("[" + input + "]"));
        return result.readArray();
    }

    public static JsonArray parse(String... input) {
        StringBuilder b = new StringBuilder("[");
        String delim = "";
        for (String s : input) {
            b.append(delim);
            delim = ",";
            b.append(s);
        }
        b.append("]");
        JsonReader result = Json.createReader(new StringReader(b.toString()));
        return result.readArray();
    }
}
