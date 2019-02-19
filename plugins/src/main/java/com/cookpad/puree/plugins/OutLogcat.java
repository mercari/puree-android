package com.cookpad.puree.plugins;

import com.cookpad.puree.outputs.PureeJsonOutput;
import com.google.gson.JsonObject;

import com.cookpad.puree.outputs.OutputConfiguration;

import android.util.Log;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OutLogcat extends PureeJsonOutput {
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
