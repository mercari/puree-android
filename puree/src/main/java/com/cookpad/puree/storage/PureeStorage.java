package com.cookpad.puree.storage;

import com.google.gson.JsonObject;
import com.google.protobuf.MessageLite;

public interface PureeStorage {

    public void insert(String type, JsonObject jsonLog);
    public void insert(String type, MessageLite protobuf);
    public JsonRecords selectJson(String type, int logsPerRequest);
    public BinaryRecords selectBinary(String type, int logsPerRequest);
    public JsonRecords selectAllJsonRecords();
    public BinaryRecords selectAllBinaryRecords();
    public void delete(Records records);
    public void truncateBufferedLogs(int maxRecords);
    public void clear();
    public boolean lock();
    public void unlock();
}
