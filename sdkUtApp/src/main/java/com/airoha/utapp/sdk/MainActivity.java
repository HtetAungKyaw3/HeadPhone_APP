/* Copyright Statement:
 *
 * (C) 2020  Airoha Technology Corp. All rights reserved.
 *
 * This software/firmware and related documentation ("Airoha Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to Airoha Technology Corp. ("Airoha") and/or its licensors.
 * Without the prior written permission of Airoha and/or its licensors,
 * any reproduction, modification, use or disclosure of Airoha Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 * You may only use, reproduce, modify, or distribute (as applicable) Airoha Software
 * if you have agreed to and been bound by the applicable license agreement with
 * Airoha ("License Agreement") and been granted explicit permission to do so within
 * the License Agreement ("Permitted User").  If you are not a Permitted User,
 * please cease any access or use of Airoha Software immediately.
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT AIROHA SOFTWARE RECEIVED FROM AIROHA AND/OR ITS REPRESENTATIVES
 * ARE PROVIDED TO RECEIVER ON AN "AS-IS" BASIS ONLY. AIROHA EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES AIROHA PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH AIROHA SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN AIROHA SOFTWARE. AIROHA SHALL ALSO NOT BE RESPONSIBLE FOR ANY AIROHA
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND AIROHA'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO AIROHA SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT AIROHA'S OPTION, TO REVISE OR REPLACE AIROHA SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * AIROHA FOR SUCH AIROHA SOFTWARE AT ISSUE.
 */
/* Airoha restricted information */

package com.airoha.utapp.sdk;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airoha.liblogger.AirohaLogger;
import com.airoha.liblogger.printer.FilePrinter;
import com.airoha.sdk.AirohaSDK;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getName();

    private AirohaService mAirohaService = null;
    private ServiceConnection mServiceConnection = null;
    private Intent mServiceIntent = null;
    private MainActivity mContext = null;

    private FragmentIndex mRunningFotaFragIndex = null;
    private FragmentIndex mRunningDumpLogFragIndex = null;
    private long mFirstClickTitleTime = 0;
    private int mClickCountOnActionBar = 0;
    public boolean mIsEnterEngineerMode = false;

    AirohaLogger gLogger = AirohaLogger.getInstance();
    private FilePrinter mInitalLogFilePrinter;

    private static final int REQUEST_PERMISSION_RETURN_CODE = 5566;
    private ArrayList<String> mPermissionList;
    private boolean mIsPermissionGranted = false;

    private static boolean mIsAutoTesting = false;
    private static final int APP_STORAGE_ACCESS_REQUEST_CODE = 5577;

    public enum FragmentIndex{
        INFO, MENU, SINGLE_ONE_CLICK_FOTA, TWS_ONE_CLICK_FOTA, SINGLE_FOTA, TWS_FOTA, PEQ, MMI, KEY_ACTION, ANTENNA, TWO_MIC_DUMP , ANC_DUMP, EXCEPTION_DUMP,
        ONLINE_DUMP, RESTORE_NVR, CUSTOM_CMD, LOG_CONFIG, LE_AUDIO, LE_AUDIO_BIS, EMP, ED, ANC_USER_TRIGGER
    }

    public enum MsgType{
        CONNECTION, GENERAL, ERROR
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            initBinPath(intent, AirohaService.gAutoTestBinPathFotaL);
            initBinPath(intent, AirohaService.gAutoTestBinPathFotaR);
            initBinPath(intent, AirohaService.gAutoTestBinPathRofsL);
            initBinPath(intent, AirohaService.gAutoTestBinPathRofsR);
            initBinPath(intent, AirohaService.gAutoTestBinPathFsImgL);
        }

        MainActivity.this.setTitle(
                getResources().getString(R.string.app_name)
                        + " V" + BuildConfig.VERSION_NAME);

        setClickListenerOnActionBar();

//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
//                try {
//                    StringWriter sw = new StringWriter();
//                    PrintWriter pw = new PrintWriter(sw);
//                    paramThrowable.printStackTrace(pw);
//                    gLogger.e(TAG, sw.toString());
//                } catch (Exception ex) {}
//            }
//        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionList = new ArrayList<>();
            mPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            mPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            mPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            mPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            mPermissionList.add(Manifest.permission.BLUETOOTH);
            mPermissionList.add(Manifest.permission.BLUETOOTH_ADMIN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mPermissionList.add(Manifest.permission.BLUETOOTH_SCAN);
                mPermissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            if (!checkPermission()) {
                return;
            }
        }

        setupBluetoothService();
    }

    protected boolean checkPermission() {
        mIsPermissionGranted = true;

        for (String permission : mPermissionList) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        mPermissionList.toArray(new String[0]),
                        REQUEST_PERMISSION_RETURN_CODE);
                mIsPermissionGranted = false;
                break;
            }
        }

        return mIsPermissionGranted;
    }

    void initBinPath(Intent intent, AirohaService.AutoTestBinPath binPathObj) {
        binPathObj.BinPath = intent.getStringExtra(binPathObj.ExtraKey.getName());

        if (binPathObj.BinPath != null) {
            Log.d(TAG, binPathObj.ExtraKey + " = " + binPathObj.BinPath);
            mIsAutoTesting = true;
        }
    }

    private void setClickListenerOnActionBar(){
        int id = this.getResources().getIdentifier("action_bar", "id", "android");
        ViewGroup actionBar = null;
        if (id != 0) {
            actionBar = mContext.findViewById(id);
        }
        if (actionBar == null) {
            actionBar = findToolbar((ViewGroup) mContext.findViewById(android.R.id.content)
                    .getRootView());
        }
        if(actionBar != null){
            actionBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mIsEnterEngineerMode){
                        return;
                    }
                    if(mClickCountOnActionBar == 0){
                        mFirstClickTitleTime = System.currentTimeMillis();
                    }
                    else{
                        long currentTime = System.currentTimeMillis();
                        if(currentTime - mFirstClickTitleTime > 3000){
                            mClickCountOnActionBar = 0;
                            mFirstClickTitleTime = currentTime;
                        }
                    }

                    mClickCountOnActionBar++;
                    if(mClickCountOnActionBar >= 3) {
                        mIsEnterEngineerMode = true;
                        Toast.makeText(mContext, "Enter Engineer Mode", Toast.LENGTH_SHORT).show();

                        if(mAirohaService != null) {
                            mAirohaService.changePrivilege(BaseFragment.PrivilegeState.EngineerMode);
                        }
                    }
                }
            });
        }
    }

    private static ViewGroup findToolbar(ViewGroup viewGroup) {
        ViewGroup toolbar = null;
        for (int i = 0, len = viewGroup.getChildCount(); i < len; i++) {
            View view = viewGroup.getChildAt(i);
            if (view.getClass().getName().equals("android.support.v7.widget.Toolbar")
                    || view.getClass().getName().equals("android.widget.Toolbar")
                    || view.getClass().getName().equals("androidx.appcompat.widget.Toolbar")) {
                toolbar = (ViewGroup) view;
            } else if (view instanceof ViewGroup) {
                toolbar = findToolbar((ViewGroup) view);
            }
            if (toolbar != null) {
                break;
            }
        }
        return toolbar;
    }

    @Override
    protected void onDestroy() {
//        if (mAirohaService != null && mServiceConnection != null) {
//            unbindService(mServiceConnection);
//        }

        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsPermissionGranted) {
            return;
        }
        if(mServiceConnection != null) {
            return;
        }
        mServiceConnection = new myServiceConnection();
        bindService(
                mServiceIntent,
                mServiceConnection,
                Context.BIND_NOT_FOREGROUND);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        if(mPreviousFragmentIndex == FragmentIndex.MENU) {
            gLogger.d(TAG, "onBackPressed on MENU");
            if (mAirohaService != null) {
                if (mAirohaService.getAirohaLinker() != null) {
                    mAirohaService.getAirohaLinker().releaseAllResource();
                }
                if (mAirohaService.getFragmentMap().containsKey(FragmentIndex.ONLINE_DUMP)) {
                    if (((OnlineLogTabFragment) (mAirohaService.getFragmentMap().get(FragmentIndex.ONLINE_DUMP))).isStartLogging())
                        mAirohaService.getAirohaLogDumpMgr().stopOnlineDump();
                }
                for (BaseFragment fragment : mAirohaService.getFragmentMap().values()) {
                    fragment.onDestroy();
                }
                mAirohaService.getFragmentMap().clear();
            }
            closeApp();
            super.onBackPressed();
        }
        else{
            gLogger.d(TAG, "onBackPressed on changeFragment");
            changeFragment(FragmentIndex.MENU);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gLogger.d(TAG, "onActivityResult: requestCode= " + requestCode
                + ", resultCode= " + resultCode);

        if (requestCode == APP_STORAGE_ACCESS_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!Environment.isExternalStorageManager()) {
                    onBackPressed();
                }
            }
        }
    }

    private FragmentIndex mPreviousFragmentIndex = FragmentIndex.MENU;

    public FragmentIndex getPreviousFragmentIndex() { return mPreviousFragmentIndex; }

    public void changeFragment(FragmentIndex index){
        if(mAirohaService == null){
            return;
        }
        Fragment previous_frag = mAirohaService.getFragmentByIndex(mPreviousFragmentIndex);
        if(mPreviousFragmentIndex == index){
            return;
        }

        switch (mPreviousFragmentIndex) {
            case SINGLE_ONE_CLICK_FOTA:
            case TWS_ONE_CLICK_FOTA:
            case SINGLE_FOTA:
            case TWS_FOTA:
                if (AirohaSDK.getInst().isFotaRunning()) {
                    mRunningFotaFragIndex = mPreviousFragmentIndex;
                } else {
                    mRunningFotaFragIndex = null;
                }
                break;
            case ONLINE_DUMP:
                if (mAirohaService.getAirohaLogDumpMgr().isBusy()) {
                    mRunningDumpLogFragIndex = mPreviousFragmentIndex;
                } else {
                    mRunningDumpLogFragIndex = null;
                }
                break;
        }

        mPreviousFragmentIndex = index;

        Fragment frag = null;
        if(mAirohaService != null) {
            frag = mAirohaService.getFragmentByIndex(index);
        }
        if(frag == null){
            return;
        }
        this.setTitle(((BaseFragment)frag).getTitle() + " V" + BuildConfig.VERSION_NAME);

        Bundle bundle = new Bundle();
        bundle.putString(BaseFragment.EXTRAS_DEVICE_BDA, mAirohaService.getTargetAddr());
        bundle.putString(BaseFragment.EXTRAS_DEVICE_PHY, mAirohaService.getTargetPhy().toString());
        frag.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!frag.isAdded()){
            transaction.hide(previous_frag)
                    .add(R.id.fragment_page_container, frag);
        }
        else{
            transaction.hide(previous_frag)
                    .show(frag);
        }
        transaction.commit();
    }

//    String[] PERMISSIONS = {
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.READ_EXTERNAL_STORAGE,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            android.Manifest.permission.INTERNET,
//    };
//
//    private boolean requestPermissions() {
//        for (String permission : PERMISSIONS) {
//            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
//                return false;
//            }
//        }
//
//        mInitalLogFilePrinter = createInitialLogFile();
//
//        return true;
//    }

    private FilePrinter createInitialLogFile() {
        FilePrinter filePrinter = new FilePrinter(mContext);
        HashMap<String, String> map = new HashMap<>();
        map.put("folder_name", "SDK_UT");
        if (!filePrinter.init(map)) {
            return null;
        }
        gLogger.addPrinter(filePrinter);
        gLogger.setLogLevel(AirohaLogger.LOG_LEVEL_V);
        gLogger.d(TAG, "Create Initial log. APP is running.");
        gLogger.d(TAG, "Ver:" + BuildConfig.VERSION_NAME);

        return filePrinter;
    }

    public FilePrinter getInitalLogFilePrinter() {
        return mInitalLogFilePrinter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
//        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            mInitalLogFilePrinter = createInitialLogFile();
//        }
        if (requestCode == REQUEST_PERMISSION_RETURN_CODE) {
            mIsPermissionGranted = true;
            for (int i = 0; i < permissions.length; ++i) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, permissions[i] + " is not Granted!");
                    mIsPermissionGranted = false;
                    break;
                }
            }
            if (!mIsPermissionGranted) {
                closeApp();
            } else {
                mInitalLogFilePrinter = createInitialLogFile();
                setupBluetoothService();
            }
        }
    }

    private void closeApp(){
        if(null != mServiceConnection){
            try {
                getApplicationContext().unbindService(mServiceConnection);
                mServiceConnection = null;
            } catch (Exception e) {

            }
        }
        if(null != mServiceIntent) {
            getApplicationContext().stopService(mServiceIntent);
        }
        this.finish();
    }

    private void setupBluetoothService(){
//        mServiceIntent = new Intent(this, AirohaService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startService(mServiceIntent);
//        } else {
//            startService(mServiceIntent);
//        }
        mServiceIntent = new Intent(this, AirohaService.class);
        startService(mServiceIntent);
        mServiceConnection = new myServiceConnection();
        bindService(
                mServiceIntent,
                mServiceConnection,
                Context.BIND_NOT_FOREGROUND);
    }

    public AirohaService getAirohaService(){
        return mAirohaService;
    }

    public void updateMsg(final MsgType type, final String msg){
        if (mAirohaService != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((InfoFragment) mAirohaService.getFragmentByIndex(FragmentIndex.INFO)).updateMsg(type, msg);
                }
            });
        }
    }

    public void updateTargetAddr(final String addr, final String phy){
        if (mAirohaService != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((InfoFragment) mAirohaService.getFragmentByIndex(FragmentIndex.INFO)).updateTargetAddr(addr, phy);
                }
            });
        }
    }

    public FragmentIndex getRunningFotaFragIndex() {
        return mRunningFotaFragIndex;
    }

    public FragmentIndex getRunningDumpLogFragIndex() {
        return mRunningDumpLogFragIndex;
    }

    class myServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            mAirohaService = ((AirohaService.LocalBinder) (service)).getService();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!mAirohaService.getFragmentByIndex(FragmentIndex.MENU).isAdded()) {
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragment_page_container, mAirohaService.getFragmentByIndex(FragmentIndex.MENU), MenuFragment.class.getSimpleName())
                                .commit();
                    }
                    if(!mAirohaService.getFragmentByIndex(FragmentIndex.INFO).isAdded()){
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragment_status_container, mAirohaService.getFragmentByIndex(FragmentIndex.INFO), InfoFragment.class.getSimpleName())
                                .commit();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (!Environment.isExternalStorageManager()) {
                            Handler handler = new Handler(mContext.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent tmpIntent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                    startActivityForResult(tmpIntent, APP_STORAGE_ACCESS_REQUEST_CODE);
                                }
                            }, 500);
                        }
                    }
                }
            });
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
//            unbindService(mServiceConnection);
            mServiceConnection = null;
            mAirohaService = null;
        }
    }
}
