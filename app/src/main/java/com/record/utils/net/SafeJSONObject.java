package com.record.utils.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SafeJSONObject {
    public static long getLong(JSONObject object, String name, long d) {
        long res = d;
        try {
            return object.getLong(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
    }

    public static int getBoolean(JSONObject object, String name, int d) {
        int res = d;
        try {
            if (object.getBoolean(name)) {
                return 1;
            }
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
    }

    public static boolean getBoolean(JSONObject object, String name, boolean d) {
        boolean res = d;
        try {
            return object.getBoolean(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
    }

    public static int getInt(JSONObject object, String name, int d) {
        int res = d;
        try {
            return object.getInt(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
    }

    public static String getString(JSONObject object, String name, String d) {
        String res = d;
        try {
            return object.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
    }

    public static void putBoolean(JSONObject object, String name, int d) {
        try {
            if (d == 0) {
                object.put(name, false);
                return;
            } else {
                object.put(name, true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

    }

    public static void putBoolean(JSONObject object, String name, Boolean d) {
        try {
            object.put(name, d);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static double getDouble(JSONObject object, String name, double d) {
        double res = d;
        try {
            return object.getDouble(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
    }

    public static JSONObject getJSONObject(JSONObject object, String name, JSONObject d) {
        JSONObject res = d;
        try {
            return object.getJSONObject(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
    }

    public static JSONArray getJSONArray(JSONObject object, String name, JSONArray d) {
        JSONArray res = d;
        try {
            return object.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
    }

    public static Object getObject(JSONObject object, String name, Object d) {
        Object res = d;
        try {
            return object.get(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
    }

    public static boolean putObject(JSONObject object, String name, Object mObject) {
        try {
            object.put(name, mObject);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean transInt(JSONObject fromObject, JSONObject toObject, String name) {
        try {
            int fromValue = fromObject.getInt(name);
            if (fromValue == getInt(toObject, name, -1)) {
                return false;
            }
            toObject.put(name, fromValue);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean transBoolean(JSONObject fromObject, JSONObject toObject, String name) {
        try {
            boolean fromValue = fromObject.getBoolean(name);
            if (fromValue == getBoolean(toObject, name, false)) {
                return false;
            }
            toObject.put(name, fromValue);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean transString(JSONObject fromObject, JSONObject toObject, String name) {
        try {
            String fromValue = fromObject.getString(name);
            if (fromValue.equals(getString(toObject, name, ""))) {
                return false;
            }
            toObject.put(name, fromValue);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean transDouble(JSONObject fromObject, JSONObject toObject, String name) {
        try {
            double fromValue = fromObject.getDouble(name);
            double toValue = getDouble(toObject, name, 0.0d);
            if (fromValue - toValue <= 1.0E-6d && toValue - fromValue <= 1.0E-6d) {
                return false;
            }
            toObject.put(name, fromValue);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean transJsonObject(JSONObject fromObject, JSONObject toObject, String name) {
        try {
            String fromValue = fromObject.getJSONObject(name).toString();
            if (fromValue.equals(toObject.getJSONObject(name).toString())) {
                return false;
            }
            JSONObject object = new JSONObject(fromValue);
            toObject.put(name, fromObject.getJSONObject(name));
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
