package com.boom.android.log;

import android.util.Log;

import com.boom.utils.StringUtils;

public class Dogger implements IDoggerConst {
    private static final String TMP = "TMP";

    public static final int PROMOTION_NONE = 0;
    public static final int PROMOTION_LOW = 1;
    public static final int PROMOTION_MID = 2;
    public static final int PROMOTION_HIGH = 3;
    public static final int PROMOTION_TOP = 4;

    private static String createBody(String content, String className, String methodName){
        StringBuffer sb = new StringBuffer();
        sb.append("[").append(className).append(":").append(methodName).append("]");
        if(!StringUtils.isEmpty(content)){
            sb.append(content);
        }
        return sb.toString();
    }

    public static void e(String module, String content, String className, String methodName){
        String msg = createBody(content,className,methodName);
        Log.e(module,msg);
    }

    public static void w(String module, String content, String className, String methodName){
        String msg = createBody(content,className,methodName);
        Log.w(module,msg);
    }

    public static void i(String module, String content, String className, String methodName){
        String msg = createBody(content,className,methodName);
        Log.i(module,msg);
    }

    public static void d(String module, String content, String className, String methodName){
        String msg = createBody(content,className,methodName);
        Log.d(module,msg);
    }

    public static void v(String module, String content, String className, String methodName){
        String msg = createBody(content,className,methodName);
        Log.v(module,msg);
    }

    public static void e(String module, String content, String className, String methodName,Throwable throwable){
        String msg = createBody(content,className,methodName);
        Log.e(module,msg,throwable);
    }

    public static void w(String module, String content, String className, String methodName,Throwable throwable){
        String msg = createBody(content,className,methodName);
        Log.w(module,msg,throwable);
    }

    public static void i(String module, String content, String className, String methodName,Throwable throwable){
        String msg = createBody(content,className,methodName);
        Log.i(module,msg,throwable);
    }

    public static void d(String module, String content, String className, String methodName,Throwable throwable){
        String msg = createBody(content,className,methodName);
        Log.d(module,msg,throwable);
    }

    public static void v(String module, String content, String className, String methodName,Throwable throwable){
        String msg = createBody(content,className,methodName);
        Log.v(module,msg,throwable);
    }

}
