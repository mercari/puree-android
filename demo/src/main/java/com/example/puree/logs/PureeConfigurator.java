package com.example.puree.logs;

import com.cookpad.puree.Puree;
import com.cookpad.puree.PureeConfiguration;
import com.cookpad.puree.PureeJsonFilter;
import com.cookpad.puree.plugins.OutBufferedLogcat;
import com.example.event.Event;
import com.example.puree.logs.plugins.OutBufferedProtobufLogcat;
import com.cookpad.puree.plugins.OutLogcat;
import com.example.puree.AddEventTimeFilter;
import com.example.puree.logs.filters.SamplingFilter;
import com.example.puree.logs.plugins.OutBufferedVoid;
import com.example.puree.logs.plugins.OutDisplay;
import com.example.puree.logs.plugins.OutProtobufDisplay;

import android.content.Context;

import java.util.concurrent.Executors;

public class PureeConfigurator {
    public static void configure(Context context) {
        Puree.initialize(buildConf(context));
    }

    public static PureeConfiguration buildConf(Context context) {
        PureeJsonFilter addEventTimeFilter = new AddEventTimeFilter();
        PureeJsonFilter samplingFilter = new SamplingFilter(1.0f);
        PureeConfiguration conf = new PureeConfiguration.Builder(context)
                .executor(Executors.newScheduledThreadPool(1)) // optional
                .register(ClickLog.class, new OutDisplay().withFilters(addEventTimeFilter))
                .register(ClickLog.class,
                        new OutBufferedLogcat().withFilters(addEventTimeFilter, samplingFilter))
                .register(PvLog.class, new OutLogcat().withFilters(addEventTimeFilter))
                .register(BenchmarkLog.class, new OutBufferedVoid().withFilters(addEventTimeFilter))
                .registerProtoLog(Event.class, new OutProtobufDisplay())
                .registerProtoLog(Event.class, new OutBufferedProtobufLogcat())
                .build();
        conf.printMapping();
        return conf;
    }
}
