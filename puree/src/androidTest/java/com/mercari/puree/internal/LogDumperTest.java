package com.mercari.puree.internal;

import com.mercari.puree.PureeLog;
import com.mercari.puree.outputs.PureeOutput;
import com.mercari.puree.storage.JsonRecords;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class LogDumperTest {

    @Test
    public void testRunLogDumper() throws Exception {
        LogDumper.out(new JsonRecords());
        LogDumper.out(new HashMap<Class<? extends PureeLog>, List<PureeOutput>>());
    }
}
