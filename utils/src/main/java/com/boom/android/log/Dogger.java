package com.boom.android.log;

import com.boom.utils.StringUtils;

public class Dogger implements IDoggerConst {
    private static final String TMP = "TMP";

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    private static String createBody(String module, String content, String className, String methodName){
        StringBuffer sb = new StringBuffer();
        sb.append("[").append(className).append(":").append(methodName).append("]");
        if(!StringUtils.isEmpty(content)){
            sb.append(content);
        }
        return sb.toString();
    }

    public static void e(String module, String content, String className, String methodName){
        log(ERROR, module, content, className, methodName, null);
    }

    public static void w(String module, String content, String className, String methodName){
        log(WARN, module, content, className, methodName, null);
    }

    public static void i(String module, String content, String className, String methodName){
        log(INFO, module, content, className, methodName, null);
    }

    public static void d(String module, String content, String className, String methodName){
        log(DEBUG, module, content, className, methodName, null);
    }

    public static void v(String module, String content, String className, String methodName){
        log(VERBOSE, module, content, className, methodName, null);
    }

    public static void e(String module, String content, String className, String methodName,Throwable throwable){
        log(ERROR, module, content, className, methodName, throwable);
    }

    public static void w(String module, String content, String className, String methodName,Throwable throwable){
        log(WARN, module, content, className, methodName, throwable);
    }

    public static void i(String module, String content, String className, String methodName,Throwable throwable){
        log(INFO, module, content, className, methodName, throwable);
    }

    public static void d(String module, String content, String className, String methodName,Throwable throwable){
        log(DEBUG, module, content, className, methodName, throwable);
    }

    public static void v(String module, String content, String className, String methodName,Throwable throwable){
        log(VERBOSE, module, content, className, methodName, throwable);
    }

    private static void log(int level, String module, String content, String className, String methodName,Throwable throwable){
        String msg = createBody(module, content,className,methodName);
        if((FactoryMgr.iPlatformFactory == null) || (FactoryMgr.iPlatformFactory.getLog() == null)){
            return;
        }
        FactoryMgr.iPlatformFactory.getLog().dump(level, module, msg, throwable);
    }

}
