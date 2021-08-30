package com.boom.android.util;

import com.boom.android.BoomApplication;

import junit.framework.TestCase;

import org.junit.Test;

public class BoomHelperTest extends TestCase {

    @Test
    public void test01(){
        System.out.println(BoomHelper.getApplicationName());
    }

}