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
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaBatteryInfo;
import com.airoha.sdk.api.ota.AirohaFOTAControl;
import com.airoha.sdk.api.ota.FotaInfo;
import com.airoha.sdk.api.ota.FotaSettings;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.sdk.api.utils.ChipType;
import com.airoha.sdk.api.utils.FotaStatus;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import static android.content.Context.NOTIFICATION_SERVICE;

public class TwsFotaFragment extends BaseFragment {
    private String TAG = TwsFotaFragment.class.getSimpleName();
    private String LOG_TAG = "[FOTA] ";
    private View mView;
    final String channelID = "Airoha";
    final int notifyID = 56;

    TelephonyManager mTelephonyManager;
    MyPhoneStateListener mPhoneStateListener;

    //    private TextView mTextViewModel;
//    private TextView mTextViewAgentChannel;
//    private TextView mTextViewAgentFotaState;
//    private TextView mTextViewAgentFotaStateDesc;
    private TextView mTextViewAgentFwVersion;
    private TextView mTextViewAgentBatteryLevel;
//    private TextView mTextViewPartnerFotaState;
//    private TextView mTextViewPartnerFotaStateDesc;
    private TextView mTextViewPartnerFwVersion;
    private TextView mTextViewPartnerBatteryLevel;

    private TextView mTextOtaStatus;
    private TextView mTextOtaError;

    private static final int REQUEST_CHOOSE_FW_BIN = 11;
    private static final int REQUEST_CHOOSE_ROFS_BIN = 33;

    // Interaction UI
    private FilePickerDialog mFwFilePickerDialog;
    private EditText mEditFwBinPath;
    private Button mBtnFwBinFilePicker;

    private FilePickerDialog mRofsFilePickerDialog;
    private EditText mEditRofsBinPath;
    private Button mBtnRofsBinFilePicker;

//    private LinearLayout mLinearLayoutFwBin;
    private LinearLayout mLinearLayoutRofsBin;

    private RadioButton mRadioBtnFwBin;
    private RadioButton mRadioBtnRofsBin;

    private Button mBtn_RequestDFU;
    private Button mBtn_Start;
    private Button mBtn_Commit;
    private Button mBtn_Cancel;

    private String mSelectedFwBinFileName;
    private String mSelectedRofsBinFileName;
    private String mUpdatingBinFileName;

    private String[] FOTA_MODE_ARRAY;
    private Spinner mSpinnerFotaMode;

    private EditText mEditBatteryThrdLevel;
    private int mBatteryLevelThrd = 60;
    private boolean mIsFotaRunning = false;
    private FotaInfo mFotaInfo;

    public TwsFotaFragment(){
        mTitle = "FOTA - TWS Mode";
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
        mView = inflater.inflate(R.layout.fragment_tws_fota, container,false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(
                    NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, "FOTA", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Error");
            channel.enableLights(true);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        initUiMember();
        initFwFileDialog();
        initRofsFileDialog();
        initTelMgr();

        mRadioBtnFwBin.performClick();

        if (AirohaService.gAutoTestBinPathFotaL.BinPath != null) {
            initAutoTestBinFile(AirohaService.ExtraKey.FOTA_BIN_PATH_L,
                    AirohaService.gAutoTestBinPathFotaL.BinPath);
        }

        if (AirohaService.gAutoTestBinPathRofsL.BinPath != null) {
            initAutoTestBinFile(AirohaService.ExtraKey.ROFS_BIN_PATH_L,
                    AirohaService.gAutoTestBinPathRofsL.BinPath);
        }

        return mView;
    }

    @Override
    protected void initAutoTestParam (AirohaService.ExtraKey key, File tmpFile) {
        if (key == AirohaService.ExtraKey.FOTA_BIN_PATH_L) {
            mSelectedFwBinFileName = tmpFile.getAbsolutePath();
            mEditFwBinPath.setText(mSelectedFwBinFileName);
        } else if (key == AirohaService.ExtraKey.ROFS_BIN_PATH_L) {
            mSelectedRofsBinFileName = tmpFile.getAbsolutePath();
            mEditRofsBinPath.setText(mSelectedRofsBinFileName);
        }
    }

    private void initUiMember() {
        mBtnFwBinFilePicker = mView.findViewById(R.id.buttonFwBinFilePicker);
        mEditFwBinPath = mView.findViewById(R.id.editTextFwBinPath);

        mBtnRofsBinFilePicker = mView.findViewById(R.id.buttonRofsBinFilePicker);
        mEditRofsBinPath = mView.findViewById(R.id.editTextRofsBinPath);

//        mTextViewModel = mView.findViewById(R.id.textView_tws_model);
//        mTextViewAgentChannel = mView.findViewById(R.id.textView_tws_agent_channel);
//        mTextViewAgentFotaState = mView.findViewById(R.id.textView_tws_agent_fota_state);
//        mTextViewAgentFotaStateDesc = mView.findViewById(R.id.textView_tws_agent_fota_state_desc);
        mTextViewAgentFwVersion = mView.findViewById(R.id.textView_tws_agent_fw_version);
        mTextViewAgentBatteryLevel = mView.findViewById(R.id.textView_tws_agent_battery_level);
//        mTextViewPartnerFotaState = mView.findViewById(R.id.textView_tws_partner_fota_state);
//        mTextViewPartnerFotaStateDesc = mView.findViewById(R.id.textView_tws_partner_fota_state_desc);
        mTextViewPartnerFwVersion = mView.findViewById(R.id.textView_tws_partner_fw_version);
        mTextViewPartnerBatteryLevel = mView.findViewById(R.id.textView_tws_partner_battery_level);

        mTextOtaStatus = mView.findViewById(R.id.textViewStatus);
        mTextOtaError = mView.findViewById(R.id.textViewError);

        mBtn_RequestDFU = mView.findViewById(R.id.buttonRequestDFU);
        mBtn_RequestDFU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtn_RequestDFU clicked");
                gLogger.d(TAG, "disable mBtn_Start");
                mBtn_Start.setEnabled(false);
//                mTextViewAgentChannel.setText("");
                mTextViewAgentFwVersion.setText("");
                mTextViewAgentBatteryLevel.setText("");
                mTextViewPartnerFwVersion.setText("");
                mTextViewPartnerBatteryLevel.setText("");
                mTextOtaError.setText("");
                mTextOtaStatus.setText("Request DFU info");
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Request DFU info");
                AirohaSDK.getInst().getAirohaDeviceControl().getBatteryInfo(new BatteryInfoListener());
                RequestDfuThread requestDfuThread = new RequestDfuThread();
                requestDfuThread.start();
            }
        });

        mBtn_Start =  mView.findViewById(R.id.buttonConSpp);
        mBtn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtn_Start clicked");
                if (mRadioBtnFwBin.isChecked() && mEditFwBinPath.getText().toString().length() == 0) {
                    mTextOtaError.setText("FW Bin file path is invalid");
                    return;
                }
                if (mRadioBtnRofsBin.isChecked() && mEditRofsBinPath.getText().toString().length() == 0) {
                    mTextOtaError.setText("ROFS Bin file path is invalid");
                    return;
                }

                if(gTargetAddr.length() > 0) {
                    mRadioBtnFwBin.setEnabled(false);
                    mRadioBtnRofsBin.setEnabled(false);
                    mBtnFwBinFilePicker.setEnabled(false);
                    mBtnRofsBinFilePicker.setEnabled(false);
                    gLogger.d(TAG, "disable mBtn_Start");
                    mBtn_Start.setEnabled(false);
                    mSpinnerFotaMode.setEnabled(false);
                    mEditBatteryThrdLevel.setEnabled(false);
                    mBtn_Cancel.setEnabled(true);

                    if (gFilePrinter != null) {
                        gLogger.removePrinter(gFilePrinter.getPrinterName());
                    }
                    gFilePrinter = createLogFile(gTargetAddr);

                    mTextOtaError.setText("");
                    mTextOtaStatus.setText("Starting...");
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Start");

                    startConnectThread(gTargetAddr);
                }
            }
        });

//        mBtnDisConSpp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mActivity.getAirohaService().getAirohaFotaMgr1568().removeAirohaFotaListener(TAG);
//                mActivity.getAirohaService().getAirohaFotaMgr1568().close();
//                mActivity.changeFragment(MENU);
//            }
//        });


        mBtnFwBinFilePicker = mView.findViewById(R.id.buttonFwBinFilePicker);
        mBtnFwBinFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnFwBinFilePicker clicked");
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
                gLogger.d(TAG, "mBtnRofsBinFilePicker clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showFileChooser(REQUEST_CHOOSE_ROFS_BIN);
                } else {
                    mRofsFilePickerDialog.show();
                }
            }
        });

        mLinearLayoutRofsBin = mView.findViewById(R.id.linearLayoutMcsyncBinFS);
        if (AirohaSDK.getInst().getChipType() == ChipType.AB155x) {
            mLinearLayoutRofsBin.setVisibility(View.GONE);
        }

        mRadioBtnFwBin = mView.findViewById(R.id.radioBtnMcsyncBinOS);
        mRadioBtnFwBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mRadioBtnFwBin clicked");
                mRadioBtnFwBin.setChecked(true);
                mRadioBtnRofsBin.setChecked(false);
                mBtnFwBinFilePicker.setEnabled(true);
                mBtnRofsBinFilePicker.setEnabled(false);
            }
        });
        mRadioBtnRofsBin = mView.findViewById(R.id.radioBtnMcsyncBinFS);
        mRadioBtnRofsBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mRadioBtnRofsBin clicked");
                mRadioBtnFwBin.setChecked(false);
                mRadioBtnRofsBin.setChecked(true);
                mBtnFwBinFilePicker.setEnabled(false);
                mBtnRofsBinFilePicker.setEnabled(true);
            }
        });

        mBtn_Commit = mView.findViewById(R.id.btnCommit);
        mBtn_Commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnTwsCommit clicked");
                mBtn_Commit.setEnabled(false);
                mBtn_Cancel.setEnabled(false);
                AirohaSDK.getInst().getAirohaFotaControl().applyNewFirmware(mBatteryLevelThrd);
                mTextOtaStatus.setText("");
                mTextOtaError.setText("Commit");
            }
        });

        mBtn_Cancel = mView.findViewById(R.id.btn_Cancel);
        mBtn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnCancel clicked");
                mBtn_Cancel.setEnabled(false);
                mBtn_Commit.setEnabled(false);
                AirohaSDK.getInst().getAirohaFotaControl().stopDataTransfer();
            }
        });

        mEditBatteryThrdLevel = mView.findViewById(R.id.editBatteryThrdLevel);

        if (AirohaSDK.getInst().getChipType() == ChipType.AB1568
                || AirohaSDK.getInst().mChipType == ChipType.AB1568_V3) {
            FOTA_MODE_ARRAY = new String[]{ "Active", "Background", "Adaptive"};
        } else if (AirohaSDK.getInst().getChipType() == ChipType.AB158x
        || AirohaSDK.getInst().getChipType() == ChipType.AB157x) {
            FOTA_MODE_ARRAY = new String[]{ "Active", "Adaptive"};
        } else {
            FOTA_MODE_ARRAY = new String[]{ "Active", "Background"};
        }

        mSpinnerFotaMode = mView.findViewById(R.id.spinner_fota_mode);
        mSpinnerFotaMode.setAdapter(
                new ArrayAdapter<>(mActivity, R.layout.spinner_item, FOTA_MODE_ARRAY));
        mSpinnerFotaMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position == 0) {
                    mBtn_Commit.setVisibility(View.GONE);
                } else {
                    mBtn_Commit.setVisibility(View.VISIBLE);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
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
                    mSelectedFwBinFileName = files[0].toString();
                    mEditFwBinPath.setText(mSelectedFwBinFileName);
                    if (mTextViewAgentFwVersion.getText().toString().length() > 0) {
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
                    mSelectedRofsBinFileName = files[0].toString();
                    mEditRofsBinPath.setText(mSelectedRofsBinFileName);
                    if (mTextViewAgentFwVersion.getText().toString().length() > 0) {
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                }
            }
        });
    }

    private void initTelMgr(){
        mTelephonyManager = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            mPhoneStateListener = new MyPhoneStateListener();
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        checkPhoneState(mActivity);
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
                    if (requestCode == REQUEST_CHOOSE_FW_BIN) {
                        mSelectedFwBinFileName = tmpFile.getAbsolutePath();
                        mEditFwBinPath.setText(mSelectedFwBinFileName);
                    } else {
                        mSelectedRofsBinFileName = tmpFile.getAbsolutePath();
                        mEditRofsBinPath.setText(mSelectedRofsBinFileName);
                    }
                    if (mFotaInfo != null) {
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                } else {
                    showInvalidBinPathDialog();
                }
                break;
        }
    }

    class RequestDfuThread extends Thread {
        @Override
        public void run() {
            AirohaFOTAControl fotaControl = AirohaSDK.getInst().getAirohaFotaControl();
            mFotaInfo = fotaControl.requestDFUInfo(FotaSettings.FotaTargetEnum.Dual);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mFotaInfo != null && mFotaInfo.getFwVersion() != null) {
                        mTextViewAgentFwVersion.setText(mFotaInfo.getFwVersion());
                        mTextViewPartnerFwVersion.setText(mFotaInfo.getPartnerFwVersion());
                        mTextOtaStatus.setText("");
                        if (mRadioBtnFwBin.isChecked() && !mEditFwBinPath.getText().toString().isEmpty()) {
                            gLogger.d(TAG, "enable mBtn_Start");
                            mBtn_Start.setEnabled(true);
                        }
                        if (mRadioBtnRofsBin.isChecked() && !mEditRofsBinPath.getText().toString().isEmpty()) {
                            gLogger.d(TAG, "enable mBtn_Start");
                            mBtn_Start.setEnabled(true);
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Request DFU done");
                    } else {
                        if (mFotaInfo == null) {
                            gLogger.e(TAG, "mFotaInfo == null");
                        }
                        mTextOtaError.setText("Failed in Request DFU");
                        mActivity.updateMsg(MainActivity.MsgType.ERROR, "Failed in Request DFU");
                    }
                }
            });
        }
    }

    private void startConnectThread(final String bdAddr) {
        mTextOtaError.setText("");
        mTextOtaStatus.setText("");

        int selectedMode = mSpinnerFotaMode.getSelectedItemPosition();
        if ((AirohaSDK.getInst().getChipType() == ChipType.AB158x
                || AirohaSDK.getInst().getChipType() == ChipType.AB157x)
            && selectedMode == 1) {
            selectedMode = 2;
        }
        final int mode = selectedMode;
        final String level = mEditBatteryThrdLevel.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    FotaSettings.FotaTypeEnum fotaType = FotaSettings.FotaTypeEnum.Typical;
                    FotaSettings.FotaTargetEnum fotaTarget = FotaSettings.FotaTargetEnum.Dual;
                    if (mRadioBtnFwBin.isChecked() && mSelectedFwBinFileName != null) {
                        mUpdatingBinFileName = mSelectedFwBinFileName;
                    } else if (mRadioBtnRofsBin.isChecked() && mSelectedRofsBinFileName != null) {
                        mUpdatingBinFileName = mSelectedRofsBinFileName;
                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextOtaError.setText("Selected BinFileName is null");
                                mActivity.updateMsg(MainActivity.MsgType.ERROR, "Selected BinFileName is null");
                                delayedEnableAllBtn(false);
                            }
                        });
                        return;
                    }

                    if(level!=null && level.length() >0){
                        mBatteryLevelThrd = Integer.valueOf(level);
                    }
                    FotaSettings fotaSettings = new FotaSettings(fotaType, fotaTarget, mUpdatingBinFileName, mUpdatingBinFileName);
                    switch (mode) {
                        case 0: //Active
                            fotaSettings.setFotaMode(FotaSettings.FotaModeEnum.Active);
                            break;
                        case 1: //Background
                            fotaSettings.setFotaMode(FotaSettings.FotaModeEnum.Background);
                            break;
                        case 2: //Adaptive
                            fotaSettings.setFotaMode(FotaSettings.FotaModeEnum.Adaptive);
                            break;
                    }
                    gLogger.d(TAG, "fota_mode= " + fotaSettings.getFotaMode());
                    fotaSettings.setBatteryLevelThrd(mBatteryLevelThrd);
                    /// In this sample code, we use the registerOTAStatusListener/unregisterOTAStatusListener instead of using the parameter
                    AirohaSDK.getInst().getAirohaFotaControl().startDataTransfer(fotaSettings, null);

                    mIsFotaRunning = true;
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
        gLogger.d(TAG, "onPause");
        super.onPause();
        if (!mIsFotaRunning) {
            removeAllListener();
        }
    }

    @Override
    public void onDestroy() {
        gLogger.d(TAG, "onDestroy");
        try {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        } catch (Exception e) {

        }
//        if (mFilePrinter != null) {
//            gLogger.removePrinter(mFilePrinter.getPrinterName());
//        }
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
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
                mSpinnerFotaMode.setEnabled(true);
                mEditBatteryThrdLevel.setEnabled(true);
                mRadioBtnFwBin.setEnabled(true);
                mRadioBtnRofsBin.setEnabled(true);
                if (mRadioBtnFwBin.isChecked()) {
                    mBtnFwBinFilePicker.setEnabled(true);
                } else {
                    mBtnRofsBinFilePicker.setEnabled(true);
                }
                mBtn_Start.setEnabled(isEnableStartBtn);
            }
        }, 3000);
    }

    private void checkPhoneState(Context context){
        if(mIsFotaRunning){
            return;
        }
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if (manager.getMode()== AudioManager.MODE_IN_CALL) {
            if (mSpinnerFotaMode.getSelectedItemPosition() == 0) {
                showAlertDialog(mActivity, "Caution", "Active FOTA is disabled because phone is in call");
            }
            if (AirohaSDK.getInst().getChipType() == ChipType.AB1568
                    || AirohaSDK.getInst().mChipType == ChipType.AB1568_V3) {
                mSpinnerFotaMode.setSelection(2);
            } else {
                mSpinnerFotaMode.setSelection(1);
            }
            mSpinnerFotaMode.setEnabled(false);
        }
    }

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
                                mTextViewAgentBatteryLevel.setText("" + batteryInfo.getMasterLevel());
                                mTextViewPartnerBatteryLevel.setText("" + batteryInfo.getSlaveLevel());
                            }
                        }
                    }  catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }

    void showNotification(String content) {

        NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(
                NOTIFICATION_SERVICE);

        final Intent intent =  new Intent(mActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final int flags = PendingIntent.FLAG_IMMUTABLE;
        final PendingIntent pendingIntent = PendingIntent.getActivity(mActivity.getApplicationContext(), 0, intent, flags);

        Notification notification = new NotificationCompat.Builder(mActivity, channelID)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle("Airoha FOTA")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .build();

        notificationManager.notify(notifyID, notification);
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            if(mIsFotaRunning){
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    mSpinnerFotaMode.setEnabled(true);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                case TelephonyManager.CALL_STATE_RINGING:
                    if (AirohaSDK.getInst().getChipType() == ChipType.AB1568
                            || AirohaSDK.getInst().mChipType == ChipType.AB1568_V3) {
                        mSpinnerFotaMode.setSelection(2);
                    } else {
                        mSpinnerFotaMode.setSelection(1);
                    }
                    mSpinnerFotaMode.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    }

    AirohaFOTAControl.AirohaFOTAStatusListener mStatusListener = new AirohaFOTAControl.AirohaFOTAStatusListener() {

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
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = "";
                    switch (newStatus) {
                        case STATUS_STARTED:
                            msg = "FOTA started";
                            mTextOtaError.setText("");
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + msg);
                            break;
                        case STATUS_REBOOT:
                            mTextOtaError.setText("");
                            if (mSpinnerFotaMode.getSelectedItemPosition() == 0) {
                                mBtn_Cancel.setEnabled(true);
                                delayedCommit(1000);
                            } else {
                                msg = "Transformation is complete, click commit to reboot device.";
                                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + msg);
                                mBtn_Commit.setEnabled(true);
                            }
                            break;
                        case STATUS_SUCCEED:
                            msg = "FOTA Succeed";
                            mIsFotaRunning = false;
                            mTextOtaError.setText("");
                            mTextViewAgentFwVersion.setText("");
                            mTextViewPartnerFwVersion.setText("");
//                            mTextViewAgentFsVersion.setText("");
//                            mTextViewPartnerFsVersion.setText("");
                            mTextViewAgentBatteryLevel.setText("");
                            mTextViewPartnerBatteryLevel.setText("");
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + msg);
                            delayedEnableAllBtn(false);
                            break;
                        case USER_CANCELLED:
                        case BATTERY_LOW:
                            if (!mIsFotaRunning) {
                                gLogger.d(TAG, "disable mBtn_Start");
                                mBtn_Start.setEnabled(false);
                                return;
                            }
                            msg = "FOTA Cancelled";
                            mIsFotaRunning = false;
                            mBtn_Cancel.setEnabled(false);
                            mBtn_Commit.setEnabled(false);
                            delayedEnableAllBtn(false);
                            mTextOtaError.setText(newStatus.getName());
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + newStatus.getName());
                            break;
                        default:
                            mTextOtaError.setText(newStatus.getName());
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + newStatus.getName());
                            if (!mIsFotaRunning) {
                                gLogger.d(TAG, "disable mBtn_Start");
                                mBtn_Start.setEnabled(false);
                                return;
                            }
                            msg = "FOTA Failed";
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
                    mTextOtaStatus.setText(msg);
                    showNotification(msg);
                }
            });
        }

        @Override
        public void onFotaProgressChanged(final int progress) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextOtaStatus.setText(String.format("Progress : %d", progress));
                }
            });
        }

    };

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