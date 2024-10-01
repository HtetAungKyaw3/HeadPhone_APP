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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.airoha.libcommon.AirohaCommonMgr;
import com.airoha.liblinker.AirohaLinker;
import com.airoha.liblinker.host.AbstractHost;
import com.airoha.liblinker.model.GattLinkParam;
import com.airoha.liblinker.model.LinkParam;
import com.airoha.liblinker.model.SppLinkParam;
import com.airoha.liblogdump.AirohaDumpMgr;
import com.airoha.liblogger.AirohaLogger;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.utils.ChipType;
import com.airoha.sdk.api.utils.ConnectionProtocol;
import com.airoha.utapp.sdk.MainActivity.FragmentIndex;
import com.airoha.utapp.sdk.BaseFragment.PrivilegeState;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;


public class AirohaService extends Service{
    private final static String TAG = "Airoha_" + AirohaService.class.getSimpleName();

    private AirohaLinker mAirohaLinker;
    private AirohaDumpMgr mAirohaLogDumpMgr;
    private AirohaCommonMgr mAirohaCommonMgr;
    private String mTargetAddr;
    private ConnectionProtocol mTargetPhy = ConnectionProtocol.PROTOCOL_UNKNOWN;
    private PrivilegeState mPrivilegeState = PrivilegeState.CustomerMode;

    private HashMap<FragmentIndex, BaseFragment> mFragmentMap;

    private LinkParam mLinkParam;

    static public final int notifyID = 0x5566;
    Notification.Builder mNotificationBuilder;

    AirohaLogger gLogger = AirohaLogger.getInstance();

    public enum ExtraKey {
        FOTA_BIN_PATH_L         (1,    "L_FotaBinPath"),
        FOTA_BIN_PATH_R         (2,     "R_FotaBinPath"),
        ROFS_BIN_PATH_L         (3,     "L_RofsBinPath"),
        ROFS_BIN_PATH_R         (4,     "R_RofsBinPath"),
        FS_IMG_PATH_L         (5,     "L_FsImgPath");

        private int mValue;
        private String mName;

        ExtraKey(int value, String name)
        {
            mValue = value;
            mName = name;
        }

        public int getValue() {
            return mValue;
        }

        public String getName() {
            return mName;
        }
    }

    static public class AutoTestBinPath {

        public ExtraKey ExtraKey;
        public String BinPath = null;

        public AutoTestBinPath(ExtraKey key) {
            ExtraKey = key;
        }
    }

    static public AutoTestBinPath gAutoTestBinPathFotaL = new AutoTestBinPath(ExtraKey.FOTA_BIN_PATH_L);
    static public AutoTestBinPath gAutoTestBinPathFotaR = new AutoTestBinPath(ExtraKey.FOTA_BIN_PATH_R);
    static public AutoTestBinPath gAutoTestBinPathRofsL = new AutoTestBinPath(ExtraKey.ROFS_BIN_PATH_L);
    static public AutoTestBinPath gAutoTestBinPathRofsR = new AutoTestBinPath(ExtraKey.ROFS_BIN_PATH_R);
    static public AutoTestBinPath gAutoTestBinPathFsImgL = new AutoTestBinPath(ExtraKey.FS_IMG_PATH_L);

    public HashMap<FragmentIndex, BaseFragment> getFragmentMap() {
        return mFragmentMap;
    }

    public void onTaskRemoved(Intent rootIntent) {
        if(mFragmentMap.containsKey(FragmentIndex.ONLINE_DUMP)) {
            if(((OnlineLogTabFragment)(mFragmentMap.get(FragmentIndex.ONLINE_DUMP))).isStartLogging())
                getAirohaLogDumpMgr().stopOnlineDump();
        }

        if(mFragmentMap.containsKey(FragmentIndex.TWO_MIC_DUMP)) {
            getAirohaLogDumpMgr().stopAirDump();
        }
    }

    public void showNotification(String content) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);

        Notification notification;
        notification = mNotificationBuilder
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(content)
                //.setProgress(100, mProgressValue, false)
                .build();

        notificationManager.notify(notifyID, notification);
    }

//    public void stopNotification(){
//        NotificationManager notificationManager = (NotificationManager) getSystemService(
//                NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.deleteNotificationChannel(getResources().getString(R.string.app_name));
//        }
//    }

    @Override
    public void onCreate() {
        gLogger.d(TAG, "onCreate");
        super.onCreate();

        Intent intent = new Intent();
//        final int flags = PendingIntent.FLAG_UPDATE_CURRENT;
//        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, flags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationBuilder = new Notification.Builder(getApplicationContext(), getResources().getString(R.string.app_name));
        }
        else{
            mNotificationBuilder = new Notification.Builder(getApplicationContext());
        }
        mNotificationBuilder.setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Airoha APP")
                //.setProgress(100, mProgressValue, false)
                .setOngoing(true);

        setForeground(getResources().getString(R.string.app_name));

        //mAirohaLinker = new AirohaLinker(this);
        AirohaSDK.getInst().init(this);
        mFragmentMap = new HashMap<>();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    paramThrowable.printStackTrace(pw);
                    gLogger.e(TAG, sw.toString());
                } catch (Exception ex) {}
            }
        });
    }

    @Override
    public void onDestroy() {
        gLogger.d(TAG, "onDestroy");
        AirohaSDK.getInst().destroy();
        if (mAirohaLinker != null) {
            mAirohaLinker.releaseAllResource();
            mAirohaLinker = null;
        }
        stopForeground(true);
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        AirohaService getService() {
            return AirohaService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public String getTargetAddr(){
        return mTargetAddr;
    }

    public ConnectionProtocol getTargetPhy(){
        return mTargetPhy;
    }

    public synchronized AirohaCommonMgr getAirohaCommonMgr(){
        if (mAirohaCommonMgr == null) {
            gLogger.d(TAG, "create AirohaCommonMgr");
            mAirohaCommonMgr = new AirohaCommonMgr(mTargetAddr, mAirohaLinker.getHost(mTargetAddr), mLinkParam);
        } else {
            if(mLinkParam.getLinkType() != mAirohaCommonMgr.getLinkParam().getLinkType() ||
                    !(mLinkParam.getLinkAddress().equals(mAirohaCommonMgr.getLinkParam().getLinkAddress()))) {
                mAirohaCommonMgr.destroy();
                gLogger.d(TAG, "re-create AirohaCommonMgr");
                mAirohaCommonMgr = new AirohaCommonMgr(mTargetAddr, mAirohaLinker.getHost(mTargetAddr), mLinkParam);
            }
        }
        return mAirohaCommonMgr;
    }

    public synchronized AirohaDumpMgr getAirohaLogDumpMgr(){
        if (mAirohaLogDumpMgr == null) {
            gLogger.d(TAG, "create AirohaDumpMgr");
            mAirohaLogDumpMgr = new AirohaDumpMgr(mTargetAddr, mAirohaLinker.getHost(mTargetAddr), mLinkParam, this.getApplicationContext());
        } else {
            if(mLinkParam.getLinkType() != mAirohaLogDumpMgr.getLinkParam().getLinkType() ||
                    !(mLinkParam.getLinkAddress().equals(mAirohaLogDumpMgr.getLinkParam().getLinkAddress()))) {
                mAirohaLogDumpMgr.destroy();
                gLogger.d(TAG, "re-create AirohaDumpMgr");
                mAirohaLogDumpMgr = new AirohaDumpMgr(mTargetAddr, mAirohaLinker.getHost(mTargetAddr), mLinkParam, this.getApplicationContext());
            }
        }
        return mAirohaLogDumpMgr;
    }

    public synchronized AirohaLinker getAirohaLinker(){
        return mAirohaLinker;
    }

    public void changePrivilege(PrivilegeState state){
        mPrivilegeState = state;
        for(HashMap.Entry<FragmentIndex, BaseFragment> entry : mFragmentMap.entrySet()){
            entry.getValue().changePrivilege(state);
        }
    }

    public PrivilegeState getPrivilegeState(){
        return mPrivilegeState;
    }

    public synchronized BaseFragment getFragmentByIndex(FragmentIndex index){
        for(HashMap.Entry<FragmentIndex, BaseFragment> entry : mFragmentMap.entrySet()){
            if(entry.getKey() == index){
                return entry.getValue();
            }
        }
        return createFragmentByIndex(index);
    }

    public synchronized BaseFragment createFragmentByIndex(FragmentIndex index){
        for(HashMap.Entry<FragmentIndex, BaseFragment> entry : mFragmentMap.entrySet()){
            if(entry.getKey() == index){
                mFragmentMap.remove(index);
                break;
            }
        }

        BaseFragment frag = null;
        switch(index){
            case INFO:
                frag = new InfoFragment();
                break;
            case MENU:
                frag = new MenuFragment();
                break;
            case SINGLE_FOTA:
                if (AirohaSDK.getInst().getChipType() == ChipType.AB1562
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1562E) {
                    frag = new SingleFota1562Fragment();
                } else if (AirohaSDK.getInst().getChipType() == ChipType.AB1565_DUAL
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1568_DUAL
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1565_DUAL_V3
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1568_DUAL_V3
                        || AirohaSDK.getInst().getChipType() == ChipType.AB158x_DUAL
                        || AirohaSDK.getInst().getChipType() == ChipType.AB157x_DUAL) {
                    frag = new SingleFotaDualChipFragment();
                } else {
                    frag = new SingleFotaFragment();
                }
                break;
            case TWS_FOTA:
                if (AirohaSDK.getInst().getChipType() == ChipType.AB1562
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1562E) {
                    frag = new TwsFota1562Fragment();
                } else {
                    frag = new TwsFotaFragment();
                }
                break;
            case PEQ:
                frag = new PeqFragment();
                break;
            case MMI:
                frag = new MmiFragment();
                break;
            case KEY_ACTION:
                if (AirohaSDK.getInst().getChipType() == ChipType.AB1562
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1562E) {
                    frag = new KeyAction1562Fragment();
                } else {
                    frag = new KeyActionFragment();
                }
                break;
            case ANTENNA:
                if (AirohaSDK.getInst().getChipType() == ChipType.AB1562
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1562E) {
                    frag = new Antenna1562Fragment();
                } else {
                    frag = new AntennaFragment();
                }
                break;
            case TWO_MIC_DUMP:
                frag = new TwoMicDumpFragment();
                break;
//            case ANC_DUMP:
//                frag = new AncDumpFragment();
//                break;
            case EXCEPTION_DUMP:
                if (AirohaSDK.getInst().getChipType() == ChipType.AB1562
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1562E) {
                    frag = new ExceptionDump1562Fragment();
                } else {
                    frag = new ExceptionDumpFragment();
                }
                break;
            case ONLINE_DUMP:
                frag = new OnlineLogTabFragment();
                break;
            case RESTORE_NVR:
                if (AirohaSDK.getInst().getChipType() == ChipType.AB1562
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1562E) {
                    frag = new RestoreNvr1562Fragment();
                } else {
                    frag = new RestoreNvrFragment();
                }
                break;
            case CUSTOM_CMD:
                frag = new CustomCmdFragment();
                break;
            case LOG_CONFIG:
                frag = new LogConfigTabFragment();
                break;
            case LE_AUDIO:
                frag = new LeAudioFragment();
                break;
            case LE_AUDIO_BIS:
                frag = new LeAudioBisFragment();
                break;
            case EMP:
                frag = new EmpFragment();
                break;
            case ED:
                frag = new EnvironmentDectecionFragment();
                break;
            case ANC_USER_TRIGGER:
                frag = new AncUserTriggerFragment();
                break;
        }
        mFragmentMap.put(index, frag);
        return frag;
    }

    public void setForeground(String strUpate){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(strUpate, strUpate, NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null)
                return;
            manager.createNotificationChannel(channel);

            Notification notification = mNotificationBuilder
                    .setContentTitle(strUpate)
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .build();

            startForeground(notifyID, notification);
        }
        else {
            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //Create the notification object through the builder
            Notification noti = mNotificationBuilder
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(strUpate)
                    .setSmallIcon(R.drawable.ic_launcher).build();

            // call startForeground
            startForeground(notifyID, noti);
        }
    }

    public void saveLinkParam(SppLinkParam linkParam){
        gLogger.d(TAG, "connect SPP");
        mTargetAddr = linkParam.getLinkAddress();
        mTargetPhy = ConnectionProtocol.PROTOCOL_SPP;
        mLinkParam = linkParam;
    }

    public void saveLinkParam(GattLinkParam linkParam){
        gLogger.d(TAG, "connect BLE");
        mTargetAddr = linkParam.getLinkAddress();
        mTargetPhy = ConnectionProtocol.PROTOCOL_BLE;
        mLinkParam = linkParam;
    }

//    public void disconnect(String addr) {
//        gLogger.d(TAG, "disconnect: " + addr);
//        // user disconnect, do not re-connect
//        mAirohaLinker.disconnect(addr);
//    }

    public void setAirohaLinker(AirohaLinker airoLinker) {
        mAirohaLinker = airoLinker;
    }

    public AbstractHost getHost() {
        return mAirohaLinker.getHost(mTargetAddr);
    }
}
