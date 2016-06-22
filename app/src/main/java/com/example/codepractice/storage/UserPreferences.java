package com.example.codepractice.storage;

import android.content.Context;
import android.content.SharedPreferences;


public class UserPreferences {

    private SharedPreferences sharedPreferences;
    private static final String PREFERENCE_NAME = "user_preferences";
    private static final String DEFAULT_STRING = "---";


    public UserPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void saveString(final String key, final String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(final String key) {
        return sharedPreferences.getString(key, DEFAULT_STRING);
    }


    public void saveInt(final String key, final int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(final String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void saveBoolean(final String key, final boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(final String key) {
        return sharedPreferences.getBoolean(key,false);
    }


    public void saveFloat(final String key, final float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public Float getFloat(final String key) {
        return sharedPreferences.getFloat(key, 0);
    }

    public void saveLong(final String key, final long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(final String key) {
        return sharedPreferences.getLong(key, 0l);
    }
}
