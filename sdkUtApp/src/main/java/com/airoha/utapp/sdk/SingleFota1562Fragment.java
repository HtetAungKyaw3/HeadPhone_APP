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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.airoha.sdk.AB1562FotaControl;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaBatteryInfo;
import com.airoha.sdk.api.ota.AirohaFOTAControl;
import com.airoha.sdk.api.ota.FotaInfo;
import com.airoha.sdk.api.ota.FotaSettings;
import com.airoha.sdk.api.ota.RofsInfo;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.sdk.api.utils.ChipType;
import com.airoha.sdk.api.utils.DeviceRole;
import com.airoha.sdk.api.utils.FotaStatus;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.LinkedList;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SingleFota1562Fragment extends BaseFragment {
    private String TAG = SingleFota1562Fragment.class.getSimpleName();
    private String LOG_TAG = "[FOTA] ";
    private View mView;
    final String channelID = "Airoha";
    final int notifyID = 56;

    private LinearLayout mLinearLayout1562AReadyUpdateFS;
    private LinearLayout mLinearLayoutFotaRofsImg;
    private LinearLayout mLinearLayoutRofsVersion;
    private LinearLayout mLinearLayoutFotaBin;
    private LinearLayout mLinearLayoutRofsBin;

    private static final int REQUEST_CHOOSE_FW_BIN = 11;
    private static final int REQUEST_CHOOSE_ROFS_IMG = 21;
    private static final int REQUEST_CHOOSE_ROFS_BIN = 33;

    private TextView mTextStatus;
    private TextView mTextOtaError;

    private FilePickerDialog mFwFilePickerDialog;
    private Button mBtnFwBinFilePicker;
    private EditText mEditFwBinPath;

    private FilePickerDialog mRofsFilePickerDialog;
    private Button mBtnRofsBinFilePicker;
    private EditText mEditRofsBinPath;

    private Button mBtnRofsImgFilePicker;
//    private FilePickerDialog mFilesystemImgFilePickerDialog;
    private EditText mEditRofsImgPath;

    private Button mBtn_RequestDFU;
    private Button mBtn_RequestRofsVersion;
    private Button mBtn_Start;
    private Button mBtn_Cancel;
    private Button mBtn_Commit;

    private String mSelectedFotaBinFileName;
    private String mSelectedRofsBinFileName;
    private String mSelectedRofsImgFileName;

    private CheckBox mChkAdaptiveMode;
    private EditText mEditBatteryThreshold;

    private TextView mTextFlashSize;
    private TextView mTextViewFwVersion;
    private TextView mTextViewFsVersion;
    private TextView mTextViewBatteryLevel;
    private TextView mTextIsReadyToUpdateFS;

    private RadioGroup mRadioGroup;
    private RadioButton mRadioButtonOS;
    private RadioButton mRadioButtonFS;

    private LinkedList<View> mSettingsViewList;

//    private boolean mIsRebooting = false;
//    private boolean mIsUpdateFsAvailable =false;

    private int mBatteryThreshold = 60;
    private boolean mIsFotaRunning = false;
    private FotaInfo mFotaInfo;

    public SingleFota1562Fragment(){
        mTitle = "FOTA - Single";
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_single_fota_1562, container,false);
        initUiMember();
        initFwFileDialog();
        initRofsFileDialog();
        //initPreferences();
        //mEditPrePollSize.setText(String.valueOf(mActivity.getAirohaService().getAirohaFotaMgrEx().getFotaStagePrePollSize()));

        if (AirohaService.gAutoTestBinPathFotaL.BinPath != null) {
            initAutoTestBinFile(AirohaService.ExtraKey.FOTA_BIN_PATH_L,
                    AirohaService.gAutoTestBinPathFotaL.BinPath);
        }

        if (AirohaService.gAutoTestBinPathRofsL.BinPath != null) {
            initAutoTestBinFile(AirohaService.ExtraKey.ROFS_BIN_PATH_L,
                    AirohaService.gAutoTestBinPathRofsL.BinPath);
        }

        if (AirohaService.gAutoTestBinPathFsImgL.BinPath != null) {
            initAutoTestBinFile(AirohaService.ExtraKey.FS_IMG_PATH_L,
                    AirohaService.gAutoTestBinPathFsImgL.BinPath);
        }

        return mView;
    }

    @Override
    protected void initAutoTestParam (AirohaService.ExtraKey key, File tmpFile) {
        if (key == AirohaService.ExtraKey.FOTA_BIN_PATH_L) {
            mSelectedFotaBinFileName = tmpFile.getAbsolutePath();
            mEditFwBinPath.setText(mSelectedFotaBinFileName);
        } else if (key == AirohaService.ExtraKey.ROFS_BIN_PATH_L) {
            mSelectedRofsBinFileName = tmpFile.getAbsolutePath();
            mEditRofsBinPath.setText(mSelectedRofsBinFileName);
        } else if (key == AirohaService.ExtraKey.FS_IMG_PATH_L) {
            mSelectedRofsImgFileName = tmpFile.getAbsolutePath();
            mEditRofsImgPath.setText(mSelectedRofsImgFileName);
        }
    }

    private void initUiMember(){
        mSettingsViewList = new LinkedList<>();

        mLinearLayoutFotaRofsImg = mView.findViewById(R.id.linearLayoutRofsImg);
        mLinearLayout1562AReadyUpdateFS = mView.findViewById(R.id.linearLayout1562A_readyUpdateFS);
        mLinearLayoutRofsVersion = mView.findViewById(R.id.linearLayoutRofsVersion);
        mLinearLayoutFotaBin = mView.findViewById(R.id.linearLayoutFotaBin);
        mLinearLayoutRofsBin = mView.findViewById(R.id.linearLayoutRofsBin);

        mEditFwBinPath = mView.findViewById(R.id.editTextFwBinPath);
        mEditRofsBinPath = mView.findViewById(R.id.editTextRofsBinPath);
        mEditRofsImgPath = mView.findViewById(R.id.editTextRofsImgPath);

        mEditBatteryThreshold = mView.findViewById(R.id.editText_batteryThreshold);
        mSettingsViewList.add(mEditBatteryThreshold);

        mTextStatus = mView.findViewById(R.id.textViewStatus);
        mTextOtaError = mView.findViewById(R.id.textViewError);

        mBtnFwBinFilePicker = mView.findViewById(R.id.buttonFwBinFilePicker);
        mBtnFwBinFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnFwBinFilePicker is clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showFileChooser(REQUEST_CHOOSE_FW_BIN);
                } else {
                    mFwFilePickerDialog.show();
                }
            }
        });


        mBtnRofsBinFilePicker = mView.findViewById(R.id.buttonRofsBinFilePicker);
        mBtnRofsBinFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnRofsBinFilePicker is clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showFileChooser(REQUEST_CHOOSE_ROFS_BIN);
                } else {
                    mRofsFilePickerDialog.show();
                }
            }
        });

        mBtnRofsImgFilePicker = mView.findViewById(R.id.buttonRofsImgFilePicker);
        mBtnRofsImgFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnRofsImgFilePicker is clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showFileChooser(REQUEST_CHOOSE_ROFS_IMG);
                }
            }
        });

        mBtn_RequestDFU = mView.findViewById(R.id.buttonRequestDFU);
        mBtn_RequestDFU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtn_RequestDFU is clicked");

                if(mEditBatteryThreshold.getText().toString().isEmpty()) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "Battery Threshold is invalid!");
                    return;
                }

                mTextStatus.setText("");
                mTextStatus.setText("");
                mTextOtaError.setText("");
                mTextFlashSize.setText("");
                mTextViewFwVersion.setText("");
                mTextViewFsVersion.setText("");
                mTextViewBatteryLevel.setText("");
                mTextIsReadyToUpdateFS.setText("");
                mBtnFwBinFilePicker.setEnabled(false);
                mBtnRofsBinFilePicker.setEnabled(false);

                mBtn_RequestDFU.setEnabled(false);
                mBtn_RequestRofsVersion.setEnabled(false);
                gLogger.d(TAG, "disable mBtn_Start");
                mBtn_Start.setEnabled(false);

                mTextStatus.setText("Request DFU info");
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Request DFU info");
                AirohaSDK.getInst().getAirohaDeviceControl().getBatteryInfo(new BatteryInfoListener());
            }
        });

        mBtn_RequestRofsVersion = mView.findViewById(R.id.button_RequestRofsVersion);
        mBtn_RequestRofsVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtn_RequestRofsVersion is clicked");

                mTextViewFsVersion.setText("");

                mTextStatus.setText("Request ROFS version");
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Request ROFS version");
                RofsInfo rofsInfo = ((AB1562FotaControl)AirohaSDK.getInst().getAirohaFotaControl()).getRofsVersion(DeviceRole.MASTER);
                if (rofsInfo == null) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "Failed in Request ROFS version");
                } else {
                    mTextViewFsVersion.setText(rofsInfo.getRofsVersion());
                }
            }
        });

        mTextStatus = mView.findViewById(R.id.textViewStatus);

        mTextFlashSize = mView.findViewById(R.id.textFlashSize);
        mTextViewFwVersion = mView.findViewById(R.id.textFwVersion);
        mTextViewFsVersion = mView.findViewById(R.id.textRofsVersion);
        mTextViewBatteryLevel = mView.findViewById(R.id.textAgentBattLevel);
        mTextIsReadyToUpdateFS = mView.findViewById(R.id.textView_ready_to_update_filesystem);

        mBtn_Start = mView.findViewById(R.id.btn_Start);
        mBtn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gTargetAddr.length() > 0) {
                    mBtn_RequestDFU.setEnabled(false);
                    mBtn_RequestRofsVersion.setEnabled(false);
                    gLogger.d(TAG, "disable mBtn_Start");
                    mBtn_Start.setEnabled(false);
                    mChkAdaptiveMode.setEnabled(false);
                    mEditBatteryThreshold.setEnabled(false);
                    mBtn_Cancel.setEnabled(true);

                    if (gFilePrinter != null) {
                        gLogger.removePrinter(gFilePrinter.getPrinterName());
                    }
                    gFilePrinter = createLogFile(gTargetAddr);

                    mTextIsReadyToUpdateFS.setText("false");
                    mTextOtaError.setText("");
                    mTextStatus.setText("Start");
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Start");

                    startConnectThread(gTargetAddr);
                }
            }
        });

        mChkAdaptiveMode = mView.findViewById(R.id.chkAdaptiveMode);
        mSettingsViewList.add(mChkAdaptiveMode);
        mChkAdaptiveMode.setChecked(false);
        mChkAdaptiveMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                gLogger.d(TAG, "mChkAdaptiveMode onCheckedChanged");
                if(b) {
                    mBtn_Commit.setVisibility(View.VISIBLE);
                } else {
                    mBtn_Commit.setVisibility(View.GONE);
                }
            }
        });

        mBtn_Cancel = mView.findViewById(R.id.btn_Cancel);
        mBtn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtn_Cancel is clicked");
                mBtn_Cancel.setEnabled(false);
                mBtn_Commit.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL,LOG_TAG + "cancel FOTA");
                AirohaSDK.getInst().getAirohaFotaControl().stopDataTransfer();
            }
        });

        mBtn_Commit = mView.findViewById(R.id.btn_Commit);
        mBtn_Commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtn_Commit is clicked");
                mActivity.updateMsg(MainActivity.MsgType.GENERAL,LOG_TAG + "FOTA committing...");

                mIsFotaRunning = true;
                mBtn_RequestDFU.setEnabled(false);
                mBtn_RequestRofsVersion.setEnabled(false);
                gLogger.d(TAG, "disable mBtn_Start");
                mBtn_Start.setEnabled(false);
                mBtn_Cancel.setEnabled(false);
                mBtn_Commit.setEnabled(false);
                mChkAdaptiveMode.setEnabled(false);
                mEditBatteryThreshold.setEnabled(false);

                final String level = mEditBatteryThreshold.getText().toString();
                if(level!=null && level.length() >0){
                    mBatteryThreshold = Integer.valueOf(level);
                }
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Commit");
                AirohaSDK.getInst().getAirohaFotaControl().applyNewFirmware(mBatteryThreshold);
                mTextStatus.setText("");
                mTextOtaError.setText("Commit");
            }
        });

        mRadioGroup = mView.findViewById(R.id.radioGroupTwsFota);
        mRadioButtonOS = mView.findViewById(R.id.radioBtnTwsBinOS);
        mSettingsViewList.add(mRadioButtonOS);
        mRadioButtonFS = mView.findViewById(R.id.radioBtnTwsBinFS);
        mSettingsViewList.add(mRadioButtonFS);

        mRadioButtonOS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gLogger.d(TAG, "mRadioButtonOS is clicked");
                    mBtn_Start.setText("START FOTA");
                    mLinearLayoutFotaBin.setVisibility(View.VISIBLE);
                    mLinearLayoutRofsBin.setVisibility(View.GONE);
                    if (!mEditFwBinPath.getText().toString().isEmpty()){
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                }
            }
        });
        mRadioButtonFS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gLogger.d(TAG, "mRadioButtonFS is clicked");
                    mBtn_Start.setText("START ROFS UPDATE");
                    mLinearLayoutFotaBin.setVisibility(View.GONE);
                    mLinearLayoutRofsBin.setVisibility(View.VISIBLE);
                    if (!mEditRofsBinPath.getText().toString().isEmpty()){
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        gLogger.d(TAG, "onActivityResult: requestCode= " + requestCode
        + ", resultCode= " + resultCode);
        if (resultCode != Activity.RESULT_OK || data == null) {
            showInvalidBinPathDialog();
            return;
        }
        switch (requestCode) {
            case REQUEST_CHOOSE_FW_BIN:
            case REQUEST_CHOOSE_ROFS_BIN:
            case REQUEST_CHOOSE_ROFS_IMG:
                Uri uri = data.getData();
                String path = uri.getPath();
                if (path != null && path.length() > 0) {
                    gLogger.d(TAG, "uri.getPath() = " + path);
                    File tmpFile = uriToFileApiQ(uri);
                    if (tmpFile == null) {
                        showInvalidBinPathDialog();
                        return;
                    }
                    mTextOtaError.setText("");

                    if (!updateBinFilePathToUI(requestCode, tmpFile)) {
                        return;
                    }

                    if (mFotaInfo.getFlashSize() == FotaInfo.FlashSizeEnum.FLASH_2M) {
                        if (mFotaInfo != null && mSelectedFotaBinFileName != null && mSelectedRofsImgFileName != null) {
                            gLogger.d(TAG, "enable mBtn_Start");
                            mBtn_Start.setEnabled(true);
                        }
                    } else if (mFotaInfo != null &&
                            (mSelectedFotaBinFileName != null || mSelectedRofsBinFileName != null)) {
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                } else {
                    showInvalidBinPathDialog();
                }
                break;
        }
    }

    boolean updateBinFilePathToUI(int requestCode, File tmpFile){
        boolean ret = false;
        String tmpFilePath = tmpFile.getAbsolutePath();
        switch (requestCode) {
            case REQUEST_CHOOSE_FW_BIN:
                if (tmpFilePath.contains("FotaPackage")) {
                    mSelectedFotaBinFileName = tmpFilePath;
                    mEditFwBinPath.setText(mSelectedFotaBinFileName);
                    ret = true;
                } else {
                    mSelectedFotaBinFileName = null;
                    mEditFwBinPath.setText("");
                    showAlertDialog(mActivity, "Error", "The file name of FW Bin should contain the keyword: \"FotaPackage\"");
                }
                break;
            case REQUEST_CHOOSE_ROFS_BIN:
                if (tmpFilePath.contains("FileSystemPackage")) {
                    mSelectedRofsBinFileName = tmpFilePath;
                    mEditRofsBinPath.setText(mSelectedRofsBinFileName);
                    ret = true;
                } else {
                    mSelectedRofsBinFileName = null;
                    mEditRofsBinPath.setText("");
                    showAlertDialog(mActivity, "Error", "The file name of FW Bin should contain the keyword: \"FileSystemPackage\"");
                }
                break;
            case REQUEST_CHOOSE_ROFS_IMG:
                if (tmpFilePath.contains("FileSystemImage")) {
                    mSelectedRofsImgFileName = tmpFilePath;
                    mEditRofsImgPath.setText(mSelectedRofsImgFileName);
                } else {
                    mSelectedRofsImgFileName = null;
                    mEditRofsImgPath.setText("");
                    showAlertDialog(mActivity, "Error", "The file name of filesystem bin should contain the keyword: \"FileSystemImage\"");
                }
                break;
        }
        return ret;
    }

    private void startConnectThread(final String bdAddr) {
        mTextOtaError.setText("");
        mTextStatus.setText("");

        final boolean isBackground = mChkAdaptiveMode.isChecked();
        final String level = mEditBatteryThreshold.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(level!=null && level.length() >0){
                        mBatteryThreshold = Integer.valueOf(level);
                    }

                    AirohaFOTAControl fotaControl = AirohaSDK.getInst().getAirohaFotaControl();
//                    if(mFotaInfo.isReadyToUpdateFileSystem()) {
//                        FotaSettings fotaSettings = new FotaSettings(
//                                FotaSettings.FotaTypeEnum.SpecialUpgrade,
//                                FotaSettings.FotaTargetEnum.Single,
//                                mSelectedFotaBinFileName,
//                                mSelectedFotaBinFileName);
//                        fotaSettings.setBackgroundFOTA(isBackground);
//                        fotaSettings.setBatteryLevelThrd(mBatteryThreshold);
//                        fotaControl.startDataTransfer(fotaSettings, mStatusListener);
//                        mIsFotaRunning = true;
//                    } else {

                    FotaSettings fotaSettings;
                    if (mFotaInfo.getFlashSize() == FotaInfo.FlashSizeEnum.FLASH_2M
                     || mRadioButtonOS.isChecked()) {
                        fotaSettings = new FotaSettings(
                                FotaSettings.FotaTypeEnum.Typical,
                                FotaSettings.FotaTargetEnum.Single,
                                mSelectedFotaBinFileName,
                                mSelectedFotaBinFileName);
                    } else {
                        fotaSettings = new FotaSettings(
                                FotaSettings.FotaTypeEnum.Typical,
                                FotaSettings.FotaTargetEnum.Single,
                                mSelectedRofsBinFileName,
                                mSelectedRofsBinFileName);
                    }
                    fotaSettings.setBackgroundFOTA(isBackground);
                    fotaSettings.setBatteryLevelThrd(mBatteryThreshold);
                    /// In this sample code, we use the registerOTAStatusListener/unregisterOTAStatusListener instead of using the parameter
                    fotaControl.startDataTransfer(fotaSettings, null);
                    mIsFotaRunning = true;
//                    }
                }catch (final Exception e){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextOtaError.setText(e.getMessage());
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, e.getMessage());
                            delayedEnableAllBtn(false);
                        }
                    });
                }
            }
        }).start();
    }

    class RequestDfuThread extends Thread {
        @Override
        public void run() {
            gLogger.d(TAG, "RequestDfuThread start");
            AirohaFOTAControl fotaControl = AirohaSDK.getInst().getAirohaFotaControl();
            mFotaInfo = fotaControl.requestDFUInfo(FotaSettings.FotaTargetEnum.Single);
            if (mFotaInfo == null) {
                gLogger.e(TAG, "mFotaInfo == null");
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mFotaInfo != null && mFotaInfo.getFwVersion() != null) {
                        mTextFlashSize.setText(mFotaInfo.getFlashSize().toString());
                        mTextViewFwVersion.setText(mFotaInfo.getFwVersion());
                        mTextIsReadyToUpdateFS.setText("" + mFotaInfo.isReadyToUpdateFileSystem());
                        mTextStatus.setText("");
                        if (mFotaInfo.getFlashSize() == FotaInfo.FlashSizeEnum.FLASH_2M) {
                            gLogger.d(TAG, "flash size is 2M");
                            mRadioGroup.setVisibility(View.GONE);
                            mBtn_RequestRofsVersion.setVisibility(View.GONE);
                            mLinearLayoutRofsVersion.setVisibility(View.GONE);
                            mLinearLayoutFotaRofsImg.setVisibility(View.VISIBLE);
                            mLinearLayout1562AReadyUpdateFS.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                mBtnRofsImgFilePicker.setVisibility(View.VISIBLE);
                            }
                            if (!mEditFwBinPath.getText().toString().isEmpty()
                                    && !mEditRofsImgPath.getText().toString().isEmpty()) {
                                gLogger.d(TAG, "enable mBtn_Start");
                                mBtn_Start.setEnabled(true);
                            }
                        } else {
                            gLogger.d(TAG, "flash size is 4M");
                            mBtn_RequestRofsVersion.setVisibility(View.VISIBLE);
                            mLinearLayoutRofsVersion.setVisibility(View.VISIBLE);
                            mLinearLayoutFotaRofsImg.setVisibility(View.GONE);
                            mLinearLayout1562AReadyUpdateFS.setVisibility(View.GONE);

                            if (AirohaSDK.getInst().getChipType() == ChipType.AB1562) {
                                mRadioGroup.setVisibility(View.VISIBLE);
                                mBtnRofsBinFilePicker.setEnabled(true);
                            }

                            if (mRadioButtonOS.isChecked() && !mEditFwBinPath.getText().toString().isEmpty()){
                                gLogger.d(TAG, "enable mBtn_Start");
                                mBtn_Start.setEnabled(true);
                            }

                            if (mRadioButtonFS.isChecked() && !mEditRofsBinPath.getText().toString().isEmpty()){
                                gLogger.d(TAG, "enable mBtn_Start");
                                mBtn_Start.setEnabled(true);
                            }

                            if (!mRadioButtonFS.isChecked()) {
                                mRadioButtonOS.performClick();
                            } else {
                                mRadioButtonFS.performClick();
                            }
                        }
                        mBtnFwBinFilePicker.setEnabled(true);
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Request DFU info done");
                    } else {
                        if (mFotaInfo == null) {
                            gLogger.e(TAG, "mFotaInfo == null");
                        }
                        if (mFotaInfo.getFwVersion() == null) {
                            gLogger.e(TAG, "mFotaInfo.getFwVersion() == null");
                        }
                        mTextOtaError.setText("Failed in Request DFU");
                        mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "Failed in Request DFU");
                    }
                    mBtn_RequestDFU.setEnabled(true);
                    mBtn_RequestRofsVersion.setEnabled(true);
                }
            });
        }
    }

    private void initFwFileDialog() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"bin", "BIN", "ext"};
        mFwFilePickerDialog = new FilePickerDialog(mActivity, properties);
        mFwFilePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    mTextOtaError.setText("");
                    mSelectedFotaBinFileName = files[0];
                    if (mSelectedFotaBinFileName != null && !mSelectedFotaBinFileName.contains("FotaPackage")) {
                        mEditFwBinPath.setText("");
                        showAlertDialog(mActivity, "Error", "The file name of FW Bin should contain the keyword: \"FotaPackage\"");
                        return;
                    }
                    mEditFwBinPath.setText(mSelectedFotaBinFileName);
                    if (mFotaInfo.getFlashSize() == FotaInfo.FlashSizeEnum.FLASH_2M)
                    {
                        if(!checkRofsNaming(mSelectedFotaBinFileName))
                        {
                            mEditRofsImgPath.setText("");
                            return;
                        }
                        mSelectedRofsImgFileName = mSelectedFotaBinFileName.replace("FotaPackage", "FileSystemImage");
                        if (mSelectedRofsImgFileName != null && !mSelectedRofsImgFileName.contains("FileSystemImage")) {
                            mEditRofsImgPath.setText("");
                            showAlertDialog(mActivity, "Error", "The file name of filesystem bin should contain the keyword: \"FileSystemImage\"");
                            return;
                        }
                        mEditRofsImgPath.setText(mSelectedRofsImgFileName);

                        if (mFotaInfo != null && mSelectedFotaBinFileName != null && mSelectedRofsImgFileName != null) {
                            gLogger.d(TAG, "enable mBtn_Start");
                            mBtn_Start.setEnabled(true);
                        }
                    } else if (mFotaInfo != null && mSelectedFotaBinFileName != null) {
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                }
            }
        });
    }

    private void initRofsFileDialog() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"bin", "BIN", "ext"};
        mRofsFilePickerDialog = new FilePickerDialog(mActivity, properties);
        mRofsFilePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    mSelectedRofsBinFileName = files[0];
                    mEditRofsBinPath.setText(mSelectedRofsBinFileName);
                    if (mTextViewFwVersion.getText().toString().length() > 0) {
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                }
            }
        });
    }

//    private void initFilesystemFileDialog() {
//        DialogProperties properties = new DialogProperties();
//        properties.selection_mode = DialogConfigs.SINGLE_MODE;
//        properties.selection_type = DialogConfigs.FILE_SELECT;
//        properties.root = new File(DialogConfigs.DEFAULT_DIR);
//        properties.extensions = new String[]{"bin", "BIN", "ext"};
//        mFilesystemImgFilePickerDialog = new FilePickerDialog(mActivity, properties);
//        mFilesystemImgFilePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
//            @Override
//            public void onSelectedFilePaths(String[] files) {
//                if (files != null && files.length > 0) {
//                    mTextOtaError.setText("");
//                    mSelectedFilesystemBinFileName = files[0].toString();
//                    mEditFilesystemImgPath.setText(mSelectedFilesystemBinFileName);
//                    if (mFotaInfo != null && mSelectedFotaBinFileName != null && mSelectedFilesystemBinFileName != null) {
//                        mBtn_Start.setEnabled(true);
//                    }
//                }
//            }
//        });
//    }

    private boolean checkRofsNaming(String select_path) {
        boolean isExist = true;
        String findFileSystemPath = select_path.replace("FotaPackage", "FileSystemImage");
        File file = new File(findFileSystemPath);
        if(!file.exists()){
            String msg = findFileSystemPath + " not found!";
            mTextOtaError.setText(msg);
            showAlertDialog(mActivity, "Error", msg);
            isExist = false;
        }
        return isExist;
    }

//    private void initPreferences() {
//        SharedPreferences preference = mActivity.getPreferences(Context.MODE_PRIVATE);
////        mTextViewSppAddr.setText(preference.getString("SppAddr", "66:55:44:33:22:11"));
//        //mEditRespTimeout.setText(preference.getString("RespTimeout", "30000"));
//    }

    @Override
    public void onResume() {
        gLogger.d(TAG, "onResume");
        super.onResume();
        if (!mIsFotaRunning) {
            addAllListener();
        }
    }

    @Override
    public void onPause() {
        gLogger.d(TAG, "onPause");
        super.onPause();
        if (!mIsFotaRunning) {
            removeAllListener();
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
        gLogger.d(TAG, "onHiddenChanged");
        super.onHiddenChanged(hidden);
        if (!mIsFotaRunning) {
            if (hidden) {
                removeAllListener();
            } else {
                addAllListener();
            }
        }
    }

    private void delayedEnableAllBtn(final boolean isEnableStartBtn){
        Handler handler = new Handler(mActivity.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gLogger.d(TAG, "delayedEnableAllBtn: isEnableStartBtn= " + isEnableStartBtn);
                mBtn_Cancel.setEnabled(false);
                mBtn_Commit.setEnabled(false);
                mBtn_Start.setEnabled(isEnableStartBtn);
                mBtn_RequestDFU.setEnabled(true);
                mBtn_RequestRofsVersion.setEnabled(true);
                mChkAdaptiveMode.setEnabled(true);
                mEditBatteryThreshold.setEnabled(true);
            }
        }, 3000);
    }

    private String getChannelSettingStr(byte val){
        String rtn = "NOT DEFINED";
        switch(val)
        {
            case 0x00:
                rtn = "AU_DSP_CH_LR";
                break;
            case 0x01:
                rtn = "AU_DSP_CH_L";
                break;
            case 0x02:
                rtn = "AU_DSP_CH_R";
                break;
            case 0x03:
                rtn = "AU_DSP_CH_SWAP";
                break;
            case 0x04:
                rtn = "AU_DSP_CH_MIX";
                break;
            case 0x05:
                rtn = "AU_DSP_CH_MIX_SHIFT";
                break;
        }
        return rtn;
    }

    void showNotification(String content) {

        NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(
                NOTIFICATION_SERVICE);

        final Intent intent =  new Intent(mActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final int flags = PendingIntent.FLAG_IMMUTABLE;
        final PendingIntent pendingIntent = PendingIntent.getActivity(mActivity.getApplicationContext(), 0, intent, flags);

        Notification notification = new NotificationCompat.Builder(mActivity, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Layfictone FOTA")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .build();

        notificationManager.notify(notifyID, notification);
    }

    AirohaFOTAControl.AirohaFOTAStatusListener mStatusListener = new AirohaFOTAControl.AirohaFOTAStatusListener() {
        void delayedRequestDfu(final int delayMs) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(delayMs);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                    AirohaSDK.getInst().getAirohaDeviceControl().getBatteryInfo(new BatteryInfoListener());
                    RequestDfuThread requestDfuThread = new RequestDfuThread();
                    requestDfuThread.start();
                }
            }).start();
        }

        void delayedCommit(final int delayMs) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(delayMs);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBtn_Commit.performClick();
                        }
                    });
                }
            }).start();
        }

        @Override
        public void onFotaStatusChanged(final FotaStatus newStatus) {
            gLogger.d(TAG, "onFotaStatusChanged: " + newStatus.getName());
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String statusMsg = newStatus.getName();
                    switch (newStatus) {
                        case STATUS_STARTED:
                            break;
                        case STATUS_REBOOT:
                            if (mIsFotaRunning) {
                                mBtn_Cancel.setEnabled(true);
                                if (!mChkAdaptiveMode.isChecked()) {
                                    delayedCommit(1000);
                                } else {
                                    mBtn_Commit.setEnabled(true);
                                    statusMsg = "Transformation is complete, click commit to reboot device.";
                                }
                            }
                            break;
                        case STATUS_SUCCEED:
                            statusMsg = "FOTA Succeed";
                            mIsFotaRunning = false;
                            delayedEnableAllBtn(false);
                            mTextOtaError.setText("");
                            mTextFlashSize.setText("");
                            mTextViewFwVersion.setText("");
                            mTextViewFsVersion.setText("");
                            mTextViewBatteryLevel.setText("");
                            mTextIsReadyToUpdateFS.setText("");
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + statusMsg);
//                            delayedRequestDfu(1000);
                            break;
                        case USER_CANCELLED:
                        case BATTERY_LOW:
                            if (!mIsFotaRunning) {
                                gLogger.d(TAG, "disable mBtn_Start");
                                mBtn_Start.setEnabled(false);
                                return;
                            }
                            statusMsg = "FOTA Cancelled";
                            mIsFotaRunning = false;
                            mBtn_Cancel.setEnabled(false);
                            mBtn_Commit.setEnabled(false);
                            delayedEnableAllBtn(false);
                            mTextOtaError.setText(newStatus.getName());
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + newStatus.getName());
                            break;
                        case STATUS_AUTO_REBOOT:
                            mBtn_Cancel.setEnabled(false);
                            break;
                        case STATUS_READY_TO_UPDATE_FILESYSTEM:
                            if (mIsFotaRunning) {
                                mBtn_Cancel.setEnabled(true);
                                mTextOtaError.setText("");
                            }
                            mTextIsReadyToUpdateFS.setText("true");
                            break;
                        default:
                            mTextOtaError.setText(newStatus.getName());
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + newStatus.getName());
                            if (!mIsFotaRunning) {
                                gLogger.d(TAG, "disable mBtn_Start");
                                mBtn_Start.setEnabled(false);
                                return;
                            }
                            statusMsg = "FOTA Failed";
                            mIsFotaRunning = false;
                            mBtn_Cancel.setEnabled(false);
                            mBtn_Commit.setEnabled(false);
                            delayedEnableAllBtn(false);
                            Handler handler = new Handler(mActivity.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showErrorMsg(mActivity);
                                }
                            }, 100);
                            break;
                    }
                    mTextStatus.setText(statusMsg);
                    showNotification(statusMsg);
                }
            });
        }

        @Override
        public void onFotaProgressChanged(final int progress) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextStatus.setText(String.format("Progress : %d", progress));
                }
            });
        }

    };

    class BatteryInfoListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            if (msg != null) {
                                AirohaBatteryInfo batteryInfo = (AirohaBatteryInfo) msg.getMsgContent();
                                mTextViewBatteryLevel.setText("" + batteryInfo.getMasterLevel());
                                RequestDfuThread requestDfuThread = new RequestDfuThread();
                                requestDfuThread.start();
                            }
                        }
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }

                    mBtn_RequestDFU.setEnabled(true);
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }

    void removeAllListener() {
        if (mActivity.getAirohaService() != null && AirohaSDK.getInst().getAirohaFotaControl() != null) {
            gLogger.d(TAG, "removeAllListener");
            AirohaSDK.getInst().getAirohaFotaControl().unregisterOTAStatusListener(mStatusListener);
        }
    }

    void addAllListener() {
        if (mActivity.getAirohaService() != null && AirohaSDK.getInst().getAirohaFotaControl() != null) {
            gLogger.d(TAG, "addAllListener");
            AirohaSDK.getInst().getAirohaFotaControl().registerOTAStatusListener(mStatusListener);
        }
    }

}