package com.mercari.puree.outputs;

import com.mercari.puree.PureeFilter;
import com.google.gson.JsonObject;

import com.mercari.puree.PureeLogger;
import com.mercari.puree.TagPattern;
import com.mercari.puree.storage.PureeStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class PureeOutput {
    protected OutputConfiguration conf;
    protected PureeStorage storage;
    protected List<PureeFilter> filters = new ArrayList<>();

    public void registerFilter(PureeFilter filter) {
        filters.add(filter);
    }

    public PureeOutput withFilters(PureeFilter... filters) {
        Collections.addAll(this.filters, filters);
        return this;
    }

    public PureeOutput withFilters(Collection<PureeFilter> filters) {
        this.filters.addAll(filters);
        return this;
    }


    public List<PureeFilter> getFilters() {
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

    @Nonnull
    public TagPattern getTagPattern() {
        return TagPattern.getDefaultInstance();
    }

    @Nullable
    protected JsonObject applyFilters(JsonObject jsonLog) {
        JsonObject filteredLog = jsonLog;
        for (PureeFilter filter : filters) {
            filteredLog = filter.apply(filteredLog);
            if (filteredLog == null) {
                return null;
            }
        }
        return filteredLog;
    }

    public void flush() {
        // do nothing because PureeOutput don't have any buffers.
    }

    @Nonnull
    public abstract String type();

    @Nonnull
    public abstract OutputConfiguration configure(OutputConfiguration conf);

    public abstract void emit(JsonObject jsonLog);
}

