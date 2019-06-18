package com.example.puree.logs.plugins;

import com.mercari.puree.outputs.PureeOutput;
import com.google.gson.JsonObject;

import com.mercari.puree.outputs.OutputConfiguration;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OutDisplay extends PureeOutput {
    public static final String TYPE = "display";

    private static WeakReference<Callback> callbackRef = new WeakReference<>(null);

    public static void register(Callback callback) {
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
    public void emit(JsonObject jsonLog) {
        Callback callback = callbackRef.get();
        if (callback == null) {
            return;
        }
        callback.onEmit(jsonLog);
    }

    public interface Callback {

        void onEmit(JsonObject jsonLog);
    }
}
