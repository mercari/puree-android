package com.cookpad.puree;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PureeJsonFilter {

    @Nullable
    JsonObject apply(JsonObject jsonLog);
}
