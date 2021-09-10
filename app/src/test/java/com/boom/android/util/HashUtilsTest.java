package com.boom.android.util;

import com.boom.android.ui.adapter.repo.Resolution;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void t2(){
        Resolution resolution = new Resolution(1,2,"10:3");
        System.out.println(String.valueOf(resolution));

        Resolution resolution2 = new Resolution("1x2_10:3");
        System.out.println(String.valueOf(resolution2));
    }
}