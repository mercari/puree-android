package com.cookpad.puree;

import com.cookpad.puree.outputs.PureeJsonOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Source {
    private PureeConfiguration.Builder builder;

    private Class<? extends PureeLog> logClass;
    private List<PureeJsonFilter> filters = new ArrayList<>();

    public Source(PureeConfiguration.Builder builder, Class<? extends PureeLog> logClass) {
        this.builder = builder;
        this.logClass = logClass;
    }

    /**
     * Specify the {@link PureeJsonFilter}.
     *
     * @param filter {@link PureeJsonFilter}.
     * @return {@link Source}.
     */
    public Source filter(PureeJsonFilter filter) {
        filters.add(filter);
        return this;
    }

    /**
     * Specify the {@link PureeJsonFilter}.
     *
     * @param filters {@link PureeJsonFilter} list.
     * @return {@link Source}.
     */
    public Source filters(PureeJsonFilter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }

    /**
     * Specify the {@link PureeJsonOutput} that is responded to source.
     *
     * @param output {@link PureeJsonOutput}.
     * @return {@link com.cookpad.puree.PureeConfiguration.Builder}.
     */
    public PureeConfiguration.Builder to(PureeJsonOutput output) {
        builder.register(logClass, output.withFilters(filters));
        return builder;
    }
}
