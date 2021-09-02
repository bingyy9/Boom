package com.boom.android.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HashUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getSHA256StrJava() {
        System.out.println(HashUtils.getSHA256StrJava("bing@c.com"));
    }
}