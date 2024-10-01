package com.airoha.utapp.sdk.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.airoha.sdk.AirohaConnector;
import com.airoha.utapp.sdk.BuildConfig;
import com.airoha.utapp.sdk.bluetooth.BluetoothPermissionAdapter;
import com.airoha.utapp.sdk.constant.SharedKey;
import com.airoha.utapp.sdk.databinding.ActivitySplashBinding;
import com.airoha.utapp.sdk.tools.LogUtils;
import com.airoha.utapp.sdk.ui.base.BaseActivity;
import com.airoha.utapp.sdk.ui.main.LayfictoneMainActivity;
import com.airoha.utapp.sdk.ui.onboarding.OnBoardingActivity;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> {

    private String TAG = "Splash Activity";
    public static final String SHARED_KEY_ONBOARDING = "SHARED_KEY_ONBOARDING";
    private final static long SPLASH_SHOWING_TIME = 1500;

    public static boolean hasDeviceConnected = false;

    private AirohaConnector mAirohaConnector;

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.tvVersionName.setText("Ver " + BuildConfig.VERSION_NAME);
        getShared().edit().putBoolean(SharedKey.SHOW_HOME, false).apply();
        LogUtils.ENTER_FUNC_LOG();
        BluetoothPermissionAdapter.shared().init(this);
        if (!BluetoothPermissionAdapter.shared().isPermissionFullGranted()) {
            LogUtils.LOG_UI("not permission");
        }
        else {
            LogUtils.LOG_UI(" permission");
            initBluetoothWithPermissionOK();
        }

        postDelay(SPLASH_SHOWING_TIME, () -> {
            if (isAlreadyOnboarding()) {
                startActivity(new Intent(SplashActivity.this, LayfictoneMainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
            }
            finish();
        });

    }



    @Override
    protected void initBinding() {
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
    }

    private boolean isAlreadyOnboarding() {
        return getShared().getBoolean(SHARED_KEY_ONBOARDING, false);
    }

}
