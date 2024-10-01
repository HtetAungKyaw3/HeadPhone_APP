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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.liblinker.host.AbstractHost;
import com.airoha.liblinker.host.HostStateListener;
import com.airoha.utapp.sdk.MainActivity.MsgType;
import com.airoha.libbase.constant.AgentPartnerEnum;
import com.airoha.libmmi.AirohaMmiListener;
import com.airoha.libmmi.AirohaMmiMgr;
import com.airoha.libmmi.model.A2dpInfo;
import com.airoha.libmmi.model.AntennaInfo;
import com.airoha.libmmi.model.FieldTrialRelatedNV;
import com.airoha.libmmi.stage.MmiStageSendKeyEvent;
import com.airoha.libmmi.stage.MmiStage_WriteNV;
import com.airoha.libmmi.stage.MmiStage_WriteNVRelay;
import com.airoha.libutils.Common.AirohaGestureInfo;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.utils.DeviceType;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RestoreNvrFragment extends BaseFragment implements HostStateListener {
    private String TAG = RestoreNvrFragment.class.getSimpleName();
    private String LOG_TAG = "[RestoreNvr] ";

    private static final int REQUEST_CHOOSE_NVR = 55;

    private RestoreNvrFragment mFragment;
    private View mView;
    private List<View> mViewList = new ArrayList<>();
    private boolean mIsAgentRight = false;
    private boolean mIsPartnerExisting = false;
    private AgentPartnerEnum mTargetRole = AgentPartnerEnum.AGENT;
    private FilePickerDialog mFilePickerDialog;
    private String mSelectedFilePath;

    private TextView mTextAgentChannel;
    private TextView mTextPartnerChannel;
    private TextView mTextLeftNvCount;
    private TextView mTextRightNvCount;
    private EditText mEditTextNvrpath;
    private RadioButton mRadioBtnLeft;
    private RadioButton mRadioBtnRight;
    private RadioButton mRadioBtnAll;
    private Button mBtnCheckPartner;
    private Button mBtnRoleSwitch;
    private Button mBtnNvrFilePicker;
    private Button mBtnStart;

    int mLeftNvCount = 0;
    int mRightNvCount = 0;
    int mTotalNvCount = 0;

    AbstractHost mHost;

    public RestoreNvrFragment(){
        mTitle = "Restore Nvr";
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_restore_nvr, container, false);

        mTextAgentChannel = mView.findViewById(R.id.textAgentChannel);
        mTextPartnerChannel = mView.findViewById(R.id.textPartnerChannel);
        mTextLeftNvCount = mView.findViewById(R.id.textLeftNvCount);
        mTextRightNvCount = mView.findViewById(R.id.textRightNvCount);
        mEditTextNvrpath = mView.findViewById(R.id.editTextNvrPath);

        mBtnCheckPartner = mView.findViewById(R.id.btnCheckPartnerConnection);
        mViewList.add(mBtnCheckPartner);
        mBtnCheckPartner.setOnClickListener(mOnClickListener);

        mBtnRoleSwitch = mView.findViewById(R.id.btnRoleSwitch);
        mViewList.add(mBtnRoleSwitch);
        mBtnRoleSwitch.setOnClickListener(mOnClickListener);

        mBtnStart = mView.findViewById(R.id.btnRestoreNVR);
        mViewList.add(mBtnStart);
        mBtnStart.setOnClickListener(mOnClickListener);

        mBtnNvrFilePicker = mView.findViewById(R.id.btnNvrFilePicker);
        mViewList.add(mBtnNvrFilePicker);
        mBtnNvrFilePicker.setOnClickListener(mOnClickListener);

        mRadioBtnLeft = mView.findViewById(R.id.radioButton_mmi_restore_nvr_left);
//        mViewList.add(mRadioBtnLeft);
        mRadioBtnLeft.setOnClickListener(mOnClickListener);
        mRadioBtnRight = mView.findViewById(R.id.radioButton_mmi_restore_nvr_right);
//        mViewList.add(mRadioBtnRight);
        mRadioBtnRight.setOnClickListener(mOnClickListener);
        mRadioBtnAll = mView.findViewById(R.id.radioButton_mmi_restore_nvr_all);
//        mViewList.add(mRadioBtnAll);
        mRadioBtnAll.setOnClickListener(mOnClickListener);

        initFileDialog();
        setAllViewsEnabled(false);

        return mView;
    }

    private void initFileDialog() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"nvr", "NVR", "ext"};
        mFilePickerDialog = new FilePickerDialog(mActivity, properties);
        mFilePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    mSelectedFilePath = files[0];
                    mEditTextNvrpath.setText(mSelectedFilePath);
                    mBtnStart.setEnabled(true);
                } else {
                    mEditTextNvrpath.setText("");
                    mBtnStart.setEnabled(false);
                    showAlertDialog(mActivity, "Error", "Invalid File Name");
                }
            }
        });
    }

    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCheckPartnerConnection:
                    setAllViewsEnabled(false);
                    mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Check Partner...");
                    ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkPartnerExistence();
                    break;
                case R.id.btnRoleSwitch:
                    resetNvCount();
                    setAllViewsEnabled(false);
                    mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "RHO Start...");
                    ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).doRoleSwitch();
                    break;
                case R.id.btnNvrFilePicker:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        showFileChooser(REQUEST_CHOOSE_NVR, "application/octet-stream", "Choose a nvr file");
                    } else {
                        mFilePickerDialog.show();
                    }
                    break;
                case R.id.btnRestoreNVR:
                    if (mSelectedFilePath.length() > 0) {
                        resetNvCount();
                        setAllViewsEnabled(false);
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Restore NVR start...");
                        if (mTargetRole == AgentPartnerEnum.BOTH) {
                            mTotalNvCount = ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).startUpdateDualNvr(mSelectedFilePath, mSelectedFilePath);
                        } else {
                            mTotalNvCount = ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).startUpdateSingleNvr(mSelectedFilePath, mTargetRole, false, true);
                        }
                        if (mTotalNvCount <= 0) {
                            showAlertDialog(mActivity, "Error",
                                    "NVR format or content is invalid!\n\n" +
                                    "Note: the UI NV (0xEE..) is not allowed to update by App.");

                            mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Restore NVR cancelled");

                            setAllViewsEnabled(true);
                        }
                    }
                    break;
                case R.id.radioButton_mmi_restore_nvr_left:
                    if (mIsAgentRight) {
                        mTargetRole = AgentPartnerEnum.PARTNER;
                    } else {
                        mTargetRole = AgentPartnerEnum.AGENT;
                    }
//                    setRadioButtonsEnabled(false);
                    break;
                case R.id.radioButton_mmi_restore_nvr_right:
                    if (mIsAgentRight) {
                        mTargetRole = AgentPartnerEnum.AGENT;
                    } else {
                        mTargetRole = AgentPartnerEnum.PARTNER;
                    }
//                    setRadioButtonsEnabled(false);
                    break;
                case R.id.radioButton_mmi_restore_nvr_all:
                    mTargetRole = AgentPartnerEnum.BOTH;
//                    setRadioButtonsEnabled(false);
                    break;
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CHOOSE_NVR:
                Uri uri = data.getData();
                String path = uri.getPath();
                if (path != null && path.length() > 0) {
                    File tmpFile = uriToFileApiQ(uri);
                    if (!tmpFile.getName().contains("nvr")) {
                        mEditTextNvrpath.setText("");
                        mBtnStart.setEnabled(false);
                        showAlertDialog(mActivity, "Error", "The file extension should be \"nvr\"");
                        return;
                    }
                    mSelectedFilePath = tmpFile.getAbsolutePath();
                    mEditTextNvrpath.setText(mSelectedFilePath);
                    mBtnStart.setEnabled(true);
                } else {
                    mEditTextNvrpath.setText("");
                    mBtnStart.setEnabled(false);
                    showAlertDialog(mActivity, "Error", "Invalid File Name");
                }
                break;
        }
    }

    private void resetNvCount() {
        mLeftNvCount = 0;
        mRightNvCount = 0;
        mTotalNvCount = 0;
        updateNvCountText(false);
        updateNvCountText(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        gLogger.d(TAG, "onResume");
        if (mActivity.getAirohaService() != null) {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(true);
            mActivity.getAirohaService().getHost().addHostStateListener(TAG, mFragment);
            ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).addListener(TAG, mAirohaMmiListener);
            ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkPartnerExistence();
//            ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkAgentChannel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        gLogger.d(TAG, "onPause");

        if (mActivity.getAirohaService() != null) {
            mActivity.getAirohaService().getHost().removeHostStateListener(TAG);
            AirohaSDK.getInst().getAirohaDeviceConnector().unregisterConnectionListener(mFragment);
            ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).removeListener(TAG);
        }
    }

    @Override
    public void onDestroy() {
        gLogger.d(TAG, "onDestroy");
        if (gFilePrinter != null) {
            gLogger.removePrinter(gFilePrinter.getPrinterName());
        }
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
        super.onHiddenChanged(hidden);
        if (mActivity.getAirohaService() == null) {
            return;
        }

        if (hidden) {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(false);
            mActivity.getAirohaService().getHost().removeHostStateListener(TAG);
            AirohaSDK.getInst().getAirohaDeviceConnector().unregisterConnectionListener(mFragment);
            ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).removeListener(TAG);
        } else {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(true);
            mActivity.getAirohaService().getHost().addHostStateListener(TAG, mFragment);
            AirohaSDK.getInst().getAirohaDeviceConnector().registerConnectionListener(mFragment);
            ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).addListener(TAG, mAirohaMmiListener);
            ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkPartnerExistence();
//            ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkAgentChannel();
        }
    }

//    @Override
//    public void onHostDisconnected() {
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mIsPartnerExisting = false;
//                setAllViewsEnabled(false);
//                resetUiText();
//                mBtnStart.setEnabled(false);
//            }
//        });
//    }
//
//    @Override
//    public void onHostInitialized() {
//        ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkPartnerExistence();
//        ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkAgentChannel();
//    }

    void setRadioButtonsEnabled(final boolean isEnabled) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRadioBtnLeft.setEnabled(isEnabled);
                mRadioBtnRight.setEnabled(isEnabled);
                mRadioBtnAll.setEnabled(isEnabled);
            }
        });
    }

    void resetUiText() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextAgentChannel.setText("");
                mTextPartnerChannel.setText("");
            }
        });
    }

    void updateNvCountText(final boolean isLeft) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int totalNvCount = mTotalNvCount;
                if (mRadioBtnAll.isChecked()) {
                    totalNvCount = totalNvCount/2;
                }
                if (isLeft) {
                    String completedNvCount = String.valueOf(mLeftNvCount);
                    mTextLeftNvCount.setText(completedNvCount + "/" + totalNvCount);
                } else {
                    String completedNvCount = String.valueOf(mRightNvCount);
                    mTextRightNvCount.setText(completedNvCount + "/" + totalNvCount);
                }
            }
        });
    }

    void setAllViewsEnabled(final boolean isEnabled) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (View view : mViewList) {
                    view.setEnabled(isEnabled);
                }
                if (AirohaSDK.getInst().getDeviceType() == DeviceType.HEADSET) {
                    mBtnCheckPartner.setEnabled(false);
                    mBtnRoleSwitch.setEnabled(false);
                }
            }
        });
    }

    void addUpdatedNvCount(boolean isAgent) {
        boolean isLeft = false;
        if (isAgent ^ mIsAgentRight) {
            isLeft = true;
            mLeftNvCount += 1;
        } else {
            mRightNvCount += 1;
        }
        updateNvCountText(isLeft);
    }

    AirohaMmiListener mAirohaMmiListener = new AirohaMmiListener() {
        @Override
        public void notifySdkVersion(String sdkVersion) {

        }

        @Override
        public void notifyDeviceName(byte role, boolean isClassic, boolean isDefault, String deviceName) {

        }

        @Override
        public void notifySetDeviceName(byte role, boolean status) {

        }

        @Override
        public void notifyDeviceAddr(byte role, boolean isClassic, String deviceAddr) {

        }

        @Override
        public void notifyReadAncNv(byte[] ancData) {

        }

        @Override
        public void notifyUpdateDeviceStatus(int moduleId, int statusCode) {

        }

        @Override
        public void notifyIrOnOff(byte irStatus) {

        }

        @Override
        public void notifyTouchOnOff(byte touchStatus) {

        }

        @Override
        public void notifyCustomResp(byte[] resp) {
            
        }

        @Override
        public void notifyRhoDone(boolean status, int agentChannel) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "RHO Done");
                    ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkPartnerExistence();
//                    ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkAgentChannel();
                }
            });
        }

        @Override
        public void notifyA2dpInfo(A2dpInfo info) {

        }

        @Override
        public void notifyFieldTrialRelatedNV(byte role, FieldTrialRelatedNV result) {

        }

        @Override
        public void notifyFwInfo(byte role, String companyName, String modelName) {

        }

        @Override
        public void notifyFwVersion(byte role, String version) {

        }

        @Override
        public void OnRespSuccess(final String stageName) {
            if (stageName.equals(MmiStageSendKeyEvent.class.getSimpleName())) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Done, device will reboot...");
                    }
                });
            } else if (stageName.equals(MmiStage_WriteNV.class.getSimpleName())) {
                addUpdatedNvCount(true);
            } else if (stageName.equals(MmiStage_WriteNVRelay.class.getSimpleName())) {
                addUpdatedNvCount(false);
            }
        }

        @Override
        public void OnFindMeState(final byte state) {

        }

        @Override
        public void OnBattery(final byte role, final byte level) {

        }

        @Override
        public void OnAncTurnOn(final byte status) {

        }

        @Override
        public void OnPassThrough(final byte status) {

        }

        @Override
        public void OnAncTurnOff(final byte status) {

        }

        @Override
        public void notifyLeakageDetectionStatus(byte mode, byte status, byte[] data, AgentPartnerEnum role) {

        }

        @Override
        public void OnAncCalibrationFailed(AgentPartnerEnum role) {

        }

        @Override
        public void notifyAgentIsRight(final boolean isRight) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIsAgentRight = isRight;
                    if (mIsAgentRight) {
                        mTextAgentChannel.setText("Right");
                        if (mTextPartnerChannel.isEnabled())
                            mTextPartnerChannel.setText("Left");
                    } else {
                        mTextAgentChannel.setText("Left");
                        if (mTextPartnerChannel.isEnabled())
                            mTextPartnerChannel.setText("Right");
                    }
                    mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Agent Channel: " + mTextAgentChannel.getText().toString() +
                            ", Partner Channel: " + mTextPartnerChannel.getText().toString());

//                    if (!mIsPartnerExisting) {
                        if (mIsAgentRight) {
//                            mRadioBtnRight.setEnabled(true);
                            mRadioBtnRight.performClick();
                        } else {
//                            mRadioBtnLeft.setEnabled(true);
                            mRadioBtnLeft.performClick();
                        }
//                    }

                    if (mEditTextNvrpath.getText().toString().length() > 0) {
                        mBtnStart.setEnabled(true);
                    }
                }
            });
        }

        @Override
        public void notifyPartnerIsExisting(final boolean isExisting) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIsPartnerExisting = isExisting;
                    setAllViewsEnabled(true);
                    setRadioButtonsEnabled(mIsPartnerExisting);
                    mTextPartnerChannel.setEnabled(mIsPartnerExisting);
                    if (!mIsPartnerExisting) {
                        mTextPartnerChannel.setText("NA");
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Partner Not Found");
                    } else {
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Partner is Found");
                    }
//                    if (mEditTextNvrpath.getText().toString().length() == 0) {
//                        mBtnStart.setEnabled(false);
//                    }

                    ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkAgentChannel();
                }
            });
        }

        @Override
        public void notifyQueryVpLanguage(final List<String> vpList) {
        }

        @Override
        public void notifyGetVpIndex(final byte index) {
        }

        @Override
        public void notifySetVpIndex(final boolean status) {
        }

        @Override
        public void onGameModeStateChanged(final boolean isEnabled) {
        }

        @Override
        public void onAudioPathChanged(final byte audioPath) {
        }

        @Override
        public void onResponseTimeout() {
        }

        @Override
        public void onStopped(final String stageName) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.updateMsg(MsgType.ERROR, LOG_TAG + "Failed in " + stageName);
                    setRadioButtonsEnabled(false);
                    mBtnStart.setEnabled(false);
                    mBtnRoleSwitch.setEnabled(false);
//                    ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkPartnerExistence();
//                    ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkAgentChannel();
                }
            });
        }

        @Override
        public void notifyGetAntennaInfo(byte role, AntennaInfo imfo) {

        }

        @Override
        public void notifySetAudioPath(final byte status) {
        }

        @Override
        public void notifyGameModeState(final byte state) {
        }

        @Override
        public void notifyGetPassThruGain(final short gain) {
        }

        @Override
        public void notifySetAncPassThruGain(final short gain) {
        }

        @Override
        public void notifyGetVaIndex(byte role, boolean status, byte index) {

        }

        @Override
        public void notifySetVaIndex(byte role, boolean status) {

        }

        @Override
        public void notifyGetKeyMap(byte role, boolean status, List<AirohaGestureInfo> gesture_info) {

        }

        @Override
        public void notifySetKeyMap(byte role, boolean status) {

        }

        @Override
        public void notifyReloadNv(byte role, boolean status) {

        }

        @Override
        public void notifyAncStatus(final byte status, final short gain) {
        }
    };

    @Override
    public void onStatusChanged(final int status) {
        switch (status) {
            case AirohaConnector.CONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            gLogger.e(e);
                        }
                        ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).addListener(TAG, mAirohaMmiListener);
                        ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkPartnerExistence();
//                        ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkAgentChannel();
                        setAllViewsEnabled(true);
                    }
                });
                break;
            case AirohaConnector.DISCONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIsPartnerExisting = false;
                        setAllViewsEnabled(false);
                        resetUiText();
                        mBtnStart.setEnabled(false);
                    }
                });
                break;
            case AirohaConnector.CONNECTING:
            case AirohaConnector.DISCONNECTING:
            case AirohaConnector.WAITING_CONNECTABLE:
            default:
                break;
        }
    }

    @Override
    public void onHostConnected() {

    }

    @Override
    public void onHostDisconnected() {

    }

    @Override
    public void onHostWaitingConnectable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                } catch (Exception e) {
                    gLogger.e(e);
                }
                mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Waiting for A2DP connected event...");
            }
        }).start();
    }

    @Override
    public void onHostInitialized() {

    }

    @Override
    public void onHostError(int errorCode) {

    }
}
