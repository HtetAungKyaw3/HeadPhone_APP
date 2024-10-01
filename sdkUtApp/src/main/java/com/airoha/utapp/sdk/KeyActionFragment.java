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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.sdk.api.utils.ChipType;
import com.airoha.utapp.sdk.MainActivity.MsgType;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.device.AirohaDevice;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaDeviceInfoMsg;
import com.airoha.sdk.api.message.AirohaGestureMsg;
import com.airoha.sdk.api.message.AirohaGestureSettings;
import com.airoha.sdk.api.utils.AirohaAiIndex;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.sdk.api.utils.AudioChannel;
import com.airoha.sdk.api.utils.DeviceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.airoha.sdk.api.message.AirohaGestureSettings.DLONG_LEFT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.DLONG_RIGHT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.DOUBLE_TAP_LEFT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.DOUBLE_TAP_RIGHT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.LONG_PRESS_LEFT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.LONG_PRESS_RIGHT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.SINGLE_TAP_LEFT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.SINGLE_TAP_RIGHT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.TRIPLE_TAP_LEFT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.TRIPLE_TAP_RIGHT;


public class KeyActionFragment extends BaseFragment {
    private String TAG = KeyActionFragment.class.getSimpleName();
    private String LOG_TAG = "[KeyAction] ";
    private KeyActionFragment mFragment;
    private View mView;

    // Interaction
    private Button mBtnCheckChannel;
    private TextView mTextAgentChannel;
    private TextView mTextPartnerChannel;

    private Spinner mSpinVaIndex;
    private Button mBtnSetVaIndex;

    private Spinner mSpinLeftSingleClickKeyAction;
    private Spinner mSpinLeftDoubleClickKeyAction;
    private Spinner mSpinLeftTripleClickKeyAction;
    private Spinner mSpinLeftLongPressKeyAction;
    private TableRow mLayoutLeftDLong;
    private Spinner mSpinLeftDLongKeyAction;
    private Button mBtnSetLeftKepMap;

    private Spinner mSpinRightSingleClickKeyAction;
    private Spinner mSpinRightDoubleClickKeyAction;
    private Spinner mSpinRightTripleClickKeyAction;
    private Spinner mSpinRightLongPressKeyAction;
    private TableRow mLayoutRightDLong;
    private Spinner mSpinRightDLongKeyAction;
    private Button mBtnSetRightKepMap;

    private HashMap<Integer, Spinner> mSpinnerGroup = new HashMap<>();
    private HashMap<Integer, String> mSpinnerChangeBeforeMap = new HashMap<>();
    private HashMap<Integer, String> mSpinnerChangeAfterMap = new HashMap<>();
    private boolean _spinner_initialized = false;
    private boolean _show_changed_gesture_list = true;

    //VA settings
    private int mVaIndex = (byte) 0xFF;

    //key action
    private boolean mIsAgentRight = true;
    private boolean mIsPartnerExist = false;
    private DeviceType mDeviceType = DeviceType.UNKNOWN;
    private List<AirohaGestureSettings> mToSetGestureSettings = null;

    private ArrayAdapter<String> mSingleClickAdapter;
    private ArrayAdapter<String> mDoubleClickAdapter;
    private ArrayAdapter<String> mTripleClickAdapter;
    private ArrayAdapter<String> mLongPressAdapter;
    private ArrayAdapter<String> mDLongAdapter;

    private static String SINGLE_CLICK = "SINGLE_CLICK";
    private static String DOUBLE_CLICK = "DOUBLE_CLICK";
    private static String TRIPLE_CLICK = "TRIPLE_CLICK";
    private static String LONG_PRESS = "LONG_PRESS";
    private static String DLONG = "DLONG";
    private static String BISTO = "BISTO";
    private static String AMA = "AMA";
    private static String XIAOWEI = "XIAOWEI";
    private static String XIAOAI = "XIAOAI";
    private static String SIRI = "SIRI";

    public KeyActionFragment() {
        mTitle = "Key Action UT";
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
        mView = inflater.inflate(R.layout.fragment_keyaction, container, false);
        initUImember();

        mDeviceType = AirohaSDK.getInst().getDeviceType();
        mBtnCheckChannel.performClick();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        gLogger.d(TAG, "onResume");
        AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        gLogger.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        gLogger.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
        super.onHiddenChanged(hidden);

        if (hidden) {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(false);
        } else {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(true);
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
                            mIsPartnerExist = isTwsConnected;
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getTwsConnectStatus: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MsgType.ERROR, "getTwsConnectStatus: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }

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
                                mTextAgentChannel.setText(airohaDevice.getChannel().getName());
                                mTextPartnerChannel.setText(airohaDevicePartner.getChannel().getName());
                            } else {
                                mTextAgentChannel.setText(airohaDevice.getChannel().getName());
                                mTextPartnerChannel.setText("NA");
                            }
                            if (airohaDevice.getChannel() == AudioChannel.STEREO_RIGHT) {
                                mIsAgentRight = true;
                            } else {
                                mIsAgentRight = false;
                            }
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getDeviceInfo: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "getDeviceInfo: " + code.getDescription());
                        }
                        enableAllBtns();
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

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

                            if(mSpinVaIndex.getSelectedItemPosition() != convertToUiIndex(MultiAiIndex)){
                                _show_changed_gesture_list = false;
                                mSpinVaIndex.setSelection(convertToUiIndex(MultiAiIndex));
                            }

                            mVaIndex = MultiAiIndex;
                            mActivity.updateMsg(MsgType.GENERAL, "getMultiAi: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "getMultiAi: " + code.getDescription());
                        }
                        enableAllBtns();
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
                            int MultiAiIndex = (int) msg.getMsgContent();

                            if(mSpinVaIndex.getSelectedItemPosition() != convertToUiIndex(MultiAiIndex)){
                                _show_changed_gesture_list = false;
                                mSpinVaIndex.setSelection(convertToUiIndex(MultiAiIndex));
                            }

                            mVaIndex = MultiAiIndex;
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setMultiAi: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "setMultiAi: " + code.getDescription());
                        }
                        enableAllBtns();
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    private int convertToUiIndex(int ai_index) {
        switch (ai_index) {
            case 0: //AMAZON_AI
                return 1;
            case 1://GOOGLE_AI
                return 0;
            case 2://XIAOWEI_AI
                return 3;
            case 3://XIAOAI_AI
                return 4;
            case -1://SIRI_AI
            default:
                return 2;
        }
    }

    private int convertToAiIndex(int ui_index) {
        switch (ui_index) {
            case 0:
                return AirohaAiIndex.GOOGLE_AI.getValue();
            case 1:
                return AirohaAiIndex.AMAZON_AI.getValue();
            case 3:
                return AirohaAiIndex.XIAOWEI_AI.getValue();
            case 4:
                return AirohaAiIndex.XIAOAI_AI.getValue();
            case 2:
            default:
                return AirohaAiIndex.SIRI_AI.getValue();
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
                            updateKeyMapUI(gestureMessage.getMsgContent());
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getGestureStatus: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "getGestureStatus: " + code.getDescription());
                        }
                        enableAllBtns();
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
                    if (code == AirohaStatusCode.STATUS_SUCCESS) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setGestureStatus: " + code.getDescription());
                    } else {
                        mActivity.updateMsg(MainActivity.MsgType.ERROR, "setGestureStatus: " + code.getDescription());
                    }
                    enableAllBtns();
                }
            });
        }
    }

    private void initUImember() {
        mBtnCheckChannel = mView.findViewById(R.id.btnCheckAgentChannel);
        mBtnCheckChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllBtns();
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getTwsConnectStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getTwsConnectStatus(new TwsConnectStatusListener());
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getDeviceInfo...");
                AirohaSDK.getInst().getAirohaDeviceControl().getDeviceInfo(new DeviceInfoListener());
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getMultiAIStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getMultiAIStatus(new MultiAiListener());
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getGestureStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getGestureStatus(AirohaGestureSettings.ALL, new GestureListener());
            }
        });

        mTextAgentChannel = mView.findViewById(R.id.textAgentChannel);
        mTextPartnerChannel = mView.findViewById(R.id.textPartnerChannel);

        mSpinLeftSingleClickKeyAction = mView.findViewById(R.id.spinLeftSingleClickKeyAction);
        mSpinLeftSingleClickKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinLeftDoubleClickKeyAction = mView.findViewById(R.id.spinLeftDoubleClickKeyAction);
        mSpinLeftDoubleClickKeyAction.setSelection(3);
        mSpinLeftDoubleClickKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinLeftTripleClickKeyAction = mView.findViewById(R.id.spinLeftTripleClickKeyAction);
        mSpinLeftTripleClickKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinLeftLongPressKeyAction = mView.findViewById(R.id.spinLeftLongPressKeyAction);
        mSpinLeftLongPressKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinLeftDLongKeyAction = mView.findViewById(R.id.spinLeftDLongKeyAction);
        mSpinLeftDLongKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mLayoutLeftDLong = mView.findViewById(R.id.layoutLeftDLongKeyAction);

        mBtnSetLeftKepMap = mView.findViewById(R.id.buttonSetLeftKeyMap);
        mBtnSetLeftKepMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllBtns();
                updateToSetGestureList(true, false);
                AirohaSDK.getInst().getAirohaDeviceControl().setGestureStatus(mToSetGestureSettings, new GestureListener());
            }
        });

        mSpinRightSingleClickKeyAction = mView.findViewById(R.id.spinRightSingleClickKeyAction);
        mSpinRightSingleClickKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinRightDoubleClickKeyAction = mView.findViewById(R.id.spinRightDoubleClickKeyAction);
        mSpinRightDoubleClickKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinRightTripleClickKeyAction = mView.findViewById(R.id.spinRightTripleClickKeyAction);
        mSpinRightTripleClickKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinRightLongPressKeyAction = mView.findViewById(R.id.spinRightLongPressKeyAction);
        mSpinRightLongPressKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinRightDLongKeyAction = mView.findViewById(R.id.spinRightDLongKeyAction);
        mSpinRightDLongKeyAction.setOnItemSelectedListener(mOnItemSelectedListener);
        mLayoutRightDLong = mView.findViewById(R.id.layoutRightDLongKeyAction);

        mBtnSetRightKepMap = mView.findViewById(R.id.buttonSetRightKeyMap);
        mBtnSetRightKepMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllBtns();
                updateToSetGestureList(false, true);
                AirohaSDK.getInst().getAirohaDeviceControl().setGestureStatus(mToSetGestureSettings, new GestureListener());
            }
        });

        mSpinVaIndex = mView.findViewById(R.id.spinVaIndex);
        mSpinVaIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String va = "";
                switch (position) {
                    case 0:
                        va = BISTO;
                        break;
                    case 1:
                        va = AMA;
                        break;
                    case 2:
                        va = SIRI;
                        break;
                    case 3:
                        va = XIAOWEI;
                        break;
                    case 4:
                        va = XIAOAI;
                        break;
                }
                updateSpinnerSelections(va);
                enableAllBtns();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        mSpinVaIndex.setSelection(mSpinVaIndex.getCount() - 1);

        mBtnSetVaIndex = mView.findViewById(R.id.buttonSetVaIndex);
        mBtnSetVaIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Set VA Index");
                builder.setMessage("The key setting will also be changed. Press OK to continue change key setting and VA. Device will reboot after VA switch completed.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disableAllBtns();
                        if (mDeviceType == DeviceType.EARBUDS) {
                            if (mIsPartnerExist) {
                                updateToSetGestureList(true, true);
                            } else {
                                mActivity.updateMsg(MsgType.ERROR, "Partner doesn't exist.");
                                return;
                            }
                        } else if (mDeviceType == DeviceType.HEADSET) {
                            updateToSetGestureList(!mIsAgentRight, mIsAgentRight);
                        } else {
                            mActivity.updateMsg(MsgType.ERROR, "Unknown device type.");
                            return;
                        }
                        AirohaSDK.getInst().getAirohaDeviceControl().setGestureStatus(mToSetGestureSettings, new GestureListener());
                        AirohaSDK.getInst().getAirohaDeviceControl().setMultiAIStatus(convertToAiIndex(mSpinVaIndex.getSelectedItemPosition()), new MultiAiListener());
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();

            }
        });

        initSpinnerGroup();
        disableAllBtns();
        setDLongVisibility();
    }

    private void initSpinnerGroup(){
        mSpinnerGroup.put(SINGLE_TAP_LEFT, mSpinLeftSingleClickKeyAction);
        mSpinnerGroup.put(DOUBLE_TAP_LEFT, mSpinLeftDoubleClickKeyAction);
        mSpinnerGroup.put(TRIPLE_TAP_LEFT, mSpinLeftTripleClickKeyAction);
        mSpinnerGroup.put(LONG_PRESS_LEFT, mSpinLeftLongPressKeyAction);
        mSpinnerGroup.put(DLONG_LEFT, mSpinLeftDLongKeyAction);

        mSpinnerGroup.put(SINGLE_TAP_RIGHT, mSpinRightSingleClickKeyAction);
        mSpinnerGroup.put(DOUBLE_TAP_RIGHT, mSpinRightDoubleClickKeyAction);
        mSpinnerGroup.put(TRIPLE_TAP_RIGHT, mSpinRightTripleClickKeyAction);
        mSpinnerGroup.put(LONG_PRESS_RIGHT, mSpinRightLongPressKeyAction);
        mSpinnerGroup.put(DLONG_RIGHT, mSpinRightDLongKeyAction);
    }

    private void setDLongVisibility(){
        if(AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab1568")
                || AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab1565")
                || AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab158x")
                || AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab157x")){
            mLayoutLeftDLong.setVisibility(View.VISIBLE);
            mLayoutRightDLong.setVisibility(View.VISIBLE);
        }
        else{
            mLayoutLeftDLong.setVisibility(View.GONE);
            mLayoutRightDLong.setVisibility(View.GONE);
        }
    }

    private void disableAllBtns() {
        mBtnSetRightKepMap.setEnabled(false);
        mBtnSetLeftKepMap.setEnabled(false);
        mBtnCheckChannel.setEnabled(false);
        mBtnSetVaIndex.setEnabled(false);
    }

    private void enableAllBtns() {
        mBtnCheckChannel.setEnabled(true);

        if ((mDeviceType == DeviceType.EARBUDS && mIsPartnerExist) ||
                mDeviceType == DeviceType.HEADSET) {
            mBtnSetVaIndex.setEnabled(true);
        }

        if (mVaIndex != convertToAiIndex(mSpinVaIndex.getSelectedItemPosition())) {
            mBtnSetRightKepMap.setEnabled(false);
            mBtnSetLeftKepMap.setEnabled(false);
            return;
        }

        if (mIsPartnerExist) {
            mBtnSetRightKepMap.setEnabled(true);
            mBtnSetLeftKepMap.setEnabled(true);
        } else {
            if (mIsAgentRight)
                mBtnSetRightKepMap.setEnabled(true);
            else
                mBtnSetLeftKepMap.setEnabled(true);
        }
    }

    private void updateSpinnerSelections(String va) {
        if(_spinner_initialized && _show_changed_gesture_list) {
            mSpinnerChangeBeforeMap.clear();
            mSpinnerChangeAfterMap.clear();
            for (HashMap.Entry<Integer, Spinner> entry : mSpinnerGroup.entrySet()) {
                mSpinnerChangeBeforeMap.put(entry.getKey(), entry.getValue().getSelectedItem().toString());
            }
        }

        mSingleClickAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, getSpinnerSelections(SINGLE_CLICK, va));
        mDoubleClickAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, getSpinnerSelections(DOUBLE_CLICK, va));
        mTripleClickAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, getSpinnerSelections(TRIPLE_CLICK, va));
        mLongPressAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, getSpinnerSelections(LONG_PRESS, va));
        mDLongAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, getSpinnerSelections(DLONG, va));

        mSpinRightSingleClickKeyAction.setAdapter(mSingleClickAdapter);
        mSpinLeftSingleClickKeyAction.setAdapter(mSingleClickAdapter);
        mSpinRightDoubleClickKeyAction.setAdapter(mDoubleClickAdapter);
        mSpinLeftDoubleClickKeyAction.setAdapter(mDoubleClickAdapter);
        mSpinRightTripleClickKeyAction.setAdapter(mTripleClickAdapter);
        mSpinLeftTripleClickKeyAction.setAdapter(mTripleClickAdapter);
        mSpinRightLongPressKeyAction.setAdapter(mLongPressAdapter);
        mSpinLeftLongPressKeyAction.setAdapter(mLongPressAdapter);
        mSpinRightDLongKeyAction.setAdapter(mLongPressAdapter);
        mSpinLeftDLongKeyAction.setAdapter(mLongPressAdapter);

        mSingleClickAdapter.notifyDataSetChanged();
        mDoubleClickAdapter.notifyDataSetChanged();
        mTripleClickAdapter.notifyDataSetChanged();
        mLongPressAdapter.notifyDataSetChanged();
        mDLongAdapter.notifyDataSetChanged();

        if(_spinner_initialized && _show_changed_gesture_list) {
            for (HashMap.Entry<Integer, String> entry : mSpinnerChangeBeforeMap.entrySet()) {
                for (int j = 0; j < mSpinnerGroup.get(entry.getKey()).getCount(); j++) {
                    if (mSpinnerGroup.get(entry.getKey()).getItemAtPosition(j).toString().equalsIgnoreCase(entry.getValue())) {
                        mSpinnerGroup.get(entry.getKey()).setSelection(j);
                        break;
                    }
                }
            }
        }

        if (mDeviceType == DeviceType.HEADSET && mIsAgentRight) {
            if (va.equalsIgnoreCase("BISTO")) {
                mSpinnerGroup.get(DOUBLE_TAP_RIGHT).setSelection(mDoubleClickAdapter.getCount() - 1);
                mSpinnerGroup.get(TRIPLE_TAP_RIGHT).setSelection(mTripleClickAdapter.getCount() - 1);
            }
            mSpinnerGroup.get(LONG_PRESS_RIGHT).setSelection(mLongPressAdapter.getCount() - 1);
            mSpinnerGroup.get(LONG_PRESS_LEFT).setSelection(0);
        } else {
            if (va.equalsIgnoreCase("BISTO")) {
                mSpinnerGroup.get(DOUBLE_TAP_LEFT).setSelection(mDoubleClickAdapter.getCount() - 1);
                mSpinnerGroup.get(TRIPLE_TAP_LEFT).setSelection(mTripleClickAdapter.getCount() - 1);
            }
            mSpinnerGroup.get(LONG_PRESS_LEFT).setSelection(mLongPressAdapter.getCount() - 1);
            mSpinnerGroup.get(LONG_PRESS_RIGHT).setSelection(0);
        }

        mSpinnerGroup.get(DLONG_LEFT).setSelection(0);
        mSpinnerGroup.get(DLONG_RIGHT).setSelection(0);

        if(_spinner_initialized && _show_changed_gesture_list) {
            for (HashMap.Entry<Integer, Spinner> entry : mSpinnerGroup.entrySet()) {
                mSpinnerChangeAfterMap.put(entry.getKey(), entry.getValue().getSelectedItem().toString());
            }
            String msg = genChangedGestureListString();
            if(msg != "") {
                showAlertDialog(mActivity, "The gestures are changed due to different VA.", msg);
            }
        }
        _spinner_initialized = true;
        _show_changed_gesture_list = true;
    }

    private String genChangedGestureListString(){
        String rtn = "";
        int[] gesture_type = new int[]{SINGLE_TAP_LEFT, DOUBLE_TAP_LEFT, TRIPLE_TAP_LEFT, LONG_PRESS_LEFT, DLONG_LEFT,
                SINGLE_TAP_RIGHT, DOUBLE_TAP_RIGHT, TRIPLE_TAP_RIGHT, LONG_PRESS_RIGHT, DLONG_RIGHT};

        for (int i = 0; i < gesture_type.length; i++) {
            if (mDeviceType == DeviceType.HEADSET) {
                if (mIsAgentRight && gesture_type[i] % 2 == 1)
                    continue;
                if (!mIsAgentRight && gesture_type[i] % 2 == 0)
                    continue;
            }
            if (AirohaSDK.getInst().getChipType() == ChipType.AB155x && (gesture_type[i] == DLONG_LEFT || gesture_type[i] == DLONG_RIGHT))
                continue;

            if (mSpinnerChangeBeforeMap.containsKey(gesture_type[i]) && mSpinnerChangeAfterMap.containsKey(gesture_type[i]) &&
                    !mSpinnerChangeBeforeMap.get(gesture_type[i]).equalsIgnoreCase(mSpinnerChangeAfterMap.get(gesture_type[i]))) {
                rtn += getKeyTypeString(gesture_type[i]) + ": \n" + mSpinnerChangeBeforeMap.get(gesture_type[i]) + " -> " + mSpinnerChangeAfterMap.get(gesture_type[i]) + "\n\n";
            }
        }
        if(rtn.length() > 0){
            rtn = rtn.substring(0, rtn.length()-2);
        }
        return rtn;
    }

    private String getKeyTypeString(int type){
        switch(type) {
            case SINGLE_TAP_LEFT:
                return "SINGLE_CLICK_LEFT";
            case SINGLE_TAP_RIGHT:
                return "SINGLE_CLICK_RIGHT";
            case DOUBLE_TAP_LEFT:
                return "DOUBLE_CLICK_LEFT";
            case DOUBLE_TAP_RIGHT:
                return "DOUBLE_CLICK_RIGHT";
            case LONG_PRESS_LEFT:
                return "LONG_PRESS_LEFT";
            case LONG_PRESS_RIGHT:
                return "LONG_PRESS_RIGHT";
            case TRIPLE_TAP_LEFT:
                return "TRIPLE_CLICK_LEFT";
            case TRIPLE_TAP_RIGHT:
                return "TRIPLE_CLICK_RIGHT";
            case DLONG_LEFT:
                return "DLONG_LEFT";
            case DLONG_RIGHT:
                return "DLONG_RIGHT";
            default:
                return "UNKNOWN";
        }
    }

    private void updateToSetGestureList(boolean update_left, boolean update_right) {
        List<AirohaGestureSettings> infoList = new ArrayList<>();
        if (update_left) {
            infoList.add(new AirohaGestureSettings(SINGLE_TAP_LEFT, getGestureActionId(mSpinLeftSingleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(DOUBLE_TAP_LEFT, getGestureActionId(mSpinLeftDoubleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(TRIPLE_TAP_LEFT, getGestureActionId(mSpinLeftTripleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(LONG_PRESS_LEFT, getGestureActionId(mSpinLeftLongPressKeyAction.getSelectedItem().toString())));
            if(AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab1568")
                    || AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab1565")
                    || AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab158x")
                    || AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab157x")){
                infoList.add(new AirohaGestureSettings(DLONG_LEFT, getGestureActionId(mSpinLeftDLongKeyAction.getSelectedItem().toString())));
            }
        }
        if (update_right) {
            infoList.add(new AirohaGestureSettings(SINGLE_TAP_RIGHT, getGestureActionId(mSpinRightSingleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(DOUBLE_TAP_RIGHT, getGestureActionId(mSpinRightDoubleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(TRIPLE_TAP_RIGHT, getGestureActionId(mSpinRightTripleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(LONG_PRESS_RIGHT, getGestureActionId(mSpinRightLongPressKeyAction.getSelectedItem().toString())));
            if(AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab1568")
                    || AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab1565")
                    || AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab158x")
                    || AirohaSDK.getInst().getChipType().getName().toLowerCase().contains("ab157x")){
                infoList.add(new AirohaGestureSettings(DLONG_RIGHT, getGestureActionId(mSpinRightDLongKeyAction.getSelectedItem().toString())));
            }
        }

        gLogger.d(TAG, "Update left = " + update_left);
        gLogger.d(TAG, "Update right = " + update_right);

        mToSetGestureSettings = infoList;
        gLogger.d(TAG, "From UI: gesture info = " + getGestureInfoListString(mToSetGestureSettings));
    }

    private String getGestureInfoListString(List<AirohaGestureSettings> gesture_info) {
        String rtn = "";
        if (gesture_info == null || gesture_info.size() == 0) {
            rtn = "null";
        } else {
            rtn += "<GestureID,ActionID> ";
            for (AirohaGestureSettings info : gesture_info) {
                rtn += "<" + info.getGestureId() + "," + info.getActionId() + "> ";
            }
        }
        return rtn;
    }

    private int getGestureActionId(String gestureActionString) {
        if (gestureActionString.equalsIgnoreCase("DISABLE"))
            return AirohaGestureSettings.ACTION_NONE;
        else if (gestureActionString.equalsIgnoreCase("VOLUME_UP"))
            return AirohaGestureSettings.VOLUME_UP;
        else if (gestureActionString.equalsIgnoreCase("VOLUME_DOWN"))
            return AirohaGestureSettings.VOLUME_DOWN;
        else if (gestureActionString.equalsIgnoreCase("FORWARD"))
            return AirohaGestureSettings.NEXT_TRACK;
        else if (gestureActionString.equalsIgnoreCase("BACKWARD"))
            return AirohaGestureSettings.PREVIOUS_TRACK;
        else if (gestureActionString.equalsIgnoreCase("PLAY/PAUSE"))
            return AirohaGestureSettings.PLAY_PAUSE;
        else if (gestureActionString.equalsIgnoreCase("ANC/PASSTHROUGH"))
            return AirohaGestureSettings.SWITCH_ANC_AND_PASSTHROUGH;
        else if (gestureActionString.equalsIgnoreCase("ANC"))
            return AirohaGestureSettings.ANC;
        else if (gestureActionString.equalsIgnoreCase("PASS_THROUGH"))
            return AirohaGestureSettings.PASS_THROUGH;
        else if (gestureActionString.equalsIgnoreCase("SHARE_MODE_SWITCH"))
            return AirohaGestureSettings.SHARE_MODE_SWITCH;
        else if (gestureActionString.equalsIgnoreCase("SHARE_MODE_FOLLOWER_SWITCH"))
            return AirohaGestureSettings.SHARE_MODE_FOLLOWER_SWITCH;

        else if (gestureActionString.equalsIgnoreCase("WAKE_UP_SIRI_NOTIFY"))
            return AirohaGestureSettings.WAKE_UP_SIRI_NOTIFY;
        else if (gestureActionString.equalsIgnoreCase("GSOUND_NOTIFY"))
            return AirohaGestureSettings.GSOUND_NOTIFY;
        else if (gestureActionString.equalsIgnoreCase("GSOUND_CANCEL"))
            return AirohaGestureSettings.GSOUND_CANCEL;
        else if (gestureActionString.equalsIgnoreCase("GSOUND_QUERY"))
            return AirohaGestureSettings.GSOUND_QUERY;
        else if (gestureActionString.equalsIgnoreCase("WAKE_UP_ALEXA_TAP"))
            return AirohaGestureSettings.WAKE_UP_ALEXA_TAP;
        else if (gestureActionString.equalsIgnoreCase("WAKE_UP_ALEXA_HOLD"))
            return AirohaGestureSettings.WAKE_UP_ALEXA_HOLD;
        else if (gestureActionString.equalsIgnoreCase("WAKE_UP_XIAOWEI_TAP"))
            return AirohaGestureSettings.WAKE_UP_XIAOWEI_TAP;
        else if (gestureActionString.equalsIgnoreCase("WAKE_UP_XIAOWEI_HOLD"))
            return AirohaGestureSettings.WAKE_UP_XIAOWEI_HOLD;
        else if (gestureActionString.equalsIgnoreCase("WAKE_UP_XIAOAI_TAP"))
            return AirohaGestureSettings.WAKE_UP_XIAOAI_TAP;
        else if (gestureActionString.equalsIgnoreCase("WAKE_UP_XIAOAI_HOLD"))
            return AirohaGestureSettings.WAKE_UP_XIAOAI_HOLD;
        else
            return AirohaGestureSettings.ACTION_DEFAULT;
    }

    private String getGestureActionString(int gestureActionId) {
        switch (gestureActionId) {
            case AirohaGestureSettings.ACTION_NONE:
                return "DISABLE";
            case AirohaGestureSettings.VOLUME_UP:
                return "VOLUME_UP";
            case AirohaGestureSettings.VOLUME_DOWN:
                return "VOLUME_DOWN";
            case AirohaGestureSettings.NEXT_TRACK:
                return "FORWARD";
            case AirohaGestureSettings.PREVIOUS_TRACK:
                return "BACKWARD";
            case AirohaGestureSettings.PLAY_PAUSE:
                return "PLAY/PAUSE";
            case AirohaGestureSettings.SWITCH_ANC_AND_PASSTHROUGH:
                return "ANC/PASSTHROUGH";
            case AirohaGestureSettings.ANC:
                return "ANC";
            case AirohaGestureSettings.PASS_THROUGH:
                return "PASS_THROUGH";
            case AirohaGestureSettings.SHARE_MODE_SWITCH:
                return "SHARE_MODE_SWITCH";
            case AirohaGestureSettings.SHARE_MODE_FOLLOWER_SWITCH:
                return "SHARE_MODE_FOLLOWER_SWITCH";

            case AirohaGestureSettings.WAKE_UP_SIRI_NOTIFY:
                return "WAKE_UP_SIRI_NOTIFY";
            case AirohaGestureSettings.GSOUND_NOTIFY:
                return "GSOUND_NOTIFY";
            case AirohaGestureSettings.GSOUND_CANCEL:
                return "GSOUND_CANCEL";
            case AirohaGestureSettings.GSOUND_QUERY:
                return "GSOUND_QUERY";
            case AirohaGestureSettings.WAKE_UP_ALEXA_TAP:
                return "WAKE_UP_ALEXA_TAP";
            case AirohaGestureSettings.WAKE_UP_ALEXA_HOLD:
                return "WAKE_UP_ALEXA_HOLD";
            case AirohaGestureSettings.WAKE_UP_XIAOWEI_TAP:
                return "WAKE_UP_XIAOWEI_TAP";
            case AirohaGestureSettings.WAKE_UP_XIAOWEI_HOLD:
                return "WAKE_UP_XIAOWEI_HOLD";
            case AirohaGestureSettings.WAKE_UP_XIAOAI_TAP:
                return "WAKE_UP_XIAOAI_TAP";
            case AirohaGestureSettings.WAKE_UP_XIAOAI_HOLD:
                return "WAKE_UP_XIAOAI_HOLD";
            default:
                return "DEFAULT";
        }
    }

    private ArrayList getSpinnerSelections(String keyType, String va) {
        ArrayList<String> rtn = getSpinnerDefaultSelections(keyType);
        ArrayList<String> tmp = getSpinnerVaRelatedSelections(keyType, va);
        if (tmp != null) {
            for (String str : tmp) {
                rtn.add(str);
            }
        }
        return rtn;
    }

    private ArrayList getSpinnerDefaultSelections(String keyType) {
        if (keyType.equalsIgnoreCase(SINGLE_CLICK) || keyType.equalsIgnoreCase(DOUBLE_CLICK) || keyType.equalsIgnoreCase(TRIPLE_CLICK)) {
            return new ArrayList<>(Arrays.asList("DEFAULT", "DISABLE", "PLAY/PAUSE", "FORWARD", "BACKWARD", "VOLUME_UP", "VOLUME_DOWN", "ANC/PASSTHROUGH", "ANC", "PASS_THROUGH", "SHARE_MODE_SWITCH", "SHARE_MODE_FOLLOWER_SWITCH"));
        } else if (keyType.equalsIgnoreCase(LONG_PRESS) || keyType.equalsIgnoreCase(DLONG)) {
            return new ArrayList<>(Arrays.asList("DEFAULT", "DISABLE", "WAKE_UP_SIRI_NOTIFY"));
        }
        return new ArrayList<>();
    }

    private ArrayList getSpinnerVaRelatedSelections(String keyType, String va) {
        if (keyType.equalsIgnoreCase(DOUBLE_CLICK)) {
            if (va.equalsIgnoreCase(BISTO)) {
                return new ArrayList<>(Arrays.asList("GSOUND_NOTIFY"));
            }
        } else if (keyType.equalsIgnoreCase(TRIPLE_CLICK)) {
            if (va.equalsIgnoreCase(BISTO)) {
                return new ArrayList<>(Arrays.asList("GSOUND_CANCEL"));
            }
        } else if (keyType.equalsIgnoreCase(LONG_PRESS) || keyType.equalsIgnoreCase(DLONG)) {
            if (va.equalsIgnoreCase(BISTO)) {
                return new ArrayList<>(Arrays.asList("GSOUND_QUERY"));
            } else if (va.equalsIgnoreCase(AMA)) {
                return new ArrayList<>(Arrays.asList("WAKE_UP_ALEXA_HOLD", "WAKE_UP_ALEXA_TAP"));
            } else if (va.equalsIgnoreCase(XIAOWEI)) {
                return new ArrayList<>(Arrays.asList("WAKE_UP_XIAOWEI_HOLD", "WAKE_UP_XIAOWEI_TAP"));
            } else if (va.equalsIgnoreCase(XIAOAI)) {
                return new ArrayList<>(Arrays.asList("WAKE_UP_XIAOAI_HOLD", "WAKE_UP_XIAOAI_TAP"));
            }
        }
        return null;
    }

    private void setBistoSpinnerSelection(boolean is_right, boolean is_gsound) {
        if (is_right) {
            if (is_gsound) {
                mSpinRightDoubleClickKeyAction.setSelection(mSpinRightDoubleClickKeyAction.getAdapter().getCount() - 1);
                mSpinRightTripleClickKeyAction.setSelection(mSpinRightTripleClickKeyAction.getAdapter().getCount() - 1);
                mSpinRightLongPressKeyAction.setSelection(mSpinRightLongPressKeyAction.getAdapter().getCount() - 1);
            } else {
                if (((Spinner) mSpinRightDoubleClickKeyAction).getSelectedItem().toString().toLowerCase().contains("gsound"))
                    mSpinRightDoubleClickKeyAction.setSelection(0);
                if (((Spinner) mSpinRightTripleClickKeyAction).getSelectedItem().toString().toLowerCase().contains("gsound"))
                    mSpinRightTripleClickKeyAction.setSelection(0);
                if (((Spinner) mSpinRightLongPressKeyAction).getSelectedItem().toString().toLowerCase().contains("gsound"))
                    mSpinRightLongPressKeyAction.setSelection(0);
            }
        } else {
            if (is_gsound) {
                mSpinLeftDoubleClickKeyAction.setSelection(mSpinLeftDoubleClickKeyAction.getAdapter().getCount() - 1);
                mSpinLeftTripleClickKeyAction.setSelection(mSpinLeftTripleClickKeyAction.getAdapter().getCount() - 1);
                mSpinLeftLongPressKeyAction.setSelection(mSpinLeftLongPressKeyAction.getAdapter().getCount() - 1);
            } else {
                if (((Spinner) mSpinLeftDoubleClickKeyAction).getSelectedItem().toString().toLowerCase().contains("gsound"))
                    mSpinLeftDoubleClickKeyAction.setSelection(0);
                if (((Spinner) mSpinLeftTripleClickKeyAction).getSelectedItem().toString().toLowerCase().contains("gsound"))
                    mSpinLeftTripleClickKeyAction.setSelection(0);
                if (((Spinner) mSpinLeftLongPressKeyAction).getSelectedItem().toString().toLowerCase().contains("gsound"))
                    mSpinLeftLongPressKeyAction.setSelection(0);
            }
        }
    }

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!mSpinVaIndex.getSelectedItem().toString().toLowerCase().contains("google")) {
                return;
            }

            switch (parent.getId()) {
                case R.id.spinLeftDoubleClickKeyAction:
                case R.id.spinLeftTripleClickKeyAction:
                case R.id.spinLeftLongPressKeyAction:
                    if (((Spinner) parent).getSelectedItem().toString().toLowerCase().contains("gsound")) {
                        setBistoSpinnerSelection(false, true);
                    } else {
                        setBistoSpinnerSelection(false, false);
                    }
                    break;
                case R.id.spinRightDoubleClickKeyAction:
                case R.id.spinRightTripleClickKeyAction:
                case R.id.spinRightLongPressKeyAction:
                    if (((Spinner) parent).getSelectedItem().toString().toLowerCase().contains("gsound")) {
                        setBistoSpinnerSelection(true, true);
                    } else {
                        setBistoSpinnerSelection(true, false);
                    }
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void updateKeyMapUI(List<AirohaGestureSettings> list) {
        for (HashMap.Entry<Integer, Spinner> entry : mSpinnerGroup.entrySet()) {
            entry.getValue().setSelection(0);
        }

        for (int i = 0; i < list.size(); i++) {
            AirohaGestureSettings info = list.get(i);
            String actionStr = getGestureActionString(info.getActionId());

            int gesture = info.getGestureId();

            for (int j = 0; j < mSpinnerGroup.get(gesture).getCount(); j++) {
                if (mSpinnerGroup.get(gesture).getItemAtPosition(j).toString().equalsIgnoreCase(actionStr)) {
                    mSpinnerGroup.get(gesture).setSelection(j);
                    break;
                }
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
                        enableAllBtns();
                    }
                });
                break;
            case AirohaConnector.DISCONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disableAllBtns();
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
}
