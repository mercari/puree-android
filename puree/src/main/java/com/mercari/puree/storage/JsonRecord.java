package com.mercari.puree.storage;

import com.google.gson.JsonObject;

public class JsonRecord {

    private final int id;

    private final String type;

    private final JsonObject jsonLog;

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public JsonObject getJsonLog() {
        return jsonLog;
    }

    public JsonRecord(int id, String type, JsonObject jsonLog) {
        this.id = id;
        this.type = type;
        this.jsonLog = jsonLog;
    }
}
