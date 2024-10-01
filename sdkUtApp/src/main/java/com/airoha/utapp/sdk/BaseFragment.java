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

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.fragment.app.Fragment;

import com.airoha.liblinker.host.HostDataListener;
import com.airoha.liblinker.host.HostStateListener;
import com.airoha.liblinker.host.TxScheduler;
import com.airoha.liblogger.AirohaLogger;
import com.airoha.liblogger.printer.FilePrinter;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.utils.ConnectionProtocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class BaseFragment extends Fragment implements HostStateListener, HostDataListener, AirohaConnector.AirohaConnectionListener {
    private String TAG = BaseFragment.class.getSimpleName();
    //protected final String LOCAL_FW_FOLDER = Environment.getExternalStorageDirectory() + "/Airoha/";
//    protected final int LONG_PACKET_CMD_COUNT = 3;
//    protected final int LONG_PACKET_DELAY = 200;
//    protected final int BATTERY_THRESHOLD = 70;

    public static final String EXTRAS_DEVICE_BDA = "EXTRAS_DEVICE_BDA";
    public static final String EXTRAS_DEVICE_PHY = "EXTRAS_DEVICE_PHY";
    public static final String EXTRAS_BLE_DEVICE_SCAN_RECORD = "EXTRAS_BLE_DEVICE_SCAN_RECORD";

    protected MainActivity mActivity;
    private AlertDialog mAlertDialog = null;

    static protected boolean gReconnectFlag = false;

    public String mTitle;
    static protected String gTargetName;
    static protected String gTargetAddr;
    static protected byte[] gBleScanRecord;
    static protected ConnectionProtocol gTargetPhy = ConnectionProtocol.PROTOCOL_SPP;

    AirohaLogger gLogger = AirohaLogger.getInstance();
    protected static FilePrinter gFilePrinter;

    protected FilePrinter createLogFile(String bdAddr) {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bdAddr);
        String _device_name = "";
        if (device != null) {
            _device_name = device.getName();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("folder_name", "SDK_UT");
        map.put("device_name", _device_name);

        // new file printer
        FilePrinter filePrinter = new FilePrinter(mActivity);
        if (!filePrinter.init(map)) {
            return null;
        }

        gLogger.addPrinter(filePrinter);
        gLogger.setLogLevel(AirohaLogger.LOG_LEVEL_V);
        gLogger.d(TAG, "Create log, device name: " + _device_name);
        gLogger.d(TAG, "Ver:" + BuildConfig.VERSION_NAME);

        return filePrinter;
    }

    public String getTargetAddr(){
        return gTargetAddr;
    }

    public String getTitle(){
        return mTitle;
    }

    @Override
    public void onHostConnected() {

    }

    @Override
    public void onHostDisconnected() {

    }

    @Override
    public void onHostWaitingConnectable() {

    }

    @Override
    public void onHostInitialized() {

    }

    @Override
    public void onHostError(int errorCode) {

    }

    @Override
    public boolean onHostPacketReceived(byte[] packet) {
        return false;
    }

    @Override
    public void onHostScheduleTimeout(TxScheduler.ITxScheduledData txData) {

    }

    public enum PrivilegeState
    {
        EngineerMode,
        CustomerMode,
    }

    public void changePrivilege(PrivilegeState state){

    }

    @Override
    public void onStatusChanged(int status) {

    }

    @Override
    public void onDataReceived(AirohaBaseMsg deviceMessage) {

    }

    protected void showAlertDialog(final Context context, final String title, final String message){
        if(mActivity.isFinishing()) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        });
    }

    protected void showErrorMsg(Activity activity) {
        if(activity.isFinishing()) {
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = "FOTA Fail!!\nPlease follow these steps to check and try again:\n" +
                        "1. Check the bin file is correct\n" +
                        "2. Close App and turn OFF device\n" +
                        "3. Restart the bluetooth of smart phone\n" +
                        "4. Turn ON device\n" +
                        "5. Launch App and run FOTA again\n\n";

                msg += "If you need support, please prepare these data:\n" +
                        "1. Screenshot, without this dialog\n" +
                        "2. Log file, which is saved in /Documents/Airoha/Log/SDK_UT/\n";
                showAlertDialog(mActivity, "Error", msg);
            }
        });
    }


    protected void showFileChooser(final int requestID, String type, String title) {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType(type);
        chooseFile.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent = Intent.createChooser(chooseFile, title);
        startActivityForResult(intent, requestID);
    }

    protected void showFileChooser(final int requestID) {
        showFileChooser(requestID, MimeTypeMap.getSingleton().getMimeTypeFromExtension("bin"), "Choose a bin file");
    }

    protected File uriToFileApiQ(Uri uri) {
        File ret = null;
        String scheme = uri.getScheme();
        if (scheme == null) {
            gLogger.d(TAG, "uri scheme= null");
            return ret;
        }

        if (scheme.equals(ContentResolver.SCHEME_FILE)) {
            gLogger.d(TAG, "new File");
            ret = new File(uri.getPath());
        } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            gLogger.d(TAG, "getContentResolver");
            ContentResolver contentResolver = mActivity.getContentResolver();
            gLogger.d(TAG, "query Cursor");
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                gLogger.d(TAG, "columnIndex= " + columnIndex);
                String displayName = cursor.getString(columnIndex);
                gLogger.d(TAG, "displayName= " + displayName);
                try {
                    gLogger.d(TAG, "openInputStream");
                    InputStream is = contentResolver.openInputStream(uri);
                    ret = copyFileToCache(is, displayName);
                } catch (Exception e) {
                    gLogger.e(e);
                }
            } else {
                gLogger.d(TAG, "cursor.moveToFirst() is false");
            }
        } else {
            gLogger.d(TAG, "uri scheme= " + scheme);
        }
        return ret;
    }

    File copyFileToCache(String filePath, String displayName) {
        gLogger.d(TAG, "copyFileToCache: filePath= " + filePath+ ", displayName= " + displayName);
        File ret = null;
        gLogger.d(TAG, "new File");
        File srcFile = new File(filePath);
        if (srcFile.exists()) {
            try {
                gLogger.d(TAG, "new FileInputStream");
                InputStream is = new FileInputStream(srcFile);
                ret = copyFileToCache(is, displayName);
                is.close();
            } catch (Exception e) {
                gLogger.e(e);
            }
        } else {
            gLogger.e(TAG, "srcFile.exists() is false");
        }
        return ret;
    }

    File copyFileToCache(InputStream is, String displayName) {
        gLogger.d(TAG, "copyFileToCache: " + displayName);
        File ret = null;
        try {
            gLogger.d(TAG, "new File");
            File cache = new File(mActivity.getExternalCacheDir().getAbsolutePath(), displayName);
            gLogger.d(TAG, "deleteOnExit()");
            cache.deleteOnExit();
            gLogger.d(TAG, "new FileOutputStream");
            FileOutputStream fos = new FileOutputStream(cache);
            gLogger.d(TAG, "FileUtils.copy");
            FileUtils.copy(is, fos);
            ret = cache;
            fos.close();
            gLogger.d(TAG, "copyFileToCache success");
        } catch (Exception e) {
            gLogger.e(e);
        }
        return ret;
    }

    protected void initAutoTestBinFile(AirohaService.ExtraKey key, String srcFilePath) {
        File tmpFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String fileName = srcFilePath.substring(srcFilePath.lastIndexOf('/'));
            tmpFile = copyFileToCache(srcFilePath, fileName);
        } else {
            tmpFile = new File(srcFilePath);
        }

        if (tmpFile != null && tmpFile.exists()) {
            initAutoTestParam(key, tmpFile);
        } else {
            gLogger.e(TAG, "file not found: " + srcFilePath);
        }
    }

    protected void initAutoTestParam (AirohaService.ExtraKey key, File tmpFile) {
        gLogger.d(TAG, "need to override this function");
    }

    protected void showInvalidBinPathDialog(){
        showAlertDialog(mActivity, "Error",
                "Invalid File Path!\n" +
                        "Please click the Menu on the upper-left corner of UI first" +
                        ", click the local machine storage (usually labeled as the phone model name) to open the internal storage directory" +
                        ", and then choose the target bin file.");
    }

    protected void showInvalidMusicPathDialog(){
        showAlertDialog(mActivity, "Error",
                "Invalid File Path!\n" +
                        "Please click the Menu on the upper-left corner of UI first" +
                        ", click the local machine storage (usually labeled as the phone model name) to open the internal storage directory" +
                        ", and then choose the target music file.");
    }
}
