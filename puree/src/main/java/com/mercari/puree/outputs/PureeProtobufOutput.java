package com.mercari.puree.outputs;

import com.mercari.puree.PureeProtobufFilter;
import com.mercari.puree.PureeLogger;
import com.mercari.puree.storage.PureeStorage;
import com.google.protobuf.MessageLite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class PureeProtobufOutput {
    protected OutputConfiguration conf;
    protected PureeStorage storage;
    protected List<PureeProtobufFilter> filters = new ArrayList<>();

    public void registerFilter(PureeProtobufFilter filter) {
        filters.add(filter);
    }

    public PureeProtobufOutput withFilters(PureeProtobufFilter... filters) {
        Collections.addAll(this.filters, filters);
        return this;
    }

    public PureeProtobufOutput withFilters(Collection<PureeProtobufFilter> filters) {
        this.filters.addAll(filters);
        return this;
    }

    public List<PureeProtobufFilter> getFilters() {
        return filters;
    }

    public void initialize(PureeLogger logger) {
        this.storage = logger.getStorage();
        OutputConfiguration defaultConfiguration = new OutputConfiguration();
        this.conf = configure(defaultConfiguration);
    }

    public void receive(MessageLite protoLog) {
        final MessageLite filteredLog = applyFilters(protoLog);
        if (filteredLog == null) {
            return;
        }

        emit(filteredLog);
    }

    @Nullable
    protected MessageLite applyFilters(MessageLite protoLog) {
        MessageLite filteredLog = protoLog;
        for (PureeProtobufFilter filter : filters) {
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

    public abstract void emit(MessageLite protoLog);
}

