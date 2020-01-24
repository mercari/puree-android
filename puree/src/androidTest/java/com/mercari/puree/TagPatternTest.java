package com.mercari.puree;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TagPatternTest {

    @Test
    public void testMatchesSuccess() {
        assertTrue(TagPattern.fromString("aaa").match("aaa"));
        assertTrue(TagPattern.fromString("aaa.bbb").match("aaa.bbb"));
        assertTrue(TagPattern.fromString("aaa.*").match("aaa.bbb"));
        assertTrue(TagPattern.fromString("aaa.*").match("aaa.ccc"));
        assertTrue(TagPattern.fromString("aaa.*.ccc").match("aaa.bbb.ccc"));
        assertTrue(TagPattern.fromString("*").match("aaa"));
        assertTrue(TagPattern.fromString("*").match("bbb"));
        assertTrue(TagPattern.fromString("*").match(""));
        assertTrue(TagPattern.fromString("a.**").match("a"));
        assertTrue(TagPattern.fromString("a.**").match("a.b"));
        assertTrue(TagPattern.fromString("a.**").match("a.b.c"));
        assertTrue(TagPattern.fromString("a.*.*.c").match("a.b.d.c"));
        assertTrue(TagPattern.fromString("a.**.g").match("a.b.c.d.e.f.g"));
        assertTrue(TagPattern.fromString("a.").match("a"));
        assertTrue(TagPattern.fromString("**").match(""));
        assertTrue(TagPattern.fromString("**").match("``!!``"));
        assertTrue(TagPattern.fromString("tag.**").match("tag"));
        assertTrue(TagPattern.fromString("tag...").match("tag"));
        assertTrue(TagPattern.fromString("tag...").match("tag.."));
    }

    @Test
    public void testMatchesFailure() {
        assertFalse(TagPattern.fromString("bbb").match("aaa"));
        assertFalse(TagPattern.fromString("*").match("aaa.bbb"));
        assertFalse(TagPattern.fromString("aaa.*").match("aaa.bbb.ccc"));
        assertFalse(TagPattern.fromString("aaa.*.ccc").match("aaa.bbb.ddd"));
        assertFalse(TagPattern.fromString("aaa.ccc").match("aaa"));
        assertFalse(TagPattern.fromString("aaa").match("aaa.ccc"));
        assertFalse(TagPattern.fromString("a.**").match("b.c"));
        assertFalse(TagPattern.fromString("a.**.e").match("a.b.c.d"));
        assertFalse(TagPattern.fromString("a.*.*.c").match("a.b.c"));
        assertFalse(TagPattern.fromString("a").match(""));
        assertFalse(TagPattern.fromString("a").match("``!!"));
        assertFalse(TagPattern.fromString("tag...").match("tag..a"));
    }

    @Test
    public void testInvalidPatterns() {
        assertNull(TagPattern.fromString(". . "));
        assertNull(TagPattern.fromString("a..b.c"));
        assertNull(TagPattern.fromString(""));
        assertNull(TagPattern.fromString("."));
        assertNull(TagPattern.fromString(".."));
        assertNull(TagPattern.fromString("a. .c"));
        assertNull(TagPattern.fromString("a.\n.c"));
        assertNull(TagPattern.fromString("a.b.c\n"));
    }
}
