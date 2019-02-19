package com.example.puree.logs.plugins;

import android.os.Handler;
import android.os.Looper;

import com.cookpad.puree.async.AsyncResult;
import com.cookpad.puree.outputs.OutputConfiguration;
import com.cookpad.puree.outputs.PureeBufferedProtobufOutput;
import com.google.gson.JsonArray;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.annotation.Nonnull;

public class OutBufferedProtobufDisplay extends PureeBufferedProtobufOutput {
    private static WeakReference<OutBufferedProtobufDisplay.Callback> callbackRef = new WeakReference<>(null);

    public static void register(OutBufferedProtobufDisplay.Callback callback) {
        callbackRef = new WeakReference<>(callback);
    }

    public static void unregister() {
        callbackRef.clear();
    }

    @Override
    public String type() {
        return "out_buffered_display";
    }

    @Nonnull
    @Override
    public OutputConfiguration configure(OutputConfiguration conf) {
        conf.setFlushIntervalMillis(3000);
        return conf;
    }

    @Override
    public void emit(final List<byte[]> binaryLogs, final AsyncResult result) {
        final OutBufferedProtobufDisplay.Callback callback = callbackRef.get();
        if (callback == null) {
            result.success();
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callback.onEmit(binaryLogs);
                result.success();
            }
        });
    }

    public interface Callback {

        void onEmit(List<byte[]> binaryLogs);
    }
}
