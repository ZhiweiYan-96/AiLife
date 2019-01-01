package com.record.utils.preference;

public interface Config {
    void clear();

    void close();

    boolean getBoolean(int i, Boolean bool);

    boolean getBoolean(String str, Boolean bool);

    byte[] getByte(int i, byte[] bArr);

    byte[] getByte(String str, byte[] bArr);

    <T> T getConfig(Class<T> cls);

    double getDouble(int i, Double d);

    double getDouble(String str, Double d);

    float getFloat(int i, Float f);

    float getFloat(String str, Float f);

    int getInt(int i, int i2);

    int getInt(String str, int i);

    long getLong(int i, Long l);

    long getLong(String str, Long l);

    short getShort(int i, Short sh);

    short getShort(String str, Short sh);

    String getString(int i, String str);

    String getString(String str, String str2);

    boolean isClosed();

    Boolean isLoadConfig();

    void loadConfig();

    void open();

    void remove(String str);

    void remove(String... strArr);

    void setBoolean(int i, Boolean bool);

    void setBoolean(String str, Boolean bool);

    void setByte(int i, byte[] bArr);

    void setByte(String str, byte[] bArr);

    void setConfig(Object obj);

    void setDouble(int i, double d);

    void setDouble(String str, double d);

    void setFloat(int i, float f);

    void setFloat(String str, float f);

    void setInt(int i, int i2);

    void setInt(String str, int i);

    void setLong(int i, long j);

    void setLong(String str, long j);

    void setShort(int i, short s);

    void setShort(String str, short s);

    void setString(int i, String str);

    void setString(String str, String str2);
}
