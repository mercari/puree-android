package com.mercari.puree.plugins;

import com.mercari.puree.outputs.PureeOutput;
import com.google.gson.JsonObject;

import com.mercari.puree.outputs.OutputConfiguration;

import android.util.Log;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OutLogcat extends PureeOutput {
    @Override
    public String type() {
        return "out_logcat";
    }

    @Nonnull
    @Override
    public OutputConfiguration configure(OutputConfiguration conf) {
        return conf;
    }

    @Override
    public void emit(JsonObject jsonLog) {
        Log.d("out_logcat", jsonLog.toString());
    }
}
