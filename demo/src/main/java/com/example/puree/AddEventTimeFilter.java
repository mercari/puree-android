package com.example.puree;

import com.google.gson.JsonObject;

import com.cookpad.puree.PureeJsonFilter;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AddEventTimeFilter implements PureeJsonFilter {

    public JsonObject apply(JsonObject jsonLog) {
        jsonLog.addProperty("event_time", System.currentTimeMillis());
        return jsonLog;
    }
}
