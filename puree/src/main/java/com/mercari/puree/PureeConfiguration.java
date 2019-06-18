package com.mercari.puree;

import com.mercari.puree.outputs.PureeOutput;
import com.mercari.puree.outputs.PureeProtobufOutput;
import com.google.gson.Gson;

import com.mercari.puree.internal.LogDumper;
import com.mercari.puree.storage.PureeSQLiteStorage;
import com.mercari.puree.storage.PureeStorage;
import com.google.protobuf.MessageLite;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PureeConfiguration {

    private final Context context;

    private final Gson gson;

    private final Map<Class<? extends PureeLog>, List<PureeOutput>> sourceOutputMap;

    private final Map<Class<? extends MessageLite>, List<PureeProtobufOutput>> protoSourceOutputMap;

    private final PureeStorage storage;

    private final ScheduledExecutorService executor;

    public Context getContext() {
        return context;
    }

    public Gson getGson() {
        return gson;
    }

    public Map<Class<? extends PureeLog>, List<PureeOutput>> getSourceOutputMap() {
        return sourceOutputMap;
    }

    public Map<Class<? extends MessageLite>, List<PureeProtobufOutput>> getProtoSourceOutputMap() {
        return protoSourceOutputMap;
    }

    public PureeStorage getStorage() {
        return storage;
    }

    public List<PureeOutput> getRegisteredOutputPlugins(Class<? extends PureeLog> logClass) {
        return sourceOutputMap.get(logClass);
    }

    public PureeLogger createPureeLogger() {
        return new PureeLogger(sourceOutputMap, gson, protoSourceOutputMap, storage, executor);
    }

    PureeConfiguration(Context context, Gson gson,
                       Map<Class<? extends PureeLog>, List<PureeOutput>> sourceOutputMap,
                       Map<Class<? extends MessageLite>, List<PureeProtobufOutput>> protoSourceOutputMap,
                       PureeStorage storage,
                       ScheduledExecutorService executor) {
        this.context = context;
        this.gson = gson;
        this.sourceOutputMap = sourceOutputMap;
        this.protoSourceOutputMap = protoSourceOutputMap;
        this.storage = storage;
        this.executor = executor;

    }

    /**
     * Print mapping of SOURCE -&gt; FILTER... OUTPUT.
     */
    public void printMapping() {
        LogDumper.out(sourceOutputMap);
        LogDumper.outProtoSourceMap(protoSourceOutputMap);
    }

    public static class Builder {
        private Context context;

        private Gson gson;

        private Map<Class<? extends PureeLog>, List<PureeOutput>> sourceOutputMap = new HashMap<>();

        private Map<Class<? extends MessageLite>, List<PureeProtobufOutput>> protoSourceOutputMap = new HashMap<>();

        private PureeStorage storage;

        private ScheduledExecutorService executor;

        /**
         * Start building a new {@link com.mercari.puree.PureeConfiguration} instance.
         *
         * @param context {@link Context}.
         */
        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        /**
         * Specify the {@link com.google.gson.Gson} to serialize logs.
         *
         * @param gson {@link Gson}.
         * @return {@link com.mercari.puree.PureeConfiguration.Builder}.
         */
        public Builder gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        /**
         * Specify a source class of logs, which returns {@link Source} an
         * {@link Source#to(PureeOutput)} must be called to register an output plugin.
         *
         * @param logClass log class.
         * @return {@link Source}.
         */
        public Source source(Class<? extends PureeLog> logClass) {
            return new Source(this, logClass);
        }

        public Builder register(Class<? extends PureeLog> logClass, PureeOutput output) {
            List<PureeOutput> outputs = sourceOutputMap.get(logClass);
            if (outputs == null) {
                outputs = new ArrayList<>();
            }
            outputs.add(output);
            sourceOutputMap.put(logClass, outputs);
            return this;
        }

        public Builder registerProtoLog(Class<? extends MessageLite> logClass, PureeProtobufOutput output) {
            List<PureeProtobufOutput> outputs = protoSourceOutputMap.get(logClass);
            if (outputs == null) {
                outputs = new ArrayList<>();
            }
            outputs.add(output);
            protoSourceOutputMap.put(logClass, outputs);
            return this;
        }

        public Builder storage(PureeStorage storage) {
            this.storage = storage;
            return this;
        }

        public Builder executor(ScheduledExecutorService executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Create the {@link com.mercari.puree.PureeConfiguration} instance.
         *
         * @return {@link com.mercari.puree.PureeConfiguration}.
         */
        public PureeConfiguration build() {
            if (gson == null) {
                gson = new Gson();
            }
            if (storage == null) {
                storage = new PureeSQLiteStorage(context);
            }

            if (executor == null) {
                executor = newBackgroundExecutor();
            }
            return new PureeConfiguration(context, gson, sourceOutputMap, protoSourceOutputMap,
                    storage, executor);
        }
    }

    static ScheduledExecutorService newBackgroundExecutor() {
        return Executors.newScheduledThreadPool(1, new BackgroundThreadFactory());
    }

    static class BackgroundThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "puree");
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    }
}
