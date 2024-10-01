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
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.airoha.libbase.RaceCommand.constant.RaceType;
import com.airoha.liblogdump.AirohaDumpListener;
import com.airoha.liblogdump.offlinedump.DumpRegionInfo;
import com.airoha.liblogdump.offlinedump.DumpTypeEnum;
import com.airoha.libutils.Converter;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaCmdSettings;
import com.airoha.sdk.api.utils.AirohaStatusCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class ExceptionDump1562Fragment extends BaseFragment {
    private String TAG = ExceptionDump1562Fragment.class.getSimpleName();
    private MainActivity mActivity;
    private ExceptionDump1562Fragment mFragment;
    private View mView;
    private Handler mHandler;

    private Button mBtnQueryDumpInfo;
    private Button mBtnStartDump;
    private Button mBtnAssert;
    private Button mBtnTriggerOfflinelog;
    private TextView mTextReadInfo;

    private int LOG_MAXLENGTH = 0;
    private int mPageCount = 0;
    private int mOfflineLogRegionMin = 0;
    private int mOfflineLogRegionMax = 0;
    private int mMinidumpRegionMin = 0;
    private int mMinidumpRegionMax = 0;
    private int mExceptionLogRegionMin = 0;
    private int mExceptionLogRegionMax = 0;

    private ArrayList<DumpRegionInfo> mRegionInfoList = new ArrayList<>();

    private String mTimeStamp;
    private String currentFilename;
    private String currentFolder;

    private TextView mTextExceptionInfo;
    private TextView mTextBootReason;
    private TextView mTextMinidumpInfo;
    private TextView mTextOfflinelogInfo;
    private TextView mTextViewLogPath;

    private ImageView mImgExceptionCheck;
    private ImageView mImgMinidumpCheck;
    private ImageView mImgOfflineCheck;
    private Button mButtonShare;

    private Switch mSwitchOfflineLogOnOff;
    private Switch mSwitchMinidumpOnOff;

    public ExceptionDump1562Fragment(){
        mTitle = "EXCEPTION DUMP";
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
        mView = inflater.inflate(R.layout.fragment_exceptiondump1562, container,false);
        initUImember();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler = new Handler();

        if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.EXCEPTION_DUMP)
            return;

        if(mActivity.getAirohaService() != null) {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(true);
            mActivity.getAirohaService().getAirohaLinker().addHostListener(gTargetAddr, TAG, mFragment);
            mActivity.getAirohaService().getAirohaLogDumpMgr().addListener(TAG, mAirohaDumpListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mActivity.getPreviousFragmentIndex() != MainActivity.FragmentIndex.EXCEPTION_DUMP)
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
                stopDumpWhenDisconnected();
                enableAllButton(false);
            }
        });
    }

    @Override
    public void onHostInitialized() {
        initUI();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
        super.onHiddenChanged(hidden);
        if (mActivity.getAirohaService() == null) {
            return;
        }
        if (hidden) {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(false);
            mActivity.getAirohaService().getAirohaLinker().removeHostListener(gTargetAddr, TAG);
            mActivity.getAirohaService().getAirohaLogDumpMgr().removeListener(TAG);
            mTextReadInfo.setText("");
        } else {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(true);
            mActivity.getAirohaService().getAirohaLinker().addHostListener(gTargetAddr, TAG, mFragment);
            mActivity.getAirohaService().getAirohaLogDumpMgr().addListener(TAG, mAirohaDumpListener);
            initUI();
        }
    }

    private void initUI() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBtnQueryDumpInfo.setEnabled(true);
                mBtnAssert.setEnabled(true);
                mBtnTriggerOfflinelog.setEnabled(true);
                mSwitchOfflineLogOnOff.setEnabled(true);
                mSwitchMinidumpOnOff.setEnabled(true);

                mActivity.getAirohaService().getAirohaLogDumpMgr().queryOfflinelogStatus();
                mActivity.getAirohaService().getAirohaLogDumpMgr().queryMinidumpStatus1562();
            }
        });
    }

    private void enableAllButton(boolean isEnable) {
        mBtnQueryDumpInfo.setEnabled(isEnable);
        mBtnStartDump.setEnabled(isEnable);
        mBtnAssert.setEnabled(isEnable);
        mBtnTriggerOfflinelog.setEnabled(isEnable);
        mButtonShare.setEnabled(isEnable);

        mSwitchOfflineLogOnOff.setEnabled(isEnable);
        mSwitchMinidumpOnOff.setEnabled(isEnable);
    }

    private void initUImember() {
        gLogger.d(TAG, "initUImember");

        mBtnQueryDumpInfo = mView.findViewById(R.id.buttonGetReason);
        mBtnQueryDumpInfo = mView.findViewById(R.id.buttonGetReason);
        mBtnStartDump = mView.findViewById(R.id.buttonStartDump);
        mBtnAssert = mView.findViewById(R.id.buttonAssert);
        mBtnTriggerOfflinelog = mView.findViewById(R.id.buttonTriggerLog);
        mTextReadInfo = mView.findViewById(R.id.textViewReadInfo);
        mTextExceptionInfo = mView.findViewById(R.id.textViewExceptionLog);
        mTextBootReason = mView.findViewById(R.id.textViewBootReason);
        mTextMinidumpInfo = mView.findViewById(R.id.textViewMinidump);
        mTextOfflinelogInfo = mView.findViewById(R.id.textViewOfflinelog);
        mTextViewLogPath = mView.findViewById(R.id.textViewLogPath);
        mButtonShare = mView.findViewById(R.id.btnShare);

        mImgExceptionCheck = mView.findViewById(R.id.imgExceptionCheck);
        mImgMinidumpCheck = mView.findViewById(R.id.imgMinidumpCheck);
        mImgOfflineCheck = mView.findViewById(R.id.imgOfflineCheck);

        mSwitchOfflineLogOnOff = mView.findViewById(R.id.switch_offlinelog_onoff);
        mSwitchMinidumpOnOff = mView.findViewById(R.id.switch_minidump_onoff);

        mSwitchOfflineLogOnOff.setOnClickListener(mOnClickListener);
        mSwitchMinidumpOnOff.setOnClickListener(mOnClickListener);

        mTextReadInfo.setMovementMethod(new ScrollingMovementMethod());

        mBtnAssert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getAirohaService().getAirohaLogDumpMgr().makeAssert();
                enableAllButton(false);
            }
        });

        mBtnTriggerOfflinelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getAirohaService().getAirohaLogDumpMgr().triggerLogDump();
            }
        });

        mBtnQueryDumpInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextReadInfo.setText("");
                mTextExceptionInfo.setText("");
                mTextBootReason.setText("");
                mTextMinidumpInfo.setText("");
                mTextOfflinelogInfo.setText("");
                mImgOfflineCheck.setVisibility(View.INVISIBLE);
                mImgExceptionCheck.setVisibility(View.INVISIBLE);
                mImgMinidumpCheck.setVisibility(View.INVISIBLE);
                mRegionInfoList.clear();
                mActivity.getAirohaService().getAirohaLogDumpMgr().getReason();
                //mActivity.getAirohaService().getAirohaLogDumpMgr().getMimiDumpAddress();
                //mActivity.getAirohaService().getAirohaLogDumpMgr().getExceptionLogAddress();
                mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpRegion(DumpTypeEnum.Mini_Dump);
                mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpRegion(DumpTypeEnum.Exception_Log);
                mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpRegion(DumpTypeEnum.Offline_Log);
            }
        });

        mBtnStartDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableAllButton(false);
                mPageCount = 0;
                mTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                getInfoToDump();
                String folderPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS) + File.separator + "Airoha" + File.separator +
                        "Dump" + File.separator + "ExceptionLog_"+mTimeStamp;
                mTextViewLogPath.setText(folderPath);
            }
        });

        mButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final byte[] data = Converter.hexStringToByteArray("055A06000C0A0036E803");
                AirohaCmdSettings setting = new AirohaCmdSettings();
                setting.setRespType(RaceType.RESPONSE);
                setting.setCommand(data);
                AirohaSDK.getInst().getAirohaDeviceControl().sendCustomCommand(setting, new CustomCmdListener());
            }
        });

        setUiStatus();
    }

    private void stopDumpWhenDisconnected() {
        if(mActivity.getAirohaService().getAirohaLogDumpMgr().mLogDumpRaw != null) {
            mActivity.getAirohaService().getAirohaLogDumpMgr().mLogDumpRaw.stop();
            mActivity.getAirohaService().getAirohaLogDumpMgr().stopDump();
            mActivity.getAirohaService().getAirohaLogDumpMgr().renewStageQueue();
        }
    }

    private void getInfoToDump() {
        if(mRegionInfoList.size() == 0) {
            mActivity.getAirohaService().getAirohaLogDumpMgr().mLogDumpRaw.stop();
            Toast.makeText(mActivity, "Dump Complete", Toast.LENGTH_SHORT).show();
            mBtnStartDump.setEnabled(false);
            mBtnQueryDumpInfo.setEnabled(true);
            mBtnAssert.setEnabled(true);
            mBtnTriggerOfflinelog.setEnabled(true);
            mButtonShare.setEnabled(true);
            mSwitchMinidumpOnOff.setEnabled(true);
            mSwitchOfflineLogOnOff.setEnabled(true);

            mActivity.getAirohaService().getAirohaLogDumpMgr().stopDump();
            return;
        }

        DumpRegionInfo info = mRegionInfoList.get(0);
        currentFilename = "";
        currentFolder = "ExceptionLog_" + mTimeStamp;
        switch(info.type) {
            case Mini_Dump:
                currentFilename = "MiniDump" + info.region + "_" + mTimeStamp + ".minidumpraw";
                break;
            case Exception_Log:
                currentFilename = "ExceptionLog" + info.region + "_" + mTimeStamp + ".minilog";
                break;
            case Offline_Log:
                currentFilename = "OfflineLog" + info.region + "_" + mTimeStamp + ".minilog";
                break;
        }
        mActivity.getAirohaService().getAirohaLogDumpMgr().startDump(info.address, info.length, currentFilename, currentFolder);
    }

    private void setUiStatus() {
        mBtnStartDump.setEnabled(false);
    }

    private AirohaDumpListener mAirohaDumpListener = new AirohaDumpListener() {

        @Override
        public void OnUpdateLog(final String log) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(LOG_MAXLENGTH > 200)
                    {
                        mTextReadInfo.setText("");
                        LOG_MAXLENGTH = 0;
                    }
                    mTextReadInfo.append(log + "\n");
                    LOG_MAXLENGTH++;

                    mPageCount++;
                    gLogger.d(TAG, "OnUpdateLog: mPageCount = " + mPageCount);

                    int currentCount = mRegionInfoList.get(0).pageCount;
                    if(mPageCount == currentCount) {
                        switch(mRegionInfoList.get(0).type) {
                            case Offline_Log:
                                if(mRegionInfoList.size() == 1)
                                    mImgOfflineCheck.setVisibility(View.VISIBLE);
                                break;
                            case Exception_Log:
                                mImgExceptionCheck.setVisibility(View.VISIBLE);
                                break;
                            case Mini_Dump:
                                mImgMinidumpCheck.setVisibility(View.VISIBLE);
                                break;
                        }
                        mRegionInfoList.remove(0);
                        mPageCount = 0;
                        // rename
                        mActivity.getAirohaService().getAirohaLogDumpMgr().renameLogFile(currentFilename, currentFolder);
                        getInfoToDump();
                    }
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
        public void OnUpdateDumpAddrLength(final int addr, final int length, final DumpTypeEnum logType, final int region) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DumpRegionInfo info = new DumpRegionInfo();
                    info.type = logType;
                    info.address = addr;
                    info.length = length;
                    info.region = region;
                    info.pageCount = length / 256;
                    switch(logType) {
                        case Exception_Log:
                            int exceptionCount = mExceptionLogRegionMax - mExceptionLogRegionMin + 1;
                            mTextExceptionInfo.setText("Addr: " + addr + "\nLength: " + length + "\nRegion Count: " + exceptionCount);
                            break;
                        case Mini_Dump:
                            int minidumpCount = mMinidumpRegionMax - mMinidumpRegionMin + 1;
                            mTextMinidumpInfo.setText("Addr: " + addr + "\nLength: " + length + "\nRegion Count: " + minidumpCount);
                            break;
                        case Offline_Log:
                            int offlineCount = mOfflineLogRegionMax - mOfflineLogRegionMin + 1;
                            mTextOfflinelogInfo.setText("Addr: " + addr + "\nLength: " + length + "\nRegion Count: " + offlineCount);
                            break;
                    }
                    if(info.pageCount != 0) {
                        mRegionInfoList.add(info);
                    }

                    if(length != 0)
                        mBtnStartDump.setEnabled(true);
                }
            });
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
        public void OnNotifyError(final String stageName, final String errorMsg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mActivity.updateMsg(MainActivity.MsgType.ERROR, "OnNotifyError: " + errorMsg);
                }
            });
        }

        @Override
        public void OnUpdateCpuFilterInfo(byte cpuId, byte onOff, byte level) {

        }

        @Override
        public void OnUpdateBootReason(final String log) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextBootReason.setText(log);
                }
            });
        }

        @Override
        public void OnUpdateOfflineLogRegion(int min, int max, DumpTypeEnum type) {
            switch(type) {
                case Mini_Dump:
                    mMinidumpRegionMin = min;
                    mMinidumpRegionMax = max;
                    for(int i = mMinidumpRegionMin; i <= mMinidumpRegionMax; i++) {
                        mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpLogAddress(DumpTypeEnum.Mini_Dump, i);
                    }
                    break;
                case Exception_Log:
                    mExceptionLogRegionMin = min;
                    mExceptionLogRegionMax = max;
                    for(int i = mExceptionLogRegionMin; i <= mExceptionLogRegionMax; i++) {
                        mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpLogAddress(DumpTypeEnum.Exception_Log, i);
                    }
                    break;
                case Offline_Log:
                    mOfflineLogRegionMin = min;
                    mOfflineLogRegionMax = max;
                    for(int i = mOfflineLogRegionMin; i <= mOfflineLogRegionMax; i++) {
                        mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpLogAddress(DumpTypeEnum.Offline_Log, i);
                    }
                    break;
            }

        }

        @Override
        public void OnUpdateOfflineLogStatus(final byte onOff) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(onOff == 1) {
                        mSwitchOfflineLogOnOff.setChecked(true);
                    } else {
                        mSwitchOfflineLogOnOff.setChecked(false);
                    }
                }
            });
        }

        @Override
        public void OnUpdateExceptionStatus(final byte onOff) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(onOff == 1) {
                        mSwitchMinidumpOnOff.setChecked(true);
                    } else {
                        mSwitchMinidumpOnOff.setChecked(false);
                    }
                }
            });
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

    private View viewSwitch;
    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewSwitch = v;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (viewSwitch.getId()) {
                        case R.id.switch_offlinelog_onoff:
                            if(((Switch)viewSwitch).isChecked()) {
                                mActivity.getAirohaService().getAirohaLogDumpMgr().setOfflinelogStatus((byte)1);
                            } else {
                                mActivity.getAirohaService().getAirohaLogDumpMgr().setOfflinelogStatus((byte)0);
                            }
                            break;
                        case R.id.switch_minidump_onoff:
                            if(((Switch)viewSwitch).isChecked()) {
                                mActivity.getAirohaService().getAirohaLogDumpMgr().setMinidumpStatus1562((byte)1);
                            } else {
                                mActivity.getAirohaService().getAirohaLogDumpMgr().setMinidumpStatus1562((byte)0);
                            }
                            break;
                    }
                }
            });
        }
    };

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
                            System.arraycopy(resp, 11, bdAddr, 0, 6);
                            String ret = Converter.byte2HerStrReverse(bdAddr);
                            String bda = ret.replace(" ", "");

                            String outPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS) + File.separator + "Airoha" + File.separator +
                                    "Dump" + File.separator + "log_" + bda + "_" + mTimeStamp + ".zip";
                            zipFolder(mTextViewLogPath.getText().toString(), outPath);

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
                            sharingIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
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
