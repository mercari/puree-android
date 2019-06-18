package com.mercari.puree.storage;

import com.google.gson.JsonArray;

import java.util.ArrayList;

public class JsonRecords extends ArrayList<JsonRecord> implements Records {

    public String getIdsAsString() {
        if (isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (JsonRecord jsonRecord : this) {
            builder.append(jsonRecord.getId()).append(',');
        }
        return builder.substring(0, builder.length() - 1);
    }

    public JsonArray getJsonLogs() {
        JsonArray jsonLogs = new JsonArray();
        for (JsonRecord jsonRecord : this) {
            jsonLogs.add(jsonRecord.getJsonLog());
        }
        return jsonLogs;
    }
}
