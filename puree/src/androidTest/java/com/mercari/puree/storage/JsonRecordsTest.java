package com.mercari.puree.storage;

import com.google.gson.JsonObject;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class JsonRecordsTest {
    @Test
    public void getIdsAsString() {
        {
            JsonRecords jsonRecords = new JsonRecords();
            assertThat(jsonRecords.getIdsAsString(), is(""));
        }
        {
            JsonRecords jsonRecords = new JsonRecords();
            jsonRecords.add(new JsonRecord(0, "logcat", new JsonObject()));
            assertThat(jsonRecords.getIdsAsString(), is("0"));
        }
        {
            JsonRecords jsonRecords = new JsonRecords();
            for (int i = 0; i < 3; i++) {
                jsonRecords.add(new JsonRecord(i, "logcat", new JsonObject()));
            }
            assertThat(jsonRecords.getIdsAsString(), is("0,1,2"));
        }
    }

    @Test
    public void getJsonLogs() {
        {
            JsonRecords jsonRecords = new JsonRecords();
            assertThat(jsonRecords.getJsonLogs().size(), Matchers.is(0));
        }
        {
            JsonRecords jsonRecords = new JsonRecords();
            for (int i = 0; i < 3; i++) {
                jsonRecords.add(new JsonRecord(i, "logcat", new JsonObject()));
            }
            assertThat(jsonRecords.getJsonLogs().size(), is(3));
        }
    }

}
