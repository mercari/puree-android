package com.cookpad.puree.outputs;

import com.cookpad.puree.storage.JsonRecords;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.cookpad.puree.PureeLogger;
import com.cookpad.puree.async.AsyncResult;
import com.cookpad.puree.internal.PureeVerboseRunnable;
import com.cookpad.puree.internal.RetryableTaskRunner;

import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class PureeBufferedJsonOutput extends PureeJsonOutput {

    RetryableTaskRunner flushTask;

    ScheduledExecutorService executor;

    public PureeBufferedJsonOutput() {
    }

    @Override
    public void initialize(PureeLogger logger) {
        super.initialize(logger);
        executor = logger.getExecutor();
        flushTask = new RetryableTaskRunner(new Runnable() {
            @Override
            public void run() {
                flush();
            }
        }, conf.getFlushIntervalMillis(), conf.getMaxRetryCount(), executor);
    }

    @Override
    public void receive(final JsonObject jsonLog) {
        executor.execute(new PureeVerboseRunnable(new Runnable() {
            @Override
            public void run() {
                JsonObject filteredLog = applyFilters(jsonLog);
                if (filteredLog != null) {
                    storage.insert(type(), filteredLog);
                }
            }
        }));

        flushTask.tryToStart();
    }

    @Override
    public void flush() {
        executor.execute(new PureeVerboseRunnable(new Runnable() {
            @Override
            public void run() {
                flushSync();
            }
        }));
    }

    public void flushSync() {
        if (!storage.lock()) {
            flushTask.retryLater();
            return;
        }
        final JsonRecords jsonRecords = getRecordsFromStorage();

        if (jsonRecords.isEmpty()) {
            storage.unlock();
            flushTask.reset();
            return;
        }

        final JsonArray jsonLogs = jsonRecords.getJsonLogs();

        emit(jsonLogs, new AsyncResult() {
            @Override
            public void success() {
                flushTask.reset();
                storage.delete(jsonRecords);
                storage.unlock();
            }

            @Override
            public void fail() {
                flushTask.retryLater();
                storage.unlock();
            }
        });
    }

    private JsonRecords getRecordsFromStorage() {
        return storage.selectJson(type(), conf.getLogsPerRequest());
    }

    public abstract void emit(JsonArray jsonArray, final AsyncResult result);

    public void emit(JsonObject jsonLog) {
        // do nothing
    }
}
