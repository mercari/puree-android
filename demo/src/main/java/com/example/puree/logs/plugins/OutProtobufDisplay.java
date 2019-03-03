package com.example.puree.logs.plugins;

import android.os.Handler;
import android.os.Looper;

import com.cookpad.puree.async.AsyncResult;
import com.cookpad.puree.outputs.OutputConfiguration;
import com.cookpad.puree.outputs.PureeProtobufOutput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.protobuf.MessageLite;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OutProtobufDisplay extends PureeProtobufOutput {
    public static final String TYPE = "display";

    private static WeakReference<OutProtobufDisplay.Callback> callbackRef = new WeakReference<>(null);

    public static void register(OutProtobufDisplay.Callback callback) {
        callbackRef = new WeakReference<>(callback);
    }

    public static void unregister() {
        callbackRef.clear();
    }

    @Override
    public String type() {
        return "display";
    }

    @Nonnull
    @Override
    public OutputConfiguration configure(OutputConfiguration conf) {
        return conf;
    }

    @Override
    public void emit(MessageLite protoLog) {
        OutProtobufDisplay.Callback callback = callbackRef.get();
        if (callback == null) {
            return;
        }
        callback.onEmit(protoLog);
    }

    public interface Callback {

        void onEmit(MessageLite protoLog);
    }
}
