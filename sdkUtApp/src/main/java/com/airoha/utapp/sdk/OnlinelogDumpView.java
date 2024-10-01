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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnlinelogDumpView extends LinearLayout {

    private static final String TAG = "OnlinelogDumpView";

    private Button mBtnGetBuildInfo;
    //private ListView mTxtDumpLog;
    private TextView mTxtDumpLog;
    private TextView mTxtLogCount;
    private Switch mSwitchLogStart;

    private ArrayList<HashMap<String, Object>> mLogArrayList;
    private SimpleAdapter mLogAdapter;
    private int mLogCount;
    private List<String> logList = new ArrayList<String>();

    private Button mButtonShare;
    private TextView mTextViewLogPath;

    private MainActivity mCtx;

    public OnlinelogDumpView(Context context) {
        super(context);

        mCtx = (MainActivity)context;

        LayoutInflater.from(context).inflate(R.layout.fragment_onlinelog_dump, this);

        mLogArrayList = new ArrayList<>();
        initUImember();
    }

    private void initUImember() {
        Log.d(TAG, "initUImember");
        mTxtDumpLog = findViewById(R.id.txtOnlineDumpLog);
        mBtnGetBuildInfo = findViewById(R.id.buttonGetBuildInfo);
        mTxtLogCount = findViewById(R.id.txtLogCount);
        mSwitchLogStart = findViewById(R.id.switch_log_start);

        mTxtDumpLog.setMovementMethod(new ScrollingMovementMethod());

        mBtnGetBuildInfo.setEnabled(false);
        mSwitchLogStart.setEnabled(false);

        mButtonShare = findViewById(R.id.buttonShare);
        mTextViewLogPath = findViewById(R.id.textViewLogPath);
    }

    public void setClickListener(OnClickListener listener) {
        mSwitchLogStart.setOnClickListener(listener);
        mButtonShare.setOnClickListener(listener);
        mBtnGetBuildInfo.setOnClickListener(listener);
    }

    public void setUITextureLog(String log) {
        /*HashMap<String, Object> item = new HashMap();
        item.put("log", log);
        mLogArrayList.add(0, item);
        int logCount = mLogArrayList.size();
        if (logCount > 500) {
            mLogArrayList.remove(logCount - 1);
        }
        mLogAdapter.notifyDataSetChanged();*/

        mTxtDumpLog.append(log + "\n");
        mLogCount++;
        if(mLogCount > 200) {
            mTxtDumpLog.setText("");
            mLogCount = 0;
            mTxtDumpLog.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    public void updateLogCount(long count) {
        Log.d(TAG, "updateLogCount: " + count);
        mTxtLogCount.setText("Log Package Count: " + count);
    }

    public void clearLog() { mTxtDumpLog.setText("");}

    public void notifyConnected() {
        mBtnGetBuildInfo.setEnabled(true);
        mLogArrayList.clear();
    }

    public void notifyDisconnected() {
        mBtnGetBuildInfo.setEnabled(false);
        mSwitchLogStart.setEnabled(false);
        mSwitchLogStart.setChecked(false);
    }

    public void enableStartButton(boolean enable) {
        mSwitchLogStart.setEnabled(enable);
        mButtonShare.setEnabled(enable);
    }

    public boolean isStartLogging() { return mSwitchLogStart.isChecked();}

    public void setLogPath(String path) {
        mTextViewLogPath.setText(path);
    }
}
