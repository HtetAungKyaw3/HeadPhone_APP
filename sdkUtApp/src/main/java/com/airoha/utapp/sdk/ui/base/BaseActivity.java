package com.airoha.utapp.sdk.ui.base;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.airoha.sdk.AirohaConnector;
import com.airoha.utapp.sdk.BuildConfig;
import com.airoha.utapp.sdk.R;
import com.airoha.utapp.sdk.bluetooth.BluetoothPermissionAdapter;
import com.airoha.utapp.sdk.tools.LogUtils;
import com.airoha.utapp.sdk.ui.connecting.FragmentConnecting1stInstruction;
import com.airoha.utapp.sdk.ui.splash.SplashActivity;
import com.airoha.utapp.sdk.util.Common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {

    protected T binding;

    protected Handler handler;
    protected View mLoadingView;
    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothA2dp mBluetoothProfileA2DP;
    protected AirohaConnector mAirohaConnector;
    protected BluetoothDevice bluetoothDevice;
    protected Set<String> connectedDeviceList;

    protected boolean hasConnectedDevice = false;
    protected boolean con = false;

    protected AlertDialog dialogNotify;

    protected boolean isShowMainActivity = false;

    protected static boolean isConnectedHeadphone = false;

    protected boolean permission = false;

    private static int retryCountConnect = 0;

    protected enum AppState {
        BACKGROUND,
        RESUME
    }

    protected enum BluetoothState {
        BT_ON,
        BT_OFF,
        IDLE
    }

    protected enum DeviceState {
        CONNECT,
        DISCONNECT,
        IDLE
    }

   protected DeviceState deviceState;

    protected AppState appState;



    protected BluetoothState bluetoothState = BluetoothState.IDLE;

    private Set<String> connectedDevice = new HashSet<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        setContentView(binding.getRoot());
        handler = new Handler();
        mLoadingView = getLoadingView();
        initStatusBarMode();
        initView();
    }
    @Override
    public void onBackPressed() {
        if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE) {
            return;
        }
        super.onBackPressed();
    }

    public void addFragment(int containerId, BaseFragment fragment, boolean isAddToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(containerId, fragment, fragment.getClass().getName());
        if (isAddToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commit();
    }

    public void replaceFragment(int containerId, BaseFragment fragment, boolean isAddToBackStack) {
        if (!getSupportFragmentManager().isStateSaved()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, fragment.getClass().getName());
            if (isAddToBackStack) {
                transaction.addToBackStack(fragment.getClass().getName());
            }
            transaction.commit();
        }
        else {

        }
    }

    public BaseFragment getCurrentFragment(int containerId) {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(containerId);
    }

    public void showProgressDialog() {
        runOnUiThread(() -> {
            if (mLoadingView != null) {
                mLoadingView.setVisibility(View.VISIBLE);
            }
        });

    }

    public void hideProgressDialog() {
        runOnUiThread(() -> {
            if (mLoadingView != null) {
                mLoadingView.setVisibility(View.GONE);
            }
        });

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        } else {
            result = getResources().getDimensionPixelSize(R.dimen.defaultStatusBarHeight);
        }
        return result;
    }

    public SharedPreferences getShared() {
        return getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    public void openSocialLink(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public String LoadConnectTarget(String key) {
        //Instance 取得
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //KEYから値取得（読めなかったらNotSelectedの値となる）
        return sharedPreferences.getString(key,"");
    }

    protected void initView() {

    }
    public void initBluetoothWithPermissionOK() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.getProfileProxy(this, new BluetoothProfileListener(), BluetoothProfile.A2DP);
    }

    protected View getLoadingView() {
        return binding.getRoot().findViewById(R.id.rlLoading);
    }

    protected void marginToolbarWithStatusBarHeight(View toolbar) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        lp.topMargin = getStatusBarHeight();
        toolbar.requestLayout();
    }

    public void SaveConnectTarget(String macAddr, String connTarget) {
        //Instance 取得
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
      //  connectedDevice.add(macAddr);
        //KEYに値格納
        editor.putString(connTarget, macAddr);
        //保存
        editor.apply();
    }




    protected abstract void initBinding();

    protected boolean isStatusBarLightMode() {
        return true;
    }

    protected void postDelay(long delay, Runnable runnable) {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
    }


    @SuppressLint("MissingPermission")
    public boolean requestBT() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Common.REQUEST_CODE_BT);
            return true;
        }
        return true;
    }

    public void appRequestPermisson(Runnable onOK) {
        BluetoothPermissionAdapter.shared().init(this);
        BluetoothPermissionAdapter.shared().request_location_permission(true, withResult -> {
            if (withResult == 0) {
                onOK.run();
                onRequestPermissionsResult(Common.REQUEST_CODE_FOR_ACCESS_COARSE_LOCATION, new String[]{},
                        new int[]{PackageManager.PERMISSION_GRANTED});
            }
            else {
                onRequestPermissionsResult(Common.REQUEST_CODE_FOR_ACCESS_COARSE_LOCATION, new String[]{},
                        new int[]{PackageManager.PERMISSION_DENIED});
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Common.REQUEST_CODE_FOR_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtils.LOG_UI("Permision OK");
                    if (LoadConnectTarget(Common.DEVICE_CONNECTED) != null) {
                        initBluetoothWithPermissionOK();
                    }

                } else {

                    LogUtils.LOG_UI("No Permision");

                }
                break;
            case Common.REQUEST_CODE_BT:
                LogUtils.LOG_UI("Permision Bluetooth OK");
                break;
        }
    }

    @SuppressWarnings({"MissingPermission"})
    protected boolean isA2dpConnected(BluetoothDevice device) {
        if (mBluetoothProfileA2DP == null) {
            LogUtils.LOG("[isA2dpConnected] null");
            mBluetoothAdapter.getProfileProxy(this, new BluetoothProfileListener(), BluetoothProfile.A2DP);
            return false;
        }
        return mBluetoothProfileA2DP.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED;
    }

    private void initStatusBarMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isStatusBarLightMode()) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    public void connect() {

    }

    public void showAddDevice() {

    }

    /** Handler Found Device
     *  Check bluetooth currently connected device.
     *  Device is connected in the past, immediately connect to it.
     */
    public void handlerFoundAirohaDevice(){
        BluetoothDevice connectingDevice = null;

        List<BluetoothDevice> bluetoothDevices = mBluetoothProfileA2DP.getConnectedDevices();

        if (bluetoothDevices.size() == 0 ){
            LogUtils.LOG_UI("bluetoothDevices null" + bluetoothDevices.size());
            Common.RunAfter(new Runnable() {
                @Override
                public void run() {
                    initBluetoothWithPermissionOK();
                }
            },2000);
            return;
        }

        String firstConnectMac = LoadConnectTarget(Common.DEVICE_CONNECTED);

        for (BluetoothDevice dev : bluetoothDevices) {
            if (mBluetoothProfileA2DP.getConnectionState(dev) == BluetoothProfile.STATE_CONNECTED) {
                bluetoothState = BluetoothState.BT_ON;
                connectingDevice = dev;
                hasConnectedDevice = true;
                SplashActivity.hasDeviceConnected = true;
                LogUtils.LOG_UI("SplashActivity.isConnected = true");
            }
        }

        if (!isShowMainActivity) {
            return;
        }

        if(!hasConnectedDevice) {
            LogUtils.LOG_UI("Device is not connected, please connect the device to continue using");
            showAddDevice();
            return;
        }

        if(hasConnectedDevice
                && deviceState.equals(DeviceState.DISCONNECT) && appState != AppState.BACKGROUND  ) {
            LogUtils.LOG_UI("CONNECT TO DEVICE");

            bluetoothDevice = connectingDevice;
            try {
                connect();
            }
            catch (Exception ex) {
            }
        }
    }

    public void showScreenDeviceNotConnect(String msg, String btnStr, Runnable methodClick) {
        AlertDialog.Builder alertNotConnect = new AlertDialog.Builder(this);
        alertNotConnect.setMessage(msg)
                .setTitle("Your Bluetooth is turned off")
                .setPositiveButton(btnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogNotify.dismiss();
                            methodClick.run();
                    }
                });
        dialogNotify = alertNotConnect.create();
        dialogNotify.show();
    }


    public void showScreenDeviceNotConnectCount2(String msg, String btnStr, Runnable methodClick) {
        AlertDialog.Builder alertNotConnect = new AlertDialog.Builder(this);
        alertNotConnect.setMessage(msg)
                .setTitle("Your Bluetooth is turned off")
                .setPositiveButton(btnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogNotify.dismiss();
                        methodClick.run();

                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        finish();
                        dialogNotify.dismiss();
                        android.os.Process.killProcess(android.os.Process.myPid());

                    }
                })
        ;

        dialogNotify = alertNotConnect.create();
        dialogNotify.show();
    }


    public void openConnectingInstructionFragment() {
        addFragment(R.id.frContainer, new FragmentConnecting1stInstruction(), true);
    }

    public class BluetoothProfileListener implements BluetoothProfile.ServiceListener {
        @Override
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (i == BluetoothProfile.A2DP) {

                mBluetoothProfileA2DP = (BluetoothA2dp) bluetoothProfile;
                LogUtils.ENTER_FUNC_LOG(this.getClass().getName(),"BluetoothProfile.A2DP " + this );
                handlerFoundAirohaDevice();
            }
        }

        @Override
        public void onServiceDisconnected(int i) {
            if (i == BluetoothProfile.A2DP) {
                LogUtils.LOG_UI("Bluetooth has been turned off");
                bluetoothState = BluetoothState.BT_OFF;
            }
        }
    }



}
