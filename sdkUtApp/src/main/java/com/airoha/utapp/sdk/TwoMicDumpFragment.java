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
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.liblogdump.AirohaDumpListener;
import com.airoha.liblogdump.offlinedump.DumpTypeEnum;

public class TwoMicDumpFragment extends BaseFragment {
    private String TAG = TwoMicDumpFragment.class.getSimpleName();
    private MainActivity mActivity;
    private TwoMicDumpFragment mFragment;
    private View mView;
    private Handler mHandler;

    private Button mBtnStartAirDump;
    private Button mBtnStopAirDump;
    private TextView mTxtDumpLog;
    private int LOG_MAXLENGTH = 0;

    public TwoMicDumpFragment(){
        mTitle = "TWO MIC DUMP";
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
        mView = inflater.inflate(R.layout.fragment_twomicdump, container,false);
        initUImember();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler = new Handler();

        if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.TWO_MIC_DUMP)
            return;

        if(mActivity.getAirohaService() != null) {
            mActivity.getAirohaService().getAirohaLinker().addHostListener(gTargetAddr, TAG, mFragment);
            mActivity.getAirohaService().getAirohaLogDumpMgr().addListener(TAG, mAirohaDumpListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.TWO_MIC_DUMP)
            return;

        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mActivity.getAirohaService() != null) {
            mActivity.getAirohaService().getAirohaLinker().removeHostListener(gTargetAddr, TAG);
            mActivity.getAirohaService().getAirohaLogDumpMgr().removeListener(TAG);
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
    public void onHostDisconnected() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBtnStartAirDump.setEnabled(false);
                mBtnStopAirDump.setEnabled(false);
            }
        });
    }

    @Override
    public void onHostInitialized() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBtnStartAirDump.setEnabled(true);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
        super.onHiddenChanged(hidden);
        if (mActivity.getAirohaService() == null) {
            return;
        }
        if (hidden) {
            stopTwoMicDump();
            mActivity.getAirohaService().getAirohaLinker().removeHostListener(gTargetAddr, TAG);
            mActivity.getAirohaService().getAirohaLogDumpMgr().removeListener(TAG);
            mTxtDumpLog.setText("");
        } else {
            mActivity.getAirohaService().getAirohaLinker().addHostListener(gTargetAddr, TAG, mFragment);
            mActivity.getAirohaService().getAirohaLogDumpMgr().addListener(TAG, mAirohaDumpListener);
            mBtnStartAirDump.setEnabled(true);
        }
    }

    private void stopTwoMicDump() {
        if(mBtnStopAirDump.isEnabled()) {
            mActivity.getAirohaService().getAirohaLogDumpMgr().stopAirDump();
            mBtnStartAirDump.setEnabled(true);
            mBtnStopAirDump.setEnabled(false);
        }
    }

    private void initUImember() {
        gLogger.d(TAG, "initUImember");

        mBtnStartAirDump = mView.findViewById(R.id.buttonStartAirDump);
        mBtnStopAirDump = mView.findViewById(R.id.buttonStopAirDump);
        mTxtDumpLog = mView.findViewById(R.id.txt2MicDumpLog);

        mTxtDumpLog.setMovementMethod(new ScrollingMovementMethod());

        mBtnStartAirDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getAirohaService().getAirohaLogDumpMgr().startAirDump();
                mTxtDumpLog.setText("");
                mBtnStartAirDump.setEnabled(false);
                mBtnStopAirDump.setEnabled(true);
            }
        });

        mBtnStopAirDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTwoMicDump();
            }
        });

        setUiStatus();
    }

    private void setUiStatus() {
        //mBtnStartAirDump.setEnabled(false);
        mBtnStopAirDump.setEnabled(false);
    }

    private AirohaDumpListener mAirohaDumpListener = new AirohaDumpListener() {

        @Override
        public void OnUpdateLog(final String log) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(LOG_MAXLENGTH > 200)
                    {
                        mTxtDumpLog.setText("");
                        LOG_MAXLENGTH = 0;
                    }
                    mTxtDumpLog.append(log + "\n");
                    LOG_MAXLENGTH++;
                }
            });
        }

        @Override
        public void OnUpdateLogCount() {

        }

        @Override
        public void OnUpdateModuleInfo(byte[] info, boolean isEnd) {

        }

        @Override
        public void OnRespSuccess(final String stageName) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL,"OnRespSuccess " + stageName);
                }
            });
        }

        @Override
        public void OnUpdateDumpAddrLength(int addr, int length, DumpTypeEnum logType, int region) {

        }

        @Override
        public void OnResponseTimeout(final String stageName) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "OnResponseTimeout " + stageName);
                }
            });
        }

        @Override
        public void OnNotifyError(String stageName, String errorMsg) {

        }

        @Override
        public void OnUpdateCpuFilterInfo(byte cpuId, byte onOff, byte level) {

        }

        @Override
        public void OnUpdateBootReason(String log) {

        }

        @Override
        public void OnUpdateOfflineLogRegion(int min, int max, DumpTypeEnum type) {

        }

        @Override
        public void OnUpdateOfflineLogStatus(byte onOff) {

        }

        @Override
        public void OnUpdateExceptionStatus(byte onOff) {

        }

        @Override
        public void onStopped(final String stageName) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "No rsp for " + stageName);
                }
            });
        }
    };
}
