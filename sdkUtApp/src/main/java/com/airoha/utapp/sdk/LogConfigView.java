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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.airoha.liblogdump.AirohaDumpMgr;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.utils.ChipType;

import java.util.ArrayList;

public class LogConfigView extends LinearLayout {

    private static final String TAG = "LogConfigView";

    private MainActivity mCtx;
    private AirohaDumpMgr mDumpMgr;

    public LinearLayout mCpu1LogLevelLayout;
    public Spinner mSpinCpu0Level;
    public Switch mSwitchCpu0OnOff;
    public Spinner mSpinCpu1Level;
    public Switch mSwitchCpu1OnOff;

    private Button btnSaveCpuSetting;

    private byte mCpu0Level;
    private byte mCpu1Level;
    private byte mCpu0OnOff;
    private byte mCpu1OnOff;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.switch_cpu0_onoff:
                    if (((Switch) v).isChecked()) {
                        mSpinCpu0Level.setEnabled(true);
                        mCpu0OnOff = (byte)0;
                    } else {
                        mSpinCpu0Level.setEnabled(false);
                        mCpu0OnOff = (byte)1;
                    }
                    mDumpMgr.setCpuFilterInfo((byte)0, mCpu0OnOff, mCpu0Level);
                    break;
                case R.id.switch_cpu1_onoff:
                    if (((Switch) v).isChecked()) {
                        mSpinCpu1Level.setEnabled(true);
                        mCpu1OnOff = (byte)0;
                    } else {
                        mSpinCpu1Level.setEnabled(false);
                        mCpu1OnOff = (byte)1;
                    }
                    mDumpMgr.setCpuFilterInfo((byte)1, mCpu1OnOff, mCpu1Level);
                    break;
            }
        }
    };

    private AdapterView.OnItemSelectedListener mOnItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            switch (parent.getId()) {
                case R.id.spin_cpu0_level:
                    mCpu0Level = (byte) mSpinCpu0Level.getSelectedItemPosition();
                    mDumpMgr.setCpuFilterInfo((byte)0, mCpu0OnOff, mCpu0Level);
                    break;
                case R.id.spin_cpu1_level:
                    mCpu1Level = (byte) mSpinCpu1Level.getSelectedItemPosition();
                    mDumpMgr.setCpuFilterInfo((byte)1, mCpu1OnOff, mCpu1Level);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            /*mLogLevel = 0;
            mSpinLogLevel.setSelection(mLogLevel,true);*/
        }
    };

    public LogConfigView(Context context) {
        super(context);

        mCtx = (MainActivity)context;
        LayoutInflater.from(context).inflate(R.layout.fragment_log_config, this);
        initUImember();

        mDumpMgr = mCtx.getAirohaService().getAirohaLogDumpMgr();
    }

    private void initUImember() {
        Log.d(TAG, "initUImember");

        mCpu1LogLevelLayout = findViewById(R.id.cpu1_log_level_layout);
        if(AirohaSDK.getInst().mChipType == ChipType.AB157x)
        {
            mCpu1LogLevelLayout.setVisibility(VISIBLE);
        }

        mSpinCpu0Level = findViewById(R.id.spin_cpu0_level);
        mSwitchCpu0OnOff = findViewById(R.id.switch_cpu0_onoff);
        mSpinCpu1Level = findViewById(R.id.spin_cpu1_level);
        mSwitchCpu1OnOff = findViewById(R.id.switch_cpu1_onoff);

        btnSaveCpuSetting = findViewById(R.id.btnSaveCpuSetting);
        btnSaveCpuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDumpMgr.saveCpuFilter();
            }
        });

        mSwitchCpu0OnOff.setText("CPU 0");
        mSwitchCpu1OnOff.setText("CPU 1");

        ArrayList cpuLevelList = new ArrayList();
        cpuLevelList.add("DEBUG");
        cpuLevelList.add("INFO");
        cpuLevelList.add("WARNING");
        cpuLevelList.add("ERROR");

        ArrayAdapter<String> cpuLevelAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,
                cpuLevelList);
        mSpinCpu0Level.setAdapter(cpuLevelAdapter);
        mSpinCpu1Level.setAdapter(cpuLevelAdapter);

        mSwitchCpu0OnOff.setOnClickListener(mOnClickListener);
        mSwitchCpu1OnOff.setOnClickListener(mOnClickListener);
    }

    public void updateCpuFilter(byte cpuId, byte onOff, byte level) {
        boolean isOn = (onOff == 0) ? true : false;
        if(cpuId == CPU_ID.CM4) {
            mSpinCpu0Level.setSelection(level, true);
            mSwitchCpu0OnOff.setChecked(isOn);
            mCpu0Level = level;
            mCpu0OnOff = onOff;
        } else if(cpuId == CPU_ID.DSP) {
            mSpinCpu1Level.setSelection(level, true);
            mSwitchCpu1OnOff.setChecked(isOn);
            mCpu1Level = level;
            mCpu1OnOff = onOff;
        }
        mSpinCpu0Level.setOnItemSelectedListener(mOnItemClickListener);
        mSpinCpu1Level.setOnItemSelectedListener(mOnItemClickListener);
    }

    private class CPU_ID {
        public static final int CM4 = 0;
        public static final int DSP = 1;
    }

}
