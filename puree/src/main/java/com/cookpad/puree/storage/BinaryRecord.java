package com.cookpad.puree.storage;

import android.util.Base64;

public class BinaryRecord {

    private final int id;

    private final String type;

    private final byte[] bytes;

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public byte[] getBytes() { return bytes; }

    public BinaryRecord(int id, String type, byte[] bytes) {
        this.id = id;
        this.type = type;
        this.bytes = bytes;
    }
}
