package com.record.utils.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.record.utils.Val;
import java.lang.reflect.Field;

public class PreferenceConfig implements Config {
    private static Config mPreferenceConfig;
    private Boolean isLoad = Boolean.valueOf(false);
    private Context mContext;
    private Editor mEditor = null;
    private SharedPreferences mSharedPreferences;

    private PreferenceConfig(Context context) {
        this.mContext = context;
    }

    public static Config getPreferenceConfig(Context context) {
        if (mPreferenceConfig == null) {
            mPreferenceConfig = new PreferenceConfig(context);
        }
        return mPreferenceConfig;
    }

    public void getTomatoStudy() {
    }

    public void loadConfig() {
        try {
            this.mSharedPreferences = this.mContext.getSharedPreferences(Val.CONFIGURE_NAME, 0);
            this.mEditor = this.mSharedPreferences.edit();
            this.isLoad = Boolean.valueOf(true);
        } catch (Exception e) {
            this.isLoad = Boolean.valueOf(false);
        }
    }

    public Boolean isLoadConfig() {
        return this.isLoad;
    }

    public void open() {
    }

    public void close() {
    }

    public boolean isClosed() {
        return false;
    }

    public void setString(String key, String value) {
        this.mEditor.putString(key, value);
        this.mEditor.commit();
    }

    public void setInt(String key, int value) {
        this.mEditor.putInt(key, value);
        this.mEditor.commit();
    }

    public void setBoolean(String key, Boolean value) {
        this.mEditor.putBoolean(key, value.booleanValue());
        this.mEditor.commit();
    }

    public void setByte(String key, byte[] value) {
        setString(key, String.valueOf(value));
    }

    public void setShort(String key, short value) {
        setString(key, String.valueOf(value));
    }

    public void setLong(String key, long value) {
        this.mEditor.putLong(key, value);
        this.mEditor.commit();
    }

    public void setFloat(String key, float value) {
        this.mEditor.putFloat(key, value);
        this.mEditor.commit();
    }

    public void setDouble(String key, double value) {
        setString(key, String.valueOf(value));
    }

    public void setString(int resID, String value) {
        setString(this.mContext.getString(resID), value);
    }

    public void setInt(int resID, int value) {
        setInt(this.mContext.getString(resID), value);
    }

    public void setBoolean(int resID, Boolean value) {
        setBoolean(this.mContext.getString(resID), value);
    }

    public void setByte(int resID, byte[] value) {
        setByte(this.mContext.getString(resID), value);
    }

    public void setShort(int resID, short value) {
        setShort(this.mContext.getString(resID), value);
    }

    public void setLong(int resID, long value) {
        setLong(this.mContext.getString(resID), value);
    }

    public void setFloat(int resID, float value) {
        setFloat(this.mContext.getString(resID), value);
    }

    public void setDouble(int resID, double value) {
        setDouble(this.mContext.getString(resID), value);
    }

    public String getString(String key, String defaultValue) {
        return this.mSharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return this.mSharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, Boolean defaultValue) {
        return this.mSharedPreferences.getBoolean(key, defaultValue.booleanValue());
    }

    public byte[] getByte(String key, byte[] defaultValue) {
        try {
            return getString(key, "").getBytes();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public short getShort(String key, Short defaultValue) {
        try {
            return Short.valueOf(getString(key, "")).shortValue();
        } catch (Exception e) {
            return defaultValue.shortValue();
        }
    }

    public long getLong(String key, Long defaultValue) {
        return this.mSharedPreferences.getLong(key, defaultValue.longValue());
    }

    public float getFloat(String key, Float defaultValue) {
        return this.mSharedPreferences.getFloat(key, defaultValue.floatValue());
    }

    public double getDouble(String key, Double defaultValue) {
        try {
            return Double.valueOf(getString(key, "")).doubleValue();
        } catch (Exception e) {
            return defaultValue.doubleValue();
        }
    }

    public String getString(int resID, String defaultValue) {
        return getString(this.mContext.getString(resID), defaultValue);
    }

    public int getInt(int resID, int defaultValue) {
        return getInt(this.mContext.getString(resID), defaultValue);
    }

    public boolean getBoolean(int resID, Boolean defaultValue) {
        return getBoolean(this.mContext.getString(resID), defaultValue);
    }

    public byte[] getByte(int resID, byte[] defaultValue) {
        return getByte(this.mContext.getString(resID), defaultValue);
    }

    public short getShort(int resID, Short defaultValue) {
        return getShort(this.mContext.getString(resID), defaultValue);
    }

    public long getLong(int resID, Long defaultValue) {
        return getLong(this.mContext.getString(resID), defaultValue);
    }

    public float getFloat(int resID, Float defaultValue) {
        return getFloat(this.mContext.getString(resID), defaultValue);
    }

    public double getDouble(int resID, Double defaultValue) {
        return getDouble(this.mContext.getString(resID), defaultValue);
    }

    private void setValue(Field field, String columnName, Object entity) {
        try {
            Class<?> clazz = field.getType();
            if (clazz.equals(String.class)) {
                setString(columnName, (String) field.get(entity));
            } else if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE)) {
                setInt(columnName, ((Integer) field.get(entity)).intValue());
            } else if (clazz.equals(Float.class) || clazz.equals(Float.TYPE)) {
                setFloat(columnName, ((Float) field.get(entity)).floatValue());
            } else if (clazz.equals(Double.class) || clazz.equals(Double.TYPE)) {
                setDouble(columnName, ((Double) field.get(entity)).doubleValue());
            } else if (clazz.equals(Short.class) || clazz.equals(Short.class)) {
                setShort(columnName, ((Short) field.get(entity)).shortValue());
            } else if (clazz.equals(Long.class) || clazz.equals(Long.TYPE)) {
                setLong(columnName, ((Long) field.get(entity)).longValue());
            } else if (clazz.equals(Boolean.class)) {
                setBoolean(columnName, (Boolean) field.get(entity));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

    private <T> void getValue(Field field, String columnName, T entity) {
        try {
            Class<?> clazz = field.getType();
            if (clazz.equals(String.class)) {
                field.set(entity, getString(columnName, ""));
            } else if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE)) {
                field.set(entity, Integer.valueOf(getInt(columnName, 0)));
            } else if (clazz.equals(Float.class) || clazz.equals(Float.TYPE)) {
                field.set(entity, Float.valueOf(getFloat(columnName, Float.valueOf(0.0f))));
            } else if (clazz.equals(Double.class) || clazz.equals(Double.TYPE)) {
                field.set(entity, Double.valueOf(getDouble(columnName, Double.valueOf(0.0d))));
            } else if (clazz.equals(Short.class) || clazz.equals(Short.class)) {
                field.set(entity, Short.valueOf(getShort(columnName, Short.valueOf((short) 0))));
            } else if (clazz.equals(Long.class) || clazz.equals(Long.TYPE)) {
                field.set(entity, Long.valueOf(getLong(columnName, Long.valueOf(0))));
            } else if (clazz.equals(Byte.class) || clazz.equals(Byte.TYPE)) {
                field.set(entity, getByte(columnName, new byte[8]));
            } else if (clazz.equals(Boolean.class)) {
                field.set(entity, Boolean.valueOf(getBoolean(columnName, Boolean.valueOf(false))));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

    public void remove(String key) {
        this.mEditor.remove(key);
        this.mEditor.commit();
    }

    public void remove(String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    public void clear() {
        this.mEditor.clear();
        this.mEditor.commit();
    }

    public void setConfig(Object entity) {
    }

    public <T> T getConfig(Class<T> cls) {
        return null;
    }
}
