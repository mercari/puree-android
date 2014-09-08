package com.example.loghouse.logs;

import com.cookpad.android.loghouse.LogHouse;
import com.cookpad.android.loghouse.async.AsyncResult;

import org.json.JSONObject;

import java.util.List;

public class OutAsyncSomething extends LogHouse.BufferedOutput {
    private static final String TYPE = "async_something";

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void emit(List<JSONObject> serializedLogs, final AsyncResult result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                result.success();
            }
        });
    }
}