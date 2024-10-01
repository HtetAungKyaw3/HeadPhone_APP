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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airoha.liblogdump.AirohaDumpListener;
import com.airoha.liblogdump.offlinedump.DumpTypeEnum;
import com.airoha.liblogdump.onlinedump.LoggerList;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.utils.ChipType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogConfigTabFragment extends BaseFragment {
    private String TAG = LogConfigTabFragment.class.getSimpleName();
    private MainActivity mActivity;
    private LogConfigTabFragment mFragment;
    private View mView;
    private Handler mHandler;

    //layout
    private ViewPager mViewPager;
    private List<LinearLayout> pageList;
    private ImageView[] tips = new ImageView[2];
    private ImageView imageView;
    private LinearLayout mViewGroup;

    public LogConfigTabFragment(){
        mTitle = "LOG CONFIG";
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
        mView = inflater.inflate(R.layout.fragment_onling_log_tab, container,false);

        initUImember();
        initPage();
        initView();

        initLogModule();
        initCpuFilter();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        gLogger.d(TAG, "onResume");

        if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.LOG_CONFIG)
            return;

        mHandler = new Handler();
        if(mActivity.getAirohaService() != null) {
            mActivity.getAirohaService().getAirohaLinker().addHostListener(gTargetAddr, TAG, mFragment);
            mActivity.getAirohaService().getAirohaLogDumpMgr().addListener(TAG, mAirohaDumpListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        gLogger.d(TAG, "onPause");

        if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.LOG_CONFIG)
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
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
        super.onHiddenChanged(hidden);
        if (mActivity.getAirohaService() == null) {
            return;
        }
        if (hidden) {
            mActivity.getAirohaService().getAirohaLogDumpMgr().saveCpuFilter();
            mActivity.getAirohaService().getAirohaLinker().removeHostListener(gTargetAddr, TAG);
            mActivity.getAirohaService().getAirohaLogDumpMgr().removeListener(TAG);
        } else {
            mActivity.getAirohaService().getAirohaLinker().addHostListener(gTargetAddr, TAG, mFragment);
            mActivity.getAirohaService().getAirohaLogDumpMgr().addListener(TAG, mAirohaDumpListener);
        }
    }

    @Override
    public void onHostDisconnected() {
        disableUI();
    }

    @Override
    public void onHostInitialized() {
        initLogModule();
    }

    private AirohaDumpListener mAirohaDumpListener = new AirohaDumpListener() {

        @Override
        public void OnUpdateLog(String log) {
        }

        @Override
        public void OnUpdateLogCount() {
        }

        @Override
        public void OnUpdateModuleInfo(byte[] info, boolean isEnd) {
            if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.LOG_CONFIG)
                return;
            updateModuleInfo(info, isEnd);
        }

        @Override
        public void OnRespSuccess(final String stageName) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "OnRespSuccess " + stageName);
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
        public void OnUpdateCpuFilterInfo(final byte cpuId, final byte onOff, final byte level) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((LogConfigView)pageList.get(0)).updateCpuFilter(cpuId, onOff, level);
                }
            });
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

    private void updateModuleInfo(byte[] info, boolean isEnd) {
        // endflag  
        // 0x0F: Not the last response segment
        // 0xF0: the last response segment

        Byte[] byteObject = new Byte[info.length];
        int i = 0;
        for(byte b: info)
            byteObject[i++] = b;

        final LoggerList<Byte> moduleRaw = new LoggerList<Byte>();
        Collections.addAll(moduleRaw, byteObject);

        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                initConfigureView(moduleRaw);
            }
        });

        if(!isEnd) {
            mActivity.getAirohaService().getAirohaLogDumpMgr().queryModuleInfoContinue();
        } else {
            // notify initial set log module
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ((CM4LogConfigureView)pageList.get(1)).notifyInitialLogModule();
                    if(AirohaSDK.getInst().mChipType == ChipType.AB157x)
                        ((DSP0LogConfigureView)pageList.get(2)).notifyInitialLogModule();
                }
            });
        }

    }

    private void disableUI() {
        gLogger.d(TAG, "disableUI");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((CM4LogConfigureView)pageList.get(1)).notifyDisconnected();
                if(AirohaSDK.getInst().mChipType == ChipType.AB157x)
                    ((DSP0LogConfigureView)pageList.get(2)).notifyDisconnected();
            }
        });
    }

    private void initLogModule() {
        gLogger.d(TAG, "initLogModule");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ((CM4LogConfigureView)pageList.get(1)).clearLayout();
                mActivity.getAirohaService().getAirohaLogDumpMgr().queryModuleInfo();
            }
        });
    }

    private void initCpuFilter() {
        gLogger.d(TAG, "initCpuFilter");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.getAirohaService().getAirohaLogDumpMgr().queryCpuFilterInfo((byte)0x00);
                if(AirohaSDK.getInst().mChipType == ChipType.AB157x)
                    mActivity.getAirohaService().getAirohaLogDumpMgr().queryCpuFilterInfo((byte)0x01);
            }
        });
    }

    private void initPage() {
        gLogger.d(TAG, "initData");
        pageList = new ArrayList<>();
        pageList.add(new LogConfigView(mActivity));
        pageList.add(new CM4LogConfigureView(mActivity));
        if(AirohaSDK.getInst().mChipType == ChipType.AB157x)
            pageList.add(new DSP0LogConfigureView(mActivity));
    }

    private void initView() {
        gLogger.d(TAG, "initView");

        tips = new ImageView[pageList.size()];
        for(int i = 0; i < pageList.size(); i++){
            imageView = new ImageView(mActivity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 40);
            params.setMargins(10, 0, 10, 40);
            imageView.setLayoutParams(params);
            //imageView.setPadding(40, 0, 40, 0);
            tips[i] = imageView;

            //預設第一張圖顯示為選中狀態
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            mViewGroup.addView(tips[i]);
        }

        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.addOnPageChangeListener(new GuidePageChangeListener());
    }

    private void initConfigureView(LoggerList<Byte> data) {
        ((CM4LogConfigureView)pageList.get(1)).initLogModuleMember(data);
    }

    private void initUImember() {
        gLogger.d(TAG, "initUImember");

        mViewGroup = mView.findViewById(R.id.viewGroup);
    }

    private class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pageList.get(position));
            return pageList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    class GuidePageChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            gLogger.d(TAG, "");
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            gLogger.d(TAG, "");
        }

        @Override
        //切換view時，下方圓點的變化。
        public void onPageSelected(int position) {
            tips[position].setBackgroundResource(R.drawable.page_indicator_focused);
            //這個圖片就是選中的view的圓點
            for(int i  =0; i < pageList.size(); i++){
                if (position != i) {
                    tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                    //這個圖片是未選中view的圓點
                }
            }
        }
    }
}
