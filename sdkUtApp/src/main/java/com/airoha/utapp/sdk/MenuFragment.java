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

import android.app.AlertDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.liblinker.AirohaLinker;
import com.airoha.liblinker.constant.LinkTypeEnum;
import com.airoha.liblinker.constant.UuidTable;
import com.airoha.liblinker.model.GattLinkParam;
import com.airoha.liblinker.model.SppLinkParam;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.device.AirohaDevice;
import com.airoha.sdk.api.utils.ChipType;
import com.airoha.sdk.api.utils.ConnectionProtocol;
import com.airoha.sdk.api.utils.ConnectionUUID;
import com.airoha.sdk.api.utils.DeviceType;

import com.airoha.utapp.sdk.MainActivity.MsgType;
import com.airoha.utapp.sdk.MainActivity.FragmentIndex;
import com.airoha.utapp.sdk.model.TestDeviceStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.airoha.liblinker.constant.UuidTable.AIROHA_GATT_RX_UUID;
import static com.airoha.liblinker.constant.UuidTable.AIROHA_GATT_SERVICE_UUID;
import static com.airoha.liblinker.constant.UuidTable.AIROHA_GATT_TX_UUID;
import static com.airoha.liblinker.constant.UuidTable.AIROHA_SPP_UUID;

public class MenuFragment extends BaseFragment {
    private String TAG = MenuFragment.class.getSimpleName();
    private MenuFragment mFragment;

    // paired list
    protected ArrayAdapter<String> mPairedDevicesArrayAdapter;
    protected ArrayAdapter<String> mLeDevicesArrayAdapter;
    private ArrayList<String> mLeAddrs;
    private LinkTypeEnum mPhyType;

    // Scan
    private BluetoothAdapter mBluetoothAdapter;

    private MyProfileServiceListener mMyProfileServiceListener;
    private BluetoothA2dp mBluetoothProfileA2DP;

    //    private Timer mStartScanTimer;
    private Timer mStopScanTimer;

    private final int NUM_SCAN_DURATIONS_KEPT = 3;
    private final int EXCESSIVE_SCANNING_PERIOD_MS = 30 * 1000;
    private ConcurrentLinkedQueue<Long> mScanStartTimeStampQueue;

    // Connect
    private Button mBtnConnect;
    private Button mBtnDisconnect;
    private Button mBtnOneClickSingle;
    private Button mBtnOneClickTws;
    private Button mBtnSingleFota;
    private Button mBtnTwsFota;
    private Button mBtnPeqUt;
    private Button mBtnMmiUt;
    private Button mBtnAntennaUt;
    private Button mBtnKeyActionUt;
    private Button mBtnTwoMicDump;
    private Button mBtnAncDump;
    private Button mBtnMiniDump;
    private Button mBtnOnlineDump;
    private Button mBtnRestoreNvr;
    private Button mBtnUUIDSetting;
    private Button mBtnCustomCmd;
    private Button mBtnLogConfig;
    private Button mBtnOneClickDump;
    private Button mBtnLeAudio;
    private Button mBtnLeAudioBIS;
    private Button mBtnEmpUT;
    private Button mBtnEnvironmentDetection;
    private Button mBtnAncUserTrigger;

    private RadioButton mRadioButtonPhySPP;
    private RadioButton mRadioButtonPhyBLE;

    //    UUID SPP_UUID = UUID.fromString("8901dfa8-5c7e-4d8f-9f0c-c2b70683f5f0");
    UUID SPP_UUID = AIROHA_SPP_UUID;

    public MenuFragment(){
    }

    @Override
    public void changePrivilege(PrivilegeState state){
        if(state == PrivilegeState.EngineerMode){
            mRadioButtonPhyBLE.setVisibility(View.VISIBLE);
            mBtnRestoreNvr.setVisibility(View.VISIBLE);
            mBtnCustomCmd.setVisibility(View.VISIBLE);
//            if (AirohaSDK.getInst().mChipType == ChipType.AB1568) {
                mBtnLeAudio.setVisibility(View.VISIBLE);
                mBtnLeAudioBIS.setVisibility(View.VISIBLE);
//            }
        }
        else{
            mRadioButtonPhyBLE.setVisibility(View.GONE);
            mBtnRestoreNvr.setVisibility(View.GONE);
            mBtnCustomCmd.setVisibility(View.GONE);
            mBtnLeAudio.setVisibility(View.GONE);
            mBtnLeAudioBIS.setVisibility(View.GONE);
        }

        checkConnectionStatus();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mTitle = getResources().getString(R.string.app_name);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;
        mScanStartTimeStampQueue = new ConcurrentLinkedQueue();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container,false);

        mBtnConnect = view.findViewById(R.id.btnConnect);
        mBtnConnect.setOnClickListener(mOnClickListener);
        mBtnDisconnect = view.findViewById(R.id.btnDisconnect);
        mBtnDisconnect.setOnClickListener(mOnClickListener);

        mRadioButtonPhySPP = view.findViewById(R.id.radioButton_PHY_SPP);
        mRadioButtonPhySPP.setOnClickListener(mOnClickListener);
        mRadioButtonPhyBLE = view.findViewById(R.id.radioButton_PHY_BLE);
        mRadioButtonPhyBLE.setOnClickListener(mOnClickListener);

        mBtnOneClickSingle = view.findViewById(R.id.btn155xOneClickSingle);
        mBtnOneClickSingle.setOnClickListener(mOnClickListener);
        mBtnOneClickTws = view.findViewById(R.id.btn155xOneClickMCSync);
        mBtnOneClickTws.setOnClickListener(mOnClickListener);
        mBtnSingleFota = view.findViewById(R.id.btnSingleFota);
        mBtnSingleFota.setOnClickListener(mOnClickListener);
        mBtnTwsFota = view.findViewById(R.id.btnTwsFota);
        mBtnTwsFota.setOnClickListener(mOnClickListener);
        mBtnPeqUt = view.findViewById(R.id.btnPeqUt);
        mBtnPeqUt.setOnClickListener(mOnClickListener);
        mBtnMmiUt = view.findViewById(R.id.btnMmiUt);
        mBtnMmiUt.setOnClickListener(mOnClickListener);
        mBtnAntennaUt = view.findViewById(R.id.btnAntennaUt);
        mBtnAntennaUt.setOnClickListener(mOnClickListener);
        mBtnKeyActionUt = view.findViewById(R.id.btnKeyActionUt);
        mBtnKeyActionUt.setOnClickListener(mOnClickListener);

        mBtnTwoMicDump = view.findViewById(R.id.btnTwoMicDump);
        mBtnTwoMicDump.setOnClickListener(mOnClickListener);
        mBtnAncDump = view.findViewById(R.id.btnAncDump);
        mBtnAncDump.setOnClickListener(mOnClickListener);
        mBtnMiniDump = view.findViewById(R.id.btnMiniDump);
        mBtnMiniDump.setOnClickListener(mOnClickListener);
        mBtnOnlineDump = view.findViewById(R.id.btnOnlineDump);
        mBtnOnlineDump.setOnClickListener(mOnClickListener);

        mBtnRestoreNvr = view.findViewById(R.id.btnRestoreNVR);
        mBtnRestoreNvr.setOnClickListener(mOnClickListener);
        mBtnUUIDSetting= view.findViewById(R.id.btnSetUUID);
        mBtnUUIDSetting.setOnClickListener(mOnClickListener);

        mBtnCustomCmd = view.findViewById(R.id.btnCustomCmd);
        mBtnCustomCmd.setOnClickListener(mOnClickListener);

        mBtnLogConfig = view.findViewById(R.id.btnLogConfig);
        mBtnLogConfig.setOnClickListener(mOnClickListener);

        mBtnOneClickDump = view.findViewById(R.id.btnOneClickDump);
        mBtnOneClickDump.setOnClickListener(mOnClickListener);

        mBtnLeAudio = view.findViewById(R.id.btnLeAudioUt);
        mBtnLeAudio.setOnClickListener(mOnClickListener);

        mBtnLeAudioBIS = view.findViewById(R.id.btnLeAudioBIS);
        mBtnLeAudioBIS.setOnClickListener(mOnClickListener);

        mBtnEmpUT = view.findViewById(R.id.btnEmpUt);
        mBtnEmpUT.setOnClickListener(mOnClickListener);

        mBtnEnvironmentDetection = view.findViewById(R.id.btnEnvironmentDetection);
        mBtnEnvironmentDetection.setOnClickListener(mOnClickListener);

        mBtnAncUserTrigger = view.findViewById(R.id.btnAncUserTrigger);
        mBtnAncUserTrigger.setOnClickListener(mOnClickListener);

        initPreference();

        return view;
    }

    private void checkConnectionStatus(){
        if(mActivity.getAirohaService() == null){
            return;
        }
        if(!BluetoothAdapter.checkBluetoothAddress(gTargetAddr)){
            return;
        }
        if(AirohaSDK.getInst().getAirohaDeviceConnector().getAirohaLinker().isConnected(gTargetAddr)){
            checkBtnStatus();
        }
        else{
            setViewAsDisconnected();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initDeviceListParams();
        checkConnectionStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mBluetoothAdapter != null && mBluetoothProfileA2DP != null) {
            gLogger.d(TAG, "closeProfileProxy(): A2DP");
            mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothProfileA2DP);
        }
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
        super.onHiddenChanged(hidden);
        checkBtnStatus();
    }

    private void checkBtnStatus() {
        MainActivity.FragmentIndex runningFotaFragIndex = mActivity.getRunningFotaFragIndex();
        MainActivity.FragmentIndex runningDumpLogFragIndex = mActivity.getRunningDumpLogFragIndex();
        if (runningFotaFragIndex != null) {
            mBtnOneClickSingle.setEnabled(false);
            mBtnOneClickTws.setEnabled(false);
            mBtnSingleFota.setEnabled(false);
            mBtnTwsFota.setEnabled(false);
            mBtnAncDump.setEnabled(false);
            mBtnMiniDump.setEnabled(false);
            mBtnTwoMicDump.setEnabled(false);
            mBtnOnlineDump.setEnabled(false);
            mBtnLeAudio.setEnabled(false);
            mBtnLeAudioBIS.setEnabled(false);
            mBtnEmpUT.setEnabled(false);
            mBtnEnvironmentDetection.setEnabled(false);
            mBtnAncUserTrigger.setEnabled(false);
            switch (runningFotaFragIndex) {
                case SINGLE_ONE_CLICK_FOTA:
                    mBtnOneClickSingle.setEnabled(true);
                    break;
                case TWS_ONE_CLICK_FOTA:
                    mBtnOneClickTws.setEnabled(true);
                    break;
                case SINGLE_FOTA:
                    mBtnSingleFota.setEnabled(true);
                    break;
                case TWS_FOTA:
                    mBtnTwsFota.setEnabled(true);
                    break;
            }
            return;
        } else if (mActivity.getAirohaService().getAirohaLinker().isConnected(gTargetAddr)){
            setViewAsConnected();
        }

        if (runningDumpLogFragIndex != null) {
            switch (runningDumpLogFragIndex) {
                case ONLINE_DUMP:
                    mBtnOneClickSingle.setEnabled(false);
                    mBtnOneClickTws.setEnabled(false);
                    mBtnSingleFota.setEnabled(false);
                    mBtnTwsFota.setEnabled(false);
                    mBtnAncDump.setEnabled(false);
                    mBtnMiniDump.setEnabled(false);
                    mBtnTwoMicDump.setEnabled(false);
                    mBtnLeAudio.setEnabled(false);
                    mBtnLeAudioBIS.setEnabled(false);
                    mBtnEmpUT.setEnabled(false);
                    //mBtnRestoreNvr.setEnabled(false);
                    //mBtnAntennaUt.setEnabled(false);
                    break;
            }
        } else if (mActivity.getAirohaService().getAirohaLinker().isConnected(gTargetAddr)){
            setViewAsConnected();
        }
    }

    private void initDeviceListParams(){
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.select_dialog_item);
        mLeDevicesArrayAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.select_dialog_item);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mLeAddrs = new ArrayList<>();

        mMyProfileServiceListener = new MyProfileServiceListener();
        mBluetoothAdapter.getProfileProxy(mActivity, mMyProfileServiceListener, BluetoothProfile.A2DP);
    }


    @SuppressWarnings({"MissingPermission"})
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (String addr : mLeAddrs) {
                                if (addr.equalsIgnoreCase(device.getAddress())) {
                                    return;
                                }
                            }
                            mLeAddrs.add(device.getAddress());
                            if (device.getName() == null) {
                                mLeDevicesArrayAdapter.add("Unknown device" + "\n"
                                        + device.getAddress());
                                gLogger.d(TAG, "Scanned LE Device: Unknown device, " + device.getAddress());
                            } else {
                                mLeDevicesArrayAdapter.add(device.getName() + "\n"
                                        + device.getAddress());
                                gLogger.d(TAG, "Scanned LE Device: " + device.getName() + ", " + device.getAddress());
                            }
                            mLeDevicesArrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    /// Android System limitation: can't start LE scan more than 5 times during 30s
    private boolean isScanningTooFrequently() {
        synchronized (mScanStartTimeStampQueue) {
            boolean ret = false;

            if (mScanStartTimeStampQueue.size() >= NUM_SCAN_DURATIONS_KEPT) {
                Long firstTimeStamp = mScanStartTimeStampQueue.peek();
                if ((System.currentTimeMillis() - firstTimeStamp) < EXCESSIVE_SCANNING_PERIOD_MS) {
                    ret = true;
                } else {
                    mScanStartTimeStampQueue.poll();
                    mScanStartTimeStampQueue.add(System.currentTimeMillis());
                }
            } else {
                mScanStartTimeStampQueue.add(System.currentTimeMillis());
            }

            return ret;
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void scanLeDevice(final boolean enable) {
        gLogger.d(TAG, "scanLeDevice: " + enable);
        synchronized (mScanStartTimeStampQueue) {
//            if (mStartScanTimer != null) {
//                mStartScanTimer.cancel();
//                mStartScanTimer = null;
//            }

            if (mStopScanTimer != null) {
                mStopScanTimer.cancel();
                mStopScanTimer = null;
            }

            if (enable) {
                mLeDevicesArrayAdapter.clear();
                mLeAddrs.clear();
                mBluetoothAdapter.startLeScan(mLeScanCallback);

//                mStartScanTimer = new Timer();
//                mStartScanTimer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        synchronized (mScanStartTimeStampQueue) {
//                        }
//                    }
//                }, 500);

                mStopScanTimer = new Timer();
                mStopScanTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (mScanStartTimeStampQueue) {
                            mStopScanTimer = null;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        }
                    }
                }, 10000);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private boolean isA2dpConnected(String bdAddr) {
        if (mActivity.mIsEnterEngineerMode) {
            return true;
        }

        if (mBluetoothProfileA2DP == null) {
            mBluetoothAdapter.getProfileProxy(mActivity, mMyProfileServiceListener, BluetoothProfile.A2DP);
            mActivity.updateMsg(MsgType.ERROR, "mBluetoothProfileA2DP == null");
            showAlertDialog(mActivity, "Error", "mBluetoothProfileA2DP == null");
            return false;
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(bdAddr);
        if (mBluetoothProfileA2DP.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED) {
            return true;
        } else {
            mActivity.updateMsg(MsgType.ERROR, "A2DP is not Connected: " + bdAddr);
            showAlertDialog(mActivity, "Error", "A2DP is not Connected: " + bdAddr);
            return false;
        }
    }

    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.radioButton_PHY_SPP:
                    gLogger.d(TAG, "Click radioButton_PHY_SPP");
                    updatePairedList();
                    showListDialog(LinkTypeEnum.SPP);
                    break;
                case R.id.radioButton_PHY_BLE:
                    gLogger.d(TAG, "Click radioButton_PHY_BLE");
                    updatePairedList();
                    if (isScanningTooFrequently()) {
                        showScanCautionDialog(mActivity, "Scanning too frequently!\n(more than 3 times during 30s)\n\nDue to Android system limitation, there would be no any scan result!");
                    } else {
                        scanLeDevice(true);
                        showListDialog(LinkTypeEnum.GATT_LE);
                    }
                    break;
                case R.id.btnConnect:
                    gLogger.d(TAG, "Click btnConnect");
                    if (gTargetPhy == ConnectionProtocol.PROTOCOL_SPP) {
                        if (!isA2dpConnected(gTargetAddr)) {
                            return;
                        }
                    }
                    mBtnUUIDSetting.setEnabled(false);
                    v.setEnabled(false);
                    if (mActivity.getInitalLogFilePrinter() != null) {
                        gLogger.removePrinter(mActivity.getInitalLogFilePrinter().getPrinterName());
                    }
                    if (gFilePrinter != null) {
                        gLogger.removePrinter(gFilePrinter.getPrinterName());
                    }
                    gFilePrinter = createLogFile(gTargetAddr);  //create new log file

                    AirohaConnector airohaDeviceConnector = AirohaSDK.getInst().getAirohaDeviceConnector();
                    airohaDeviceConnector.registerConnectionListener(mFragment);
                    airohaDeviceConnector.registerConnectionListener(mActivity.getAirohaService().getFragmentByIndex(FragmentIndex.INFO));

                    AirohaDevice airohaDevice = new AirohaDevice();
                    airohaDevice.setApiStrategy(new TestDeviceStrategy());
                    airohaDevice.setTargetAddr(gTargetAddr);
                    airohaDevice.setDeviceName(gTargetName);
                    if(gTargetPhy == ConnectionProtocol.PROTOCOL_BLE) {
                        airohaDevice.setPreferredProtocol(ConnectionProtocol.PROTOCOL_BLE);
                        airohaDeviceConnector.connect(airohaDevice);
                    }
                    else{
                        airohaDevice.setPreferredProtocol(ConnectionProtocol.PROTOCOL_SPP);
                        ConnectionUUID connectionUUID = new ConnectionUUID(getCurrentUUID());
                        airohaDeviceConnector.connect(airohaDevice, connectionUUID);
                    }

                    // used for debugging related fragments
                    AirohaLinker airoLinker = airohaDeviceConnector.getAirohaLinker();
                    mActivity.getAirohaService().setAirohaLinker(airoLinker);

                    if(gTargetPhy == ConnectionProtocol.PROTOCOL_SPP) {
                        SppLinkParam linkParam = new SppLinkParam(gTargetAddr, getCurrentUUID());
                        mActivity.getAirohaService().saveLinkParam(linkParam);
                    }
                    else{
                        GattLinkParam linkParam = new GattLinkParam(gTargetAddr, AIROHA_GATT_SERVICE_UUID, AIROHA_GATT_TX_UUID, AIROHA_GATT_RX_UUID);
                        mActivity.getAirohaService().saveLinkParam(linkParam);
                    }
                    break;
                case R.id.btnDisconnect:
                    gLogger.d(TAG, "Click btnDisconnect");
                    v.setEnabled(false);
                    gReconnectFlag = false;
                    AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(false);
                    AirohaSDK.getInst().getAirohaDeviceConnector().disconnect();
                    break;
                case R.id.btn155xOneClickSingle:
                    changeFragment(FragmentIndex.SINGLE_ONE_CLICK_FOTA);
                    break;
                case R.id.btn155xOneClickMCSync:
                    changeFragment(FragmentIndex.TWS_ONE_CLICK_FOTA);
                    break;
                case R.id.btnSingleFota:
                    changeFragment(FragmentIndex.SINGLE_FOTA);
                    break;
                case R.id.btnTwsFota:
                    changeFragment(FragmentIndex.TWS_FOTA);
                    break;
                case R.id.btnPeqUt:
                    changeFragment(FragmentIndex.PEQ);
                    break;
                case R.id.btnMmiUt:
                    changeFragment(FragmentIndex.MMI);
                    break;
                case R.id.btnKeyActionUt:
                    changeFragment(FragmentIndex.KEY_ACTION);
                    break;
                case R.id.btnAntennaUt:
                    changeFragment(FragmentIndex.ANTENNA);
                    break;
                case R.id.btnTwoMicDump:
                    changeFragment(FragmentIndex.TWO_MIC_DUMP);
                    break;
                case R.id.btnAncDump:
                    changeFragment(FragmentIndex.ANC_DUMP);
                    break;
                case R.id.btnMiniDump:
                    changeFragment(FragmentIndex.EXCEPTION_DUMP);
                    break;
                case R.id.btnOnlineDump:
                    changeFragment(FragmentIndex.ONLINE_DUMP);
                    break;
                case R.id.btnRestoreNVR:
                    changeFragment(FragmentIndex.RESTORE_NVR);
                    break;
                case R.id.btnSetUUID:
                    showDialogForSetUUID(mActivity, gUUIDSelectedName, "", "", "");
                    break;
                case R.id.btnCustomCmd:
                    changeFragment(FragmentIndex.CUSTOM_CMD);
                    break;
                case R.id.btnLogConfig:
                    changeFragment(FragmentIndex.LOG_CONFIG);
                    break;
                case R.id.btnOneClickDump:
                    if(AirohaSDK.getInst().mChipType == ChipType.AB1568
                            || AirohaSDK.getInst().mChipType == ChipType.AB1568_V3) {
                        OneClickDumpMgr.getInst().initDialog(mActivity);
                        OneClickDumpMgr.getInst().startQueryDumpInfo();
                    } else if(AirohaSDK.getInst().mChipType == ChipType.AB1562
                            || AirohaSDK.getInst().mChipType == ChipType.AB1562E) {
                        OneClickDumpMgr1562 mgr = new OneClickDumpMgr1562(mActivity);
                        mgr.startQueryDumpInfo();
                    }
                    break;
                case R.id.btnLeAudioUt:
                    changeFragment(FragmentIndex.LE_AUDIO);
                    break;
                case R.id.btnLeAudioBIS:
                    changeFragment(FragmentIndex.LE_AUDIO_BIS);
                    break;
                case R.id.btnEmpUt:
                    changeFragment(FragmentIndex.EMP);
                    break;
                case R.id.btnEnvironmentDetection:
                    changeFragment(FragmentIndex.ED);
                    break;
                case R.id.btnAncUserTrigger:
                    changeFragment(FragmentIndex.ANC_USER_TRIGGER);
                    break;
            }
        }
    };

    private void changeFragment(MainActivity.FragmentIndex index){
        gLogger.d(TAG, "changeFragment: " + index.toString());
        String previous_addr = (mActivity.getAirohaService().getFragmentByIndex(index)).getTargetAddr();
        if(previous_addr != null) {
            if (!previous_addr.equalsIgnoreCase(gTargetAddr)) {
                mActivity.getAirohaService().createFragmentByIndex(index);
            }
        }
        mActivity.changeFragment(index);
    }

    @SuppressWarnings({"MissingPermission"})
    private void updatePairedList() {
        // Remove all element from the list
        mPairedDevicesArrayAdapter.clear();
        // Get a set of currently paired devices
        BluetoothAdapter mBlurAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBlurAdapter.getBondedDevices();
        if (pairedDevices.isEmpty()) {
            Log.e(TAG, "Device not founds");
            mPairedDevicesArrayAdapter.add("No Device");
            return;
        }

        for (BluetoothDevice device : pairedDevices) {
            gLogger.d(TAG, "Paired Device: " + device.getName() + ", " + device.getAddress());
            mPairedDevicesArrayAdapter.add(device.getName() + "\n"
                    + device.getAddress());
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void checkAirohaDevice(BluetoothDevice device){
        try {
            ParcelUuid[] parcelUuids = device.getUuids();

            for (ParcelUuid parcelUuid : parcelUuids) {
                gLogger.d(TAG, parcelUuid.toString());

                if (parcelUuid.getUuid().compareTo(UuidTable.AIROHA_SPP_UUID) == 0) {
                    gLogger.d(TAG, "found Airoha device");
                    mActivity.updateMsg(MsgType.GENERAL, "Found Airoha Device:" + device.getName());
                    return;
                }
            }
        } catch (Exception e) {
            gLogger.d(TAG, e.getMessage());
            mActivity.updateMsg(MsgType.ERROR, e.getMessage());
        }
    }

    private void showScanCautionDialog(final Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("CAUTION");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scanLeDevice(true);
                showListDialog(LinkTypeEnum.GATT_LE);
            }
        });
        builder.create().show();
    }

//    private void showAlertDialog(final Context context, String title, String message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        builder.create().show();
//    }

    private void showListDialog(final LinkTypeEnum phyType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);;
        View titleView = inflater.inflate(R.layout.custom_dialog_title, null, false);
        TextView tvTitle = titleView.findViewById(R.id.textView_dialog_title);
        tvTitle.setText("Select One Device");

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setCustomTitle(titleView);

        if (phyType != mPhyType) {
            gTargetAddr = null;
            mActivity.updateTargetAddr(null, null);
            mBtnConnect.setEnabled(false);
        }

        ArrayAdapter<String> temp = new ArrayAdapter<String>(mActivity,
                android.R.layout.select_dialog_item);

        if(phyType == LinkTypeEnum.SPP){
            temp = mPairedDevicesArrayAdapter;
        }
        else if(phyType == LinkTypeEnum.GATT_LE){
            temp = mLeDevicesArrayAdapter;
            for(int i=0; i<mPairedDevicesArrayAdapter.getCount(); ++i) {
                mLeDevicesArrayAdapter.add(mPairedDevicesArrayAdapter.getItem(i));
            }
        }
        final ArrayAdapter<String> arrayAdapter = temp;

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] tmp = arrayAdapter.getItem(which).split("\n");
                if(tmp.length < 2 && tmp[0] == "No Device"){
                    return;
                }
                gTargetName = tmp[0];
                gTargetAddr = tmp[1];
                gLogger.d(TAG, "Target address: " + gTargetAddr);
                mActivity.updateTargetAddr(gTargetAddr, phyType.toString());
                mBtnConnect.setEnabled(true);

                if(phyType == LinkTypeEnum.SPP){
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(gTargetAddr);
                    checkAirohaDevice(device);
                    mPhyType = LinkTypeEnum.SPP;
                    gTargetPhy = ConnectionProtocol.PROTOCOL_SPP;
                    mBtnUUIDSetting.setEnabled(true);
                }
                else{
                    scanLeDevice(false);
                    mPhyType = LinkTypeEnum.GATT_LE;
                    gTargetPhy = ConnectionProtocol.PROTOCOL_BLE;
                    mBtnUUIDSetting.setEnabled(false);
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scanLeDevice(false);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void setButtonEnableStatus(boolean enable){
        mBtnOneClickSingle.setEnabled(enable);
        mBtnOneClickTws.setEnabled(enable);
        mBtnSingleFota.setEnabled(enable);
        mBtnTwsFota.setEnabled(enable);
        mBtnPeqUt.setEnabled(enable);
        mBtnMmiUt.setEnabled(enable);
        mBtnAntennaUt.setEnabled(enable);
        mBtnKeyActionUt.setEnabled(enable);
        mBtnTwoMicDump.setEnabled(enable);
        mBtnAncDump.setEnabled(enable);
        mBtnMiniDump.setEnabled(enable);
        mBtnOnlineDump.setEnabled(enable);
        mBtnRestoreNvr.setEnabled(enable);
        mBtnCustomCmd.setEnabled(enable);
        mBtnLogConfig.setEnabled(enable);
//        mBtnOneClickDump.setEnabled(enable);
        mBtnLeAudio.setEnabled(enable);
        mBtnLeAudioBIS.setEnabled(enable);
        if (mActivity.mIsEnterEngineerMode == false) {
            if (AirohaSDK.getInst().getDeviceType() == DeviceType.HEADSET) {
                mBtnTwsFota.setEnabled(false);
            } else if (AirohaSDK.getInst().getDeviceType() == DeviceType.EARBUDS) {
                mBtnSingleFota.setEnabled(false);
            }
        }
        if (AirohaSDK.getInst().mChipType == ChipType.AB1562
            || AirohaSDK.getInst().mChipType == ChipType.AB1562E) {
            mBtnEmpUT.setVisibility(View.VISIBLE);
            mBtnEmpUT.setEnabled(enable);
            mBtnEnvironmentDetection.setVisibility(View.VISIBLE);
            mBtnEnvironmentDetection.setEnabled(enable);
        }
        if (AirohaSDK.getInst().mChipType == ChipType.AB1568
                || AirohaSDK.getInst().mChipType == ChipType.AB1568_V3
                || AirohaSDK.getInst().mChipType == ChipType.AB158x
                || AirohaSDK.getInst().mChipType == ChipType.AB157x) {
            mBtnEnvironmentDetection.setVisibility(View.VISIBLE);
            mBtnEnvironmentDetection.setEnabled(enable);
        }
        if (AirohaSDK.getInst().mChipType == ChipType.AB1568
                || AirohaSDK.getInst().mChipType == ChipType.AB1568_V3) {
            mBtnAncUserTrigger.setVisibility(View.VISIBLE);
            mBtnAncUserTrigger.setEnabled(enable);
        }
    }

    private void setViewAsConnected(){
        mBtnDisconnect.setEnabled(true);
        mBtnConnect.setVisibility(View.GONE);
        mBtnDisconnect.setVisibility(View.VISIBLE);
        mBtnUUIDSetting.setEnabled(false);
        mRadioButtonPhySPP.setEnabled(false);
        mRadioButtonPhyBLE.setEnabled(false);
        setButtonEnableStatus(true);
    }

    private void setViewAsDisconnected(){
        mBtnConnect.setEnabled(true);
        mBtnConnect.setVisibility(View.VISIBLE);
        mBtnDisconnect.setVisibility(View.GONE);
        mRadioButtonPhySPP.setEnabled(true);
        if (mRadioButtonPhySPP.isChecked()) {
            mBtnUUIDSetting.setEnabled(true);
        }
        mRadioButtonPhyBLE.setEnabled(true);
        setButtonEnableStatus(false);
    }

    private static String CURRENT_UUID = UuidTable.AIROHA_SPP_UUID.toString();
    public static UUID getCurrentUUID(){
        return UUID.fromString(CURRENT_UUID);
    }

    public static ArrayList<PreferenceUtility.UUID_INFO> gUUIDInfoList = new ArrayList<>();
    public static String gUUIDSelectedName;

    public static void setUUID(PreferenceUtility.UUID_INFO uuid_info){
        gUUIDSelectedName = uuid_info.mName;
        CURRENT_UUID = uuid_info.mUUID;
        Log.d("Utils", "Set UUID:" + gUUIDSelectedName + ", " + CURRENT_UUID);
    }

    public static void setUUID(String selected_name) {
        for (PreferenceUtility.UUID_INFO item : gUUIDInfoList) {
            if (item.mName.equalsIgnoreCase(selected_name)) {
                setUUID(item);
                break;
            }
        }
    }

    private void initPreference(){
        gUUIDInfoList = PreferenceUtility.getUUIDList(mActivity);
        gUUIDSelectedName = PreferenceUtility.getUUIDSelectedName(mActivity);

        if(gUUIDInfoList.size() == 0){
            PreferenceUtility.UUID_INFO uuid_airoha = new PreferenceUtility.UUID_INFO();
            uuid_airoha.mName = PreferenceUtility.AIROHA_UUID_NAME;
            uuid_airoha.mUUID = UuidTable.AIROHA_SPP_UUID.toString();
            gUUIDInfoList.add(uuid_airoha);

            PreferenceUtility.UUID_INFO uuid_common = new PreferenceUtility.UUID_INFO();
            uuid_common.mName = PreferenceUtility.COMMON_UUID_NAME;
            uuid_common.mUUID = UuidTable.COMMON_SPP_UUID.toString();
            gUUIDInfoList.add(uuid_common);

            PreferenceUtility.saveUUIDList(mActivity, gUUIDInfoList);
            PreferenceUtility.saveUUIDSelectedName(mActivity, gUUIDSelectedName);
        }
        setUUID(gUUIDSelectedName);
    }

    private void showDialogForSetUUID(final Context context, String name_spinner_select,
                                      final String name, final String uuid,
                                      String message){

        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
        alert.setTitle("Change UUID");

        LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.set_uuid,null);
        alert.setView(myView);

        final Spinner spinner_saved_uuid = (Spinner)(myView.findViewById(R.id.spinner_uuid_name));
        final EditText et_name = (EditText)(myView.findViewById(R.id.editText_uuid_name));
        final EditText et_uuid = (EditText)(myView.findViewById(R.id.editText_uuid));


        PreferenceUtility.UUID_INFO uuid_info = new PreferenceUtility.UUID_INFO();
        List<String> nameList = new ArrayList<>();
        nameList.add("ADD");
        int selected_pos = 0;
        int count = 0;
        for(PreferenceUtility.UUID_INFO item : gUUIDInfoList){
            count++;
            nameList.add(item.mName);
            if(item.mName.equalsIgnoreCase(name_spinner_select)){
                uuid_info = item;
                selected_pos = count;
            }
        }
        spinner_saved_uuid.setAdapter(
                new ArrayAdapter<>(mActivity, R.layout.spinner_item, nameList));

        spinner_saved_uuid.setSelection(selected_pos);
        spinner_saved_uuid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position == 0) {
                    et_name.setText(name);
                    et_uuid.setText(uuid);
                    et_name.setEnabled(true);
                    et_uuid.setEnabled(true);
                }
                else{
                    for(PreferenceUtility.UUID_INFO item : gUUIDInfoList){
                        if(item.mName.equalsIgnoreCase(spinner_saved_uuid.getSelectedItem().toString())){
                            et_name.setText(item.mName);
                            et_uuid.setText(item.mUUID);
                            break;
                        }
                    }
                    et_name.setEnabled(false);
                    et_uuid.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        if(selected_pos == 0) {
            et_name.setText(name);
            et_uuid.setText(uuid);
        }
        else{
            et_name.setText(uuid_info.mName);
            et_uuid.setText(uuid_info.mUUID);
            et_name.setEnabled(false);
            et_uuid.setEnabled(false);
        }

        TextView tv = myView.findViewById(R.id.textView_add_uuid_errorMsg);
        tv.setText(message);
        if(message.length() == 0) {
            tv.setVisibility(View.GONE);
        }

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(spinner_saved_uuid.getSelectedItemPosition() == 0) {
                    String input_name = et_name.getText().toString();
                    String input_uuid = et_uuid.getText().toString();

                    input_uuid = FormatUUID(input_uuid);

                    et_uuid.setText(FormatUUID(input_uuid));

                    for (PreferenceUtility.UUID_INFO item : gUUIDInfoList) {
                        if (item.mName.equalsIgnoreCase(input_name)) {
                            showDialogForSetUUID(mActivity, "ADD", input_name, input_uuid, "Name is duplicate.");
                            return;
                        }
                    }

                    if (input_name.isEmpty()) {
                        showDialogForSetUUID(mActivity, "ADD", input_name, input_uuid, "Name is empty.");
                        return;
                    }
                    if (!checkUUIDFormat(input_uuid)) {
                        showDialogForSetUUID(mActivity, "ADD", input_name, input_uuid, "Format of UUID is invalid.");
                        return;
                    }
                    PreferenceUtility.UUID_INFO uuid_info = new PreferenceUtility.UUID_INFO();
                    uuid_info.mName = input_name;
                    uuid_info.mUUID = input_uuid;

                    gLogger.d(TAG, "Add UUID:" + input_name + ", " + input_uuid);

                    setUUID(uuid_info);
                    gUUIDInfoList.add(uuid_info);
                    gUUIDSelectedName = input_name;

                    PreferenceUtility.saveUUIDList(mActivity, gUUIDInfoList);
                    PreferenceUtility.saveUUIDSelectedName(mActivity, gUUIDSelectedName);
                }
                else {
                    String selected_name = spinner_saved_uuid.getSelectedItem().toString();
                    setUUID(selected_name);
                    gUUIDSelectedName = selected_name;
                    PreferenceUtility.saveUUIDSelectedName(mActivity, gUUIDSelectedName);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.dismiss();
            }
        });

        alert.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String selected_name = spinner_saved_uuid.getSelectedItem().toString();
                if(spinner_saved_uuid.getSelectedItemPosition() == 0){
                    showDialogForSetUUID(mActivity, selected_name, "", "", "Please select one name.");
                    return;
                }
                if(selected_name.equalsIgnoreCase(PreferenceUtility.AIROHA_UUID_NAME)){
                    showDialogForSetUUID(mActivity, selected_name, "", "", "\"" + PreferenceUtility.AIROHA_UUID_NAME + "\" can't be deleted.");
                    return;
                }
                if(selected_name.equalsIgnoreCase(PreferenceUtility.COMMON_UUID_NAME)){
                    showDialogForSetUUID(mActivity, selected_name, "", "", "\"" + PreferenceUtility.COMMON_UUID_NAME + "\" can't be deleted.");
                    return;
                }
                for(PreferenceUtility.UUID_INFO item : gUUIDInfoList){
                    if(item.mName.equalsIgnoreCase(selected_name)){
                        gUUIDInfoList.remove(item);
                        PreferenceUtility.saveUUIDList(mActivity, gUUIDInfoList);
                        break;
                    }
                }
                for(PreferenceUtility.UUID_INFO item : gUUIDInfoList){
                    selected_name = item.mName;
                    setUUID(item);

                    gUUIDSelectedName = selected_name;
                    PreferenceUtility.saveUUIDSelectedName(mActivity, gUUIDSelectedName);
                    break;
                }
                showDialogForSetUUID(mActivity, selected_name, "", "", "Set " + selected_name + " as current UUID.");
                return;
            }
        });

        alert.show();
    }

    private boolean checkUUIDFormat(String uuid){
        String _uuid = uuid.replace("-","");
        if(_uuid.length() != 32){
            return false;
        }
        return true;
    }

    private String FormatUUID(String uuid){
        String _uuid = uuid.replace("-","");
        String rtn = "";
        for(int i = 0; i < _uuid.length(); i++){
            if(i == 8 || i == 12 || i == 16 || i == 20){
                rtn += "-";
            }
            rtn += _uuid.substring(i,i+1);
        }
        return rtn.toUpperCase();
    }

//    @Override
//    public void onHostConnected() {
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mActivity.getAirohaService().getAirohaLinker().init(mTargetAddr);
//            }
//        });
//    }
//
//    @Override
//    public void onHostDisconnected() {
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                setViewAsDisconnected();
//                if (gReconnectFlag) {
//                    mBtnConnect.setEnabled(false);
//                    mBtnUUIDSetting.setEnabled(false);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onHostInitialized() {
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                setViewAsConnected();
//            }
//        });
//    }
//
//    @Override
//    public void onHostError(final int errorCode) {
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mActivity.updateMsg(MainActivity.MsgType.ERROR, "onHostError: " + errorCode);
//                setViewAsDisconnected();
//            }
//        });
//    }

    public class MyProfileServiceListener implements BluetoothProfile.ServiceListener {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
            if(profile == BluetoothProfile.A2DP){
                mBluetoothProfileA2DP = (BluetoothA2dp) bluetoothProfile;
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if(profile == BluetoothProfile.A2DP){
                mBluetoothProfileA2DP = null;
            }
        }
    }

    @Override
    public void onStatusChanged(final int status) {
        switch (status) {
            case AirohaConnector.CONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setViewAsConnected();
                    }
                });
                break;
            case AirohaConnector.DISCONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setViewAsDisconnected();
                        if (gReconnectFlag) {
                            mBtnConnect.setEnabled(false);
                            mBtnUUIDSetting.setEnabled(false);
                            mRadioButtonPhySPP.setEnabled(false);
                            mRadioButtonPhyBLE.setEnabled(false);
                        }
                    }
                });
                break;
            case AirohaConnector.CONNECTING:
            case AirohaConnector.DISCONNECTING:
            case AirohaConnector.WAITING_CONNECTABLE:
                break;
            default:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mActivity.updateMsg(MsgType.ERROR, "Connection error: " + ConnectionStatus.getDescription(status));
                        mBtnConnect.setEnabled(true);
                        mBtnDisconnect.setEnabled(true);
                        mRadioButtonPhySPP.setEnabled(true);
                        mRadioButtonPhyBLE.setEnabled(true);
                    }
                });
                break;
        }
    }
}