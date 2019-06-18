package com.mercari.puree.storage;

import java.util.ArrayList;
import java.util.List;

public class BinaryRecords extends ArrayList<BinaryRecord> implements Records {
    public String getIdsAsString() {
        if (isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (BinaryRecord binaryRecord : this) {
            builder.append(binaryRecord.getId()).append(',');
        }
        return builder.substring(0, builder.length() - 1);
    }

    public List<byte[]> getLogs() {
        List<byte[]> binaryLogs = new ArrayList<>();
        for (BinaryRecord binaryRecord : this) {
            binaryLogs.add(binaryRecord.getBytes());
        }
        return binaryLogs;
    }
}
