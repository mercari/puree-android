package com.cookpad.puree;

import android.os.Message;
import android.util.Log;

import com.cookpad.puree.outputs.PureeJsonOutput;
import com.cookpad.puree.outputs.PureeProtobufOutput;
import com.cookpad.puree.storage.BinaryRecords;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.cookpad.puree.storage.PureeStorage;
import com.cookpad.puree.storage.JsonRecords;
import com.google.protobuf.MessageLite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PureeLogger {

    final Gson gson;

    final Map<Class<? extends PureeLog>, List<PureeJsonOutput>> sourceOutputMap = new HashMap<>();

    final Map<Class<? extends MessageLite>, List<PureeProtobufOutput>> protoSourceOutputMap = new HashMap<>();

    final PureeStorage storage;

    final ScheduledExecutorService executor;

    public PureeLogger(Map<Class<? extends PureeLog>, List<PureeJsonOutput>> sourceOutputMap,
                       Gson gson,
                       Map<Class<? extends MessageLite>, List<PureeProtobufOutput>> protoSourceOutputMap,
                       PureeStorage storage,
                       ScheduledExecutorService executor) {
        this.sourceOutputMap.putAll(sourceOutputMap);
        this.gson = gson;
        this.protoSourceOutputMap.putAll(protoSourceOutputMap);
        this.storage = storage;
        this.executor = executor;

        forEachJsonOutput(new PureeLogger.Consumer<PureeJsonOutput>() {
            @Override
            public void accept(@Nonnull PureeJsonOutput value) {
                value.initialize(PureeLogger.this);
            }
        });

        forEachProtobufOutput(new PureeLogger.Consumer<PureeProtobufOutput>() {
            @Override
            public void accept(@Nonnull PureeProtobufOutput value) {
                value.initialize(PureeLogger.this);
            }
        });
    }

    public PureeLogger(Map<Class<? extends PureeLog>, List<PureeJsonOutput>> sourceOutputMap,
                       Gson gson,
                       PureeStorage storage,
                       ScheduledExecutorService executor) {
        this(sourceOutputMap, gson,
                new HashMap<Class<? extends MessageLite>, List<PureeProtobufOutput>>(),
                storage, executor);
    }

    public void send(PureeLog log) {
        List<PureeJsonOutput> outputs = getRegisteredOutputPlugins(log);
        for (PureeJsonOutput output : outputs) {
            JsonObject jsonLog = serializeLog(log);
            output.receive(jsonLog);
        }
    }

    public void send(MessageLite protoLog) {
        List<PureeProtobufOutput> outputs = getRegisteredOutputPlugins(protoLog);
        String protoStr = protoLog.toString();
        for (PureeProtobufOutput output : outputs) {
            output.receive(protoLog);
        }
    }

    public PureeStorage getStorage() {
        return storage;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public JsonRecords getBufferedJsonLogs() {
        return storage.selectAllJsonRecords();
    }

    public BinaryRecords getBufferedBinaryLogs() {
        return storage.selectAllBinaryRecords();
    }

    public void discardBufferedLogs() {
        storage.clear();
    }

    public void truncateBufferedLogs(int truncateThresholdInRows) {
        storage.truncateBufferedLogs(truncateThresholdInRows);
    }

    public void flush() {
        forEachJsonOutput(new PureeLogger.Consumer<PureeJsonOutput>() {
            @Override
            public void accept(@Nonnull PureeJsonOutput value) {
                value.flush();
            }
        });
    }

    /**
     * Serialize a {@link PureeLog} into {@link JsonObject} with {@link Gson}.
     *
     * @param log {@link PureeLog}.
     * @return serialized json object.
     */
    @Nonnull
    public JsonObject serializeLog(PureeLog log) {
        return (JsonObject) gson.toJsonTree(log);
    }

    @Nonnull
    public List<PureeJsonOutput> getRegisteredOutputPlugins(PureeLog log) {
        return getRegisteredOutputPlugins(log.getClass());
    }

    public List<PureeProtobufOutput> getRegisteredOutputPlugins(MessageLite protoLog) {
        return getRegisteredProtoOutputPlugins(protoLog.getClass());
    }

    @Nonnull
    public List<PureeJsonOutput> getRegisteredOutputPlugins(Class<? extends PureeLog> logClass) {
        List<PureeJsonOutput> outputs = sourceOutputMap.get(logClass);
        if (outputs == null) {
            throw new NoRegisteredOutputPluginException("No output plugin registered for " + logClass);
        }
        return outputs;
    }

    @Nonnull
    public List<PureeProtobufOutput> getRegisteredProtoOutputPlugins(Class<? extends MessageLite> logClass) {
        List<PureeProtobufOutput> outputs = protoSourceOutputMap.get(logClass);
        if (outputs == null) {
            throw new NoRegisteredOutputPluginException("No output plugin registered for " + logClass);
        }
        return outputs;
    }

    public interface Consumer<T> {

        void accept(@Nonnull T value);
    }

    public void forEachJsonOutput(Consumer<PureeJsonOutput> f) {
        for (List<PureeJsonOutput> outputs : new HashSet<>(sourceOutputMap.values())) {
            for (PureeJsonOutput output : outputs) {
                f.accept(output);
            }
        }
    }

    public void forEachProtobufOutput(Consumer<PureeProtobufOutput> f) {
        for (List<PureeProtobufOutput> outputs : new HashSet<>(protoSourceOutputMap.values())) {
            for (PureeProtobufOutput output : outputs) {
                f.accept(output);
            }
        }
    }

    public static class NoRegisteredOutputPluginException extends IllegalStateException {

        public NoRegisteredOutputPluginException(String detailMessage) {
            super(detailMessage);
        }
    }
}
