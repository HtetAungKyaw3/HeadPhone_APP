package com.airoha.utapp.sdk.bluetooth;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import com.airoha.utapp.sdk.R;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;


import com.airoha.utapp.sdk.tools.LogUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class BluetoothPermissionAdapter {

    public        boolean         is_full_permission    = false;
    private       boolean         did_register_receiver = false;
    private Activity activity;

    private  AlertDialog bluetooth_permission_denied_dialog;
    private  AlertDialog                    bluetooth_permission_gps_dialog;

    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    @RequiresApi(api = Build.VERSION_CODES.S)
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
    };
    private static final int      REQUEST_LOCATION_SERVICE   = 1;

    private boolean    is_startResolutionForResult = false;
    private boolean    is_startRequestAndroidBTON  = false;

    private static BluetoothPermissionAdapter S_share=new BluetoothPermissionAdapter();

    public void init(Activity activity) {
        this.activity=activity;
        DialogInterface.OnClickListener click_ok = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bluetooth_permission_denied_dialog.dismiss();
                // go to setting
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri    = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);

                activity.startActivity(intent);

                if (requestCompletion!=null) {
                    requestCompletion.requestLocationWithResult(1);
                    requestCompletion = null;
                }
            }
        };

        DialogInterface.OnClickListener click_ok_gps = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                is_startResolutionForResult = false;
                bluetooth_permission_gps_dialog.dismiss();
                // go to setting
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity).
                setCancelable(false).
                setPositiveButton("OK", click_ok);

        if (isAndroid_S_Or_Above()) {

            builder.setTitle(activity.getResources().getString(R.string.neabyTitle)).
                    setMessage(activity.getResources().getString(R.string.neabyContent));

        } else {

            builder.setTitle(activity.getResources().getString(R.string.locationTitle)).
                    setMessage(activity.getResources().getString(R.string.locationContent));
        }

        bluetooth_permission_denied_dialog = builder.create();

        if (isAndroid_S_Or_Above()) {
            bluetooth_permission_gps_dialog = null;
        } else {

            builder = new AlertDialog.Builder(activity).
                    setCancelable(false).
                    setPositiveButton("OK", click_ok_gps);

            builder.setTitle("Location service required").
                    setMessage("The system requires apps to be granted " +
                            "location service in order to scan for BLE devices." +
                            "\nYou can grant them in app settings.");
            bluetooth_permission_gps_dialog = builder.create();
        }
    }

    public static BluetoothPermissionAdapter shared(){
        return S_share;
    }

    public static boolean isAndroid_S_Or_Above() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    public boolean isPermissionGrant(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean isPermissionGrant_2() {
        String[] permissions = isAndroid_S_Or_Above() ? ANDROID_12_BLE_PERMISSIONS : BLE_PERMISSIONS;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private boolean isGPSOn() {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    public boolean isPermissionFullGranted() {
        String[] permissions = isAndroid_S_Or_Above() ? ANDROID_12_BLE_PERMISSIONS : BLE_PERMISSIONS;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



    public interface Runner{
        void requestLocationWithResult(int withResult);
    }

    private Runner requestCompletion;
    public void request_location_permission(boolean needDialog, Runner runner){
        if (bluetooth_permission_denied_dialog.isShowing()){
            return;
        }

        String[] permissions = isAndroid_S_Or_Above() ? ANDROID_12_BLE_PERMISSIONS : BLE_PERMISSIONS;
        if (isPermissionGrant(permissions)) {
            runner.requestLocationWithResult(0);
            LogUtils.LOG_UI("BLE: bi return");
            return ;
        }

        LogUtils.LOG_UI("BLE: BLUETOOTH PERMISSION NOT GRANT");
        Dexter.withContext(activity).withPermissions(permissions).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    LogUtils.LOG_UI("BLE: BLUETOOTH PERMISSION ALL GRANT");
                    runner.requestLocationWithResult(0);
                }
                if (!report.areAllPermissionsGranted()) {
                    if (!needDialog){
                        runner.requestLocationWithResult(1);
                        return;
                    }
                    requestCompletion = runner;

                    try {
                    bluetooth_permission_denied_dialog.dismiss();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    LogUtils.LOG_UI("BLE: BLUETOOTH PERMISSION DISABLE -> SHOW DIALOG");
                    bluetooth_permission_denied_dialog.show();

                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    public void request_location_permission(Runner runner) {
        request_location_permission(true, runner);
    }
    public boolean request_ble_permission() {
        LogUtils.LOG_UI("BLE: check");
        String[] permissions = isAndroid_S_Or_Above() ? ANDROID_12_BLE_PERMISSIONS : BLE_PERMISSIONS;
        if (isPermissionGrant(permissions)) {
            return true;
        }

        LogUtils.LOG_UI("BLE: BLUETOOTH PERMISSION NOT GRANT");
        Dexter.withContext(activity).withPermissions(permissions).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    LogUtils.LOG_UI("BLE: BLUETOOTH PERMISSION ALL GRANT");

                }
                else {

                        try {
                           bluetooth_permission_denied_dialog.dismiss();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        LogUtils.LOG_UI("BLE: BLUETOOTH PERMISSION DISABLE -> SHOW DIALOG");
                        bluetooth_permission_denied_dialog.show();

            }}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

        return false;
    }

    private boolean request_location_on() {
        if (isGPSOn()) {
            return true;
        }

        // prevent show multiple time
        if (is_startResolutionForResult) {
            return false;
        }

        is_startResolutionForResult = true;
        if (bluetooth_permission_gps_dialog != null) {
            try {
                bluetooth_permission_gps_dialog.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    public void ENTRY_POINT(){
        is_full_permission = false;

        if (bluetooth_permission_denied_dialog.isShowing()) {
            return;
        }

        if (is_startResolutionForResult) {
            return;
        }

        if (is_startRequestAndroidBTON) {
            return;
        }

        if (bluetooth_permission_gps_dialog != null && bluetooth_permission_gps_dialog.isShowing()) {
            return;
        }

        //********************************LATER
//        boolean is_disconnected = !BLEManager.ble.isConnected();
        boolean is_disconnected = true;


        // check ble permission
        if (is_disconnected && !request_ble_permission()) {
            return;
        }
        LogUtils.LOG_UI("BLE: BLUETOOTH PERMISSION OK -> CHECK BLUETOOTH IS ON?");

        LogUtils.LOG_UI("BLE: BLUETOOTH IS ON -> ask for LOCATION");

        if (isAndroid_S_Or_Above()) {
            LogUtils.LOG_UI("BLE: LOCATION IS NO NEED on ANDROID 12-> start to scan");
        } else {

            if (is_disconnected && !request_location_on()) {
                return;
            }

            LogUtils.LOG_UI("BLE: LOCATION IS OK on OLD Android -> start to scan");
        }

        is_full_permission = true;
    }

    private final Handler mHandler              = new Handler();

    public void onResume() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(entry, 1000);
    }

    Runnable entry = new Runnable() {
        @Override
        public void run() {
            ENTRY_POINT();
        }
    };

    public void onPause() {
        try {
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
