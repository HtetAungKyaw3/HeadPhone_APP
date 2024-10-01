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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.libbase.RaceCommand.constant.RaceType;
import com.airoha.libcommon.AirohaCommonListener;
import com.airoha.libutils.Converter;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaCmdSettings;
import com.airoha.sdk.api.utils.AirohaStatusCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomCmdFragment extends BaseFragment {
    private String TAG = CustomCmdFragment.class.getSimpleName();
    private String LOG_TAG = "[CustomCmd] ";
    private MainActivity mActivity;
    private CustomCmdFragment mFragment;
    private View mView;

    private Button mBtnSendCmd;
    private Button mBtnClear;

    private EditText mEditTextCmd;
    //log list
    private ListView mRspView;
    private static ArrayAdapter<String> gRspAdapter;
    private final int RSP_MAX_COUNT = 50;

    private RadioButton mRadioButtonResp5B;
    private RadioButton mRadioButtonResp5C;
    private RadioButton mRadioButtonResp5D;
    private RadioButton mRadioButtonRespNone;
    private List<RadioButton> mRadioButtonList;

    private byte mRespType = RaceType.RESPONSE;

    private CheckBox mCheckBoxRelay;
    private LinearLayout mLinearLayoutPartnerID;
    private Button mBtnGetPartnerID;
    private EditText mEditTextPartnerID;
    private LinearLayout mLinearLayoutRelayCmd;
    private EditText mEditTextRelayCmd;

    public CustomCmdFragment(){
        mTitle = "CustomCmd UT";
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
        if(gRspAdapter == null) {
            gRspAdapter = new ArrayAdapter<>(mActivity, R.layout.message);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_custom_cmd, container,false);

        initUImember();

        return mView;
    }

    private void initUImember() {
        Bundle bundle = getArguments();

        mRadioButtonList = new ArrayList<>();

        mBtnSendCmd = mView.findViewById(R.id.buttonSendCmd);
        mEditTextCmd = mView.findViewById(R.id.editTextCmd);
//        mTextResp = mView.findViewById(R.id.textViewCmdResp);
        mRspView = mView.findViewById(R.id.listView_rsp);
        mRspView.setAdapter(gRspAdapter);

        mEditTextCmd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditTextCmd.length()%2 != 0) {
                    mBtnSendCmd.setEnabled(false);
                } else {
                    updateRelayCmd();
                    mBtnSendCmd.setEnabled(true);
                }
            }
        });

        mBtnSendCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmd;
                if (mCheckBoxRelay.isChecked()) {
                    cmd = mEditTextRelayCmd.getText().toString();
                } else {
                    cmd = mEditTextCmd.getText().toString();
                }

                final byte[] data = Converter.hexStringToByteArray(cmd);

//                mTextResp.setText("");
                String ret = Converter.byte2HexStr(data);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS    ");
                String timeStr = sdf.format(new Date());
                synchronized (gRspAdapter) {
                    gRspAdapter.add(timeStr + "Tx: " + ret);
                    if (gRspAdapter.getCount() >= RSP_MAX_COUNT) {
                        gRspAdapter.remove(gRspAdapter.getItem(0));
                    }
                }

                if (mRadioButtonRespNone.isChecked()) {
                    mActivity.getAirohaService().getHost().send(data);
                } else {
                    AirohaCmdSettings setting = new AirohaCmdSettings();
                    setting.setRespType(mRespType);
                    setting.setCommand(data);
                    AirohaSDK.getInst().getAirohaDeviceControl().sendCustomCommand(setting, new CustomCmdListener());
                }
            }
        });

        mBtnClear = mView.findViewById(R.id.buttonClear);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gRspAdapter.clear();
            }
        });

        mRadioButtonResp5B = mView.findViewById(R.id.radioButton_resp_5b);
        mRadioButtonResp5C = mView.findViewById(R.id.radioButton_resp_5c);
        mRadioButtonResp5D = mView.findViewById(R.id.radioButton_resp_5d);
        mRadioButtonRespNone = mView.findViewById(R.id.radioButton_resp_none);
        mRadioButtonResp5B.setOnClickListener(mOnClickListener);
        mRadioButtonResp5C.setOnClickListener(mOnClickListener);
        mRadioButtonResp5D.setOnClickListener(mOnClickListener);
        mRadioButtonRespNone.setOnClickListener(mOnClickListener);

        mRadioButtonList.add(mRadioButtonResp5B);
        mRadioButtonList.add(mRadioButtonResp5C);
        mRadioButtonList.add(mRadioButtonResp5D);
        mRadioButtonList.add(mRadioButtonRespNone);

        mCheckBoxRelay = mView.findViewById(R.id.checkbox_Relay);
        mCheckBoxRelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mBtnSendCmd.setText("Send Relay");
                    mRadioButtonRespNone.setChecked(true);
                    mLinearLayoutRelayCmd.setVisibility(View.VISIBLE);
                    updateRelayCmd();
                } else {
                    mBtnSendCmd.setText("Send");
                    mLinearLayoutRelayCmd.setVisibility(View.GONE);
                }
            }
        });

        mLinearLayoutPartnerID = mView.findViewById(R.id.linearLayout_PartnerId);
        mEditTextPartnerID = mView.findViewById(R.id.editTextPartnerId);
        mBtnGetPartnerID = mView.findViewById(R.id.buttonGetPartnerID);
        mBtnGetPartnerID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getAirohaService().getAirohaCommonMgr().getAvailableDstID();
            }
        });

        mLinearLayoutRelayCmd = mView.findViewById(R.id.linearLayout_RelayCmd);
        mEditTextRelayCmd = mView.findViewById(R.id.editTextRelayCmd);
    }

    @Override
    public void onResume() {
        gLogger.d(TAG, "onResume");
        super.onResume();
        if (mActivity.getAirohaService() != null) {
            mActivity.getAirohaService().getHost().addHostDataListener(TAG, mFragment);
            mActivity.getAirohaService().getAirohaCommonMgr().addListener(TAG, mAirohaCommonListener);
            if (AirohaSDK.getInst().isPartnerExisting()) {
                mActivity.getAirohaService().getAirohaCommonMgr().getAvailableDstID();
            }
        }
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
        if (mActivity.getAirohaService() == null) {
            return;
        }

        if (hidden) {
            mActivity.getAirohaService().getHost().removeHostDataListener(TAG);
            mActivity.getAirohaService().getAirohaCommonMgr().removeListener(TAG);
        } else {
            mActivity.getAirohaService().getHost().addHostDataListener(TAG, mFragment);
            mActivity.getAirohaService().getAirohaCommonMgr().addListener(TAG, mAirohaCommonListener);
        }
    }

    class CustomCmdListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            byte[] resp = (byte[]) msg.getMsgContent();
                            String ret = Converter.byte2HexStr(resp);
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS    ");
                            String timeStr = sdf.format(new Date());
                            synchronized (gRspAdapter) {
                                gRspAdapter.add(timeStr + "Rx: " + ret);
                                if (gRspAdapter.getCount() >= RSP_MAX_COUNT) {
                                    gRspAdapter.remove(gRspAdapter.getItem(0));
                                }
                            }
                        }

                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "sendCustomCmdStatus: " + code.getDescription());
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
        }
    }

    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.radioButton_resp_5b:
                    mRespType = RaceType.RESPONSE;
                    break;
                case R.id.radioButton_resp_5c:
                    mRespType = RaceType.CMD_NO_RESP;
                    break;
                case R.id.radioButton_resp_5d:
                    mRespType = RaceType.INDICATION;
                    break;
                case R.id.radioButton_resp_none:
                    mRespType = (byte)0xFF;
                    break;
            }
        }
    };

    void updateRelayCmd() {
        String cmd = mEditTextCmd.getText().toString();
        if (cmd.length() > 0 && cmd.length()%2 == 0) {
            byte[] cmdRaw = Converter.hexStrToBytes(cmd);

            byte[] payload = new byte[2 + cmdRaw.length];

            String id = mEditTextPartnerID.getText().toString();
            byte[] dst = new byte[]{0x05, Converter.hexStrToBytes(id)[0]};
            System.arraycopy(dst, 0, payload, 0, 2);
            System.arraycopy(cmdRaw, 0, payload, 2, cmdRaw.length);

            byte[] relayRaceID = new byte[]{0x0D, 0x01};
            int relayCmdLength = relayRaceID.length + payload.length;

            String relayCmd = "055A";
            relayCmd += Converter.byte2HexStr((byte) (relayCmdLength & 0xFF));
            relayCmd += Converter.byte2HexStr((byte) ((relayCmdLength >> 8) & 0xFF));
            relayCmd += Converter.byte2HexStr(relayRaceID[1]);
            relayCmd += Converter.byte2HexStr(relayRaceID[0]);
            relayCmd += Converter.byte2HexStr(payload).replace(" ", "");
            mEditTextRelayCmd.setText(relayCmd);
        }
    }

    @Override
    public boolean onHostPacketReceived(final byte[] packet) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRadioButtonRespNone.isChecked()) {
                    try {
                        String ret = Converter.byte2HexStr(packet);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS    ");
                        String timeStr = sdf.format(new Date());
                        synchronized (gRspAdapter) {
                            gRspAdapter.add(timeStr + "Rx: " + ret);
                            if (gRspAdapter.getCount() >= RSP_MAX_COUNT) {
                                gRspAdapter.remove(gRspAdapter.getItem(0));
                            }
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            }
        });
        return false;
    }

    AirohaCommonListener mAirohaCommonListener = new AirohaCommonListener() {

        @Override
        public void OnRespSuccess(String stageName) {

        }

        @Override
        public void onStopped(String stageName) {

        }

        @Override
        public void onResponseTimeout() {

        }

        @Override
        public void onNotifyReadChipName(boolean isOk, String chipName) {

        }

        @Override
        public void onNotifyReadDeviceType(byte type, byte awsMode) {

        }

        @Override
        public void onNotifyAvailableDstId(final byte type, final byte dstId) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dstId == (byte)0xFF) {
                        mCheckBoxRelay.setEnabled(false);
                        mCheckBoxRelay.setChecked(false);
                    } else {
                        mCheckBoxRelay.setEnabled(true);
                        mEditTextPartnerID.setText(Converter.byte2HexStr(dstId));
                    }
                }
            });
        }

        @Override
        public void onNotifyReadNvdmVersion(boolean b, byte b1) {

        }
    };

}

