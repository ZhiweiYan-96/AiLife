package com.record.utils;

public class DbPassword {
    public static native String getAuthPassword();

    public static native String getPassword();

    public static native String getUndefine1();

    public static native String getUndefine2();

    public static native String getUndefine3();

    public static native String getUndefine4();

    public static native String getUndefine5();

    static {
        System.loadLibrary("dbpw");
    }
}
