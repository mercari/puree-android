package com.example.puree.logs.plugins;

import android.util.Log;

import com.mercari.puree.async.AsyncResult;
import com.mercari.puree.outputs.OutputConfiguration;
import com.mercari.puree.outputs.PureeBufferedProtobufOutput;
import com.google.protobuf.InvalidProtocolBufferException;
import com.example.event.Event;
import com.example.event.EventBatch;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OutBufferedProtobufLogcat extends PureeBufferedProtobufOutput {
    public static final String TYPE = "buffered_protobuf_logcat";

    private static final String TAG = OutBufferedProtobufLogcat.class.getSimpleName();

    @Override
    public String type() {
        return TYPE;
    }

    @Nonnull
    @Override
    public OutputConfiguration configure(OutputConfiguration conf) {
        conf.setFlushIntervalMillis(2000);
        conf.setLogsPerRequest(3);
        return conf;
    }

    @Override
    public void emit(List<byte[]> binaryLogs, AsyncResult asyncResult) {
        EventBatch.Builder batch = EventBatch.newBuilder();
        batch.setTime(Calendar.getInstance().getTime().toString());
        for (byte[] log : binaryLogs) {
            try {
                Event event = Event.parseFrom(log);
                batch.addEvents(event);
            } catch (InvalidProtocolBufferException e) {
                Log.e("protobuf", e.getMessage());
            }
        }
        EventBatch batchRequest = batch.build();
        Log.d(TAG, "Batch request:\n" + batchRequest.toString());
        asyncResult.success();
    }
}
