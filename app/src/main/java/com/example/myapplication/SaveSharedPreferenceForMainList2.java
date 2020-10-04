package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreferenceForMainList2
{
    static final String PREF_USER_NAME= "initial";
    static final String INT_STRING = "initialInteger";
    static final String INT_STACK = "initialStack";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);

        editor.commit();
    }
    public static void setIntegerCount(Context ctx,int c)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(INT_STRING, c);
        editor.commit();
    }
    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
    public static int getIntegerCount(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(INT_STRING,0);
    }



}