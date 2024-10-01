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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.libbase.RaceCommand.constant.RaceType;
import com.airoha.libbase.constant.AgentPartnerEnum;
import com.airoha.libcommon.AirohaCommonListener;
import com.airoha.libmmi.AirohaMmiMgr;
import com.airoha.libmmi1568.AirohaMmiListener1568;
import com.airoha.sdk.AB1568DeviceControl;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.utils.AirohaStatusCode;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LeAudioFragment extends BaseFragment {
    private String TAG = LeAudioFragment.class.getSimpleName();
    private String LOG_TAG = "[LeAudio] ";
    private MainActivity mActivity;
    private LeAudioFragment mFragment;
    private View mView;

    private RadioButton mRadioButtonOn;
    private RadioButton mRadioButtonOff;

    private Button mBtnGetState;

    public LeAudioFragment(){
        mTitle = "LE Audio UT";
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
        AirohaSDK.getInst().getAirohaDeviceConnector().registerConnectionListener(mFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_le_audio, container,false);
        initUImember();
        return mView;
    }

    private void initUImember() {
        mRadioButtonOn = mView.findViewById(R.id.radioButtonLeAudioOn);
        mRadioButtonOff = mView.findViewById(R.id.radioButtonLeAudioOff);
        mRadioButtonOn.setOnClickListener(mOnClickListener);
        mRadioButtonOff.setOnClickListener(mOnClickListener);

        mBtnGetState = mView.findViewById(R.id.btnGetLeAudioState);
        mBtnGetState.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        gLogger.d(TAG, "onResume");
        if (mActivity.getAirohaService() != null) {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(true);
            ((AB1568DeviceControl) AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr1568().addListener(TAG, mAirohaMmiListener1568);
            mBtnGetState.performClick();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        gLogger.d(TAG, "onPause");
        if (mActivity.getAirohaService() != null) {
            mActivity.getAirohaService().getHost().removeHostDataListener(TAG);
            AirohaSDK.getInst().getAirohaDeviceConnector().unregisterConnectionListener(mFragment);
            ((AirohaMmiMgr)AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).removeListener(TAG);
        }
    }

    @Override
    public void onDestroy() {
        gLogger.d(TAG, "onDestroy");
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

    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.radioButtonLeAudioOn:
                    setUiEnabled(false);
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "switch LE Audio to ON...");
                    ((AB1568DeviceControl) AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr1568().setLeAudioState(true);
                    break;
                case R.id.radioButtonLeAudioOff:
                    setUiEnabled(false);
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "switch LE Audio to OFF...");
                    ((AB1568DeviceControl) AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr1568().setLeAudioState(false);
                    break;
                case R.id.btnGetLeAudioState:
                    setUiEnabled(false);
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "get LE Audio state...");
                    ((AB1568DeviceControl) AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr1568().getLeAudioState();
                    break;
            }
        }
    };

    private void setUiEnabled(final boolean isEnabled) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRadioButtonOn.setEnabled(isEnabled);
                mRadioButtonOff.setEnabled(isEnabled);
                mBtnGetState.setEnabled(isEnabled);
            }
        });
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
                            showAlertDialog(mActivity, "Error", "BT disconnected. Please reconnect BT.");
                        }
                    });
                }
                break;
            case AirohaConnector.CONNECTED:
            case AirohaConnector.CONNECTING:
            case AirohaConnector.DISCONNECTING:
            case AirohaConnector.WAITING_CONNECTABLE:
            default:
                break;
        }
    }

    AirohaMmiListener1568 mAirohaMmiListener1568 = new AirohaMmiListener1568() {

        @Override
        public void OnRespSuccess(String stageName) {

        }

        @Override
        public void onResponseTimeout() {
            setUiEnabled(true);
            mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "rsp timeout");
        }

        @Override
        public void onStopped(String stageName) {
            if (stageName != null) {
                setUiEnabled(true);
                mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + stageName + " is stopped");
            }
        }

        @Override
        public void notifyUpdateDeviceStatus(int moduleId, int statusCode) {

        }

        @Override
        public void notifySetShareModeStatus(byte status) {

        }

        @Override
        public void notifyShareModeState(byte state) {

        }

        @Override
        public void notifyIrOnOff(byte irStatus) {

        }

        @Override
        public void notifyTouchOnOff(byte touchStatus) {

        }

        @Override
        public void notifyAdvancedPassthroughOnOff(byte status) {

        }

        @Override
        public void notifySetAutoPowerOffStatus(byte status) {

        }

        @Override
        public void notifyAutoPowerOffStatus(byte status, int time) {

        }

        @Override
        public void notifyLeAudioState(final byte state) {
            mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "notifyLeAudioState: " + state);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUiEnabled(true);
                    if (state == (byte)0x01) {
                        mRadioButtonOn.setChecked(true);
                        mRadioButtonOff.setChecked(false);
                    } else {
                        mRadioButtonOn.setChecked(false);
                        mRadioButtonOff.setChecked(true);
                    }
                }
            });
        }

        @Override
        public void notifyReadAncNv(byte[] ancData) {

        }

        @Override
        public void notifySidetoneLevel(short level) {

        }

        @Override
        public void notifySidetoneState(byte state) {

        }
    };
}

