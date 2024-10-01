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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.device.AirohaDevice;
import com.airoha.sdk.api.message.AirohaA2DPConnectionMsg;
import com.airoha.sdk.api.message.AirohaAncSettings;
import com.airoha.sdk.api.message.AirohaAncStatusMsg;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaBatteryInfo;
import com.airoha.sdk.api.message.AirohaDeviceInfoMsg;
import com.airoha.sdk.api.message.AirohaGestureMsg;
import com.airoha.sdk.api.message.AirohaGestureSettings;
import com.airoha.sdk.api.message.AirohaMyBudsInfo;
import com.airoha.sdk.api.message.AirohaMyBudsMsg;
import com.airoha.sdk.api.message.AirohaOTAInfo;
import com.airoha.sdk.api.message.AirohaOTAInfoMsg;
import com.airoha.sdk.api.message.AirohaSealingInfo;
import com.airoha.sdk.api.message.AirohaShareModeInfo;
import com.airoha.sdk.api.message.AirohaSidetoneInfo;
import com.airoha.sdk.api.utils.AirohaAiIndex;
import com.airoha.sdk.api.utils.AirohaAncMode;
import com.airoha.sdk.api.utils.AirohaMessageID;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.sdk.api.utils.ChipType;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MmiFragment extends BaseFragment {
    private String TAG = MmiFragment.class.getSimpleName();
    private String LOG_TAG = "[MMI] ";
    private MmiFragment mFragment;
    private View mView;

    static final byte AUDIO_PATH_A2DP = 0x00;
    static final byte AUDIO_PATH_LINE_IN = 0x01;

    // Interaction
    private Button mBtnCheckChannel;
    private Button mBtnRoleSwitch;
    private TextView mTextAgentChannel;
    private TextView mTextPartnerChannel;

    private Switch mSwitchLightOnOff;
    private Switch mSwitchAlertOnOff;
    private RadioButton mRadioButtonLeft;
    private RadioButton mRadioButtonRight;
    private RadioButton mRadioButtonDual;
    private RadioButton mRadioButtonStop;
    private Button mBtnFindMe;
    private Button mBtnGetFindMeState;

    private Button mBtnGetAncSetting;
    private Button mBtnSaveAncSetting;
    private List<RadioButton> mAncRadioButtonList;
    private RadioButton mRadioButtonAncFilter1;
    private RadioButton mRadioButtonAncFilter2;
    private RadioButton mRadioButtonAncFilter3;
    private RadioButton mRadioButtonAncFilter4;
    private RadioButton mRadioButtonPassThrough1;
    private RadioButton mRadioButtonPassThrough2;
    private RadioButton mRadioButtonPassThrough3;
    private RadioButton mRadioButtonHybridPassThrough1;
    private RadioButton mRadioButtonHybridPassThrough2;
    private RadioButton mRadioButtonHybridPassThrough3;
    private RadioButton mRadioButtonAncPtOff;
    private List<View> mViewList = new ArrayList<>();
    private int mAncPassthruFilterSelected = 0;
    private String mAncModeSelectedString = "";
    private double mAncGainSelected = 0;
    private double mPassthruGainSelected = 0;
    private byte mAncMode = 0;

    private String mFindMeLightOnOff = "1";
    private String mFindMeAlertOnOff = "1";
    private byte mFindMeChannel = 0x00;
    private byte mFindMeAction = 0x00;

    private TextView mTextIsTwsConnected;
    private Button mBtnIsTwsConnected;

    private TextView mTextOtaStatus;
    private Button mBtnGetOtaStatus;

    private Spinner mSpinVaIndex;
    private Button mBtnSetVaIndex;
    private Button mBtnGetVaInfo;

    private Spinner mSpinGesture;
    private Spinner mSpinGestureAction;
    private Button mBtnSetGesture;
    private Button mBtnGetLeftGesture;
    private Button mBtnGetRightGesture;
    private Button mBtnResetLeftGesture;
    private Button mBtnResetRightGesture;
    private TextView mTextLeftSingleClickGestureAction;
    private TextView mTextLeftDoubleClickGestureAction;
    private TextView mTextLeftLongPressGestureAction;
    private TextView mTextLeftTripleClickGestureAction;
    private TextView mTextRightSingleClickGestureAction;
    private TextView mTextRightDoubleClickGestureAction;
    private TextView mTextRightLongPressGestureAction;
    private TextView mTextRightTripleClickGestureAction;

    private EditText mEditTextDeviceName;
    private Button mBtnSetDeviceName;

    private TextView mTextDeviceVid;
    private TextView mTextDevicePid;
    private TextView mTextDeviceMid;
    private TextView mTextDeviceFwVer;
    private TextView mTextDeviceMAC;
    private TextView mTextDeviceName;
    private TextView mTextRole;
    private TextView mTextAudioChannel;
    private TextView mTextIsConnectable;
    private TextView mTextPreferredProtocol;
    private TextView mTextDeviceUid;
    private Button mBtnGetDeviceInfo;

    private AppCompatSeekBar mSeekBarAncGain;
    private TextView mTextViewAncGain;
    private AppCompatSeekBar mSeekBarPassThroughGain;
    private TextView mTextViewPassThroughGain;
    private LinearLayout mLinearLayoutAncBar;

    private RadioButton mRadioButtonAutoPlayPauseON;
    private RadioButton mRadioButtonAutoPlayPauseOFF;
    private Button mBtnGetAutoPlayPauseStatus;
    private boolean mIsAutoPlayPauseON;

    private List<View> mAutoPowerOffViewList;
    private EditText mEditTextPowerOffInterval;
    private Button mBtnGetAutoPowerOffStatus;
    private Button mBtnSetAutoPowerOffStatus;

    private TextView mTextSealingStatus;
    private Button mBtnGetSealingStatus;
    private LinearLayout mLinearLayoutSeaingStatusWithMusic;
    private CheckBox mCkbSealingStatusWithMusic;
    private FilePickerDialog mSealingStatusMusicPathFilePickerDialog;
    private EditText mEditSealingStatusMusicPath;
    private Button mBtnSealingStatusMusicPathFilePicker;
    private static final int REQUEST_CHOOSE_SEALING_STATUS_MUSIC = 101;

    private TextView mTextA2dpConnectionStatus;
    private TextView mTextA2dpPhoneAddr;
    private Button mBtnGetA2dpConnectionStatus;

    private TextView mTextAgentBattery;
    private TextView mTextPartnerBattery;
    private Button mBtnGetBatteryInfo;

    private Button mBtnGetSmartSwitchStatus;
    private Button mBtnSetSmartSwitchStatus;
    private RadioButton mRadioButtonNormalMode;
    private RadioButton mRadioButtonGameMode;
    private int mSmartSwitchStatus = 0x01;

    private RadioButton mRadioButtonTouchON;
    private RadioButton mRadioButtonTouchOFF;
    private Button mBtnGetTouchStatus;
    private int mTouchStatus = 0x00;

    private RadioButton mRadioButtonAdvancedPassthroughON;
    private RadioButton mRadioButtonAdvancedPassthroughOFF;
    private Button mBtnGetAdvancedPassthroughStatus;
    private int mAdvancedPassthroughStatus = 0x00;

    private RadioButton mRadioButtonShareModeOFF;
    private RadioButton mRadioButtonShareModeAgent;
    private RadioButton mRadioButtonShareModeFollower;
    private Button mBtnGetShareModeState;
    private TextView mTextShareModeState;

    private RadioButton mRadioButtonSidetoneON;
    private RadioButton mRadioButtonSidetoneOFF;
    private EditText mEditTextSidetoneLevel;
    private Button mBtnGetSidetoneState;
    private Button mBtnSetSidetoneState;

    private boolean mIsResetGesture = false;

    //general layout
    private LinearLayout mIsTwsConnectedLayout;
    private LinearLayout mGetDeviceInfoLayout;
    private LinearLayout mGetBatteryInfoLayout;
    private LinearLayout mGetOtaStatusLayout;
    private LinearLayout mGetAncStatusLayout;
    private LinearLayout mLowLatencyStatusLayout;

    //155x/1565/1568/158x specific layout
    private LinearLayout mFindMuBudsLayout;
    private LinearLayout mSealingStatusLayout;
    private LinearLayout mAutoPlayPauseLayout;
    private LinearLayout mAutoPowerOffLayout;
    private LinearLayout mShareModeLayout;
    private LinearLayout mTouchStatusLayout;
    private LinearLayout mSidetoneStateLayout;
    private LinearLayout mAdvancedPassthroughStatusLayout;
    private LinearLayout mHybridPassthroughLayout;


    public MmiFragment(){
        mTitle = "MMI UT";
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
        AirohaSDK.getInst().getAirohaDeviceControl().registerGlobalListener(new UpdateDeviceStatusListener());
        AirohaSDK.getInst().getAirohaDeviceConnector().registerConnectionListener(mFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mmi, container,false);
        initUImember();

//        if(mActivity.getAirohaService() != null) {
//            initFlow();
//        }
        enableAllButton(false);

        mSidetoneStateLayout.setVisibility(View.GONE);

        if(AirohaSDK.getInst().getChipType() == ChipType.AB1562
                || AirohaSDK.getInst().mChipType == ChipType.AB1562E){
            mFindMuBudsLayout.setVisibility(View.GONE);
            mSealingStatusLayout.setVisibility(View.VISIBLE);
//            mShareModeLayout.setVisibility(View.GONE);
            mAutoPowerOffLayout.setVisibility(View.GONE);
            mTouchStatusLayout.setVisibility(View.GONE);
            mAdvancedPassthroughStatusLayout.setVisibility(View.GONE);
            mLinearLayoutSeaingStatusWithMusic.setVisibility(View.GONE);

            mBtnIsTwsConnected.performClick();
            mBtnGetDeviceInfo.performClick();
            mBtnGetOtaStatus.performClick();
            mBtnGetFindMeState.performClick();
            mBtnGetAncSetting.performClick();
            mBtnGetBatteryInfo.performClick();
            mBtnGetAutoPlayPauseStatus.performClick();
            mBtnGetSmartSwitchStatus.performClick();
            mBtnGetShareModeState.performClick();
//          mBtnGetSealingStatus.performClick();
            mBtnGetSealingStatus.setEnabled(true);
        }
        else if(AirohaSDK.getInst().getChipType() == ChipType.AB155x){
            mFindMuBudsLayout.setVisibility(View.VISIBLE);
            mSealingStatusLayout.setVisibility(View.VISIBLE);
//            mShareModeLayout.setVisibility(View.VISIBLE);
            mAutoPlayPauseLayout.setVisibility(View.GONE);
            mAutoPowerOffLayout.setVisibility(View.GONE);
            mTouchStatusLayout.setVisibility(View.GONE);
            mAdvancedPassthroughStatusLayout.setVisibility(View.GONE);
            mLinearLayoutSeaingStatusWithMusic.setVisibility(View.GONE);

            mBtnIsTwsConnected.performClick();
            mBtnGetDeviceInfo.performClick();
            mBtnGetOtaStatus.performClick();
            mBtnGetFindMeState.performClick();
            mBtnGetAncSetting.performClick();
            mBtnGetBatteryInfo.performClick();
            mBtnGetSmartSwitchStatus.performClick();
//          mBtnGetShareModeState.performClick();
            mBtnGetShareModeState.setEnabled(true);
//          mBtnGetSealingStatus.performClick();
            mBtnGetSealingStatus.setEnabled(true);
        }
        else if(AirohaSDK.getInst().getChipType() == ChipType.AB1568
                || AirohaSDK.getInst().getChipType() == ChipType.AB1568_V3){
            mFindMuBudsLayout.setVisibility(View.VISIBLE);
            mSealingStatusLayout.setVisibility(View.VISIBLE);
//            mShareModeLayout.setVisibility(View.VISIBLE);
            mAutoPlayPauseLayout.setVisibility(View.VISIBLE);
            mAutoPowerOffLayout.setVisibility(View.VISIBLE);
            mSidetoneStateLayout.setVisibility(View.VISIBLE);
            mTouchStatusLayout.setVisibility(View.VISIBLE);
            mAdvancedPassthroughStatusLayout.setVisibility(View.VISIBLE);
            mLinearLayoutSeaingStatusWithMusic.setVisibility(View.GONE);
            mHybridPassthroughLayout.setVisibility(View.VISIBLE);

            mBtnIsTwsConnected.performClick();
            mBtnGetDeviceInfo.performClick();
            mBtnGetOtaStatus.performClick();
            mBtnGetFindMeState.performClick();
            mBtnGetAncSetting.performClick();
            mBtnGetBatteryInfo.performClick();
            mBtnGetSmartSwitchStatus.performClick();
            mBtnGetTouchStatus.performClick();
            mBtnGetAdvancedPassthroughStatus.performClick();
//          mBtnGetShareModeState.performClick();
            mBtnGetShareModeState.setEnabled(true);
//          mBtnGetSealingStatus.performClick();
            mBtnGetSealingStatus.setEnabled(true);
            mBtnGetAutoPowerOffStatus.performClick();
//            mBtnGetAutoPlayPauseStatus.performClick();
            mBtnGetAutoPlayPauseStatus.setEnabled(true);
        }
        else if(AirohaSDK.getInst().getChipType() == ChipType.AB1565_DUAL
                || AirohaSDK.getInst().getChipType() == ChipType.AB1568_DUAL
                || AirohaSDK.getInst().getChipType() == ChipType.AB1565_DUAL_V3
                || AirohaSDK.getInst().getChipType() == ChipType.AB1568_DUAL_V3) {
            mFindMuBudsLayout.setVisibility(View.VISIBLE);
            mSealingStatusLayout.setVisibility(View.GONE);
            mShareModeLayout.setVisibility(View.GONE);
            mAutoPlayPauseLayout.setVisibility(View.GONE);
            mAutoPowerOffLayout.setVisibility(View.VISIBLE);
            mSidetoneStateLayout.setVisibility(View.VISIBLE);
            mIsTwsConnectedLayout.setVisibility(View.GONE);
            mAdvancedPassthroughStatusLayout.setVisibility(View.GONE);
            mLinearLayoutSeaingStatusWithMusic.setVisibility(View.GONE);
            mHybridPassthroughLayout.setVisibility(View.VISIBLE);

//            mBtnIsTwsConnected.performClick();
            mBtnGetDeviceInfo.performClick();
            mBtnGetOtaStatus.performClick();
            mBtnGetFindMeState.performClick();
            mBtnGetAncSetting.performClick();
            mBtnGetBatteryInfo.performClick();
            mBtnGetSmartSwitchStatus.performClick();
            mBtnGetTouchStatus.performClick();
//            mBtnGetShareModeState.setEnabled(true);
            mBtnGetSealingStatus.setEnabled(true);
            mBtnGetAutoPowerOffStatus.performClick();
            mBtnGetSidetoneState.performClick();
        }
        else if(AirohaSDK.getInst().getChipType() == ChipType.AB158x
        || AirohaSDK.getInst().getChipType() == ChipType.AB157x){
            mFindMuBudsLayout.setVisibility(View.VISIBLE);
            mSealingStatusLayout.setVisibility(View.VISIBLE);
//            mShareModeLayout.setVisibility(View.VISIBLE);
            mAutoPlayPauseLayout.setVisibility(View.VISIBLE);
            mAutoPowerOffLayout.setVisibility(View.VISIBLE);
            mTouchStatusLayout.setVisibility(View.VISIBLE);
            mSidetoneStateLayout.setVisibility(View.VISIBLE);
            mAdvancedPassthroughStatusLayout.setVisibility(View.GONE);
            mLinearLayoutSeaingStatusWithMusic.setVisibility(View.VISIBLE);
            mHybridPassthroughLayout.setVisibility(View.VISIBLE);

            mBtnIsTwsConnected.performClick();
            mBtnGetDeviceInfo.performClick();
            mBtnGetOtaStatus.performClick();
            mBtnGetFindMeState.performClick();
            mBtnGetAncSetting.performClick();
            mBtnGetBatteryInfo.performClick();
            mBtnGetSmartSwitchStatus.performClick();
            mBtnGetTouchStatus.performClick();
//            mBtnGetAdvancedPassthroughStatus.performClick();
//          mBtnGetShareModeState.performClick();
            mBtnGetShareModeState.setEnabled(true);
//          mBtnGetSealingStatus.performClick();
            mBtnGetSealingStatus.setEnabled(true);
            mBtnGetAutoPowerOffStatus.performClick();
//            mBtnGetAutoPlayPauseStatus.performClick();
            mBtnGetAutoPlayPauseStatus.setEnabled(true);
        }

        return mView;
    }

    private boolean mIsHidden = false;
    @Override
    public void onStatusChanged(int status) {
        switch (status) {
            case AirohaConnector.DISCONNECTED:
                if(!mIsHidden) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enableAllButton(false);
                            showMessageDialog("BT disconnected. Please reconnect BT.");
                        }
                    });
                }
                break;
            case AirohaConnector.CONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enableAllButton(true);
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

    private void showMessageDialog(String msg) {
        if(mActivity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Info.");
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mActivity.changeFragment(MainActivity.FragmentIndex.MENU);
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void enableAllButton(boolean isEnable) {
        mBtnIsTwsConnected.setEnabled(isEnable);
        mBtnGetDeviceInfo.setEnabled(isEnable);
        mBtnGetBatteryInfo.setEnabled(isEnable);
        mBtnGetOtaStatus.setEnabled(isEnable);
        mBtnSetDeviceName.setEnabled(isEnable);
        mBtnFindMe.setEnabled(isEnable);
        mBtnGetFindMeState.setEnabled(isEnable);
        mBtnGetVaInfo.setEnabled(isEnable);
        mBtnSetVaIndex.setEnabled(isEnable);
        mBtnSetGesture.setEnabled(isEnable);
        mBtnGetLeftGesture.setEnabled(isEnable);
        mBtnGetRightGesture.setEnabled(isEnable);
        mBtnResetLeftGesture.setEnabled(isEnable);
        mBtnResetRightGesture.setEnabled(isEnable);
        mBtnGetAncSetting.setEnabled(isEnable);
        mBtnSaveAncSetting.setEnabled(isEnable);
        mBtnGetSealingStatus.setEnabled(isEnable);
        mBtnGetAutoPlayPauseStatus.setEnabled(isEnable);
        mBtnGetAutoPowerOffStatus.setEnabled(isEnable);
        mBtnGetShareModeState.setEnabled(isEnable);
        mBtnGetTouchStatus.setEnabled(isEnable);
        mBtnGetSmartSwitchStatus.setEnabled(isEnable);
        mBtnSetSmartSwitchStatus.setEnabled(isEnable);
    }

    private void initUImember() {

        // layout
        mIsTwsConnectedLayout = mView.findViewById(R.id.layoutIsTwsConnected);
        mGetDeviceInfoLayout = mView.findViewById(R.id.layoutGetDeviceInfo);
        mGetBatteryInfoLayout = mView.findViewById(R.id.layoutGetBatteryInfo);
        mGetOtaStatusLayout = mView.findViewById(R.id.layoutGetOtaStatus);
        mFindMuBudsLayout = mView.findViewById(R.id.layoutFindMyBuds);
        mAutoPlayPauseLayout = mView.findViewById(R.id.layoutAutoPlayPause);
        mAutoPowerOffLayout = mView.findViewById(R.id.layoutAutoPowerOff);
        mGetAncStatusLayout = mView.findViewById(R.id.layoutAncStatus);
        mSealingStatusLayout = mView.findViewById(R.id.layoutSealingStatus);
        mLowLatencyStatusLayout = mView.findViewById(R.id.layoutLowLatencyStatus);
        mShareModeLayout = mView.findViewById(R.id.layoutShareMode);
        mTouchStatusLayout = mView.findViewById(R.id.layoutTouchStatus);
        mSidetoneStateLayout = mView.findViewById(R.id.layoutSidetoneState);
        mAdvancedPassthroughStatusLayout = mView.findViewById(R.id.layoutAdvancedPassthroughStatus);
        mHybridPassthroughLayout = mView.findViewById(R.id.linearLayout_hybrid_passthru);

        mBtnGetFindMeState = mView.findViewById(R.id.btnQueryFindMeState);
        addToViewList(mBtnGetFindMeState);
        mBtnGetFindMeState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetFindMeState.setEnabled(false);
                mBtnFindMe.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getFindMyBuds...");
                AirohaSDK.getInst().getAirohaDeviceControl().getFindMyBuds(new FindMyBudsListener());
            }
        });

        mBtnFindMe = mView.findViewById(R.id.btnFindMe);
        addToViewList(mBtnFindMe);
        mBtnFindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetFindMeState.setEnabled(false);
                mBtnFindMe.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setFindMyBuds...");
                AirohaSDK.getInst().getAirohaDeviceControl().setFindMyBuds(mFindMeChannel, mFindMeAction, new FindMyBudsListener());
            }
        });

        mSwitchLightOnOff = mView.findViewById(R.id.switch_mmi_find_me_light_onoff);
        addToViewList(mSwitchLightOnOff);
        mSwitchAlertOnOff = mView.findViewById(R.id.switch_mmi_find_me_alert_onoff);
        addToViewList(mSwitchAlertOnOff);
        mRadioButtonLeft = mView.findViewById(R.id.radioButton_mmi_find_me_left);
        addToViewList(mRadioButtonLeft);
        mRadioButtonRight = mView.findViewById(R.id.radioButton_mmi_find_me_right);
        addToViewList(mRadioButtonRight);
        mRadioButtonDual = mView.findViewById(R.id.radioButton_mmi_find_me_dual);
        addToViewList(mRadioButtonDual);
        mRadioButtonStop = mView.findViewById(R.id.radioButton_mmi_find_me_stop);
        addToViewList(mRadioButtonStop);

        mSwitchLightOnOff.setOnClickListener(mOnClickListener);
        mSwitchAlertOnOff.setOnClickListener(mOnClickListener);
        mRadioButtonLeft.setOnClickListener(mOnClickListener);
        mRadioButtonRight.setOnClickListener(mOnClickListener);
        mRadioButtonDual.setOnClickListener(mOnClickListener);
        mRadioButtonStop.setOnClickListener(mOnClickListener);

        mBtnGetAncSetting = mView.findViewById(R.id.buttonGetAncStatus);
        addToViewList(mBtnGetAncSetting);
        mBtnGetAncSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAncUiEnableStatus(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Get ANC setting...");
                AirohaSDK.getInst().getAirohaDeviceControl().getAncSetting(new AncSettingListener());
            }
        });

        mBtnSaveAncSetting = mView.findViewById(R.id.buttonSaveAncStatus);
        addToViewList(mBtnSaveAncSetting);
        mBtnSaveAncSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAncUiEnableStatus(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Save ANC setting...");
                AirohaAncSettings ancSetting = new AirohaAncSettings();
                ancSetting.setFilter(mAncPassthruFilterSelected);
                if (mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.ANC1.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.ANC2.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.ANC3.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.ANC4.ordinal()) {
                    ancSetting.setGain(mAncGainSelected);

                    if(mAncMode != AirohaAncMode.HYBRID.getValue()
                            && mAncMode != AirohaAncMode.FF.getValue()
                            && mAncMode != AirohaAncMode.FB.getValue()){
                        mAncMode = (byte)AirohaAncMode.HYBRID.getValue();
                    }
                } else if (mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough1.ordinal()
                        || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough2.ordinal()
                        || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough3.ordinal()) {
                    ancSetting.setGain(mPassthruGainSelected);

                    if(mAncMode != AirohaAncMode.HYBRID_PASSTHROUGH.getValue()
                            && mAncMode != AirohaAncMode.PASSTHROUGH.getValue()
                            && mAncMode != AirohaAncMode.PASSTHROUGH_FB.getValue()){
                        mAncMode = (byte)AirohaAncMode.HYBRID_PASSTHROUGH.getValue();
                    }
                } else if (mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.PassThrough1.ordinal()
                        || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.PassThrough2.ordinal()
                        || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.PassThrough3.ordinal()) {
                    ancSetting.setGain(mPassthruGainSelected);

                    if(mAncMode != AirohaAncMode.PASSTHROUGH.getValue()){
                        mAncMode = (byte)AirohaAncMode.PASSTHROUGH.getValue();
                    }
                } else{
                    ancSetting.setGain(mPassthruGainSelected);
                }

                ancSetting.setAncMode(mAncMode);
                AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, true, new AncSettingListener());
            }
        });

        mAncRadioButtonList = new ArrayList<>();
        mRadioButtonAncFilter1 = mView.findViewById(R.id.radioButton_mmi_anc_filter1);
        addToViewList(mRadioButtonAncFilter1);
        mAncRadioButtonList.add(mRadioButtonAncFilter1);
        mRadioButtonAncFilter2 = mView.findViewById(R.id.radioButton_mmi_anc_filter2);
        addToViewList(mRadioButtonAncFilter2);
        mAncRadioButtonList.add(mRadioButtonAncFilter2);
        mRadioButtonAncFilter3 = mView.findViewById(R.id.radioButton_mmi_anc_filter3);
        addToViewList(mRadioButtonAncFilter3);
        mAncRadioButtonList.add(mRadioButtonAncFilter3);
        mRadioButtonAncFilter4 = mView.findViewById(R.id.radioButton_mmi_anc_filter4);
        addToViewList(mRadioButtonAncFilter4);
        mAncRadioButtonList.add(mRadioButtonAncFilter4);
        mRadioButtonPassThrough1 = mView.findViewById(R.id.radioButton_mmi_pass_through1);
        addToViewList(mRadioButtonPassThrough1);
        mAncRadioButtonList.add(mRadioButtonPassThrough1);
        mRadioButtonPassThrough2 = mView.findViewById(R.id.radioButton_mmi_pass_through2);
        addToViewList(mRadioButtonPassThrough2);
        mAncRadioButtonList.add(mRadioButtonPassThrough2);
        mRadioButtonPassThrough3 = mView.findViewById(R.id.radioButton_mmi_pass_through3);
        addToViewList(mRadioButtonPassThrough3);
        mAncRadioButtonList.add(mRadioButtonPassThrough3);
        mRadioButtonHybridPassThrough1 = mView.findViewById(R.id.radioButton_mmi_hybrid_passthru1);
        addToViewList(mRadioButtonHybridPassThrough1);
        mAncRadioButtonList.add(mRadioButtonHybridPassThrough1);
        mRadioButtonHybridPassThrough2 = mView.findViewById(R.id.radioButton_mmi_hybrid_passthru2);
        addToViewList(mRadioButtonHybridPassThrough2);
        mAncRadioButtonList.add(mRadioButtonHybridPassThrough2);
        mRadioButtonHybridPassThrough3 = mView.findViewById(R.id.radioButton_mmi_hybrid_passthru3);
        addToViewList(mRadioButtonHybridPassThrough3);
        mAncRadioButtonList.add(mRadioButtonHybridPassThrough3);
        mRadioButtonAncPtOff = mView.findViewById(R.id.radioButton_mmi_anc_pt_off);
        addToViewList(mRadioButtonAncPtOff);
        mAncRadioButtonList.add(mRadioButtonAncPtOff);

        mRadioButtonAncFilter1.setOnClickListener(mOnClickListener);
        mRadioButtonAncFilter2.setOnClickListener(mOnClickListener);
        mRadioButtonAncFilter3.setOnClickListener(mOnClickListener);
        mRadioButtonAncFilter4.setOnClickListener(mOnClickListener);
        mRadioButtonPassThrough1.setOnClickListener(mOnClickListener);
        mRadioButtonPassThrough2.setOnClickListener(mOnClickListener);
        mRadioButtonPassThrough3.setOnClickListener(mOnClickListener);
        mRadioButtonHybridPassThrough1.setOnClickListener(mOnClickListener);
        mRadioButtonHybridPassThrough2.setOnClickListener(mOnClickListener);
        mRadioButtonHybridPassThrough3.setOnClickListener(mOnClickListener);
        mRadioButtonAncPtOff.setOnClickListener(mOnClickListener);

        mTextIsTwsConnected = mView.findViewById(R.id.textViewIsTwsConnected);
        mBtnIsTwsConnected = mView.findViewById(R.id.btnIsTwsConnected);
        addToViewList(mBtnIsTwsConnected);
        mBtnIsTwsConnected.setOnClickListener(mOnClickListener);

        mTextAgentBattery = mView.findViewById(R.id.textViewAgentBattery);
        mTextPartnerBattery = mView.findViewById(R.id.textViewPartnerBattery);

        mBtnGetBatteryInfo = mView.findViewById(R.id.btnGetBatteryInfo);
        addToViewList(mBtnGetBatteryInfo);
        mBtnGetBatteryInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetBatteryInfo.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getBatteryInfo...");
                mTextAgentBattery.setText("");
                mTextPartnerBattery.setText("");
                AirohaSDK.getInst().getAirohaDeviceControl().getBatteryInfo(new BatteryInfoListener());
            }
        });

        mTextOtaStatus = mView.findViewById(R.id.textViewOtaStatus);
        mBtnGetOtaStatus = mView.findViewById(R.id.btnGetOtaStatus);
        addToViewList(mBtnGetOtaStatus);
        mBtnGetOtaStatus.setOnClickListener(mOnClickListener);

        mSpinVaIndex = mView.findViewById(R.id.spinVaIndex);
        mSpinVaIndex.setSelection(2);
        addToViewList(mSpinVaIndex);

        mBtnGetVaInfo = mView.findViewById(R.id.buttonGetVaInfo);
        addToViewList(mBtnGetVaInfo);
        mBtnGetVaInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetVaInfo.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getMultiAIStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getMultiAIStatus(new MultiAiListener());
            }
        });

        mBtnSetVaIndex = mView.findViewById(R.id.buttonSetVaIndex);
        addToViewList(mBtnSetVaIndex);
        mBtnSetVaIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSetVaIndex.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setMultiAIStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().setMultiAIStatus(convertToAiIndex(mSpinVaIndex.getSelectedItemPosition()), new MultiAiListener());
            }
        });

        mEditTextDeviceName = mView.findViewById(R.id.editTextDeviceName);
        addToViewList(mEditTextDeviceName);
        mBtnSetDeviceName = mView.findViewById(R.id.btnSetDeviceName);
        addToViewList(mBtnSetDeviceName);
        mBtnSetDeviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = mEditTextDeviceName.getText().toString();
                if (deviceName.length() == 0) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "Invalid DeviceName!");
                    return;
                }
                mBtnSetDeviceName.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setDeviceName...");
                AirohaSDK.getInst().getAirohaDeviceControl().setDeviceName(deviceName, new SetDeviceNameListener());
            }
        });

        mTextDeviceVid = mView.findViewById(R.id.textViewVid);
        mTextDevicePid = mView.findViewById(R.id.textViewPid);
        mTextDeviceMid = mView.findViewById(R.id.textViewMid);
        mTextDeviceFwVer = mView.findViewById(R.id.textViewFwVer);
        mTextDeviceMAC = mView.findViewById(R.id.textViewDeviceMAC);
        mTextDeviceName = mView.findViewById(R.id.textViewDeviceName);
        mTextRole = mView.findViewById(R.id.textViewRole);
        mTextAudioChannel = mView.findViewById(R.id.textViewAudioChannel);
        mTextIsConnectable = mView.findViewById(R.id.textViewIsConnectable);
        mTextPreferredProtocol = mView.findViewById(R.id.textViewPreferredProtocol);
        mTextDeviceUid = mView.findViewById(R.id.textViewDeviceUid);

        mBtnGetDeviceInfo = mView.findViewById(R.id.btnGetDeviceInfo);
        addToViewList(mBtnGetDeviceInfo);
        mBtnGetDeviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetDeviceInfo.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getDeviceInfo...");
                AirohaSDK.getInst().getAirohaDeviceControl().getDeviceInfo(new DeviceInfoListener());
            }
        });

        mLinearLayoutAncBar = mView.findViewById(R.id.linearLayout_mmi_realtime_anc_gain);
        mTextViewAncGain = mView.findViewById(R.id.textView_anc_gain);
        mTextViewPassThroughGain = mView.findViewById(R.id.textView_passthru_gain);
        mSeekBarAncGain = mView.findViewById(R.id.seekBar_anc_gain);
        mSeekBarPassThroughGain = mView.findViewById(R.id.seekBar_passthru_gain);
//        addToViewList(mSeekBarAncGain);
//        addToViewList(mSeekBarPassThroughGain);

        mSeekBarAncGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = (float) Math.round((progress - mSeekBarAncGain.getMax()) / 100);
                mTextViewAncGain.setText(String.format("%.2f", value));
                mAncGainSelected = Double.parseDouble(mTextViewAncGain.getText().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setAncUiEnableStatus(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Set ANC Gain...");
                AirohaAncSettings ancSetting = new AirohaAncSettings();
                ancSetting.setFilter(mAncPassthruFilterSelected);
                ancSetting.setGain(mAncGainSelected);
                ancSetting.setAncMode(mAncMode);
                AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
            }
        });
        mSeekBarPassThroughGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = (float) Math.round((progress - mSeekBarPassThroughGain.getMax()) / 100);
                mTextViewPassThroughGain.setText(String.format("%.2f", value));
                mPassthruGainSelected = Double.parseDouble(mTextViewPassThroughGain.getText().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setAncUiEnableStatus(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Set Passthrough Gain...");
                AirohaAncSettings ancSetting = new AirohaAncSettings();
                ancSetting.setFilter(mAncPassthruFilterSelected);
                ancSetting.setGain(mPassthruGainSelected);
                if((mAncMode != AirohaAncMode.HYBRID_PASSTHROUGH.getValue() && mAncMode != AirohaAncMode.PASSTHROUGH.getValue() && mAncMode != AirohaAncMode.PASSTHROUGH_FB.getValue())
                        && (mRadioButtonHybridPassThrough1.isChecked() || mRadioButtonHybridPassThrough2.isChecked() || mRadioButtonHybridPassThrough3.isChecked())){
                    mAncMode = (byte)AirohaAncMode.HYBRID_PASSTHROUGH.getValue();
                } else if(mAncMode != AirohaAncMode.PASSTHROUGH.getValue()
                        && (mRadioButtonPassThrough1.isChecked() || mRadioButtonPassThrough2.isChecked() || mRadioButtonPassThrough3.isChecked())){
                    mAncMode = (byte)AirohaAncMode.PASSTHROUGH.getValue();
                }
                ancSetting.setAncMode(mAncMode);
                AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
            }
        });

//        mBtnSetDeviceName.setEnabled(false);
        mBtnIsTwsConnected.setEnabled(true);

        mSeekBarAncGain.setEnabled(false);
        mSeekBarPassThroughGain.setEnabled(false);

        mTextLeftSingleClickGestureAction = mView.findViewById(R.id.textLeftSingleClickGestureAction);
        mTextLeftDoubleClickGestureAction = mView.findViewById(R.id.textLeftDoubleClickGestureAction);
        mTextLeftLongPressGestureAction = mView.findViewById(R.id.textLeftLongPressGestureAction);
        mTextLeftTripleClickGestureAction = mView.findViewById(R.id.textLeftTripleClickGestureAction);
        mTextRightSingleClickGestureAction = mView.findViewById(R.id.textRightSingleClickGestureAction);
        mTextRightDoubleClickGestureAction = mView.findViewById(R.id.textRightDoubleClickGestureAction);
        mTextRightLongPressGestureAction = mView.findViewById(R.id.textRightLongPressGestureAction);
        mTextRightTripleClickGestureAction = mView.findViewById(R.id.textRightTripleClickGestureAction);

        mSpinGesture = mView.findViewById(R.id.spinGesture);
        mSpinGestureAction = mView.findViewById(R.id.spinGestureAction);
        mBtnSetGesture = mView.findViewById(R.id.btnSetGesture);
        mBtnSetGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSetGesture.setEnabled(false);
                mBtnResetLeftGesture.setEnabled(false);
                mBtnResetRightGesture.setEnabled(false);
                mIsResetGesture = false;
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setGestureStatus...");
                List<AirohaGestureSettings> list = new ArrayList<>();
                AirohaGestureSettings info = new AirohaGestureSettings();
                info.setGestureId(mSpinGesture.getSelectedItemPosition() + 1);
                info.setActionId(getGestureActionId(mSpinGestureAction.getSelectedItem().toString()));
                list.add(info);
                AirohaSDK.getInst().getAirohaDeviceControl().setGestureStatus(list, new GestureListener());
            }
        });

        mBtnGetLeftGesture = mView.findViewById(R.id.btnGetLeftGesture);
        mBtnGetLeftGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetLeftGesture.setEnabled(false);
                mBtnGetRightGesture.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getGestureStatus...");
                mTextLeftSingleClickGestureAction.setText("");
                mTextLeftDoubleClickGestureAction.setText("");
                mTextLeftLongPressGestureAction.setText("");
                mTextLeftTripleClickGestureAction.setText("");
                AirohaSDK.getInst().getAirohaDeviceControl().getGestureStatus(AirohaGestureSettings.LEFT_ALL, new GestureListener());
            }
        });
        mBtnGetRightGesture = mView.findViewById(R.id.btnGetRightGesture);
        mBtnGetRightGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetLeftGesture.setEnabled(false);
                mBtnGetRightGesture.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getGestureStatus...");
                mTextRightSingleClickGestureAction.setText("");
                mTextRightDoubleClickGestureAction.setText("");
                mTextRightLongPressGestureAction.setText("");
                mTextRightTripleClickGestureAction.setText("");
                AirohaSDK.getInst().getAirohaDeviceControl().getGestureStatus(AirohaGestureSettings.RIGHT_ALL, new GestureListener());
            }
        });

        mBtnResetLeftGesture = mView.findViewById(R.id.btnResetLeftGesture);
        mBtnResetLeftGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSetGesture.setEnabled(false);
                mBtnResetLeftGesture.setEnabled(false);
                mBtnResetRightGesture.setEnabled(false);
                mIsResetGesture = true;
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "ResetGestureStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().resetGestureStatus(AirohaGestureSettings.LEFT_ALL, new GestureListener());
            }
        });
        mBtnResetRightGesture = mView.findViewById(R.id.btnResetRightGesture);
        mBtnResetRightGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSetGesture.setEnabled(false);
                mBtnResetLeftGesture.setEnabled(false);
                mBtnResetRightGesture.setEnabled(false);
                mIsResetGesture = true;
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "ResetGestureStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().resetGestureStatus(AirohaGestureSettings.RIGHT_ALL, new GestureListener());
            }
        });

        mTextSealingStatus = mView.findViewById(R.id.textSealingStatus);
        mBtnGetSealingStatus = mView.findViewById(R.id.btnGetSeakingStatus);
        mBtnGetSealingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = mEditSealingStatusMusicPath.getText().toString();
                if(mCkbSealingStatusWithMusic.isChecked() && path.isEmpty()){
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "Invalid music path!");
                    return;
                }
                mBtnGetSealingStatus.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getSealingStatus...");
                mTextSealingStatus.setText("");
                if(mCkbSealingStatusWithMusic.isChecked()){
                    AirohaSDK.getInst().getAirohaDeviceControl().getSealingStatusWithMusic(path, new SealingStatusListener());
                }
                else {
                    AirohaSDK.getInst().getAirohaDeviceControl().getSealingStatus(new SealingStatusListener());
                }
            }
        });
        mLinearLayoutSeaingStatusWithMusic = mView.findViewById(R.id.linearLayoutSealingStatusWithMusic);
        mCkbSealingStatusWithMusic = mView.findViewById(R.id.ckbSealingStatusWithMusic);
        mEditSealingStatusMusicPath= mView.findViewById(R.id.editTextSealingStatusMusicPath);
        mBtnSealingStatusMusicPathFilePicker = mView.findViewById(R.id.buttonSealingStatusMusicPathFilePicker);
        mBtnSealingStatusMusicPathFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnSealingStatusMusicPathFilePicker clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showFileChooser(REQUEST_CHOOSE_SEALING_STATUS_MUSIC);
                } else {
                    mSealingStatusMusicPathFilePickerDialog.show();
                }
            }
        });
        initSealingStatusMusicPathFileDialog();

        mRadioButtonAutoPlayPauseON = mView.findViewById(R.id.radioButtonMmiAutoPlayPauseOn);
        mRadioButtonAutoPlayPauseON.setOnClickListener(mOnClickListener);
        mRadioButtonAutoPlayPauseOFF = mView.findViewById(R.id.radioButtonMmiAutoPlayPauseOff);
        mRadioButtonAutoPlayPauseOFF.setOnClickListener(mOnClickListener);
        mBtnGetAutoPlayPauseStatus = mView.findViewById(R.id.btnGetAutoPlayPauseStatus);
        mBtnGetAutoPlayPauseStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetAutoPlayPauseStatus.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getAutoPlayPauseStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getAutoPlayPauseStatus(new AutoPlayPauseStatusListener());
            }
        });

        mAutoPowerOffViewList = new LinkedList<>();
        mEditTextPowerOffInterval = mView.findViewById(R.id.editTextAutoPowerOffInterval);
        mBtnGetAutoPowerOffStatus = mView.findViewById(R.id.btnGetAutoPowerOffStatus);
        mBtnGetAutoPowerOffStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (View view: mAutoPowerOffViewList) {
                    view.setEnabled(false);
                }
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getAutoPowerOffStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getAutoPowerOffStatus(new AutoPowerOffStatusListener());
            }
        });

        mBtnSetAutoPowerOffStatus = mView.findViewById(R.id.btnSetAutoPowerOffStatus);
        mBtnSetAutoPowerOffStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String interval = mEditTextPowerOffInterval.getText().toString();
                if (interval.isEmpty()) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "Invalid Auto Power Off interval!");
                    return;
                }
                for (View view: mAutoPowerOffViewList) {
                    view.setEnabled(false);
                }
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setAutoPowerOffStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().setAutoPowerOffStatus(Integer.valueOf(interval), new AutoPowerOffStatusListener());
            }
        });
        mAutoPowerOffViewList.add(mEditTextPowerOffInterval);
        mAutoPowerOffViewList.add(mBtnGetAutoPowerOffStatus);
        mAutoPowerOffViewList.add(mBtnSetAutoPowerOffStatus);

        mBtnGetSmartSwitchStatus = mView.findViewById(R.id.btnGetSmartSwitchStatus);
        mBtnSetSmartSwitchStatus = mView.findViewById(R.id.btnSetSmartSwitchStatus);
        mRadioButtonNormalMode = mView.findViewById(R.id.radioButton_mmi_normal_mode);
        mRadioButtonGameMode = mView.findViewById(R.id.radioButton_mmi_game_mode);
        mBtnGetSmartSwitchStatus.setOnClickListener(mOnClickListener);
        mBtnSetSmartSwitchStatus.setOnClickListener(mOnClickListener);
        mRadioButtonNormalMode.setOnClickListener(mOnClickListener);
        mRadioButtonGameMode.setOnClickListener(mOnClickListener);

        mRadioButtonTouchON = mView.findViewById(R.id.radioButton_touch_enable);
        mRadioButtonTouchOFF = mView.findViewById(R.id.radioButton_touch_disable);
        mBtnGetTouchStatus = mView.findViewById(R.id.btnGetTouchStatus);
        mBtnGetTouchStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetTouchStatus.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getTouchStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getTouchStatus(new TouchStatusListener());
            }
        });
        mRadioButtonTouchON.setOnClickListener(mOnClickListener);
        mRadioButtonTouchOFF.setOnClickListener(mOnClickListener);

        mRadioButtonAdvancedPassthroughON = mView.findViewById(R.id.radioButton_advanced_passthrough_enable);
        mRadioButtonAdvancedPassthroughOFF = mView.findViewById(R.id.radioButton_advanced_passthrough_disable);
        mBtnGetAdvancedPassthroughStatus = mView.findViewById(R.id.btnGetAdvancedPassthroughStatus);
        mBtnGetAdvancedPassthroughStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGetAdvancedPassthroughStatus.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getAdvancedPassthroughStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getAdvancedPassthroughStatus(new AdvancedPassthroughStatusListener());
            }
        });
        mRadioButtonAdvancedPassthroughON.setOnClickListener(mOnClickListener);
        mRadioButtonAdvancedPassthroughOFF.setOnClickListener(mOnClickListener);

        mTextShareModeState = mView.findViewById(R.id.textViewShareModeState);
        mRadioButtonShareModeOFF = mView.findViewById(R.id.radioButton_mmi_share_mode_off);
        mRadioButtonShareModeOFF.setOnClickListener(mOnClickListener);
        mRadioButtonShareModeAgent = mView.findViewById(R.id.radioButton_mmi_share_mode_agent);
        mRadioButtonShareModeAgent.setOnClickListener(mOnClickListener);
        mRadioButtonShareModeFollower = mView.findViewById(R.id.radioButton_mmi_share_mode_follower);
        mRadioButtonShareModeFollower.setOnClickListener(mOnClickListener);
        mBtnGetShareModeState = mView.findViewById(R.id.btnGetShareModeState);
        mBtnGetShareModeState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getShareModeState...");
                mTextShareModeState.setText("");
                AirohaSDK.getInst().getAirohaDeviceControl().getShareModeState(new ShareModeStateListener());
            }
        });

        mRadioButtonSidetoneON = mView.findViewById(R.id.radioButtonMmiSidetoneOn);
        mRadioButtonSidetoneOFF = mView.findViewById(R.id.radioButtonMmiSidetoneOff);
        mEditTextSidetoneLevel = mView.findViewById(R.id.editTextSidetoneLevel);
        mBtnGetSidetoneState = mView.findViewById(R.id.btnGetSidetoneState);
        mBtnGetSidetoneState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "mBtnGetSidetoneState...");
                AirohaSDK.getInst().getAirohaDeviceControl().getSidetoneState(new SidetoneStateListener());
            }
        });
        mBtnSetSidetoneState = mView.findViewById(R.id.btnSetSidetoneState);
        mBtnSetSidetoneState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isON = mRadioButtonSidetoneON.isChecked()?true:false;
                String level = mEditTextSidetoneLevel.getText().toString();
                if (level.isEmpty()) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "Invalid Sidetone Level!");
                    return;
                }
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "mBtnSetSidetoneState...");
                AirohaSidetoneInfo info = new AirohaSidetoneInfo(isON, Short.valueOf(level));
                AirohaSDK.getInst().getAirohaDeviceControl().setSidetoneState(info, new SidetoneStateListener());
            }
        });
    }

    private void addToViewList(View view){
        mViewList.add(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        gLogger.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        gLogger.d(TAG, "onPause");
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
        super.onHiddenChanged(hidden);
        if (hidden) {
            mIsHidden = true;
        } else {
            mIsHidden = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        gLogger.d(TAG, "onActivityResult: requestCode= " + requestCode
                + ", resultCode= " + resultCode);
        if (resultCode != Activity.RESULT_OK || data == null) {
            showInvalidBinPathDialog();
            return;
        }
        switch (requestCode) {
            case REQUEST_CHOOSE_SEALING_STATUS_MUSIC:
                Uri uri = data.getData();
                String path = uri.getPath();
                if (path != null && path.length() > 0) {
                    gLogger.d(TAG, "uri.getPath() = " + path);
                    File tmpFile = uriToFileApiQ(uri);
                    if (tmpFile == null) {
                        showInvalidMusicPathDialog();
                        return;
                    }
                    mEditSealingStatusMusicPath.setText(tmpFile.getAbsolutePath());
                } else {
                    showInvalidMusicPathDialog();
                }
                break;
        }
    }

    @Override
    protected void showFileChooser(final int requestID) {
        showFileChooser(requestID, "audio/*", "Choose music file");
    }

    private void initSealingStatusMusicPathFileDialog(){
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"wav", "mp3"};
        mSealingStatusMusicPathFilePickerDialog = new FilePickerDialog(mActivity, properties);
        mSealingStatusMusicPathFilePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    String path = files[0].toString();
                    mEditSealingStatusMusicPath.setText(path);
                    gLogger.d(TAG, "Select SealingStatusMusic: " + path);
                }
            }
        });
    }

    private int convertToAiIndex(int ui_index){
        switch(ui_index){
            case 0:
                return AirohaAiIndex.GOOGLE_AI.getValue();
            case 1:
                return AirohaAiIndex.AMAZON_AI.getValue();
            case 3:
                return AirohaAiIndex.XIAOWEI_AI.getValue();
            case 2:
            default:
                return AirohaAiIndex.SIRI_AI.getValue();
        }
    }

    private int convertToUiIndex(int ai_index){
        switch(ai_index){
            case 0: //AMAZON_AI
                return 1;
            case 1://GOOGLE_AI
                return 0;
            case 2://XIAOWEI_AI
                return 3;
            case -1://SIRI_AI
            default:
                return 2;
        }
    }

    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnIsTwsConnected:
                    mBtnIsTwsConnected.setEnabled(false);
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getTwsConnectStatus...");
                    AirohaSDK.getInst().getAirohaDeviceControl().getTwsConnectStatus(new TwsConnectStatusListener());
                    break;
                case R.id.btnGetOtaStatus:
                    mBtnGetOtaStatus.setEnabled(false);
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getRunningAirohaOTAInfo...");
                    AirohaSDK.getInst().getAirohaDeviceControl().getRunningOTAInfo(new OtaStatusListener());
                    break;
                case R.id.switch_mmi_find_me_light_onoff:
                    mFindMeLightOnOff = ((Switch)v).isChecked() ? "1":"0";
                    break;
                case R.id.switch_mmi_find_me_alert_onoff:
                    mFindMeAlertOnOff = ((Switch)v).isChecked() ? "1":"0";
                    break;
                case R.id.radioButton_mmi_find_me_left:
                    if (((RadioButton)v).isChecked()) {
                        mFindMeChannel = 0x02;
                        mFindMeAction = 0x03;
                    }
                    break;
                case R.id.radioButton_mmi_find_me_right:
                    if (((RadioButton)v).isChecked()) {
                        mFindMeChannel = 0x01;
                        mFindMeAction = 0x03;
                    }
                    break;
                case R.id.radioButton_mmi_find_me_dual:
                    if (((RadioButton)v).isChecked()) {
                        mFindMeChannel = 0x03;
                        mFindMeAction = 0x03;
                    }
                    break;
                case R.id.radioButton_mmi_find_me_stop:
                    if (((RadioButton)v).isChecked()) {
                        mFindMeChannel = 0x00;
                        mFindMeAction = 0x00;
                    }
                    break;
                case R.id.radioButtonMmiAutoPlayPauseOn:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setAutoPlayPauseStatus...");
                        mIsAutoPlayPauseON = true;
                        AirohaSDK.getInst().getAirohaDeviceControl().setAutoPlayPauseStatus(mIsAutoPlayPauseON, new AutoPlayPauseStatusListener());
                    }
                    break;
                case R.id.radioButtonMmiAutoPlayPauseOff:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setAutoPlayPauseStatus...");
                        mIsAutoPlayPauseON = false;
                        AirohaSDK.getInst().getAirohaDeviceControl().setAutoPlayPauseStatus(mIsAutoPlayPauseON, new AutoPlayPauseStatusListener());
                    }
                    break;
                case R.id.radioButton_mmi_anc_filter1:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.ANC1.ordinal();
                        showAncModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_anc_filter2:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.ANC2.ordinal();
                        showAncModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_anc_filter3:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.ANC3.ordinal();
                        showAncModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_anc_filter4:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.ANC4.ordinal();
                        showAncModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_pass_through1:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        setAncRadioButtonText(0, "");
                        ((RadioButton) v).setChecked(true);
                        mAncModeSelectedString = "";
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.PassThrough1.ordinal();
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Set Passthrough1...");
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mPassthruGainSelected);
                        ancSetting.setAncMode(4);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                    }
                    break;
                case R.id.radioButton_mmi_pass_through2:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        setAncRadioButtonText(0, "");
                        ((RadioButton) v).setChecked(true);
                        mAncModeSelectedString = "";
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.PassThrough2.ordinal();
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Set Passthrough2...");
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mPassthruGainSelected);
                        ancSetting.setAncMode(4);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                    }
                    break;
                case R.id.radioButton_mmi_pass_through3:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        setAncRadioButtonText(0, "");
                        ((RadioButton) v).setChecked(true);
                        mAncModeSelectedString = "";
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.PassThrough3.ordinal();
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Set Passthrough3...");
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mPassthruGainSelected);
                        ancSetting.setAncMode(4);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                    }
                    break;
                case R.id.radioButton_mmi_hybrid_passthru1:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough1.ordinal();
                        showHybridPassthroughModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_hybrid_passthru2:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough2.ordinal();
                        showHybridPassthroughModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_hybrid_passthru3:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough3.ordinal();
                        showHybridPassthroughModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_anc_pt_off:
                    if (((RadioButton) v).isChecked()) {
                        setAncUiEnableStatus(false);
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncModeSelectedString = "";
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.OFF.ordinal();
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Set ANC OFF...");
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(0);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                    }
                    break;
                case R.id.radioButton_mmi_normal_mode:
                    if (((RadioButton)v).isChecked()) {
                        mSmartSwitchStatus = 0x01;
                    }
                    break;
                case R.id.radioButton_mmi_game_mode:
                    if (((RadioButton)v).isChecked()) {
                        mSmartSwitchStatus = 0x02;
                    }
                    break;
                case R.id.btnGetSmartSwitchStatus:
                    AirohaSDK.getInst().getAirohaDeviceControl().getSmartSwitchStatus(new SmartSwitchStatusListener());
                    break;
                case R.id.btnSetSmartSwitchStatus:
                    AirohaSDK.getInst().getAirohaDeviceControl().setSmartSwitchStatus(mSmartSwitchStatus, new SmartSwitchStatusListener());
                    break;
                case R.id.radioButton_touch_enable:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setTouchStatus...");
                        mTouchStatus = 0x01;
                        AirohaSDK.getInst().getAirohaDeviceControl().setTouchStatus(mTouchStatus, new TouchStatusListener());
                    }
                    break;
                case R.id.radioButton_touch_disable:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setTouchStatus...");
                        mTouchStatus = 0x00;
                        AirohaSDK.getInst().getAirohaDeviceControl().setTouchStatus(mTouchStatus, new TouchStatusListener());
                    }
                    break;
                case R.id.radioButton_mmi_share_mode_off:
                    if (((RadioButton) v).isChecked()) {
                        mRadioButtonShareModeOFF.setChecked(true);
                        mRadioButtonShareModeOFF.setEnabled(false);
                        mRadioButtonShareModeAgent.setChecked(false);
                        mRadioButtonShareModeAgent.setEnabled(false);
                        mRadioButtonShareModeFollower.setChecked(false);
                        mRadioButtonShareModeFollower.setEnabled(false);
                        mTextShareModeState.setText("");
                        AirohaSDK.getInst().getAirohaDeviceControl().setShareMode((byte)0x00, new ShareModeStateListener());
                    }
                    break;
                case R.id.radioButton_mmi_share_mode_agent:
                    if (((RadioButton) v).isChecked()) {
                        mRadioButtonShareModeOFF.setChecked(false);
                        mRadioButtonShareModeOFF.setEnabled(false);
                        mRadioButtonShareModeAgent.setChecked(true);
                        mRadioButtonShareModeAgent.setEnabled(false);
                        mRadioButtonShareModeFollower.setChecked(false);
                        mRadioButtonShareModeFollower.setEnabled(false);
                        mTextShareModeState.setText("");
                        AirohaSDK.getInst().getAirohaDeviceControl().setShareMode((byte)0x01, new ShareModeStateListener());
                    }
                    break;
                case R.id.radioButton_mmi_share_mode_follower:
                    if (((RadioButton) v).isChecked()) {
                        mRadioButtonShareModeOFF.setChecked(false);
                        mRadioButtonShareModeOFF.setEnabled(false);
                        mRadioButtonShareModeAgent.setChecked(false);
                        mRadioButtonShareModeAgent.setEnabled(false);
                        mRadioButtonShareModeFollower.setChecked(true);
                        mRadioButtonShareModeFollower.setEnabled(false);
                        mTextShareModeState.setText("");
                        AirohaSDK.getInst().getAirohaDeviceControl().setShareMode((byte)0x02, new ShareModeStateListener());
                    }
                    break;
                case R.id.radioButton_advanced_passthrough_enable:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setAdvancedPassthroughStatus...");
                        mAdvancedPassthroughStatus = 0x01;
                        AirohaSDK.getInst().getAirohaDeviceControl().setAdvancedPassthroughStatus(mAdvancedPassthroughStatus, new AdvancedPassthroughStatusListener());
                    }
                    break;
                case R.id.radioButton_advanced_passthrough_disable:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setAdvancedPassthroughStatus...");
                        mAdvancedPassthroughStatus = 0x00;
                        AirohaSDK.getInst().getAirohaDeviceControl().setAdvancedPassthroughStatus(mAdvancedPassthroughStatus, new AdvancedPassthroughStatusListener());
                    }
                    break;
            }
        }
    };


    class DeviceInfoListener implements AirohaDeviceListener {

        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            final AirohaDeviceInfoMsg deviceInfoMessage = (AirohaDeviceInfoMsg) msg;
                            LinkedList<AirohaDevice> content = (LinkedList<AirohaDevice>) deviceInfoMessage.getMsgContent();
                            AirohaDevice airohaDevice = content.get(0);
                            if (AirohaSDK.getInst().isPartnerExisting()) {
                                AirohaDevice airohaDevicePartner = content.get(1);
                                mTextDeviceVid.setText(airohaDevice.getDeviceVid() + ", " + airohaDevicePartner.getDeviceVid());
                                mTextDevicePid.setText(airohaDevice.getDevicePid() + ", " + airohaDevicePartner.getDevicePid());
                                mTextDeviceMid.setText(airohaDevice.getDeviceMid() + ", " + airohaDevicePartner.getDeviceMid());
                                mTextDeviceFwVer.setText(airohaDevice.getFirmwareVer() + ", " + airohaDevicePartner.getFirmwareVer());
                                mTextDeviceMAC.setText(airohaDevice.getDeviceMAC() + ", " + airohaDevicePartner.getDeviceMAC());
                                mTextDeviceName.setText(airohaDevice.getDeviceName() + ", " + airohaDevicePartner.getDeviceName());
                                mTextRole.setText(airohaDevice.getRole().getName() + ", " + airohaDevicePartner.getRole().getName());
                                mTextAudioChannel.setText(airohaDevice.getChannel().getName() + ", " + airohaDevicePartner.getChannel().getName());
                                mTextIsConnectable.setText(String.valueOf(airohaDevice.isConnectable()) + ", " + String.valueOf(airohaDevicePartner.isConnectable()));
                                mTextPreferredProtocol.setText(airohaDevice.getPreferredProtocol().getName() + ", " + airohaDevicePartner.getPreferredProtocol().getName());
                                mTextDeviceUid.setText(airohaDevice.getDeviceUid() + ", " + airohaDevicePartner.getDeviceUid());
                            } else {
                                mTextDeviceVid.setText(airohaDevice.getDeviceVid() + ", not found");
                                mTextDevicePid.setText(airohaDevice.getDevicePid() + ", not found");
                                mTextDeviceMid.setText(airohaDevice.getDeviceMid() + ", not found");
                                mTextDeviceFwVer.setText(airohaDevice.getFirmwareVer() + ", not found");
                                mTextDeviceMAC.setText(airohaDevice.getDeviceMAC() + ", not found");
                                mTextDeviceName.setText(airohaDevice.getDeviceName() + ", not found");
                                mTextRole.setText(airohaDevice.getRole().getName() + ", not found");
                                mTextAudioChannel.setText(airohaDevice.getChannel().getName() + ", not found");
                                mTextIsConnectable.setText(String.valueOf(airohaDevice.isConnectable()) + ", not found");
                                mTextPreferredProtocol.setText(airohaDevice.getPreferredProtocol().getName() + ", not found");
                                mTextDeviceUid.setText(airohaDevice.getDeviceUid() + ", not found");
                            }

                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getDeviceInfo: " + code.getDescription());
                        mBtnGetDeviceInfo.setEnabled(true);
                        mBtnSetDeviceName.setEnabled(true);
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }


    class TwsConnectStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            Boolean isTwsConnected = (Boolean) msg.getMsgContent();
                            String text = isTwsConnected ? "True" : "False";
                            mTextIsTwsConnected.setText(text);
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getTwsConnectStatus: " + code.getDescription());
                        mBtnIsTwsConnected.setEnabled(true);
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }

    class OtaStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            final AirohaOTAInfoMsg otaInfoMessage = (AirohaOTAInfoMsg) msg;
                            AirohaOTAInfo content = otaInfoMessage.getMsgContent();
                            mTextOtaStatus.setText(content.getFotaStatus().getName());
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getRunningAirohaOTAInfo: " + code.getDescription());
                        mBtnGetOtaStatus.setEnabled(true);
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }

    void updateFindMyBudsUI(final AirohaStatusCode code, final AirohaMyBudsMsg msg) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (code == AirohaStatusCode.STATUS_SUCCESS) {
                        AirohaMyBudsInfo info = msg.getMsgContent();
                        switch (info.getTargetDeviceChannel()) {
                            case 0x01: /// right
                                mRadioButtonRight.performClick();
                                break;
                            case 0x02:  /// left
                                mRadioButtonLeft.performClick();
                                break;
                            case 0x03:
                                mRadioButtonDual.performClick();
                                break;
                            default:
                                mRadioButtonStop.performClick();
                                break;
                        }
                    }
                    mBtnGetFindMeState.setEnabled(true);
                    mBtnFindMe.setEnabled(true);
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "FindMyBuds: " + code.getDescription());
                    mBtnGetOtaStatus.setEnabled(true);
                }  catch (Exception e) {
                    gLogger.e(e);
                }
            }
        });
    }

    class FindMyBudsListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, AirohaBaseMsg msg) {
            try {
                final AirohaMyBudsMsg myBudsMessage = (AirohaMyBudsMsg) msg;
                updateFindMyBudsUI(code, myBudsMessage);
            }  catch (Exception e) {
                gLogger.e(e);
            }
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {
            try {
                final AirohaMyBudsMsg myBudsMessage;
                if (code.equals(AirohaStatusCode.STATUS_FAIL)) {
                    AirohaMyBudsInfo info = new AirohaMyBudsInfo();
                    AirohaMyBudsMsg budsMsg = new AirohaMyBudsMsg(info);
                    myBudsMessage = (AirohaMyBudsMsg) budsMsg;
                } else {
                    myBudsMessage = (AirohaMyBudsMsg) msg;
                }
                updateFindMyBudsUI(code, myBudsMessage);
            }  catch (Exception e) {
                gLogger.e(e);
            }
        }
    }

    class SetDeviceNameListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, AirohaBaseMsg msg) {
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            String deviceName = (String) msg.getMsgContent();
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setDeviceName: " + code.getDescription());
                        mBtnSetDeviceName.setEnabled(true);
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    private int getGestureActionId(String gestureActionString){
        if(gestureActionString.equalsIgnoreCase("Disable"))
            return AirohaGestureSettings.ACTION_NONE;
        else if(gestureActionString.equalsIgnoreCase("Volume Up"))
            return AirohaGestureSettings.VOLUME_UP;
        else if(gestureActionString.equalsIgnoreCase("Volume Down"))
            return AirohaGestureSettings.VOLUME_DOWN;
        else if(gestureActionString.equalsIgnoreCase("ANC"))
            return AirohaGestureSettings.ANC;
        else if(gestureActionString.equalsIgnoreCase("Pass Through"))
            return AirohaGestureSettings.PASS_THROUGH;
        else if(gestureActionString.equalsIgnoreCase("Next Track"))
            return AirohaGestureSettings.NEXT_TRACK;
        else if(gestureActionString.equalsIgnoreCase("Previous Track"))
            return AirohaGestureSettings.PREVIOUS_TRACK;
        else if(gestureActionString.equalsIgnoreCase("Play/Pause"))
            return AirohaGestureSettings.PLAY_PAUSE;
        else if(gestureActionString.equalsIgnoreCase("Wake Up SIRI"))
            return AirohaGestureSettings.WAKE_UP_SIRI;
        else if(gestureActionString.equalsIgnoreCase("Switch EQ"))
            return AirohaGestureSettings.SWITCH_EQ;
        else if(gestureActionString.equalsIgnoreCase("ANC / Passthrough"))
            return AirohaGestureSettings.SWITCH_ANC_AND_PASSTHROUGH;
        else
            return AirohaGestureSettings.ACTION_NONE;
    }

    private String getGestureActionString(int gestureActionId) {
        switch (gestureActionId) {
            case AirohaGestureSettings.ACTION_NONE:
                return "Disable";
            case AirohaGestureSettings.VOLUME_UP:
                return "Volume Up";
            case AirohaGestureSettings.VOLUME_DOWN:
                return "Volume Down";
            case AirohaGestureSettings.ANC:
                return "ANC";
            case AirohaGestureSettings.PASS_THROUGH:
                return "Pass Through";
            case AirohaGestureSettings.NEXT_TRACK:
                return "Next Track";
            case AirohaGestureSettings.PREVIOUS_TRACK:
                return "Previous Track";
            case AirohaGestureSettings.PLAY_PAUSE:
                return "Play/Pause";
            case AirohaGestureSettings.WAKE_UP_SIRI:
                return "Wake Up SIRI";
            case AirohaGestureSettings.SWITCH_EQ:
                return "Switch EQ";
            case AirohaGestureSettings.SWITCH_ANC_AND_PASSTHROUGH:
                return "ANC / Passthrough";
            default:
                return null;
        }
    }

    class GestureListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            AirohaGestureMsg gestureMessage = (AirohaGestureMsg) msg;
                            for (AirohaGestureSettings info : gestureMessage.getMsgContent()) {
                                int gesture = info.getGestureId();
                                int action = info.getActionId();
                                switch (gesture) {
                                    case AirohaGestureSettings.SINGLE_TAP_LEFT:
                                        mTextLeftSingleClickGestureAction.setText(getGestureActionString(action));
                                        break;
                                    case AirohaGestureSettings.SINGLE_TAP_RIGHT:
                                        mTextRightSingleClickGestureAction.setText(getGestureActionString(action));
                                        break;
                                    case AirohaGestureSettings.DOUBLE_TAP_LEFT:
                                        mTextLeftDoubleClickGestureAction.setText(getGestureActionString(action));
                                        break;
                                    case AirohaGestureSettings.DOUBLE_TAP_RIGHT:
                                        mTextRightDoubleClickGestureAction.setText(getGestureActionString(action));
                                        break;
                                    case AirohaGestureSettings.LONG_PRESS_LEFT:
                                        mTextLeftLongPressGestureAction.setText(getGestureActionString(action));
                                        break;
                                    case AirohaGestureSettings.LONG_PRESS_RIGHT:
                                        mTextRightLongPressGestureAction.setText(getGestureActionString(action));
                                        break;
                                    case AirohaGestureSettings.TRIPLE_TAP_LEFT:
                                        mTextLeftTripleClickGestureAction.setText(getGestureActionString(action));
                                        break;
                                    case AirohaGestureSettings.TRIPLE_TAP_RIGHT:
                                        mTextRightTripleClickGestureAction.setText(getGestureActionString(action));
                                        break;
                                }
                            }
                        }

                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getGestureStatus: " + code.getDescription());
                        mBtnGetLeftGesture.setEnabled(true);
                        mBtnGetRightGesture.setEnabled(true);
                        mBtnSetGesture.setEnabled(true);
                        mBtnResetLeftGesture.setEnabled(true);
                        mBtnResetRightGesture.setEnabled(true);
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            final AirohaGestureMsg gestureMessage = (AirohaGestureMsg) msg;
                        }
                        if (!mIsResetGesture) {
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setGestureStatus: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "ResetGestureStatus: " + code.getDescription());
                        }

                        mBtnSetGesture.setEnabled(true);
                        mBtnResetLeftGesture.setEnabled(true);
                        mBtnResetRightGesture.setEnabled(true);
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class MultiAiListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int MultiAiIndex = (int) msg.getMsgContent();
                            mSpinVaIndex.setSelection(convertToUiIndex(MultiAiIndex));
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getMultiAi: " + code.getDescription());
                        mBtnGetVaInfo.setEnabled(true);
                        mBtnSetVaIndex.setEnabled(true);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setMultiAi: " + code.getDescription());
                        mBtnSetVaIndex.setEnabled(true);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class AutoPlayPauseStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            boolean status = (boolean)msg.getMsgContent();
                            mIsAutoPlayPauseON = status;
                            if (mIsAutoPlayPauseON) {
                                mRadioButtonAutoPlayPauseON.setChecked(true);
                            } else {
                                mRadioButtonAutoPlayPauseOFF.setChecked(true);
                            }
                            mRadioButtonAutoPlayPauseON.setEnabled(true);
                            mRadioButtonAutoPlayPauseOFF.setEnabled(true);

                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "GetAutoPlayPauseStatus: " + code.getDescription());
                        }
                        else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "GetAutoPlayPauseStatus: " + code.getDescription());
                        }
                        mBtnGetAutoPlayPauseStatus.setEnabled(true);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            boolean status = (boolean)msg.getMsgContent();
                            mIsAutoPlayPauseON = status;
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "SetAutoPlayPauseStatus: " + code.getDescription());
                        } else{
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "SetAutoPlayPauseStatus: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class AutoPowerOffStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int interval = (int) msg.getMsgContent();
                            mEditTextPowerOffInterval.setText(String.valueOf(interval));
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "GetAutoPowerOffStatus: " + code.getDescription());
                        }
                        else{
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "GetAutoPowerOffStatus: " + code.getDescription());
                        }
                        for (View view : mAutoPowerOffViewList) {
                            view.setEnabled(true);
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int interval = (int) msg.getMsgContent();
                            mEditTextPowerOffInterval.setText(String.valueOf(interval));
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "SetAutoPowerOffStatus: " + code.getDescription());
                        }
                        else{
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "SetAutoPowerOffStatus: " + code.getDescription());
                        }
                        for (View view : mAutoPowerOffViewList) {
                            view.setEnabled(true);
                        }
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class SealingStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            AirohaSealingInfo result = (AirohaSealingInfo) msg.getMsgContent();
                            if (result.getLeftSealing() == AirohaSealingInfo.READY && result.getRightSealing() == AirohaSealingInfo.READY)
                                mTextSealingStatus.setText("Both left and right are ready.");
                            else if (result.getLeftSealing() == AirohaSealingInfo.READY)
                                mTextSealingStatus.setText("Left is ready.");
                            else if (result.getRightSealing() == AirohaSealingInfo.READY)
                                mTextSealingStatus.setText("Right is ready.");
                            else
                                mTextSealingStatus.setText("Both left and right are not ready.");

                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getSealingStatus: " + code.getDescription());
                        }
                        else{
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, String.format("getSealingStatus: %s", msg.getMsgContent().toString()));
                        }
                        mBtnGetSealingStatus.setEnabled(true);
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
        }
    }

    class A2dpConnectionStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            final AirohaA2DPConnectionMsg a2dpConnectionMessage = (AirohaA2DPConnectionMsg) msg;
                            if (a2dpConnectionMessage.getMsgContent().isConnected()) {
                                mTextA2dpConnectionStatus.setText("Connected");
                                mTextA2dpPhoneAddr.setText(a2dpConnectionMessage.getMsgContent().getAirohaDevice().getDeviceMAC());
                            } else {
                                mTextA2dpConnectionStatus.setText("Disconnected");
                            }
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getA2dpConnectionStatus: " + code.getDescription());
                        mBtnGetA2dpConnectionStatus.setEnabled(true);
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
        }
    }

    void updateAncUI(final AirohaStatusCode code, final AirohaAncStatusMsg msg, final String title) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (msg != null) {
                        LinkedList<AirohaAncSettings> content = (LinkedList<AirohaAncSettings>) msg.getMsgContent();
                        AirohaAncSettings setting = content.get(0);
                        int filter = setting.getFilter();
                        double ancGain = setting.getAncGain();
                        double passthruGain = setting.getPassthruGain();
                        mAncPassthruFilterSelected = filter;
                        resetAncRadioButtons();
                        if(filter == mAncPassthruFilterSelected) {
                            setAncRadioButtonText(mAncPassthruFilterSelected, mAncModeSelectedString);
                        }
                        mAncModeSelectedString = "";

                        switch (AirohaAncSettings.UI_ANC_FILTER.values()[filter]) {
                            case OFF:
                                mRadioButtonAncPtOff.setChecked(true);
                                break;
                            case ANC1:
                                mRadioButtonAncFilter1.setChecked(true);
                                break;
                            case ANC2:
                                mRadioButtonAncFilter2.setChecked(true);
                                break;
                            case ANC3:
                                mRadioButtonAncFilter3.setChecked(true);
                                break;
                            case ANC4:
                                mRadioButtonAncFilter4.setChecked(true);
                                break;
                            case PassThrough1:
                                mRadioButtonPassThrough1.setChecked(true);
                                break;
                            case PassThrough2:
                                mRadioButtonPassThrough2.setChecked(true);
                                break;
                            case PassThrough3:
                                mRadioButtonPassThrough3.setChecked(true);
                                break;
                            case HybridPassThrough1:
                                mRadioButtonHybridPassThrough1.setChecked(true);
                                break;
                            case HybridPassThrough2:
                                mRadioButtonHybridPassThrough2.setChecked(true);
                                break;
                            case HybridPassThrough3:
                                mRadioButtonHybridPassThrough3.setChecked(true);
                                break;
                        }

                        if (filter == AirohaAncSettings.UI_ANC_FILTER.ANC1.ordinal()
                                || filter == AirohaAncSettings.UI_ANC_FILTER.ANC2.ordinal()
                                || filter == AirohaAncSettings.UI_ANC_FILTER.ANC3.ordinal()
                                || filter == AirohaAncSettings.UI_ANC_FILTER.ANC4.ordinal()) {
                            mSeekBarAncGain.setEnabled(true);
                            mSeekBarPassThroughGain.setEnabled(false);
                        } else if (filter == AirohaAncSettings.UI_ANC_FILTER.PassThrough1.ordinal()
                                || filter == AirohaAncSettings.UI_ANC_FILTER.PassThrough2.ordinal()
                                || filter == AirohaAncSettings.UI_ANC_FILTER.PassThrough3.ordinal()
                                || filter == AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough1.ordinal()
                                || filter == AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough2.ordinal()
                                || filter == AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough3.ordinal()) {
                            mSeekBarAncGain.setEnabled(false);
                            mSeekBarPassThroughGain.setEnabled(true);
                        } else {
                            mSeekBarAncGain.setEnabled(false);
                            mSeekBarPassThroughGain.setEnabled(false);
                        }
                        mSeekBarAncGain.setProgress((short) ancGain * 100 + mSeekBarAncGain.getMax());
                        mSeekBarPassThroughGain.setProgress((short) passthruGain * 100 + mSeekBarPassThroughGain.getMax());
                    }
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, title + ": " + code.getDescription());
                } catch (Exception e) {
                    gLogger.e(e);
                }
            }
        });
    }

    class AncSettingListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            final AirohaAncStatusMsg ancStatusMessage = (AirohaAncStatusMsg) msg;
                            updateAncUI(code, ancStatusMessage, "GetAirohaAncSettings");
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "GetAirohaAncSettings: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                    setAncUiEnableStatus(true);
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            final AirohaAncStatusMsg ancStatusMessage = (AirohaAncStatusMsg) msg;
                            updateAncUI(code, ancStatusMessage, "SetAirohaAncSettings");
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "SetAirohaAncSettings: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                    setAncUiEnableStatus(true);
                }
            });
        }
    }

    class BatteryInfoListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            if (msg != null) {
                                AirohaBatteryInfo batteryInfo = (AirohaBatteryInfo) msg.getMsgContent();
                                mTextAgentBattery.setText("" + batteryInfo.getMasterLevel());
                                mTextPartnerBattery.setText("" + batteryInfo.getSlaveLevel());
                            }
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "GetBatteryInfo: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "GetBatteryInfo: " + code.getDescription());
                        }
                        mBtnGetBatteryInfo.setEnabled(true);
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }

    void updateSmartSwitchUI(final AirohaStatusCode code, final AirohaBaseMsg msg) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (code == AirohaStatusCode.STATUS_SUCCESS) {
                        int smartSwitchStatus = (int) msg.getMsgContent();
                        switch (smartSwitchStatus) {
                            case 0x01: // normal
                                mRadioButtonNormalMode.performClick();
                                break;
                            case 0x02:  // game
                                mRadioButtonGameMode.performClick();
                                break;
                        }
                        mBtnGetSmartSwitchStatus.setEnabled(true);
                        mBtnSetSmartSwitchStatus.setEnabled(true);
                    }
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "SmartSwitchStatus: " + code.getDescription());
                } catch (Exception e) {
                    gLogger.e(e);
                }
            }
        });
    }

    class SmartSwitchStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, AirohaBaseMsg msg) {
            updateSmartSwitchUI(code, msg);
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {
            try {
                if (code == AirohaStatusCode.STATUS_FAIL) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "SetAirohaSmartSwitchStatus: " + code.getDescription());
                } else {
                    updateSmartSwitchUI(code, msg);
                }
            } catch (Exception e) {
                gLogger.e(e);
            }
        }
    }

    class TouchStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int touchStatus = (int)msg.getMsgContent();
                            mTouchStatus = touchStatus;
                            if (touchStatus == 1) {
                                mRadioButtonTouchON.setChecked(true);
                            } else {
                                mRadioButtonTouchOFF.setChecked(true);
                            }
                            mRadioButtonTouchON.setEnabled(true);
                            mRadioButtonTouchOFF.setEnabled(true);

                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "GetTouchStatus: " + code.getDescription());
                        }
                        else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "GetTouchStatus: " + code.getDescription());
                        }
                        mBtnGetTouchStatus.setEnabled(true);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int touchStatus = (int)msg.getMsgContent();
                            mTouchStatus = touchStatus;
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "SetTouchStatus: " + code.getDescription());
                        } else{
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "SetTouchStatus: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class AdvancedPassthroughStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int advancedPassthroughStatus = (int)msg.getMsgContent();
                            mAdvancedPassthroughStatus = advancedPassthroughStatus;
                            if (advancedPassthroughStatus == 1) {
                                mRadioButtonAdvancedPassthroughON.setChecked(true);
                            } else {
                                mRadioButtonAdvancedPassthroughOFF.setChecked(true);
                            }
                            mRadioButtonAdvancedPassthroughON.setEnabled(true);
                            mRadioButtonAdvancedPassthroughOFF.setEnabled(true);

                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "GetAdvancedPassthroughStatus: " + code.getDescription());
                        }
                        else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "GetAdvancedPassthroughStatus: " + code.getDescription());
                        }
                        mBtnGetAdvancedPassthroughStatus.setEnabled(true);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int advancedPassthroughStatus = (int)msg.getMsgContent();
                            mAdvancedPassthroughStatus = advancedPassthroughStatus;
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "SetAdvancedPassthroughStatus: " + code.getDescription());
                        } else{
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "SetAdvancedPassthroughStatus: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class ShareModeStateListener implements AirohaDeviceListener {
        void updateUI(final String msgPrefix, final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            AirohaShareModeInfo info = (AirohaShareModeInfo) msg.getMsgContent();
                            mTextShareModeState.setText(info.getShareModeState().getName());
//                        if (state == 2) {
//                            mRadioButtonShareModeON.performClick();
//                        } else {
//                            mRadioButtonShareModeOFF.performClick();
//                        }

//                            switch (info.getShareModeState()) {
//                                case 0:
//                                    mTextShareModeState.setText("STATE_NORMAL");
////                                    mRadioButtonShareModeAgent.setEnabled(true);
////                                    mRadioButtonShareModeFollower.setEnabled(true);
//                                    break;
//                                case 1:
//                                    mTextShareModeState.setText("STATE_PREPARING");
////                                    mRadioButtonShareModeOFF.setEnabled(true);
//                                    break;
//                                case 2:
//                                    mTextShareModeState.setText("STATE_SHARING");
////                                    mRadioButtonShareModeOFF.setEnabled(true);
//                                    break;
//                                case 3:
//                                    mTextShareModeState.setText("STATE_LEAVING");
////                                    mRadioButtonShareModeAgent.setEnabled(true);
////                                    mRadioButtonShareModeFollower.setEnabled(true);
//                                    break;
//                            }
                        } else {
                            mTextShareModeState.setText("cmd failed");
//                            mRadioButtonShareModeOFF.setChecked(false);
//                            mRadioButtonShareModeAgent.setChecked(false);
//                            mRadioButtonShareModeFollower.setChecked(false);
                        }
                        mRadioButtonShareModeOFF.setEnabled(true);
                        mRadioButtonShareModeAgent.setEnabled(true);
                        mRadioButtonShareModeFollower.setEnabled(true);
                        mBtnGetShareModeState.setEnabled(true);

                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, msgPrefix + code.getDescription());
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI("getShareModeState: ", code, msg);
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI("setShareMode: ", code, msg);
        }
    }

    class UpdateDeviceStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {

        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String strMsg = "";
                        if (msg.isPush()) {
                            strMsg = "[Push Msg] statusCode = "
                                    + code.getValue() + "(" + code.getDescription() + "), msg = "
                                    + msg.getMsgID().getCmdName();

                            if (msg.getMsgID().getCmdName().equals(AirohaMessageID.ANC_STATUS.toString())) {
                                final AirohaAncStatusMsg ancStatusMessage = (AirohaAncStatusMsg) msg;
                                updateAncUI(code, ancStatusMessage, "GetAirohaAncSettings");
                            }
                            else if (msg.getMsgID().getCmdName().equals(AirohaMessageID.TOUCH_STATUS.toString())) {
                                int touchStatus = (int)msg.getMsgContent();
                                mTouchStatus = touchStatus;
                                if (touchStatus == 1) {
                                    mRadioButtonTouchON.setChecked(true);
                                } else {
                                    mRadioButtonTouchOFF.setChecked(true);
                                }
                            }
                            else if (msg.getMsgID().getCmdName().equals(AirohaMessageID.ADVANCED_PASSTHROUGH_STATUS.toString())) {
                                int advancePassthroughStatus = (int)msg.getMsgContent();
                                mAdvancedPassthroughStatus = advancePassthroughStatus;
                                if (advancePassthroughStatus == 1) {
                                    mRadioButtonAdvancedPassthroughON.setChecked(true);
                                } else {
                                    mRadioButtonAdvancedPassthroughOFF.setChecked(true);
                                }
                            }
                        }
                        else {
                            strMsg = "[Global Msg] statusCode = "
                                    + code.getValue() + "(" + code.getDescription() + "), msg = "
                                    + msg.getMsgID().getCmdName();
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, strMsg);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class SidetoneStateListener implements AirohaDeviceListener {
        void updateUI(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            AirohaSidetoneInfo info = (AirohaSidetoneInfo)msg.getMsgContent();
                            if (info.isOn()) {
                                mRadioButtonSidetoneON.performClick();
                            } else {
                                mRadioButtonSidetoneOFF.performClick();
                            }
                            mEditTextSidetoneLevel.setText(String.valueOf(info.getLevel()));
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "SidetoneState: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI(code, msg);
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI(code, msg);
        }
    }

    private void setAncUiEnableStatus(boolean status){
        for (RadioButton btn : mAncRadioButtonList) {
            btn.setEnabled(status);
        }
        if(status) {
            if (mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.ANC1.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.ANC2.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.ANC3.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.ANC4.ordinal()) {
                mSeekBarAncGain.setEnabled(true);
                mSeekBarPassThroughGain.setEnabled(false);
            } else if (mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.PassThrough1.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.PassThrough2.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.PassThrough3.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough1.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough2.ordinal()
                    || mAncPassthruFilterSelected == AirohaAncSettings.UI_ANC_FILTER.HybridPassThrough3.ordinal()) {
                mSeekBarAncGain.setEnabled(false);
                mSeekBarPassThroughGain.setEnabled(true);
            } else {
                mSeekBarAncGain.setEnabled(false);
                mSeekBarPassThroughGain.setEnabled(false);
            }
        }
        else{
            mSeekBarAncGain.setEnabled(false);
            mSeekBarPassThroughGain.setEnabled(false);
        }
        mBtnGetAncSetting.setEnabled(status);
        mBtnSaveAncSetting.setEnabled(status);
    }

    private void resetAncRadioButtons() {
        mRadioButtonAncFilter1.setText("");
        mRadioButtonAncFilter2.setText("");
        mRadioButtonAncFilter3.setText("");
        mRadioButtonAncFilter4.setText("");
        mRadioButtonHybridPassThrough1.setText("");
        mRadioButtonHybridPassThrough2.setText("");
        mRadioButtonHybridPassThrough3.setText("");

        for (RadioButton btn : mAncRadioButtonList) {
            btn.setChecked(false);
        }
    }

    private void setAncRadioButtonText(int filter_num, String mode_str){
        mRadioButtonAncFilter1.setText("");
        mRadioButtonAncFilter2.setText("");
        mRadioButtonAncFilter3.setText("");
        mRadioButtonAncFilter4.setText("");
        mRadioButtonHybridPassThrough1.setText("");
        mRadioButtonHybridPassThrough2.setText("");
        mRadioButtonHybridPassThrough3.setText("");

        switch (filter_num){
            case 1:
                mRadioButtonAncFilter1.setText(mode_str);
                break;
            case 2:
                mRadioButtonAncFilter2.setText(mode_str);
                break;
            case 3:
                mRadioButtonAncFilter3.setText(mode_str);
                break;
            case 7:
                mRadioButtonAncFilter4.setText(mode_str);
                break;
            case 8:
                mRadioButtonHybridPassThrough1.setText(mode_str);
                break;
            case 9:
                mRadioButtonHybridPassThrough2.setText(mode_str);
                break;
            case 10:
                mRadioButtonHybridPassThrough3.setText(mode_str);
                break;
        }
    }

    private void showAncModeSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                .setTitle("Select ANC Mode")
                .setItems(R.array.ANC_Filter_Control, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] Item = getResources().getStringArray(
                                R.array.ANC_Filter_Control
                        );
                        gLogger.d(TAG, "Select ANC Mode: " + Item[which]);
                        setAncRadioButtonText(mAncPassthruFilterSelected, Item[which]);
                        switch(which)
                        {
                            case 0:
                                mAncMode = (byte)AirohaAncMode.HYBRID.getValue();
                                break;
                            case 1:
                                mAncMode = (byte)AirohaAncMode.FF.getValue();
                                break;
                            case 2:
                                mAncMode = (byte)AirohaAncMode.FB.getValue();
                                break;
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "SetAncSetting...");
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mAncGainSelected);
                        ancSetting.setAncMode(mAncMode);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                        mAncModeSelectedString = Item[which];
                        setAncRadioButtonText(mAncPassthruFilterSelected, mAncModeSelectedString);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showHybridPassthroughModeSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                .setTitle("Select Hybrid Passthrough Mode")
                .setItems(R.array.Hybrid_Passthrough_Filter_Control, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] Item = getResources().getStringArray(
                                R.array.Hybrid_Passthrough_Filter_Control
                        );
                        gLogger.d(TAG, "Select Hybrid Passthrough Mode: " + Item[which]);
                        setAncRadioButtonText(mAncPassthruFilterSelected, Item[which]);

                        switch(which)
                        {
                            case 0:
                                mAncMode = (byte)AirohaAncMode.HYBRID_PASSTHROUGH.getValue();
                                break;
                            case 1:
                                mAncMode = (byte)AirohaAncMode.PASSTHROUGH.getValue();
                                break;
                            case 2:
                                mAncMode = (byte)AirohaAncMode.PASSTHROUGH_FB.getValue();
                                break;
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "SetAncSetting...");
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mPassthruGainSelected);
                        ancSetting.setAncMode(mAncMode);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                        mAncModeSelectedString = Item[which];
                        setAncRadioButtonText(mAncPassthruFilterSelected, mAncModeSelectedString);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}

