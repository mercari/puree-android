package com.cookpad.puree;

import com.cookpad.puree.internal.LogDumper;
import com.cookpad.puree.storage.BinaryRecords;
import com.cookpad.puree.storage.JsonRecords;
import com.google.protobuf.MessageLite;

import android.util.Log;

import java.util.concurrent.Executor;


public class Puree {

    private static final String TAG = Puree.class.getSimpleName();

    private static PureeLogger logger;

    public static synchronized void initialize(PureeConfiguration conf) {
        if (logger != null) {
            Log.w(TAG, "Puree has already been initialized; re-initialize it with the configuration");
        }
        setPureeLogger(conf.createPureeLogger());
    }

    public static void setPureeLogger(PureeLogger instance) {
        logger = instance;
    }

    /**
     * Try to send log.
     * <p>
     * This log is sent immediately or put into a buffer depending on the output plugin.
     *
     * @param log {@link PureeLog}.
     */
    public static void send(final PureeLog log) {
        checkIfPureeHasInitialized();
        logger.send(log);
    }

    /**
     * Tries to send a protobuf log entry.
     * <p>
     * This log is sent immediately or put into a buffer depending on the output plugin.
     * @param protoLog the protobuf object representing the log entry.
     */
    public static void send(MessageLite protoLog) {
        checkIfPureeHasInitialized();
        logger.send(protoLog);
    }

    /**
     * Try to flush all logs that are in buffer.
     */
    public static void flush() {
        checkIfPureeHasInitialized();
        logger.flush();
    }

    public static void dump() {
        LogDumper.out(getBufferedJsonLogs());
        LogDumper.out(getBufferedBinaryLogs());
    }

    /**
     * Get all JSON logs in the buffer.
     *
     * @return {@link JsonRecords}.
     */
    public static JsonRecords getBufferedJsonLogs() {
        checkIfPureeHasInitialized();
        return logger.getBufferedJsonLogs();
    }

    /**
     * Get all binary logs in the buffer.
     *
     * @return {@link BinaryRecords}.
     */
    public static BinaryRecords getBufferedBinaryLogs() {
        checkIfPureeHasInitialized();
        return logger.getBufferedBinaryLogs();
    }


    /**
     * Discards all logs in buffer.
     */
    public static void discardBufferedLogs() {
        checkIfPureeHasInitialized();
        logger.discardBufferedLogs();
    }

    /**
     * Truncate logs in buffer.
     *
     * @param truncateThresholdInRows truncate logs that are over this variable
     */
    public static void truncateBufferedLogs(int truncateThresholdInRows) {
        checkIfPureeHasInitialized();
        logger.truncateBufferedLogs(truncateThresholdInRows);
    }

    public static Executor getExecutor() {
        return logger.getExecutor();
    }

    private static void checkIfPureeHasInitialized() {
        if (logger == null) {
            throw new NotInitializedException();
        }
    }

    public static class NotInitializedException extends IllegalStateException {

    }
}
