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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.airoha.liblogdump.AirohaDumpMgr;

import java.util.ArrayList;

public class OnlinelogConfigItemView extends LinearLayout {

    public Spinner mSpinLogLevel;
    public Switch mSwitchLogOnOff;

    private String mTitle;
    private byte mCpu;
    private boolean mIsLogOn;
    private byte mLogLevel;
    private int mModuleId;

    private AirohaDumpMgr mMgr;

    //0: on
    //1: off
    private byte ON = 0;
    private byte OFF = 1;
    private byte OnOff = 0;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.switch_log_onoff:
                    if (((Switch) v).isChecked()) {
                        mSpinLogLevel.setEnabled(true);
                        //0: on
                        //1: off
                        mMgr.setModuleInfo(mCpu, mModuleId, ON, mLogLevel);
                        OnOff = ON;
                    } else {
                        mSpinLogLevel.setEnabled(false);
                        mMgr.setModuleInfo(mCpu, mModuleId, OFF, mLogLevel);
                        OnOff = OFF;
                    }

                    //savePreferences(mTitle + "_level", mLogLevel);
                    //savePreferences(mTitle + "_onoff", OnOff);

                    break;
            }
        }
    };

    private AdapterView.OnItemSelectedListener mOnItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mLogLevel = (byte) mSpinLogLevel.getSelectedItemPosition();
            mMgr.setModuleInfo(mCpu, mModuleId, ON, mLogLevel);
            //savePreferences(mTitle + "_level", mLogLevel);
            //savePreferences(mTitle + "_onoff", ON);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mLogLevel = 0;
            mSpinLogLevel.setSelection(mLogLevel,true);
        }
    };

    public OnlinelogConfigItemView(Context context, AttributeSet attrs, byte cpu, String title, int module_id, byte isLogOn, byte level) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_onlinelog_module_item, this);

        mTitle = title.toUpperCase();

        /*int defaultOnOff = getPreferences(mTitle + "_onoff");
        if(defaultOnOff != 99)
            isLogOn = (byte)defaultOnOff;
        int defaultLevel = getPreferences(mTitle + "_level");
        if(defaultLevel != 99)
            level = (byte)defaultLevel;*/

        mCpu = cpu;
        mIsLogOn = (isLogOn == 0)? true: false;
        mLogLevel = level;
        mModuleId = module_id;

        initUImember();
    }

    private void initUImember() {
        mSpinLogLevel = findViewById(R.id.spin_log_level);
        mSwitchLogOnOff = findViewById(R.id.switch_log_onoff);

        ArrayList logLevelList = new ArrayList();
        logLevelList.add("DEBUG");
        logLevelList.add("INFO");
        logLevelList.add("WARNING");
        logLevelList.add("ERROR");

        ArrayAdapter<String> logLevelAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item,
                logLevelList);
        mSpinLogLevel.setAdapter(logLevelAdapter);
        mSpinLogLevel.setSelection(mLogLevel,true);

        mSwitchLogOnOff.setText(mModuleId + " : " + mTitle);
        mSwitchLogOnOff.setChecked(mIsLogOn);
        mSpinLogLevel.setEnabled(mIsLogOn);
    }

    public void initLogModule() {
        if (mTitle.compareToIgnoreCase("MPLOG") == 0) {
            mIsLogOn = true;
        }
        if(mIsLogOn) {
            mSwitchLogOnOff.setChecked(false);
            mSpinLogLevel.setEnabled(mIsLogOn);
            mSwitchLogOnOff.performClick();
            mSpinLogLevel.setSelection(mLogLevel,true);
            mMgr.setModuleInfo(mCpu, mModuleId, ON, mLogLevel);
        }

        mSwitchLogOnOff.setOnClickListener(mOnClickListener);
        mSpinLogLevel.setOnItemSelectedListener(mOnItemClickListener);
    }

    public void clearListener() {
        mSwitchLogOnOff.setOnClickListener(null);
        mSpinLogLevel.setOnItemSelectedListener(null);
    }

    public void setTitle(String title) {
        mSwitchLogOnOff.setText(title);
    }

    public void setMgr(AirohaDumpMgr mgr) {
        mMgr = mgr;
    }

    /*private void savePreferences(String key, int value) {
        SharedPreferences pref = getContext().getSharedPreferences("onlinelog_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
        editor.apply();
    }*/

    /*private int getPreferences(String key) {
        SharedPreferences pref = getContext().getSharedPreferences("onlinelog_config", Context.MODE_PRIVATE);
        return pref.getInt(key, 99);
    }*/

}
