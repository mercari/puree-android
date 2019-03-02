package com.cookpad.puree.internal;

import com.cookpad.puree.PureeFilter;
import com.cookpad.puree.PureeLog;
import com.cookpad.puree.PureeProtobufFilter;
import com.cookpad.puree.outputs.PureeOutput;
import com.cookpad.puree.outputs.PureeProtobufOutput;
import com.cookpad.puree.storage.BinaryRecords;
import com.cookpad.puree.storage.JsonRecords;
import com.google.protobuf.MessageLite;

import android.util.Base64;
import android.util.Log;

import java.util.List;
import java.util.Map;

public class LogDumper {

    private static final String TAG = LogDumper.class.getSimpleName();

    public static void out(JsonRecords jsonRecords) {
        switch (jsonRecords.size()) {
            case 0:
                Log.d(TAG, "No JSON records in Puree's buffer");
                break;
            case 1:
                Log.d(TAG, "1 JSON record in Puree's buffer" + "\n"
                        + jsonRecords.getJsonLogs().get(0));
                break;
            default:
                StringBuilder builder = new StringBuilder();
                int size = jsonRecords.size();
                builder.append(size).append(" json records in Puree's buffer\n");
                for (int i = 0; i < size; i++) {
                    builder.append(jsonRecords.getJsonLogs().get(0)).append("\n");
                }
                Log.d(TAG, builder.substring(0, builder.length() - 1));
        }
    }

    public static void out(BinaryRecords binaryRecords) {
        switch (binaryRecords.size()) {
            case 0:
                Log.d(TAG, "No binary records in Puree's buffer");
                break;
            case 1:
                Log.d(TAG, "1 binary record in Puree's buffer" + "\n"
                        + Base64.encodeToString(binaryRecords.getLogs().get(0), Base64.DEFAULT));
                break;
            default:
                StringBuilder builder = new StringBuilder();
                int size = binaryRecords.size();
                builder.append(size).append(" binary records in Puree's buffer\n");
                for (int i = 0; i < size; i++) {
                    builder.append(Base64.encodeToString(binaryRecords.getLogs().get(0),
                            Base64.DEFAULT)).append("\n");
                }
                Log.d(TAG, builder.substring(0, builder.length() - 1));
        }
    }

    public static void out(Map<Class<? extends PureeLog>, List<PureeOutput>> sourceOutputMap) {
        Log.i(TAG, "# SOURCE -> FILTER... -> OUTPUT");
        for (Class<?> key : sourceOutputMap.keySet()) {
            StringBuilder builder;
            for (PureeOutput output : sourceOutputMap.get(key)) {
                builder = new StringBuilder(key.getSimpleName());
                for (PureeFilter filter : output.getFilters()) {
                    builder.append(" -> ").append(filter.getClass().getSimpleName());
                }
                builder.append(" -> ").append(output.getClass().getSimpleName());
                Log.i(TAG, builder.toString());
            }
        }
    }

    public static void outProtoSourceMap(Map<Class<? extends MessageLite>,
            List<PureeProtobufOutput>> protoSourceOutputMap) {
        Log.i(TAG, "# SOURCE -> FILTER... -> OUTPUT");
        for (Class<?> key : protoSourceOutputMap.keySet()) {
            StringBuilder builder;
            for (PureeProtobufOutput output : protoSourceOutputMap.get(key)) {
                builder = new StringBuilder(key.getSimpleName());
                for (PureeProtobufFilter filter : output.getFilters()) {
                    builder.append(" -> ").append(filter.getClass().getSimpleName());
                }
                builder.append(" -> ").append(output.getClass().getSimpleName());
                Log.i(TAG, builder.toString());
            }
        }
    }
}
