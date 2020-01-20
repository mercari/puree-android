package com.mercari.puree.outputs;

import com.mercari.puree.PureeLogger;
import com.mercari.puree.async.AsyncResult;
import com.mercari.puree.internal.PureeVerboseRunnable;
import com.mercari.puree.internal.RetryableTaskRunner;
import com.mercari.puree.storage.BinaryRecords;
import com.google.protobuf.MessageLite;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class PureeBufferedProtobufOutput extends PureeProtobufOutput {
    RetryableTaskRunner flushTask;

    ScheduledExecutorService executor;

    public PureeBufferedProtobufOutput() {
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
    public void receive(final MessageLite protoLog) {
        executor.execute(new PureeVerboseRunnable(new Runnable() {
            @Override
            public void run() {
                MessageLite filteredLog = applyFilters(protoLog);
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

    public void cancel() {
        flushTask.cancel();
    }

    public void flushSync() {
        if (!storage.lock()) {
            flushTask.retryLater();
            return;
        }
        final BinaryRecords binaryRecords = getRecordsFromStorage();

        if (binaryRecords.isEmpty()) {
            storage.unlock();
            flushTask.reset();
            return;
        }

        final List<byte[]> protoLogs = binaryRecords.getLogs();

        emit(protoLogs, new AsyncResult() {
            @Override
            public void success() {
                flushTask.reset();
                storage.delete(binaryRecords);
                storage.unlock();
            }

            @Override
            public void fail() {
                flushTask.retryLater();
                storage.unlock();
            }
        });
    }

    private BinaryRecords getRecordsFromStorage() {
        return storage.selectBinary(type(), conf.getLogsPerRequest());
    }

    public abstract void emit(List<byte[]> binaryLogs, final AsyncResult result);

    public void emit(MessageLite protoLog) {
        // do nothing
    }
}
