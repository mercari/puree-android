package com.cookpad.puree.outputs;

import com.google.gson.JsonObject;

import com.cookpad.puree.PureeJsonFilter;
import com.cookpad.puree.PureeLogger;
import com.cookpad.puree.storage.PureeStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class PureeJsonOutput {
    protected OutputConfiguration conf;
    protected PureeStorage storage;
    protected List<PureeJsonFilter> filters = new ArrayList<>();

    public void registerFilter(PureeJsonFilter filter) {
        filters.add(filter);
    }

    public PureeJsonOutput withFilters(PureeJsonFilter... filters) {
        Collections.addAll(this.filters, filters);
        return this;
    }

    public PureeJsonOutput withFilters(Collection<PureeJsonFilter> filters) {
        this.filters.addAll(filters);
        return this;
    }


    public List<PureeJsonFilter> getFilters() {
        return filters;
    }

    public void initialize(PureeLogger logger) {
        this.storage = logger.getStorage();
        OutputConfiguration defaultConfiguration = new OutputConfiguration();
        this.conf = configure(defaultConfiguration);
    }

    public void receive(JsonObject jsonLog) {
        final JsonObject filteredLog = applyFilters(jsonLog);
        if (filteredLog == null) {
            return;
        }

        emit(filteredLog);
    }

    @Nullable
    protected JsonObject applyFilters(JsonObject jsonLog) {
        JsonObject filteredLog = jsonLog;
        for (PureeJsonFilter filter : filters) {
            filteredLog = filter.apply(filteredLog);
            if (filteredLog == null) {
                return null;
            }
        }
        return filteredLog;
    }

    public void flush() {
        // do nothing because PureeJsonOutput don't have any buffers.
    }

    @Nonnull
    public abstract String type();

    @Nonnull
    public abstract OutputConfiguration configure(OutputConfiguration conf);

    public abstract void emit(JsonObject jsonLog);
}

