package com.cookpad.puree;

import com.cookpad.puree.outputs.PureeOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Source {
    private PureeConfiguration.Builder builder;

    private Class<? extends PureeLog> logClass;
    private List<PureeFilter> filters = new ArrayList<>();

    public Source(PureeConfiguration.Builder builder, Class<? extends PureeLog> logClass) {
        this.builder = builder;
        this.logClass = logClass;
    }

    /**
     * Specify the {@link PureeFilter}.
     *
     * @param filter {@link PureeFilter}.
     * @return {@link Source}.
     */
    public Source filter(PureeFilter filter) {
        filters.add(filter);
        return this;
    }

    /**
     * Specify the {@link PureeFilter}.
     *
     * @param filters {@link PureeFilter} list.
     * @return {@link Source}.
     */
    public Source filters(PureeFilter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }

    /**
     * Specify the {@link PureeOutput} that is responded to source.
     *
     * @param output {@link PureeOutput}.
     * @return {@link com.cookpad.puree.PureeConfiguration.Builder}.
     */
    public PureeConfiguration.Builder to(PureeOutput output) {
        builder.register(logClass, output.withFilters(filters));
        return builder;
    }
}
