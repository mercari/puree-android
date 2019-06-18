package com.mercari.puree.storage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mercari.puree.internal.ProcessName;
import com.google.protobuf.MessageLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PureeSQLiteStorage extends SQLiteOpenHelper implements PureeStorage {

    private static final String DATABASE_NAME = "puree.new.db";

    private static final String TABLE_NAME = "logs";

    private static final String COLUMN_NAME_FORMAT = "format";

    private static final String FORMAT_JSON = "j";

    private static final String FORMAT_BINARY = "b";

    private static final String COLUMN_NAME_TYPE = "type";

    private static final String COLUMN_NAME_LOG = "log";

    private static final int DATABASE_VERSION = 1;

    private final JsonParser jsonParser = new JsonParser();

    private final SQLiteDatabase db;

    private final AtomicBoolean lock = new AtomicBoolean(false);

    static String databaseName(Context context) {
        // do not share the database file in multi processes
        String processName = ProcessName.getAndroidProcessName(context);
        if (TextUtils.isEmpty(processName)) {
            return DATABASE_NAME;
        } else {
            return processName + "." + DATABASE_NAME;
        }
    }

    public PureeSQLiteStorage(Context context) {
        super(context, databaseName(context), null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    public void insert(String outputType, JsonObject jsonLog) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_FORMAT, FORMAT_JSON);
        contentValues.put(COLUMN_NAME_TYPE, outputType);
        contentValues.put(COLUMN_NAME_LOG, jsonLog.toString());
        db.insert(TABLE_NAME, null, contentValues);
    }

    public void insert(String outputType, MessageLite protobuf) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_FORMAT, FORMAT_BINARY);
        contentValues.put(COLUMN_NAME_TYPE, outputType);
        // TODO: Should store bytes in a BLOB rather than base-64 encoded byte string
        contentValues.put(COLUMN_NAME_LOG, Base64.encodeToString(protobuf.toByteArray(),
                Base64.DEFAULT));
        db.insert(TABLE_NAME, null, contentValues);
    }

    public JsonRecords selectJson(String type, int logsPerRequest) {
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME_TYPE + " = ?" +
                " ORDER BY id ASC" +
                " LIMIT " + logsPerRequest;
        Cursor cursor = db.rawQuery(query, new String[]{type});

        try {
            return jsonRecordsFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public BinaryRecords selectBinary(String type, int logsPerRequest) {
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME_TYPE + " = ?" +
                " ORDER BY id ASC" +
                " LIMIT " + logsPerRequest;
        Cursor cursor = db.rawQuery(query, new String[]{type});

        try {
            return binaryRecordsFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    @Override
    public JsonRecords selectAllJsonRecords() {
        String query = "SELECT id, type, log FROM " + TABLE_NAME +
                " WHERE format='" + FORMAT_JSON + "' ORDER BY id ASC";
        Cursor cursor = db.rawQuery(query, null);

        try {
            return jsonRecordsFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public BinaryRecords selectAllBinaryRecords() {
        String query = "SELECT id, type, log FROM " + TABLE_NAME +
                " WHERE format='" + FORMAT_BINARY + "' ORDER BY id ASC";
        Cursor cursor = db.rawQuery(query, null);

        try {
            return binaryRecordsFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    private JsonRecords jsonRecordsFromCursor(Cursor cursor) {
        JsonRecords jsonRecords = new JsonRecords();
        while (cursor.moveToNext()) {
            JsonRecord jsonRecord = buildJsonRecord(cursor);
            jsonRecords.add(jsonRecord);
        }
        return jsonRecords;
    }

    private JsonRecord buildJsonRecord(Cursor cursor) {
        return new JsonRecord(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE)),
                parseJsonString(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOG))));

    }

    private BinaryRecords binaryRecordsFromCursor(Cursor cursor) {
        BinaryRecords binaryRecords = new BinaryRecords();
        while (cursor.moveToNext()) {
            BinaryRecord binaryRecord = buildBinaryRecord(cursor);
            binaryRecords.add(binaryRecord);
        }
        return binaryRecords;
    }

    private BinaryRecord buildBinaryRecord(Cursor cursor) {
        // TODO: Probably use blob in a separate table rather than base64 encoding.
        String base64 = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOG));
        return new BinaryRecord(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE)),
                Base64.decode(base64, Base64.DEFAULT));
    }
    private int getRecordCount() {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    private JsonObject parseJsonString(String jsonString) {
        return (JsonObject) jsonParser.parse(jsonString);
    }


    @Override
    public void delete(Records records) {
        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE id IN (" + records.getIdsAsString() + ")";
        db.execSQL(query);
    }

    @Override
    public void truncateBufferedLogs(int maxRecords) {
        int recordSize = getRecordCount();
        if (recordSize > maxRecords) {
            String query = "DELETE FROM " + TABLE_NAME +
                    " WHERE id IN ( SELECT id FROM " + TABLE_NAME +
                    " ORDER BY id ASC LIMIT " + String.valueOf(recordSize - maxRecords) +
                    ")";
            db.execSQL(query);
        }
    }

    @Override
    public void clear() {
        String query = "DELETE FROM " + TABLE_NAME;
        db.execSQL(query);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_TYPE + " TEXT," +
                COLUMN_NAME_FORMAT + " TEXT," +
                COLUMN_NAME_LOG + " TEXT" +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("PureeDbHelper", "unexpected onUpgrade(db, " + oldVersion + ", " + newVersion + ")");
    }

    @Override
    protected void finalize() throws Throwable {
        db.close();
        super.finalize();
    }

    @Override
    public boolean lock() {
        return lock.compareAndSet(false, true);
    }

    @Override
    public void unlock() {
        lock.set(false);
    }
}
