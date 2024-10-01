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
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.control.PEQControl;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaEQPayload;
import com.airoha.sdk.api.message.AirohaEQSettings;
import com.airoha.sdk.api.message.AirohaEQStatusMsg;
import com.airoha.sdk.api.utils.AirohaEQBandType;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.sdk.api.utils.ConnectionProtocol;
import com.airoha.utapp.sdk.MainActivity.MsgType;

import java.util.ArrayList;
import java.util.LinkedList;

public class PeqFragment extends BaseFragment {
    private String TAG = PeqFragment.class.getSimpleName();
    private String LOG_TAG = "[PEQ] ";
    private PeqFragment mPeqFragment;
    private View mView;

    protected SeekBar mSeekBarGain0;
    protected SeekBar mSeekBarGain1;
    protected SeekBar mSeekBarGain2;
    protected SeekBar mSeekBarGain3;
    protected SeekBar mSeekBarGain4;
    protected SeekBar mSeekBarGain5;
    protected SeekBar mSeekBarGain6;
    protected SeekBar mSeekBarGain7;
    protected SeekBar mSeekBarGain8;
    protected SeekBar mSeekBarGain9;

    protected EditText mEditTextGain0;
    protected EditText mEditTextGain1;
    protected EditText mEditTextGain2;
    protected EditText mEditTextGain3;
    protected EditText mEditTextGain4;
    protected EditText mEditTextGain5;
    protected EditText mEditTextGain6;
    protected EditText mEditTextGain7;
    protected EditText mEditTextGain8;
    protected EditText mEditTextGain9;

    protected EditText mEditTextFreq0;
    protected EditText mEditTextFreq1;
    protected EditText mEditTextFreq2;
    protected EditText mEditTextFreq3;
    protected EditText mEditTextFreq4;
    protected EditText mEditTextFreq5;
    protected EditText mEditTextFreq6;
    protected EditText mEditTextFreq7;
    protected EditText mEditTextFreq8;
    protected EditText mEditTextFreq9;

    protected EditText mEditTextQ0;
    protected EditText mEditTextQ1;
    protected EditText mEditTextQ2;
    protected EditText mEditTextQ3;
    protected EditText mEditTextQ4;
    protected EditText mEditTextQ5;
    protected EditText mEditTextQ6;
    protected EditText mEditTextQ7;
    protected EditText mEditTextQ8;
    protected EditText mEditTextQ9;

    // interaction
//    protected Button mBtnLoadUiData;
    protected Button mBtnUpdateRealTimePEQ;
    protected Button mBtnSavePeqCoef;
    private Button mBtnResetPEQ;
//    protected Button mBtnUpdatePeqUiData;
//    protected Button mBtnLazyForTest;

    //protected Button mBtnDisableEq;

    private Button mBtnBandTotal1;
    private Button mBtnBandTotal2;
    private Button mBtnBandTotal3;
    private Button mBtnBandTotal4;
    private Button mBtnBandTotal5;
    private Button mBtnBandTotal6;
    private Button mBtnBandTotal7;
    private Button mBtnBandTotal8;
    private Button mBtnBandTotal9;
    private Button mBtnBandTotal10;

    //    private Button mBtnPEQGrpNum;
    private Button mBtnPEQGrpIdx;
    private TextView mTxtPresetEqCount;
    private TextView mTxtCustomizedEqCount;
    private Spinner mSpinnerPEQGrp;
    private Button mBtnSetPEQGrpIdx;

    private Button mBtnReplacePEQ;
    private TextView mTxtReplacePEQFrom;
    private TextView mTxtResetPEQ;

    private CheckBox mCkbSupportLDAC;
    private EditText[] mEditFreqs;
    private SeekBar[] mSeekBars;
    private EditText[] mEditGains;
    private EditText[] mEditQs;
    private Button[] mBtnBandTotals;

    private LinearLayout mEqTableLayout;

    private final double PROGRESS_STEP = 0.01;

    private int mBandTotals = 10;
    private int mPeqSpinnerIndex = 0;

    private int mPresetEqCount = 0;
    private int mCustomEqCount = 0;
    private int mRunningCategoryID = 0;

    private boolean mIsBusy = false;
    private boolean mIsInitFlow = true;
    private boolean mIsSetEQIndex = false;
    private boolean mIsSupportLDAC = false;
    final int SAMPLE_RATE = 44100;

    LinkedList<AirohaEQSettings> mTmpEqSettingList = null;
    LinkedList<AirohaEQSettings> mEqSettingList = null;

    private AirohaEQPayload mTargetEqPayload = null;

    private boolean mIsSaving = false;
    private byte mRealtimeIndex = 0;

    public PeqFragment() {
        mTitle = "PEQ UT";
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPeqFragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_peq, container, false);
        AirohaConnector airohaDeviceConnector = AirohaSDK.getInst().getAirohaDeviceConnector();
        airohaDeviceConnector.registerConnectionListener(mPeqFragment);

        initUImember();

        if (mActivity.getAirohaService() != null) {
//            mBtnPEQGrpIdx.performClick();
            mTmpEqSettingList = null;
            mEqSettingList = null;
            AirohaSDK.getInst().getAirohaDeviceControl().getTwsConnectStatus(new TwsConnectStatusListener());
            AirohaSDK.getInst().getAirohaEQControl().getAllEQSettings(new GetAllEQSettingsListener());
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        gLogger.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onStatusChanged(final int status) {
        switch (status) {
            case AirohaConnector.CONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AirohaSDK.getInst().getAirohaEQControl().getAllEQSettings(new GetAllEQSettingsListener());
                    }
                });
                break;
            case AirohaConnector.DISCONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setBtnEnable(false);
                        showMessageDialog("BT disconnected. Please reconnect BT.");
                    }
                });
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
        super.onHiddenChanged(hidden);
        if (mActivity.getAirohaService() == null) {
            return;
        }
        if (!hidden) {
            AirohaSDK.getInst().getAirohaEQControl().getAllEQSettings(new GetAllEQSettingsListener());
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

    private void setBtnEnable(boolean isEnable) {
        mSpinnerPEQGrp.setEnabled(isEnable);
        mBtnPEQGrpIdx.setEnabled(isEnable);
        mBtnSetPEQGrpIdx.setEnabled(isEnable);
        mBtnUpdateRealTimePEQ.setEnabled(isEnable);
        mBtnSavePeqCoef.setEnabled(isEnable);
        //mEqTableLayout.setEnabled(isEnable);
        setLayoutChildEnable(mEqTableLayout, isEnable);
    }

    private void setLayoutChildEnable(LinearLayout layout, boolean isEnable) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof LinearLayout)
                setLayoutChildEnable((LinearLayout) child, isEnable);
            else {
                child.setEnabled(isEnable);
            }
        }
    }

    private void initUImember() {
        mEqTableLayout = mView.findViewById(R.id.linearLayout_peq_band_table);

        mBtnSavePeqCoef = mView.findViewById(R.id.buttonSavePeqCoef);
        mBtnSavePeqCoef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.updateMsg(MsgType.GENERAL, "setEQSetting, please wait...");
                setEQ(true);
            }
        });

        mBtnReplacePEQ = mView.findViewById(R.id.buttonReplacePEQ);
        mBtnReplacePEQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceAndSaveEQ();
            }
        });

        mBtnUpdateRealTimePEQ = mView.findViewById(R.id.buttonUpdateRealtimePEQ);
        mBtnUpdateRealTimePEQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.updateMsg(MsgType.GENERAL, "realtimeEQSetting...");
                setEQ(false);
            }
        });

        mBtnResetPEQ = mView.findViewById(R.id.buttonResetPEQ);
        mBtnResetPEQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEQ();
            }
        });

        mBtnPEQGrpIdx = mView.findViewById(R.id.buttonPEQGrpIdx);
        mBtnPEQGrpIdx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBusy) {
                    return;
                }
                v.setEnabled(false);
                mIsBusy = true;
                mActivity.updateMsg(MsgType.GENERAL, "getRunningEQ...");
                AirohaSDK.getInst().getAirohaEQControl().getRunningEQSetting(new GetRunningEQSettingListener());
            }
        });

        mBtnSetPEQGrpIdx = mView.findViewById(R.id.buttonSetPEQGrpIdx);
        mBtnSetPEQGrpIdx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBusy) {
                    return;
                }
                try {
//                    mBtnDisableEq.setEnabled(false);
//                    mBtnSetPEQGrpIdx.setEnabled(false);
//                    mSpinnerPEQGrp.setEnabled(false);
                    mPeqSpinnerIndex = mSpinnerPEQGrp.getSelectedItemPosition();
                    mIsBusy = true;
                    mActivity.updateMsg(MsgType.GENERAL, "set EQ Index...");
                    if(mPeqSpinnerIndex == 0) { // disable
                        showAlertDialog(mActivity, "Error", "invalid EQ index");
//                        AirohaSDK.getInst().getAirohaDeviceControl().getTwsConnectStatus(new TwsConnectStatusListener());
//                        AirohaSDK.getInst().getAirohaEQControl().setEQSetting(0, null, false, new SetEQSettingListener());
                        return;
                    }
                    mIsSetEQIndex = true;
                    int categoryId = Integer.parseInt(mSpinnerPEQGrp.getSelectedItem().toString().replace("EQ", "").replace("+", "").trim());
                    AirohaSDK.getInst().getAirohaEQControl().setEQSetting(categoryId, null, false, new SetEQSettingListener());
                } catch (Exception e) {
                    e.printStackTrace();
                    gLogger.e(TAG, e.getMessage());
                    mIsBusy = false;
//                    mBtnDisableEq.setEnabled(true);
                    mBtnSetPEQGrpIdx.setEnabled(true);
                    mSpinnerPEQGrp.setEnabled(true);
                }
            }
        });

        mTxtPresetEqCount = mView.findViewById(R.id.txtPresetEqCount);
        mTxtCustomizedEqCount = mView.findViewById(R.id.txtCustomizedEqCount);
        mSpinnerPEQGrp = mView.findViewById(R.id.spinPEQGrp);
        mTxtReplacePEQFrom = mView.findViewById(R.id.txtReplacePEQFrom);
        mTxtResetPEQ = mView.findViewById(R.id.txtResetPEQ);
        mCkbSupportLDAC = mView.findViewById(R.id.ckbSupportLDAC);

        configUiInputGroup();

        configFreqsTextChangedListener();
        configSeekBarChangedListener();
        configGainsTextChangedListener();
        configBandTotalsChangedListener();

        giveDefaultInputParam();

        mBtnBandTotals[mBandTotals - 1].setTextColor(Color.RED);

        mSpinnerPEQGrp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                /// position 0 is disable
                mPeqSpinnerIndex = position;
                mIsSetEQIndex = false;
                if(mSpinnerPEQGrp.getSelectedItem().toString().length() == 0)
                {
//                    mBtnReplacePEQ.setEnabled(false);
                    hideEqSettingLayout();
                    mBtnSetPEQGrpIdx.setEnabled(false);
                    return;
                }
                int presetEQIndex = Integer.parseInt(mSpinnerPEQGrp.getSelectedItem().toString().replace("EQ", "").replace("+", "").trim());
                String msg = "";
                if(mSpinnerPEQGrp.getSelectedItem().toString().contains("+")) {
                    msg = mSpinnerPEQGrp.getSelectedItem().toString() + " replace to EQ" + (presetEQIndex - 100);
                    mBtnReplacePEQ.setEnabled(true);
                } else {
                    msg = "Not supported";
                    mBtnReplacePEQ.setEnabled(false);
                }
                mTxtReplacePEQFrom.setText(msg);

                String resetMsg = "";
                if(presetEQIndex < 100 || mSpinnerPEQGrp.getSelectedItem().toString().contains("+"))
                {
                    resetMsg = "Not supported";
                    //mBtnResetPEQ.setEnabled(false);
                }
                else
                {
                    resetMsg = "Reset " + mSpinnerPEQGrp.getSelectedItem().toString();
                    mBtnResetPEQ.setEnabled(true);
                }

                mTxtResetPEQ.setText(resetMsg);

                if(mRealtimeTmpEqSettings != null && mRealtimeTmpEqSettings.getEqPayload() != null) {
                    if(mRealtimeTmpEqSettings.getEqPayload().getIndex() == mPeqSpinnerIndex) {
                        if(mPeqSpinnerIndex > mEqSettingList.size())
                            mBtnSetPEQGrpIdx.setEnabled(false);
                        else
                            mBtnSetPEQGrpIdx.setEnabled(true);
                        mEqTableLayout.setVisibility(View.VISIBLE);
                        mBtnSavePeqCoef.setEnabled(true);
                        mBtnUpdateRealTimePEQ.setEnabled(true);
                        return;
                    } else {
                        mRealtimeTmpEqSettings = null;
                    }
                }
                if(mPeqSpinnerIndex > mEqSettingList.size()) {
                    giveDefaultInputParam();
                    mBtnBandTotals[9].performClick();
                    mBtnSetPEQGrpIdx.setEnabled(false);
                    mEqTableLayout.setVisibility(View.VISIBLE);
                    mBtnSavePeqCoef.setEnabled(true);
                    mBtnUpdateRealTimePEQ.setEnabled(true);
                    return;
                }
                int categoryId = mEqSettingList.get(mPeqSpinnerIndex - 1).getCategoryId();
                if(categoryId > 100) {
                    AirohaEQSettings eqSettings = getCustomEQSettings(categoryId);
                    if (eqSettings != null) {
                        updateBandTable(eqSettings.getEqPayload());
                        mBtnSetPEQGrpIdx.setEnabled(true);
                    } else {
                        giveDefaultInputParam();
                        mBtnBandTotals[9].performClick();
                        mBtnSetPEQGrpIdx.setEnabled(false);
                    }
                    mEqTableLayout.setVisibility(View.VISIBLE);
                    mBtnSavePeqCoef.setEnabled(true);
                    mBtnUpdateRealTimePEQ.setEnabled(true);
                } else {
                    hideEqSettingLayout();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    void hideEqSettingLayout() {
        mBtnSetPEQGrpIdx.setEnabled(true);
        mEqTableLayout.setVisibility(View.GONE);
        mBtnSavePeqCoef.setEnabled(false);
        mBtnReplacePEQ.setEnabled(false);
        mBtnUpdateRealTimePEQ.setEnabled(false);
    }

    void updateEQSettingList(int categoryId, AirohaEQPayload eqPayload) {
        AirohaEQSettings eqSettings = getCustomEQSettings(categoryId);
        if (eqSettings == null) {
            eqSettings = new AirohaEQSettings();
            eqSettings.setCategoryId(categoryId);
            eqSettings.setEqPayload(eqPayload);
            mTmpEqSettingList.add(eqSettings);
        } else {
            eqSettings.setEqPayload(eqPayload);
        }
    }

    AirohaEQSettings getCustomEQSettings(int categoryId) {
        AirohaEQSettings ret = null;
        for(int i=0; i<mTmpEqSettingList.size(); ++i) {
            AirohaEQSettings eqSettings = mTmpEqSettingList.get(i);
            if (eqSettings.getCategoryId() == categoryId) {
                ret = eqSettings;
                break;
            }
        }
        return ret;
    }

    void resetEQ() {
        mActivity.updateMsg(MsgType.GENERAL, "reset EQ Settings...");
        mPeqSpinnerIndex = 0;
        mRunningCategoryID = 0;
        mEqSettingList.clear();
        int presetIndex = mSpinnerPEQGrp.getSelectedItemPosition();
        AirohaSDK.getInst().getAirohaEQControl().resetEQSetting(presetIndex, new ResetEQSettingListener());
    }

    void setEQ(Boolean saveOrNot) {
        if (mIsBusy) {
            return;
        }
        AirohaSDK.getInst().getAirohaDeviceControl().getTwsConnectStatus(new TwsConnectStatusListener());
        try {
            mIsSaving = saveOrNot;
//            mBtnSetPEQGrpIdx.setEnabled(false);
//            mSpinnerPEQGrp.setEnabled(false);
//            mBtnSavePeqCoef.setEnabled(false);
//            mBtnPEQGrpIdx.setEnabled(false);
//            mBtnUpdateRealTimePEQ.setEnabled(false);
//            mBtnReplacePEQ.setEnabled(false);

            mIsBusy = true;

            AirohaEQPayload eqPayload = new AirohaEQPayload();
            int[] allSR = {44100, 48000};
            mIsSupportLDAC = mCkbSupportLDAC.isChecked();
            if (mIsSupportLDAC) {
                allSR = new int[]{44100, 48000, 88200, 96000};
            }
            eqPayload.setAllSampleRates(allSR);

            int categoryID = Integer.parseInt(mSpinnerPEQGrp.getSelectedItem().toString().replace("EQ", "").replace("+", "").trim());
            int peqIndex = checkCustomCategoryId(categoryID);
            if(peqIndex == -1) // not saved yet
            {
                if(!saveOrNot) //realtime
                    mPeqSpinnerIndex = mSpinnerPEQGrp.getSelectedItemPosition();
                else
                    mPeqSpinnerIndex = mEqSettingList.size() + 1;
            }
            else
            {
                mPeqSpinnerIndex = peqIndex;
            }
            if(categoryID > 100) {
                LinkedList<AirohaEQPayload.EQIDParam> params = createUiData();
                eqPayload.setBandCount(params.size());
                eqPayload.setIirParams(params);
                eqPayload.setIndex(mPeqSpinnerIndex);
                //eqPayload.setLeftGain(-4);
                if(mIsSaving)
                    updateEQSettingList(categoryID, eqPayload);
            }

            AirohaSDK.getInst().getAirohaEQControl().setEQSetting(categoryID, eqPayload, saveOrNot, new SetEQSettingListener());
        } catch (Exception e) {
            e.printStackTrace();
            gLogger.e(TAG, e.getMessage());
            mIsBusy = false;
//                    mBtnDisableEq.setEnabled(true);
            mBtnSavePeqCoef.setEnabled(true);
            mBtnUpdateRealTimePEQ.setEnabled(true);
            mBtnReplacePEQ.setEnabled(true);
            mIsSaving = false;
        }
    }

    void replaceAndSaveEQ() {
        if (mIsBusy) {
            return;
        }
        try {

            mIsSaving = true;
            mBtnReplacePEQ.setEnabled(false);
            mBtnSetPEQGrpIdx.setEnabled(false);
            mSpinnerPEQGrp.setEnabled(false);
            mBtnSavePeqCoef.setEnabled(false);
            mBtnUpdateRealTimePEQ.setEnabled(false);
            mIsBusy = true;

            AirohaEQPayload eqPayload = new AirohaEQPayload();
            int[] allSR = {44100, 48000};
            mIsSupportLDAC = mCkbSupportLDAC.isChecked();
            if (mIsSupportLDAC) {
                allSR = new int[]{44100, 48000, 88200, 96000};
            }
            eqPayload.setAllSampleRates(allSR);

            int categoryIDFrom = Integer.parseInt(mSpinnerPEQGrp.getSelectedItem().toString().replace("EQ", "").replace("+", "").trim());
            int categoryIDTo = categoryIDFrom - 100;

            int peqIndex = checkCustomCategoryId(categoryIDFrom);
            if(peqIndex == -1)
            {
                mPeqSpinnerIndex = mEqSettingList.size() + 1;
            }
            else
            {
                mPeqSpinnerIndex = peqIndex;
            }
            if(categoryIDFrom > 100) {
                LinkedList<AirohaEQPayload.EQIDParam> params = createUiData();
                eqPayload.setBandCount(params.size());
                eqPayload.setIirParams(params);
                eqPayload.setIndex(mPeqSpinnerIndex);
                eqPayload.setLeftGain(0);
                updateEQSettingList(categoryIDFrom, eqPayload);
            }

            mActivity.updateMsg(MsgType.GENERAL, "setEQSetting...");
            AirohaSDK.getInst().getAirohaDeviceControl().getTwsConnectStatus(new TwsConnectStatusListener());
            AirohaSDK.getInst().getAirohaEQControl().setEQSetting(categoryIDFrom, eqPayload, true, new SetEQSettingListener());


            mPeqSpinnerIndex = mSpinnerPEQGrp.getSelectedItemPosition();
            int peqIndexFrom = checkCustomCategoryId(categoryIDFrom);
            int peqIndexTo = checkCustomCategoryId(categoryIDTo);
            if(peqIndexFrom == -1)
            {
                mPeqSpinnerIndex = mEqSettingList.size() + 1;
            }
            else
            {
                mPeqSpinnerIndex = peqIndexFrom;
            }

            mActivity.updateMsg(MsgType.GENERAL, "replaceEQ setting...");
            AirohaSDK.getInst().getAirohaEQControl().replaceEQSetting(categoryIDFrom, categoryIDTo, new ReplaceEQSettingListener());
        } catch (Exception e) {
            e.printStackTrace();
            gLogger.e(TAG, e.getMessage());
            mIsBusy = false;
            mBtnSavePeqCoef.setEnabled(true);
            mBtnUpdateRealTimePEQ.setEnabled(true);
            mBtnReplacePEQ.setEnabled(true);
            mIsSaving = false;
        }
    }

    void updateAllEq(final AirohaEQStatusMsg msg) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinkedList<AirohaEQSettings> eqSettingsList = msg.getMsgContent();
                mPresetEqCount = 0;
                mCustomEqCount = 0;
                mRunningCategoryID = 0;
                int runningIndex = 0;
                for (int i = 0; i < eqSettingsList.size(); ++i) {
                    ++runningIndex;
                    AirohaEQSettings eqSettings = eqSettingsList.get(i);
                    if (eqSettings.getCategoryId() < 100) {
                        ++mPresetEqCount;
                    } else {
                        ++mCustomEqCount;
                    }
                    if (eqSettings.getStatus() == 1) {
                        mRunningCategoryID = eqSettings.getCategoryId();
                        mPeqSpinnerIndex = runningIndex;
                    }
                }
                mTxtPresetEqCount.setText(String.valueOf(mPresetEqCount));
                mTxtCustomizedEqCount.setText(String.valueOf(mCustomEqCount));

                if (mEqSettingList == null || mEqSettingList.size() == 0) {
                    mEqSettingList = eqSettingsList;
                    mTmpEqSettingList = new LinkedList<>();
                    for (AirohaEQSettings settings:mEqSettingList) {
                        mTmpEqSettingList.add(settings);
                    }
                }

                updateRunningEqUI();
            }
        });
    }

    private AirohaEQSettings mRealtimeTmpEqSettings;
    void updateRunningEqUI() {
        initSpinnerGrpItem();

        AirohaEQPayload eqPayload = null;
        for(int i = 0; i < mEqSettingList.size(); i++) {
            AirohaEQSettings eqSettings = mEqSettingList.get(i);
            if (mRunningCategoryID == eqSettings.getCategoryId()) {
                eqPayload = eqSettings.getEqPayload();
                break;
            }
        }
        if (eqPayload != null) {
            updateBandTable(eqPayload);
        } else {
            if(mRealtimeTmpEqSettings != null && mRealtimeTmpEqSettings.getEqPayload() != null)
                updateBandTable(mRealtimeTmpEqSettings.getEqPayload());
        }

        mIsBusy = false;
        mBtnPEQGrpIdx.setEnabled(true);
        mBtnSetPEQGrpIdx.setEnabled(true);
        mSpinnerPEQGrp.setEnabled(true);
        mTxtPresetEqCount.setText(String.valueOf(mPresetEqCount));
        mTxtCustomizedEqCount.setText(String.valueOf(mCustomEqCount));
    }

    void updateBandTable(AirohaEQPayload eqPayload) {
        int enabledBandCount = (int) eqPayload.getBandCount();
        LinkedList<AirohaEQPayload.EQIDParam> eqParams = eqPayload.getIirParams();
        for (int i = 0; i < eqParams.size(); ++i) {
            AirohaEQPayload.EQIDParam param = eqParams.get(i);
            mEditFreqs[i].setText(String.valueOf(param.getFrequency()));
            mEditGains[i].setText(String.valueOf(param.getGainValue()));
            mEditQs[i].setText(String.valueOf(param.getQValue()));
        }
        if(enabledBandCount > 0 && enabledBandCount <= mBtnBandTotals.length) {
            mBtnBandTotals[enabledBandCount - 1].performClick();
        }
        else{
            mActivity.updateMsg(MsgType.ERROR, "updateUI: Please check number of bands.");
        }
    }

//    class UpdateAndSaveThread extends Thread {
//        @Override
//        public void run() {
//            String tmp;
//            try {
//                AirohaEQPayload eqPayload = new AirohaEQPayload();
//                eqPayload.setSampleRate(SAMPLE_RATE);
//                if (mPeqSpinnerIndex > 10) {
//                    LinkedList<AirohaEQPayload.EQIDParam> params = createUiData();
//                    eqPayload.setBandCount(mBandTotals);
//                    eqPayload.setIirParams(params);
//                    if ((mPeqSpinnerIndex - 10) > mCustomEqCount) {
//                        AirohaEQSettings eqSettings = new AirohaEQSettings();
//                        eqSettings.setEqIndex(mPeqSpinnerIndex);
//                        eqSettings.setEqPayload(eqPayload);
//                        mEqSettingList.add(eqSettings);
//                    }
//                }
//
////                final ControlResult ret = mActivity.getAirohaService().getAirohaEQControl().setAndEnable(mPeqSpinnerIndex, eqPayload, true);
////                tmp = ret.getStatus().getDescription();
////                if (ret.getStatus().getValue() == 0) {
////                    updateRunningEqIndex(ret);
////                }
//            } catch (Exception e) {
//                gLogger.e(TAG, e.getMessage());
//                tmp = e.getMessage();
//            }
//
////            final String status = tmp;
////            mActivity.runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    mIsBusy = false;
////                    mActivity.updateMsg(MsgType.GENERAL, "setAndEnable: " + status);
//////                    mBtnDisableEq.setEnabled(true);
////                    mBtnSavePeqCoef.setEnabled(true);
////                    mBtnSetPEQGrpIdx.setEnabled(true);
////                    mSpinnerPEQGrp.setEnabled(true);
////                }
////            });
//        }
//    }

    private LinkedList<AirohaEQPayload.EQIDParam> createUiData() {

        LinkedList<AirohaEQPayload.EQIDParam> ret = new LinkedList<>();

        for (int i = 0; i < mBandTotals; i++) {
            AirohaEQPayload.EQIDParam bandInfo = new AirohaEQPayload.EQIDParam();
            float freq = Float.valueOf(mEditFreqs[i].getText().toString());
            float gain = Float.valueOf(mEditGains[i].getText().toString());
            float q = Float.valueOf(mEditQs[i].getText().toString());

            if (i < mBandTotals) {
                bandInfo.setBandType(AirohaEQBandType.BAND_PASS.getValue());
                bandInfo.setFrequency(freq);
                bandInfo.setGainValue(gain);
                bandInfo.setQValue(q);
            }
//            else {
//                bandInfo.setBandType(0x02);
//                bandInfo.setFrequency(0);
//                bandInfo.setGainValue(0);
//                bandInfo.setQValue(0);
//            }

            ret.add(bandInfo);
        }

        return ret;
    }

    private void configSeekBarChangedListener() {
        for (int i = 0; i < mSeekBars.length; i++) {

            final EditText editGain = mEditGains[i];

            mSeekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    double value = convertProgressBar(progress);

                    editGain.setText(String.format("%2.2f", value));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    private void configGainsTextChangedListener() {
        for (int i = 0; i < mEditGains.length; i++) {
            final SeekBar seekBar = mSeekBars[i];
            mEditGains[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        double val = Double.valueOf(editable.toString());
                        seekBar.setMax(getGainProgressMax());
                        seekBar.setProgress(covertToProgress(val));
                    } catch (Exception e) {

                    }

                }
            });
        }
    }

    private void configBandTotalsChangedListener() {
        for (int i = 0; i < mBtnBandTotals.length; i++) {
            BandButton_Click tmp = new BandButton_Click(i + 1);
            mBtnBandTotals[i].setOnClickListener(tmp);
        }
    }

    private double convertProgressBar(int progress) {
        return PEQControl.UserInputConstraint.GAIN_MIN + progress * PROGRESS_STEP;
    }

    private int covertToProgress(double d) {
        return (int) ((d - PEQControl.UserInputConstraint.GAIN_MIN) / PROGRESS_STEP);
    }

    private void configFreqsTextChangedListener() {
        for (int i = 0; i < mEditFreqs.length; i++) {
            final SeekBar seekBar = mSeekBars[i];
//            final EditText editBw = mEditQs[i];
            mEditFreqs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int v, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int v, int i1, int i2) {
//                    try {
//                        double val = Double.valueOf(charSequence.toString());
//                        editBw.setText(String.format("%1.2f", val / 2));
//                    } catch (NumberFormatException e) {
//                        // don't care
//                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }

    private int getGainProgressMax() {

        int max = PEQControl.UserInputConstraint.GAIN_MAX;//UserInputConstraint.getGainDbRangeMax(inputFreq);
        int min = PEQControl.UserInputConstraint.GAIN_MIN;//UserInputConstraint.getGainDbRangeMin(inputFreq);

        return (int) ((max - min) / PROGRESS_STEP);
    }

    private void configUiInputGroup() {
        mEditTextFreq0 = mView.findViewById(R.id.editTextFreq0);
        mEditTextFreq1 = mView.findViewById(R.id.editTextFreq1);
        mEditTextFreq2 = mView.findViewById(R.id.editTextFreq2);
        mEditTextFreq3 = mView.findViewById(R.id.editTextFreq3);
        mEditTextFreq4 = mView.findViewById(R.id.editTextFreq4);
        mEditTextFreq5 = mView.findViewById(R.id.editTextFreq5);
        mEditTextFreq6 = mView.findViewById(R.id.editTextFreq6);
        mEditTextFreq7 = mView.findViewById(R.id.editTextFreq7);
        mEditTextFreq8 = mView.findViewById(R.id.editTextFreq8);
        mEditTextFreq9 = mView.findViewById(R.id.editTextFreq9);

        mEditTextGain0 = mView.findViewById(R.id.editTextGain0);
        mEditTextGain1 = mView.findViewById(R.id.editTextGain1);
        mEditTextGain2 = mView.findViewById(R.id.editTextGain2);
        mEditTextGain3 = mView.findViewById(R.id.editTextGain3);
        mEditTextGain4 = mView.findViewById(R.id.editTextGain4);
        mEditTextGain5 = mView.findViewById(R.id.editTextGain5);
        mEditTextGain6 = mView.findViewById(R.id.editTextGain6);
        mEditTextGain7 = mView.findViewById(R.id.editTextGain7);
        mEditTextGain8 = mView.findViewById(R.id.editTextGain8);
        mEditTextGain9 = mView.findViewById(R.id.editTextGain9);

        mSeekBarGain0 = mView.findViewById(R.id.seekBar0);
        mSeekBarGain1 = mView.findViewById(R.id.seekBar1);
        mSeekBarGain2 = mView.findViewById(R.id.seekBar2);
        mSeekBarGain3 = mView.findViewById(R.id.seekBar3);
        mSeekBarGain4 = mView.findViewById(R.id.seekBar4);
        mSeekBarGain5 = mView.findViewById(R.id.seekBar5);
        mSeekBarGain6 = mView.findViewById(R.id.seekBar6);
        mSeekBarGain7 = mView.findViewById(R.id.seekBar7);
        mSeekBarGain8 = mView.findViewById(R.id.seekBar8);
        mSeekBarGain9 = mView.findViewById(R.id.seekBar9);

        mEditTextQ0 = mView.findViewById(R.id.editTextQ0);
        mEditTextQ1 = mView.findViewById(R.id.editTextQ1);
        mEditTextQ2 = mView.findViewById(R.id.editTextQ2);
        mEditTextQ3 = mView.findViewById(R.id.editTextQ3);
        mEditTextQ4 = mView.findViewById(R.id.editTextQ4);
        mEditTextQ5 = mView.findViewById(R.id.editTextQ5);
        mEditTextQ6 = mView.findViewById(R.id.editTextQ6);
        mEditTextQ7 = mView.findViewById(R.id.editTextQ7);
        mEditTextQ8 = mView.findViewById(R.id.editTextQ8);
        mEditTextQ9 = mView.findViewById(R.id.editTextQ9);

        mBtnBandTotal1 = mView.findViewById(R.id.btnBandTotal1);
        mBtnBandTotal2 = mView.findViewById(R.id.btnBandTotal2);
        mBtnBandTotal3 = mView.findViewById(R.id.btnBandTotal3);
        mBtnBandTotal4 = mView.findViewById(R.id.btnBandTotal4);
        mBtnBandTotal5 = mView.findViewById(R.id.btnBandTotal5);
        mBtnBandTotal6 = mView.findViewById(R.id.btnBandTotal6);
        mBtnBandTotal7 = mView.findViewById(R.id.btnBandTotal7);
        mBtnBandTotal8 = mView.findViewById(R.id.btnBandTotal8);
        mBtnBandTotal9 = mView.findViewById(R.id.btnBandTotal9);
        mBtnBandTotal10 = mView.findViewById(R.id.btnBandTotal10);

        mEditFreqs = new EditText[]{
                mEditTextFreq0, mEditTextFreq1, mEditTextFreq2, mEditTextFreq3, mEditTextFreq4,
                mEditTextFreq5, mEditTextFreq6, mEditTextFreq7, mEditTextFreq8, mEditTextFreq9
        };

        mSeekBars = new SeekBar[]{
                mSeekBarGain0, mSeekBarGain1, mSeekBarGain2, mSeekBarGain3, mSeekBarGain4,
                mSeekBarGain5, mSeekBarGain6, mSeekBarGain7, mSeekBarGain8, mSeekBarGain9
        };

        mEditGains = new EditText[]{
                mEditTextGain0, mEditTextGain1, mEditTextGain2, mEditTextGain3, mEditTextGain4,
                mEditTextGain5, mEditTextGain6, mEditTextGain7, mEditTextGain8, mEditTextGain9
        };

        mEditQs = new EditText[]{
                mEditTextQ0, mEditTextQ1, mEditTextQ2, mEditTextQ3, mEditTextQ4,
                mEditTextQ5, mEditTextQ6, mEditTextQ7, mEditTextQ8, mEditTextQ9
        };

        mBtnBandTotals = new Button[]{
                mBtnBandTotal1, mBtnBandTotal2, mBtnBandTotal3, mBtnBandTotal4, mBtnBandTotal5,
                mBtnBandTotal6, mBtnBandTotal7, mBtnBandTotal8, mBtnBandTotal9, mBtnBandTotal10,
        };

    }

    private void giveDefaultInputParam() {
        mEditTextFreq0.setText("32");
        mEditTextFreq1.setText("64");
        mEditTextFreq2.setText("125");
        mEditTextFreq3.setText("250");
        mEditTextFreq4.setText("500");
        mEditTextFreq5.setText("1000");
        mEditTextFreq6.setText("2000");
        mEditTextFreq7.setText("4000");
        mEditTextFreq8.setText("8000");
        mEditTextFreq9.setText("16000");

        for (int i = 0; i < mEditGains.length; i++) {
            mEditGains[i].setText("0.00");
        }
    }

    private int checkCustomCategoryId(int CategoryId) {
        int isFind = -1;
        for(int i = 0; i < mEqSettingList.size(); i++) {
            AirohaEQSettings setting = mEqSettingList.get(i);
            if(setting.getCategoryId() == CategoryId) {
                isFind = i + 1;
                break;
            }
        }
        return isFind;
    }

    private void initSpinnerGrpItem() {
        ArrayList groups = new ArrayList();
        groups.add("");

        for(AirohaEQSettings setting:mEqSettingList) {
            groups.add("EQ " + setting.getCategoryId());
        }

        String customEqSpinnerText[] = new String[4];
        for(int i=0; i<4; ++i) {
            if(checkCustomCategoryId(101+i) == -1)
                customEqSpinnerText[i] = "+EQ " + (101+i);
        }

        for(int i=0; i<4; ++i) {
            if(customEqSpinnerText[i] != null)
                groups.add(customEqSpinnerText[i]);
        }

        ArrayAdapter<String> groupsList = new ArrayAdapter<>(mActivity,
                android.R.layout.simple_list_item_checked,
                groups);
        mSpinnerPEQGrp.setAdapter(groupsList);
        mSpinnerPEQGrp.setSelection(mPeqSpinnerIndex, true);
    }

    private int getAirohaEQSettingIndex(int categoryId) {
        for(int i = 0; i < mEqSettingList.size(); i++) {
            AirohaEQSettings setting = mEqSettingList.get(i);
            if(setting.getCategoryId() == categoryId)
                return i;
        }
        return -1;
    }

    class BandButton_Click implements View.OnClickListener {
        private int mNum;

        BandButton_Click(int num) {
            mNum = num;
        }

        @Override
        public void onClick(View v) {
//            mBtnDisableEq.setTextColor(Color.BLACK);
            for (int i = 0; i < mBtnBandTotals.length; i++) {
                mBtnBandTotals[i].setTextColor(Color.BLACK);
            }

            ((Button) v).setTextColor(Color.RED);

            mBandTotals = mNum;
            for (int j = 0; j < mEditFreqs.length; j++) {
                mEditFreqs[j].setEnabled(false);
                mEditGains[j].setEnabled(false);
                mSeekBars[j].setEnabled(false);
                mEditQs[j].setEnabled(false);
            }

            for (int j = 0; j < mBandTotals; j++) {
                mEditFreqs[j].setEnabled(true);
                mEditGains[j].setEnabled(true);
                mSeekBars[j].setEnabled(true);
                mEditQs[j].setEnabled(true);
            }
        }
    }

    class GetAllEQSettingsListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            setBtnEnable(true);
                            if(mEqSettingList != null)
                                mEqSettingList.clear();
                            updateAllEq((AirohaEQStatusMsg) msg);
                        } else {
                            setBtnEnable(false);
                        }
                        mIsBusy = false;
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getAllEQSettings: " + code.getDescription());
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

    class GetRunningEQSettingListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String msgText = code.getDescription();
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            AirohaEQStatusMsg eqMsg = (AirohaEQStatusMsg) msg;
                            LinkedList<AirohaEQSettings> eqSettingsList = eqMsg.getMsgContent();
                            AirohaEQSettings eqSettings = eqSettingsList.get(0);
                            if (eqSettings != null) {
                                mRunningCategoryID = eqSettings.getCategoryId();
                            } else {
                                mRunningCategoryID = 0;
                            }
                            for (AirohaEQSettings setting : mEqSettingList) {
                                setting.setStatus(0);
                            }
                            int index = getAirohaEQSettingIndex(mRunningCategoryID);
                            if (index != -1)
                                mPeqSpinnerIndex = index + 1;
                            else
                                mPeqSpinnerIndex = 0;
                            if (index != -1) {
                                mEqSettingList.get(index).setEqPayload(eqSettingsList.get(0).getEqPayload());
                                mTmpEqSettingList.get(index).setEqPayload(eqSettingsList.get(0).getEqPayload());
                                mEqSettingList.get(index).setStatus(1);
                            }
                            updateRunningEqUI();
                        } else if (code == AirohaStatusCode.STATUS_CANCEL) {
                            mBtnUpdateRealTimePEQ.setEnabled(true);
                            mBtnSavePeqCoef.setEnabled(true);
                            mBtnReplacePEQ.setEnabled(true);
                            mSpinnerPEQGrp.setEnabled(true);
                            mBtnPEQGrpIdx.setEnabled(true);
                            mBtnSetPEQGrpIdx.setEnabled(true);
                            msgText += ". Partner is not found";
                        }
                        mIsBusy = false;
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getRunningEQSetting: " + msgText);
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

    class SetEQSettingListener implements AirohaDeviceListener {
        @Override
        public void onRead(AirohaStatusCode code, AirohaBaseMsg msg) {
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String msgText = code.getDescription();
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            AirohaEQStatusMsg eqMsg = (AirohaEQStatusMsg) msg;
                            LinkedList<AirohaEQSettings> eqSettingsList = eqMsg.getMsgContent();
                            AirohaEQSettings eqSettings = eqSettingsList.get(0);
                            if (mIsSaving || mIsSetEQIndex) {
                                mRealtimeTmpEqSettings = null;
                            }
                            else {
                                if(eqSettings.getEqPayload() != null)
                                    eqSettings.getEqPayload().setIndex(mPeqSpinnerIndex);
                                mRealtimeTmpEqSettings = eqSettings;
                            }
                            if (eqSettings != null) {
                                mRunningCategoryID = eqSettings.getCategoryId();
                            } else {
                                mRunningCategoryID = 0;
                            }

                            int tmpSpinnerIndex = 1;
                            for (AirohaEQSettings settings : mEqSettingList) {
                                if (settings.getCategoryId() == mRunningCategoryID) {
                                    mPeqSpinnerIndex = tmpSpinnerIndex;
                                }
                                tmpSpinnerIndex++;
                            }

                            if (mIsSaving) {
                                boolean isNewEqSettings = true;
                                for (AirohaEQSettings settings : mEqSettingList) {
                                    if (settings.getCategoryId() == mRunningCategoryID) {
                                        isNewEqSettings = false;
                                        break;
                                    }
                                }

                                if (isNewEqSettings) {
                                    mEqSettingList.add(eqSettings);
                                    ++mCustomEqCount;
                                }

                                mIsSaving = false;
                                mIsSetEQIndex = false;
                            }
                            updateRunningEqUI();
                        } else if (code == AirohaStatusCode.STATUS_CANCEL) {
                            mBtnUpdateRealTimePEQ.setEnabled(true);
                            mBtnSavePeqCoef.setEnabled(true);
                            mBtnReplacePEQ.setEnabled(true);
                            mSpinnerPEQGrp.setEnabled(true);
                            mBtnPEQGrpIdx.setEnabled(true);
                            mBtnSetPEQGrpIdx.setEnabled(true);
                            msgText += ". Partner is not found";
                        }
                        mIsBusy = false;
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setEQSetting: " + msgText);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class ResetEQSettingListener implements AirohaDeviceListener {
        @Override
        public void onRead(AirohaStatusCode code, AirohaBaseMsg msg) {
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            mEqSettingList.clear();
                            AirohaSDK.getInst().getAirohaEQControl().getAllEQSettings(new GetAllEQSettingsListener());
                        }

                        mIsBusy = false;
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "resetEQSetting: " + code.getDescription());
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
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
                        }
                        mIsBusy = false;
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getTwsConnectStatus: " + code.getDescription());
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

    class ReplaceEQSettingListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String msgText = code.getDescription();
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            mEqSettingList.clear();
                            //updateAllEq((AirohaEQStatusMsg) msg);
                            AirohaSDK.getInst().getAirohaEQControl().getAllEQSettings(new GetAllEQSettingsListener());
                        } else if (code == AirohaStatusCode.STATUS_CANCEL) {
                            mBtnUpdateRealTimePEQ.setEnabled(true);
                            mBtnSavePeqCoef.setEnabled(true);
                            mBtnReplacePEQ.setEnabled(true);
                            mSpinnerPEQGrp.setEnabled(true);
                            mBtnPEQGrpIdx.setEnabled(true);
                            mBtnSetPEQGrpIdx.setEnabled(true);
                            mIsBusy = false;
                            msgText += ". Partner is not found";
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "replaceEQSetting: " + msgText);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }
}
