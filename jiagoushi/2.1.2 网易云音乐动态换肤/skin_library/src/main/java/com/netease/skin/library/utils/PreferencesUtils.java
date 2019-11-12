/**
 * Project Name:SimonFramework
 * File Name:PreferencesUtils.java
 * Package Name:com.simon.framework.utils
 * Date:2016-5-19 下午2:24:22
 * Copyright (c) 2016, simon@cmonbaby.com All Rights Reserved.
 */
package com.netease.skin.library.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * <strong>自定义Preferences存储器工具类</strong>
 * <ul>
 * <strong>Preference Name</strong>
 * <li>you can change preference name by {@link #PREFERENCE_NAME}</li>
 * </ul>
 * <ul>
 * <strong>Put Value</strong>
 * <li>put string {@link #putString(Context, String, String)}</li>
 * <li>put int {@link #putInt(Context, String, int)}</li>
 * <li>put long {@link #putLong(Context, String, long)}</li>
 * <li>put float {@link #putFloat(Context, String, float)}</li>
 * <li>put boolean {@link #putBoolean(Context, String, boolean)}</li>
 * </ul>
 * <ul>
 * <strong>Get Value</strong>
 * <li>get string {@link #getString(Context, String)}, {@link #getString(Context, String, String)}</li>
 * <li>get int {@link #getInt(Context, String)}, {@link #getInt(Context, String, int)}</li>
 * <li>get long {@link #getLong(Context, String)}, {@link #getLong(Context, String, long)}</li>
 * <li>get float {@link #getFloat(Context, String)}, {@link #getFloat(Context, String, float)}</li>
 * <li>get boolean {@link #getBoolean(Context, String)}, {@link #getBoolean(Context, String, boolean)}</li>
 * </ul>
 * <ul>
 * <strong>Other Opration</strong>
 * <li>remove String...keys {@link #removeSomeThing(Context, String...)}</li>
 * <li>clearSP {@link #clearSP(Context)}</li>
 * <li>contains {@link #contains(Context, String)}</li>
 * <li>getAll {@link #getAll(Context)}</li>
 * </ul>
 * <ul>
 * <strong>Important Usage</strong>
 * <li>put {@link #put(Context, String, Object)}</li>
 * <li>get {@link #get(Context, String, Object)}</li>
 * </ul>
 *
 * @Title PreferencesUtils
 * @Package com.cmonbaby.framework.utils
 * @Description 自定义Preferences存储器工具类
 * @author simon
 * @date 2016-5-19 下午2:24:22
 * @since JDK1.8 SDK6.0.1
 * @version V2.3.4
 */
public class PreferencesUtils {

    public static String PREFERENCE_NAME = "com.netease.skin";

    /**
     * put string preferences
     *
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or "". Throws ClassCastException if there is a preference with this
     *         name that is not a string
     * @see #getString(Context, String, String)
     */
    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a string
     */
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     * put int preferences
     *
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     *         name that is not a int
     * @see #getInt(Context, String, int)
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a int
     */
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    /**
     * put long preferences
     *
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     *         name that is not a long
     * @see #getLong(Context, String, long)
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a long
     */
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    /**
     * put float preferences
     *
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putFloat(Context context, String key, float value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     *         name that is not a float
     * @see #getFloat(Context, String, float)
     */
    public static float getFloat(Context context, String key) {
        return getFloat(context, key, -1);
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a float
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * put boolean preferences
     *
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * get boolean preferences, default is false
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws ClassCastException if there is a preference with this
     *         name that is not a boolean
     * @see #getBoolean(Context, String, boolean)
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * get boolean preferences
     *
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a boolean
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    /**
     * get all key and values
     * @param context
     * @return Map of all key and values
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getAll();
    }

    /**
     * check sp's key already exists if save
     * @param context
     * @param key The name of the preference to retrieve
     * @return true is exists
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.contains(key);
    }

    /**
     * remove SomeThing from preferences
     *
     * @param context context
     * @param keys keys The name of the preference to retrieve
     */
    public static boolean removeSomeThing(Context context, String...keys) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (keys == null)
            return false;
        for (String k : keys) {
            editor.remove(k);
        }
        editor.commit();
        return true;
    }

    /**
     * clear all params from preferences
     * @param context context
     * @return True if the clear values were successfully.
     */
    public static boolean clearSP(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        return true;
    }

    /**
     * Methods to save data, we need to get the specific type of the preservation of the data,
     * and then call different methods based on type
     *
     * @param context context
     * @param key The need to store the key
     * @param object The need to store the value
     */
    public static void put(Context context, String key, Object object) {
        if (object instanceof String) {
            putString(context, key, (String) object);
        } else if (object instanceof Integer) {
            putInt(context, key, (Integer) object);
        } else if (object instanceof Boolean) {
            putBoolean(context, key, (Boolean) object);
        } else if (object instanceof Float) {
            putFloat(context, key, (Float) object);
        } else if (object instanceof Long) {
            putLong(context, key, (Long) object);
        } else {
            putString(context, key, object.toString());
        }
    }

    /**
     * Methods to save the data, we get the specific type of the data saved according to the default values,
     * and then call the method to obtain the value of the method
     *
     * @param context context
     * @param key The need to obtain the key
     * @param defaultObject if obtain nothing, input default value
     * @return if nothing return the result was null
     */
    public static Object get(Context context, String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return getString(context, key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return getInt(context, key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return getBoolean(context, key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return getFloat(context, key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return getLong(context, key, (Long) defaultObject);
        }
        return null;
    }

}

