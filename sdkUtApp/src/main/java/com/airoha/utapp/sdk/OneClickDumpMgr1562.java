package com.airoha.utapp.sdk;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.airoha.libbase.RaceCommand.constant.RaceType;
import com.airoha.liblogdump.AirohaDumpListener;
import com.airoha.liblogdump.offlinedump.DumpRegionInfo;
import com.airoha.liblogdump.offlinedump.DumpTypeEnum;
import com.airoha.liblogger.AirohaLogger;
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
import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class OneClickDumpMgr1562 {
    private static final String TAG = "OneClickDumpMgr";

    private MainActivity mActivity;

    private ArrayList<DumpRegionInfo> mRegionInfoList = new ArrayList<>();
    private ArrayList<String> mLogFileList = new ArrayList<>();

    private int mPageCount = 0;
    private int mTmpRegionCount = 0;
    private int mOfflineLogRegionMin = 0;
    private int mOfflineLogRegionMax = 0;
    private int mTotalOfflineLogRegionCount = -1;
    private int mMinidumpRegionMin = 0;
    private int mMinidumpRegionMax = 0;
    private int mTotalMinidumpRegionCount = -1;
    private int mExceptionLogRegionMin = 0;
    private int mExceptionLogRegionMax = 0;
    private int mTotalExceptionLogRegionCount = -1;

    private String mTimeStamp;
    private String currentFilename;
    private String currentFolder;

    private int mQueryLogStep = 0;

    private ProgressDialogUtil mProgressDialog;

    AirohaLogger gLogger = AirohaLogger.getInstance();

    public OneClickDumpMgr1562(Context ctx) {
        mActivity = (MainActivity) ctx;
        mProgressDialog = new ProgressDialogUtil();
    }

    public void startQueryDumpInfo() {
        mActivity.getAirohaService().getAirohaLogDumpMgr().addListener(TAG, mAirohaDumpListener);
        mRegionInfoList.clear();
        mLogFileList.clear();
        mTotalOfflineLogRegionCount = -1;
        mTotalMinidumpRegionCount = -1;
        mTotalExceptionLogRegionCount = -1;
        mTmpRegionCount = 0;
        mPageCount = 0;
        mQueryLogStep = 0;
        mProgressDialog.showProgressDialog(mActivity);
        mTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        mProgressDialog.updateProgressMsg("Get mini dump info");
        mActivity.getAirohaService().getAirohaLogDumpMgr().triggerLogDump();
        mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpRegion(DumpTypeEnum.Mini_Dump);
        mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpRegion(DumpTypeEnum.Exception_Log);
        mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpRegion(DumpTypeEnum.Offline_Log);
        //mActivity.getAirohaService().getAirohaLogDumpMgr().getMimiDumpAddress();
    }

    private void getInfoToDump() {
        if(mRegionInfoList.size() == 0) {
            gLogger.d(TAG, "getInfoToDump size=0");
            mProgressDialog.updateProgressMsg("Start to zip log");
            mActivity.getAirohaService().getAirohaLogDumpMgr().stopDump();

            try {
                for(String filename: mLogFileList) {
                    if(!checkFileExist(filename)) {
                        gLogger.d(TAG, "check retry: " + filename);
                        mActivity.getAirohaService().getAirohaLogDumpMgr().stopDump();
                        Thread.sleep(1000);
                        if(!checkFileExist(filename)) {
                            mActivity.getAirohaService().getAirohaLogDumpMgr().removeListener(TAG);
                            mProgressDialog.dismiss();
                            showMessageDialog("zip fail");
                            return;
                        }
                    }
                }
            } catch (InterruptedException ex) {
                gLogger.d(TAG, "InterruptedException: " + ex.getMessage());
                mActivity.getAirohaService().getAirohaLogDumpMgr().removeListener(TAG);
                mProgressDialog.dismiss();
                showMessageDialog("zip fail");
                return;
            }

            startShareProcess();
            mProgressDialog.dismiss();
            mActivity.getAirohaService().getAirohaLogDumpMgr().removeListener(TAG);
            return;
        }

        DumpRegionInfo info = mRegionInfoList.get(0);
        currentFilename = "";
        currentFolder = "ExceptionLog_" + mTimeStamp;
        switch(info.type) {
            case Mini_Dump:
                currentFilename = "MiniDump" + info.region + "_" + mTimeStamp + ".minidumpraw";
                mProgressDialog.updateProgressMsg("Start to dump mini dump");
                break;
            case Exception_Log:
                currentFilename = "ExceptionLog" + info.region + "_" + mTimeStamp + ".minilog";
                mProgressDialog.updateProgressMsg("Start to dump exception log");
                break;
            case Offline_Log:
                currentFilename = "OfflineLog" + info.region + "_" + mTimeStamp + ".minilog";
                mProgressDialog.updateProgressMsg("Start to dump offline log");
                break;
        }
        gLogger.d(TAG, "startDump address: " + info.address);
        gLogger.d(TAG, "startDump length: " + info.length);
        gLogger.d(TAG, "startDump currentFilename: " + currentFilename);
        mActivity.getAirohaService().getAirohaLogDumpMgr().startDump(info.address, info.length, currentFilename, "ExceptionLog_"+mTimeStamp);
    }

    private void startShare(String zipPath, String exceptionFolder) {
        zipFolder(exceptionFolder, zipPath);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("application/zip");
        String shareBody = "Here's your debug log";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Airoha Exception Log");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        File file = new File(zipPath);
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
        mActivity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
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

    private AirohaDumpListener mAirohaDumpListener = new AirohaDumpListener() {

        @Override
        public void OnUpdateLog(final String log) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPageCount++;

                    int currentCount = mRegionInfoList.get(0).pageCount;
                    if(mPageCount == currentCount) {
                        switch(mRegionInfoList.get(0).type) {
                            case Offline_Log:
                                if(mRegionInfoList.size() == 1)
                                    mProgressDialog.updateProgressMsg("Offline_Log Dump Done");
                                break;
                            case Exception_Log:
                                mProgressDialog.updateProgressMsg("Exception_Log Dump Done");
                                break;
                            case Mini_Dump:
                                mProgressDialog.updateProgressMsg("Mini_Dump Dump Done");
                                break;
                        }
                        mPageCount = 0;
                        mRegionInfoList.remove(0);
                        mActivity.getAirohaService().getAirohaLogDumpMgr().renameLogFile(currentFilename, currentFolder);
                        mLogFileList.add(mActivity.getAirohaService().getAirohaLogDumpMgr().getNewLogFileName());
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
                        case Mini_Dump:
                            int minidumpCount = mMinidumpRegionMax - mMinidumpRegionMin + 1;
                            mTmpRegionCount++;
                            mProgressDialog.updateProgressMsg("Mini dump \nAddr:" + addr +"\nLength:" + length + "\nRegion Count: " + minidumpCount);
                            if(info.pageCount != 0) {
                                mRegionInfoList.add(info);
                            }
                            if(mTotalMinidumpRegionCount == mTmpRegionCount) {
                                mTmpRegionCount = 0;
                                mQueryLogStep ++;
                            }
                            //mActivity.getAirohaService().getAirohaLogDumpMgr().getExceptionLogAddress();
                            break;
                        case Exception_Log:
                            int exceptionCount = mExceptionLogRegionMax - mExceptionLogRegionMin + 1;
                            mTmpRegionCount ++;
                            mProgressDialog.updateProgressMsg("Exception_Log \nAddr:" + addr +"\nLength:" + length + "\nRegion Count: " + exceptionCount);
                            if(info.pageCount != 0) {
                                mRegionInfoList.add(info);
                            }
                            if(mTotalExceptionLogRegionCount == mTmpRegionCount) {
                                mTmpRegionCount = 0;
                                mQueryLogStep ++;
                            }
                            //mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpRegion(DumpTypeEnum.Offline_Log);
                            break;
                        case Offline_Log:
                            int offlineCount = mOfflineLogRegionMax - mOfflineLogRegionMin + 1;
                            mTmpRegionCount++;
                            mProgressDialog.updateProgressMsg("Offline_Log \nAddr:" + addr +"\nLength:" + length + "\nRegion Count: " + offlineCount);
                            if(info.pageCount != 0) {
                                mRegionInfoList.add(info);

                            }
                            if(mTotalOfflineLogRegionCount == mTmpRegionCount) {
                                mTmpRegionCount = 0;
                                mQueryLogStep ++;
                            }
                            //if(mTotalRegionCount == mTmpRegionCount) {
                            //    getInfoToDump();
                            //}
                            break;
                    }
                    gLogger.d(TAG, "mQueryLogStep=" + mQueryLogStep);
                    if(mQueryLogStep == 3)
                    {
                        gLogger.d(TAG, "OnUpdateDumpAddrLength start dump");
                        getInfoToDump();
                    }
                }
            });
        }

        @Override
        public void OnResponseTimeout(final String stageName) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "OnResponseTimeout " + stageName);
                    mProgressDialog.dismiss();
                }
            });
        }

        @Override
        public void OnNotifyError(final String stageName, final String errorMsg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch(stageName) {
                        /*case "StageGetDumpAddress":
                            mProgressDialog.updateProgressMsg("No mini dump\nGet exception log info");
                            mActivity.getAirohaService().getAirohaLogDumpMgr().getExceptionLogAddress();
                            break;
                        case "StageGetExceptionLogAddress":
                            mProgressDialog.updateProgressMsg("No exception log\nGet offline log info");
                            mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpRegion(DumpTypeEnum.Offline_Log);
                            break;*/
                        case "StageGetDumpRegionInfo":
                            //mProgressDialog.updateProgressMsg("No offline log");
                            /*if(errorMsg.contains("Offline_Log")) {
                                mProgressDialog.dismiss();
                                showMessageDialog("Get dump info error");
                            }*/
                            gLogger.d(TAG, "OnNotifyError: " + errorMsg);
                            mQueryLogStep ++;
                            break;
                    }
                    gLogger.d(TAG, "mQueryLogStep=" + mQueryLogStep);
                    if(mQueryLogStep == 3)
                    {
                        gLogger.d(TAG, "OnNotifyError start dump");
                        getInfoToDump();
                    }
                }
            });
        }

        @Override
        public void OnUpdateCpuFilterInfo(byte cpuId, byte onOff, byte level) {

        }

        @Override
        public void OnUpdateBootReason(final String log) {

        }

        @Override
        public void OnUpdateOfflineLogRegion(int min, int max, DumpTypeEnum type) {
            switch(type) {
                case Mini_Dump:
                    mMinidumpRegionMin = min;
                    mMinidumpRegionMax = max;
                    mTotalMinidumpRegionCount = max - min + 1;
                    for(int i = mMinidumpRegionMin; i <= mMinidumpRegionMax; i++) {
                        mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpLogAddress(DumpTypeEnum.Mini_Dump, i);
                    }
                    break;
                case Exception_Log:
                    mExceptionLogRegionMin = min;
                    mExceptionLogRegionMax = max;
                    mTotalExceptionLogRegionCount = max - min + 1;
                    for(int i = mExceptionLogRegionMin; i <= mExceptionLogRegionMax; i++) {
                        mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpLogAddress(DumpTypeEnum.Exception_Log, i);
                    }
                    break;
                case Offline_Log:
                    mOfflineLogRegionMin = min;
                    mOfflineLogRegionMax = max;
                    mTotalOfflineLogRegionCount = max - min + 1;
                    for(int i = mOfflineLogRegionMin; i <= mOfflineLogRegionMax; i++) {
                        mActivity.getAirohaService().getAirohaLogDumpMgr().queryDumpLogAddress(DumpTypeEnum.Offline_Log, i);
                    }
                    break;
            }
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

    private boolean checkFileExist(String filename) {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                gLogger.d(TAG, "checkLogFileExist Android R");
                String folderPath = DIRECTORY_DOCUMENTS + File.separator + "Airoha" + File.separator +
                        "Dump" + File.separator + currentFolder + File.separator ;

                Uri contentUri = MediaStore.Files.getContentUri("external");
                String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";
                String[] selectionArgs = new String[]{folderPath};
                Cursor cursor = mActivity.getContentResolver().query(contentUri, null, selection, selectionArgs, null);

                gLogger.d(TAG, "check file: " + filename);
                if (cursor.getCount() == 0) {
                    gLogger.d(TAG, filename + "not foune");
                    return false;
                } else {
                    boolean isFound = false;
                    while (cursor.moveToNext()) {
                        String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                        if (fileName.equals(filename)) {
                            isFound = true;
                            break;
                        }
                    }
                    return isFound;
                }
            } else {
                gLogger.d(TAG, "checkLogFileExist under Android Q");
                String folderPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS) + File.separator + "Airoha" + File.separator +
                        "Dump" + File.separator + currentFolder + File.separator ;
                File file = new File(folderPath + filename);

                if (!file.exists()) {
                    gLogger.d(TAG, filename + "not foune");
                    return false;
                }
            }
        } catch (Exception ex) {
            gLogger.d(TAG, "Exception: " + ex.getMessage());
            return false;
        }

        return true;
    }

    private void startShareProcess() {
        gLogger.d(TAG, "get BDA");
        final byte[] data = Converter.hexStringToByteArray("055A06000C0A0036E803");
        AirohaCmdSettings setting = new AirohaCmdSettings();
        setting.setRespType(RaceType.RESPONSE);
        setting.setCommand(data);
        AirohaSDK.getInst().getAirohaDeviceControl().sendCustomCommand(setting, new CustomCmdListener());
    }

    private void showMessageDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Info.");
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mActivity.changeFragment(MainActivity.FragmentIndex.MENU);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                            System.arraycopy(resp, 11, bdAddr, 0, 6);
                            String ret = Converter.byte2HerStrReverse(bdAddr);
                            String bda = ret.replace(" ", "");

                            gLogger.d(TAG, "BDA=" + bda);

                            String zipPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS) + File.separator + "Airoha" + File.separator +
                                    "Dump" + File.separator + "log_" + bda + "_" + mTimeStamp + ".zip";
                            String exceptionFolder = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS) + File.separator + "Airoha" + File.separator +
                                    "Dump" + File.separator + "ExceptionLog_"+mTimeStamp;

                            File file = new File(exceptionFolder);
                            if (!file.exists()) {
                                gLogger.d(TAG, "ExceptionDump folder not found");
                                mActivity.updateMsg(MainActivity.MsgType.GENERAL,"ExceptionDump folder not found.");
                                //mProgressDialog.dismiss();
                                mActivity.getAirohaService().getAirohaLogDumpMgr().removeListener(TAG);
                                return;
                            }

                            startShare(zipPath, exceptionFolder);
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
