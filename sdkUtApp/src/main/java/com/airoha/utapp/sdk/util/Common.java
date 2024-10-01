package com.airoha.utapp.sdk.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.airoha.utapp.sdk.BuildConfig;

import java.util.Timer;
import java.util.TimerTask;

public class Common {

    public static final int REQUEST_CODE_FOR_ACCESS_COARSE_LOCATION = 1000;
    public static final int REQUEST_CODE_BT = 1001;
    public static final int REQUEST_LOCATION_CODE = 1002;
    public static final String DEVICE_CONNECTED = "MAC_CONNECTED";
    public static void RunAfter(Runnable run, long delay){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                run.run();
            }
        }, delay);
    }
    private static final Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    public static void runOnUIThreadIfNeeded(Runnable runnable)
    {
        // current thread is main thread
        if (Looper.myLooper() == Looper.getMainLooper()){
            runnable.run();
        }else {
            uiThreadHandler.post(runnable);
        }
    }

}
