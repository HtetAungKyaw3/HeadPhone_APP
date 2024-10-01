/*
 * Created by dotrinh on 6/30/20 8:27 PM
 * Copyright (c) 2020. USE Inc. All rights reserved.
 */

package com.airoha.utapp.sdk.tools;

import android.util.Log;

import com.airoha.utapp.sdk.BuildConfig;
import com.airoha.utapp.sdk.util.Common;

public class LogUtils {
    private final static String TAG    = "LAYFICTONE_LOG";

    /** show log
     * @param message log to Logcat
     */
    public static void LOG(String message) {
        if (BuildConfig.DEBUG)
            Log.i(TAG, message);
    }

    public static void LOG(String TAG, String message) {
        if (BuildConfig.DEBUG)
            Log.i(TAG, message);
    }


    public static void LOG_UI(String message) {
        if (BuildConfig.DEBUG) Log.i(TAG, message);
    }




    public static void ENTER_FUNC_LOG(String message){
        LogUtils.LOG("🔽🔽🔽"+message+" START 🔽🔽🔽");
    }


    public static void ENTER_FUNC_LOG(String TAG, String message){
        LogUtils.LOG(TAG,"🔽🔽🔽"+message+" START 🔽🔽🔽");
    }

    /**
     * Mark into function
     */
    public static void ENTER_FUNC_LOG() {
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();
        StackTraceElement tr = trace[1];
        String s = tr.getClassName();
        s=s.substring(s.lastIndexOf(".")+1) + "::" + tr.getMethodName() + ":" + tr.getLineNumber();
        ENTER_FUNC_LOG(s);
    }
    public static void LEAVE_FUNC_LOG(String message){
        LogUtils.LOG("⏫️️️⏫️️️⏫️️️️️️"+message+" END ⏫️️️⏫️️️⏫️️️");
    }

    public static void LEAVE_FUNC_LOG(String TAG, String message){
        LogUtils.LOG(TAG,"⏫️️️⏫️️️⏫️️️️️️"+message+" END ⏫️️️⏫️️️⏫️️️");
    }
    public static void LEAVE_FUNC_LOG(){
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();
        StackTraceElement tr = trace[1];
        String s = tr.getClassName();
        s=s.substring(s.lastIndexOf(".")+1) + "::" + tr.getMethodName() + ":" + tr.getLineNumber();
        LEAVE_FUNC_LOG(s);
    }

    public static void FUNC_LOG() {
        if (BuildConfig.DEBUG) {
            Throwable stack = new Throwable().fillInStackTrace();
            StackTraceElement[] trace = stack.getStackTrace();
            StackTraceElement tr = trace[1];
            String s = tr.getClassName();
            s=s.substring(s.lastIndexOf(".")+1) + "::" + tr.getMethodName() + ":" + tr.getLineNumber();
            LOG(s);
        }
    }

}

