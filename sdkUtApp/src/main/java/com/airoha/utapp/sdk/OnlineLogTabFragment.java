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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airoha.libbase.RaceCommand.constant.RaceType;
import com.airoha.liblogdump.AirohaDumpListener;
import com.airoha.liblogdump.offlinedump.DumpTypeEnum;
import com.airoha.liblogdump.onlinedump.LoggerList;
import com.airoha.libutils.Converter;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaCmdSettings;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.sdk.api.utils.ChipType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class OnlineLogTabFragment extends BaseFragment {
    private String TAG = OnlineLogTabFragment.class.getSimpleName();
    private OnlineLogTabFragment mFragment;
    private View mView;
    private Handler mHandler;

    //layout
    private ViewPager mViewPager;
    private List<LinearLayout> pageList;

    private long mTotalLogCount = 0;

    private String mTimeStamp;
    private String mFolderPath;

    public OnlineLogTabFragment(){
        mTitle = "ONLINE LOG";
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
        initOnlineLog();

        mActivity.getAirohaService().showNotification("Online Log Running");

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

        if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.ONLINE_DUMP)
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

        if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.ONLINE_DUMP)
            return;

        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mActivity.getAirohaService() != null) {
            //mActivity.getAirohaService().getAirohaLinker().removeHostListener(mTargetAddr, TAG);
            //mActivity.getBluetoothService().getAirohaLogDumpMgr().removeListener(TAG);
        }
    }

    @Override
    public void onDestroy() {
        gLogger.d(TAG, "onDestroy");
        if (gFilePrinter != null) {
            gLogger.removePrinter(gFilePrinter.getPrinterName());
            //mActivity.getAirohaService().getAirohaLogDumpMgr().stopOnlineLogger();
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
            //resetOnlineLog();
            //disableUI();
            //mActivity.getBluetoothService().getAirohaLinker().removeHostListener(mTargetAddr, TAG);
            //mActivity.getBluetoothService().getAirohaLogDumpMgr().removeListener(TAG);
            //mActivity.getBluetoothService().showNotification("");
        } else {
            mActivity.getAirohaService().getAirohaLinker().addHostListener(gTargetAddr, TAG, mFragment);
            mActivity.getAirohaService().getAirohaLogDumpMgr().addListener(TAG, mAirohaDumpListener);
            //initOnlineLog();
            mActivity.getAirohaService().showNotification("Online Log Running");
        }
    }

    @Override
    public void onHostDisconnected() {
        disableUI();
    }

    @Override
    public void onHostInitialized() {
        initOnlineLog();
    }

    private AirohaDumpListener mAirohaDumpListener = new AirohaDumpListener() {

        @Override
        public void OnUpdateLog(String log) {
            updateLog(log);
        }

        @Override
        public void OnUpdateLogCount() {
            updateLogCount();
        }

        @Override
        public void OnUpdateModuleInfo(byte[] info, boolean isEnd) {
            if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.ONLINE_DUMP)
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
                initLogModule(moduleRaw);
            }
        });

        if(!isEnd) {
            mActivity.getAirohaService().getAirohaLogDumpMgr().queryModuleInfoContinue();
        }

        if(isEnd) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.getAirohaService().getAirohaLogDumpMgr().setModuleInfo(mCpuId, mMpModuleId, (byte)0, mMpLevel);
                }
            });
        }
    }

    private byte mCpuId = 0;
    private byte mMpModuleId = 0;
    private byte mMpLevel = 0;
    private void initLogModule(LoggerList<Byte> info) {
        // [cpu id:1b] [module id:1b] [module name leng:1b] [name....] [switch:1b] [level:1b]

        int index = 0;
        while(info.size() > 0) {
            if(info.size() < 3) {
                break;
            }
            final byte cpuid = info.get(0);
            final byte moduleid = info.get(1);
            byte module_name_length = info.get(2);

            if((info.size() - 3) < module_name_length) {
                break;
            }
            info.removeRange(0, 3);

            byte[] data = info.subArray(0, module_name_length, info);
            String module_name = Converter.hexToAsciiString(data);
            info.removeRange(0, module_name_length);

            if(info.size() < 1) {
                break;
            }

            byte isSwitch = (byte)((info.get(0) & (byte)0xF0) >> 4);
            final byte level = (byte)(info.get(0) & (byte)0x0F);
            info.removeRange(0, 1);

            if(module_name.compareToIgnoreCase("MPLOG") == 0) {
                // turn on MP log
                mCpuId = cpuid;
                mMpModuleId = moduleid;
                mMpLevel = level;
                /*mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mActivity.getAirohaService().getAirohaLogDumpMgr().setModuleInfo(cpuid, moduleid, (byte)0, level);
                        //mActivity.getAirohaService().getAirohaLogDumpMgr().setModuleInfo(cpuid, moduleid, (byte)0, level);
                    }
                });*/
            }
        }

    }

    private View viewSwitch;
    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewSwitch = v;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (viewSwitch.getId()) {
                        case R.id.switch_log_start:
                            if(((Switch)viewSwitch).isChecked()) {
                                setOnlineLog();
                            } else {
                                resetOnlineLog();
                            }
                            break;
                        case R.id.buttonGetBuildInfo:
                            mTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                            mFolderPath = DIRECTORY_DOCUMENTS + File.separator + "Airoha" + File.separator +
                                    "Dump" + File.separator + "OnlineLog_" + mTimeStamp;
                            ((OnlinelogDumpView)pageList.get(0)).setLogPath(mFolderPath);
                            ((OnlinelogDumpView)pageList.get(0)).enableStartButton(true);
                            mActivity.getAirohaService().getAirohaLogDumpMgr().getBuildInfo("OnlineLog_" + mTimeStamp);
                            break;
                        case R.id.buttonShare:
                            byte[] data = null;
                            if(AirohaSDK.getInst().mChipType == ChipType.AB1562
                                    || AirohaSDK.getInst().mChipType == ChipType.AB1562E) {
                                data = Converter.hexStringToByteArray("055A06000C0A0036E803");
                            } else {
                                data = Converter.hexStringToByteArray("055A0300D50C00");
                            }
                            AirohaCmdSettings setting = new AirohaCmdSettings();
                            setting.setRespType(RaceType.RESPONSE);
                            setting.setCommand(data);
                            AirohaSDK.getInst().getAirohaDeviceControl().sendCustomCommand(setting, new CustomCmdListener());
                            break;
                    }
                }
            });
        }
    };

    private void updateLogCount() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTotalLogCount++;
                ((OnlinelogDumpView)pageList.get(0)).updateLogCount(mTotalLogCount);
                if(mTotalLogCount % 50 == 0) {
                    mActivity.getAirohaService().showNotification("Log Package Count: " + mTotalLogCount);
                }
            }
        });
    }

    private void updateLog(final String log) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((OnlinelogDumpView)pageList.get(0)).setUITextureLog(log);
            }
        });
    }

    private void disableUI() {
        gLogger.d(TAG, "disableUI");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((OnlinelogDumpView)pageList.get(0)).notifyDisconnected();
            }
        });
    }

    private void initOnlineLog() {
        gLogger.d(TAG, "initOnlineLog");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.getAirohaService().getAirohaLogDumpMgr().stopOnlineDump();

                ((OnlinelogDumpView)pageList.get(0)).notifyConnected();

                mTotalLogCount = 0;
                ((OnlinelogDumpView)pageList.get(0)).updateLogCount(mTotalLogCount);
                ((OnlinelogDumpView)pageList.get(0)).setClickListener(mOnClickListener);

                HashMap<MainActivity.FragmentIndex, BaseFragment> fragmentMap = mActivity.getAirohaService().getFragmentMap();
                if(!fragmentMap.containsKey(MainActivity.FragmentIndex.LOG_CONFIG))
                {
                    mActivity.getAirohaService().getAirohaLogDumpMgr().queryModuleInfo();
                }
            }
        });
    }

    private void resetOnlineLog() {
        gLogger.d(TAG, "resetOnlineLog");
        mTotalLogCount = 0;
        ((OnlinelogDumpView)pageList.get(0)).updateLogCount(mTotalLogCount);
        ((OnlinelogDumpView)pageList.get(0)).clearLog();
        mActivity.getAirohaService().getAirohaLogDumpMgr().stopOnlineDump();
        mActivity.getAirohaService().getAirohaLogDumpMgr().stopOnlineLogger();
    }

    private void setOnlineLog() {
        gLogger.d(TAG, "setOnlineLog");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        mActivity.getAirohaService().getAirohaLogDumpMgr().startOnlineLogger("OnlineLog_" + mTimeStamp, "Onlinelog_" + timeStamp + ".wirelesslog");
        mActivity.getAirohaService().getAirohaLogDumpMgr().startOnlineDump();
    }

    public boolean isStartLogging() {
        return ((OnlinelogDumpView)pageList.get(0)).isStartLogging();
    }

    private void initPage() {
        gLogger.d(TAG, "initData");
        pageList = new ArrayList<>();
        pageList.add(new OnlinelogDumpView(mActivity));
    }

    private void initView() {
        gLogger.d(TAG, "initView");

        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.addOnPageChangeListener(new GuidePageChangeListener());
    }

    private void initUImember() {
        gLogger.d(TAG, "initUImember");
    }

    private void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            gLogger.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                gLogger.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            gLogger.e("", ioe.getMessage());
        }
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

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        //切換view時，下方圓點的變化。
        public void onPageSelected(int position) {

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
                            byte[] bdAddr = new byte[6];
                            if(AirohaSDK.getInst().mChipType == ChipType.AB1562
                                    || AirohaSDK.getInst().mChipType == ChipType.AB1562E) {
                                System.arraycopy(resp, 11, bdAddr, 0, 6);
                            } else {
                                System.arraycopy(resp, 8, bdAddr, 0, 6);
                            }

                            String ret = Converter.byte2HerStrReverse(bdAddr);
                            String bda = ret.replace(" ", "");

                            String outPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS) + File.separator + "Airoha" + File.separator +
                                    "Dump" + File.separator + "log_" + bda + "_" + mTimeStamp + ".zip";
                            String zipFolder = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS) + File.separator + "Airoha" + File.separator +
                                    "Dump" + File.separator + "OnlineLog_" + mTimeStamp;

                            zipFolder(zipFolder, outPath);

                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("application/zip");
                            String shareBody = "Here's your debug log";
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Airoha Exception Log");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            File file = new File(outPath);
                            if (!file.exists()) {
                                mActivity.updateMsg(MainActivity.MsgType.GENERAL,"ExceptionDump zip file not found.");
                                return;
                            }

                            Uri uri = FileProvider.getUriForFile(
                                    mActivity,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    file);
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        }

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
}
