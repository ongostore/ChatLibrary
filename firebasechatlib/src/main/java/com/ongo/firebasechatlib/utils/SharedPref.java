package com.ongo.firebasechatlib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SharedPref {
    private static SharedPreferences mSharedPref;
    static final String shName = "TextView";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public static void setFirstTimeLaunch(boolean isFirstTime) {
        SharedPreferences.Editor prefsEditors = mSharedPref.edit();
        prefsEditors.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        prefsEditors.commit();
    }

    public static boolean isFirstTimeLaunch() {
        SharedPreferences.Editor edit = mSharedPref.edit();
        return mSharedPref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(OnGoConstants.PREF_NAME, Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }


    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static void writeArry(String key, ArrayList<String> array) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        //Set the values
        Set<String> set = new HashSet<String>();
        set.addAll(array);
        prefsEditor.putStringSet(key, set);
        prefsEditor.commit();
    }

    public static Set<String> readArry(String key) {
        return mSharedPref.getStringSet(key, null);
    }

    public static boolean SignOut(Context context) {

        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.clear();
        return edit.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static int read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, int value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    public static boolean setPrivacyJson(Context context, String json) {
        SharedPreferences sh = context.getSharedPreferences(shName, 0);
        SharedPreferences.Editor edit = sh.edit();
        edit.putString("PRIVACY_JSON", json);
        return edit.commit();
    }

    public static String getPrivacyJson(Context context) {
        SharedPreferences sh = context.getSharedPreferences(shName, 0);
        return sh.getString("PRIVACY_JSON", "");
    }
}