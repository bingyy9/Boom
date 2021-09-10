package com.boom.utils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtilsTest {

    @Test
    public void t1(){
        String a = "11000x1123_31:12123";
        String regex = "([\\d]+)x([\\d]+)_([\\d+:]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(a);
        while(matcher.find()) {
            System.out.println(matcher.group(0));
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3));
        }
    }

}