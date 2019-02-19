package com.example.puree.logs.plugins;

import com.cookpad.puree.outputs.PureeBufferedJsonOutput;
import com.google.gson.JsonArray;

import com.cookpad.puree.async.AsyncResult;
import com.cookpad.puree.outputs.OutputConfiguration;
import com.example.puree.FakeApiClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OutFakeApi extends PureeBufferedJsonOutput {
    private static final FakeApiClient CLIENT = new FakeApiClient();

    @Override
    public String type() {
        return "out_fake_api";
    }

    @Nonnull
    @Override
    public OutputConfiguration configure(OutputConfiguration conf) {
        // you can change settings of this plugin
        conf.setFlushIntervalMillis(1000); // set interval of sending logs. defaults to 2 * 60 * 1000 (2 minutes).
        conf.setLogsPerRequest(10);        // set num of logs per request. defaults to 100.
        conf.setMaxRetryCount(
                3);          // set retry count. if fail to send logs, logs will be sending at next timeInMillis. defaults to 5.
        return conf;
    }

    @Override
    public void emit(JsonArray jsonArray, final AsyncResult result) {
        // you have to call result.success or result.fail()
        // to notify whether if puree can clear logs from buffer
        CLIENT.sendLog(jsonArray, new FakeApiClient.Callback() {
            @Override
            public void success() {
                result.success();
            }

            @Override
            public void fail() {
                result.fail();
            }
        });
    }
}
