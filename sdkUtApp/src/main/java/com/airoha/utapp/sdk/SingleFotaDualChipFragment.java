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
import android.os.SystemClock;
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

public class SingleFotaDualChipFragment extends BaseFragment {
    private String TAG = SingleFotaDualChipFragment.class.getSimpleName();
    private String LOG_TAG = "[FOTA] ";
    private View mView;
    final String channelID = "Airoha";
    final int notifyID = 56;

    TelephonyManager mTelephonyManager;
    MyPhoneStateListener mPhoneStateListener;

    private TextView mTextViewAgentFwVersion;
    private TextView mTextViewAgentBatteryLevel;

    private TextView mTextOtaStatus;
    private TextView mTextOtaError;

    private TextView mTextFwTitle1565;
    private TextView mTextFwTitle1568;
    private TextView mTextRofsTitle1565;
    private TextView mTextRofsTitle1568;

    private static final int REQUEST_CHOOSE_FW_BIN_1565 = 11;
    private static final int REQUEST_CHOOSE_FW_BIN_1568 = 22;
    private static final int REQUEST_CHOOSE_ROFS_BIN_1565 = 33;
    private static final int REQUEST_CHOOSE_ROFS_BIN_1568 = 44;

    // Interaction UI
    private FilePickerDialog mFwFilePickerDialog1565;
    private EditText mEditFwBinPath1565;
    private Button mBtnFwBinFilePicker1565;
    private FilePickerDialog mFwFilePickerDialog1568;
    private EditText mEditFwBinPath1568;
    private Button mBtnFwBinFilePicker1568;

    private FilePickerDialog mRofsFilePickerDialog1565;
    private EditText mEditRofsBinPath1565;
    private Button mBtnRofsBinFilePicker1565;
    private FilePickerDialog mRofsFilePickerDialog1568;
    private EditText mEditRofsBinPath1568;
    private Button mBtnRofsBinFilePicker1568;

    private LinearLayout mLinearLayoutFwBin;
    private LinearLayout mLinearLayoutRofsBin;

    private RadioButton mRadioBtnFwBin;
    private RadioButton mRadioBtnRofsBin;

    private Button mBtn_RequestDFU;
    private Button mBtn_Start;
    private Button mBtn_Commit;
    private Button mBtn_Cancel;

    private String mSelectedFwBinFileName1565;
    private String mSelectedRofsBinFileName1565;
    private String mUpdatingBinFileName1565;
    private String mSelectedFwBinFileName1568;
    private String mSelectedRofsBinFileName1568;
    private String mUpdatingBinFileName1568;

    private String[] FOTA_MODE_ARRAY;
    private Spinner mSpinnerFotaMode;

    private EditText mEditBatteryThrdLevel;
    private int mBatteryLevelThrd = 50;
    private boolean mIsFotaRunning = false;
    private FotaInfo mFotaInfo;

    public SingleFotaDualChipFragment(){
        mTitle = "FOTA - Single Mode (Dual-Chip)";
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
        mView = inflater.inflate(R.layout.fragment_single_fota_dual_chip, container,false);

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

        if (AirohaService.gAutoTestBinPathFotaR.BinPath != null) {
            initAutoTestBinFile(AirohaService.ExtraKey.FOTA_BIN_PATH_R,
                    AirohaService.gAutoTestBinPathFotaR.BinPath);
        }

        if (AirohaService.gAutoTestBinPathRofsL.BinPath != null) {
            initAutoTestBinFile(AirohaService.ExtraKey.ROFS_BIN_PATH_L,
                    AirohaService.gAutoTestBinPathRofsL.BinPath);
        }

        if (AirohaService.gAutoTestBinPathRofsR.BinPath != null) {
            initAutoTestBinFile(AirohaService.ExtraKey.ROFS_BIN_PATH_R,
                    AirohaService.gAutoTestBinPathRofsR.BinPath);
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
            mSelectedFwBinFileName1565 = tmpFile.getAbsolutePath();
            mEditFwBinPath1565.setText(mSelectedFwBinFileName1565);
        } else if (key == AirohaService.ExtraKey.ROFS_BIN_PATH_L) {
            mSelectedRofsBinFileName1565 = tmpFile.getAbsolutePath();
            mEditRofsBinPath1565.setText(mSelectedRofsBinFileName1565);
        }
    }

    private void initUiMember() {
        mBtnFwBinFilePicker1565 = mView.findViewById(R.id.buttonFwBinFilePicker1565);
        mEditFwBinPath1565 = mView.findViewById(R.id.editTextFwBinPath1565);

        mBtnFwBinFilePicker1568 = mView.findViewById(R.id.buttonFwBinFilePicker1568);
        mEditFwBinPath1568 = mView.findViewById(R.id.editTextFwBinPath1568);

        mBtnRofsBinFilePicker1565 = mView.findViewById(R.id.buttonRofsBinFilePicker1565);
        mEditRofsBinPath1565 = mView.findViewById(R.id.editTextRofsBinPath1565);

        mBtnRofsBinFilePicker1568 = mView.findViewById(R.id.buttonRofsBinFilePicker1568);
        mEditRofsBinPath1568 = mView.findViewById(R.id.editTextRofsBinPath1568);

        mTextViewAgentFwVersion = mView.findViewById(R.id.textView_tws_agent_fw_version);
        mTextViewAgentBatteryLevel = mView.findViewById(R.id.textView_tws_agent_battery_level);

        mTextOtaStatus = mView.findViewById(R.id.textViewStatus);
        mTextOtaError = mView.findViewById(R.id.textViewError);

        mTextFwTitle1565 = mView.findViewById(R.id.textViewFwBinTitle1565);
        mTextFwTitle1568 = mView.findViewById(R.id.textViewFwBinTitle1568);
        mTextRofsTitle1565 = mView.findViewById(R.id.textViewRofsBinTitle1565);
        mTextRofsTitle1568 = mView.findViewById(R.id.textViewRofsBinTitle1568);

        if (AirohaSDK.getInst().mChipType == ChipType.AB158x_DUAL
        || AirohaSDK.getInst().mChipType == ChipType.AB157x_DUAL) {
            mTextFwTitle1565.setText("BT FW Bin:");
            mTextFwTitle1568.setText("ULL FW Bin:");
            mTextRofsTitle1565.setText("BT ROFS Bin:");
            mTextRofsTitle1568.setText("ULL ROFS Bin:");
        }

        mBtn_RequestDFU = mView.findViewById(R.id.buttonRequestDFU);
        mBtn_RequestDFU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtn_RequestDFU clicked");
                gLogger.d(TAG, "disable mBtn_Start");
                mBtn_Start.setEnabled(false);
                mTextViewAgentFwVersion.setText("");
                mTextViewAgentBatteryLevel.setText("");
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
                if (mRadioBtnFwBin.isChecked()
                        && mEditFwBinPath1565.getText().toString().length() == 0
                        && mEditFwBinPath1568.getText().toString().length() == 0) {
                    mTextOtaError.setText("FW Bin file path is invalid");
                    return;
                }
                if (mRadioBtnRofsBin.isChecked()
                        && mEditRofsBinPath1565.getText().toString().length() == 0
                        && mEditRofsBinPath1568.getText().toString().length() == 0) {
                    mTextOtaError.setText("ROFS Bin file path is invalid");
                    return;
                }

                if(gTargetAddr.length() > 0) {
                    mRadioBtnFwBin.setEnabled(false);
                    mRadioBtnRofsBin.setEnabled(false);
                    mBtnFwBinFilePicker1565.setEnabled(false);
                    mBtnRofsBinFilePicker1565.setEnabled(false);
                    mBtnFwBinFilePicker1568.setEnabled(false);
                    mBtnRofsBinFilePicker1568.setEnabled(false);
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


        mLinearLayoutFwBin = mView.findViewById(R.id.linearLayoutMcsyncBinOS);

        mBtnFwBinFilePicker1565 = mView.findViewById(R.id.buttonFwBinFilePicker1565);
        mBtnFwBinFilePicker1565.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnFwBinFilePicker1565 clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showFileChooser(REQUEST_CHOOSE_FW_BIN_1565);
                } else {
                    mFwFilePickerDialog1565.show();
                }
            }
        });

        mBtnRofsBinFilePicker1565 = mView.findViewById(R.id.buttonRofsBinFilePicker1565);
        mBtnRofsBinFilePicker1565.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnRofsBinFilePicker1565 clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showFileChooser(REQUEST_CHOOSE_ROFS_BIN_1565);
                } else {
                    mRofsFilePickerDialog1565.show();
                }
            }
        });

        mBtnFwBinFilePicker1568 = mView.findViewById(R.id.buttonFwBinFilePicker1568);
        mBtnFwBinFilePicker1568.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnFwBinFilePicker1568 clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showFileChooser(REQUEST_CHOOSE_FW_BIN_1568);
                } else {
                    mFwFilePickerDialog1568.show();
                }
            }
        });

        mBtnRofsBinFilePicker1568 = mView.findViewById(R.id.buttonRofsBinFilePicker1568);
        mBtnRofsBinFilePicker1568.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnRofsBinFilePicker1568 clicked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showFileChooser(REQUEST_CHOOSE_ROFS_BIN_1568);
                } else {
                    mRofsFilePickerDialog1568.show();
                }
            }
        });

        mLinearLayoutRofsBin = mView.findViewById(R.id.linearLayoutMcsyncBinFS);

        mRadioBtnFwBin = mView.findViewById(R.id.radioBtnMcsyncBinOS);
        mRadioBtnFwBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mRadioBtnFwBin clicked");
                mLinearLayoutFwBin.setVisibility(View.VISIBLE);
                mLinearLayoutRofsBin.setVisibility(View.GONE);
            }
        });
        mRadioBtnRofsBin = mView.findViewById(R.id.radioBtnMcsyncBinFS);
        mRadioBtnRofsBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mRadioBtnRofsBin clicked");
                mLinearLayoutFwBin.setVisibility(View.GONE);
                mLinearLayoutRofsBin.setVisibility(View.VISIBLE);
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

        FOTA_MODE_ARRAY = new String[]{ "Active", "Background", "Adaptive"};

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
        mFwFilePickerDialog1565 = new FilePickerDialog(mActivity, properties);
        mFwFilePickerDialog1565.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    mSelectedFwBinFileName1565 = files[0].toString();
                    mEditFwBinPath1565.setText(mSelectedFwBinFileName1565);
                    if (mSelectedFwBinFileName1565 != null
                            && mSelectedFwBinFileName1568 != null
                            && mTextViewAgentFwVersion.getText().toString().length() > 0) {
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                }
            }
        });
        mFwFilePickerDialog1568 = new FilePickerDialog(mActivity, properties);
        mFwFilePickerDialog1568.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    mSelectedFwBinFileName1568 = files[0].toString();
                    mEditFwBinPath1568.setText(mSelectedFwBinFileName1568);
                    if (mSelectedFwBinFileName1565 != null
                            && mSelectedFwBinFileName1568 != null
                            && mTextViewAgentFwVersion.getText().toString().length() > 0) {
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
        mRofsFilePickerDialog1565 = new FilePickerDialog(mActivity, properties);
        mRofsFilePickerDialog1565.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    mSelectedRofsBinFileName1565 = files[0].toString();
                    mEditRofsBinPath1565.setText(mSelectedRofsBinFileName1565);
                    if (mSelectedRofsBinFileName1565 != null
                            && mSelectedRofsBinFileName1568 != null
                            && mTextViewAgentFwVersion.getText().toString().length() > 0) {
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                }
            }
        });
        mRofsFilePickerDialog1568 = new FilePickerDialog(mActivity, properties);
        mRofsFilePickerDialog1568.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    mSelectedRofsBinFileName1568 = files[0].toString();
                    mEditRofsBinPath1568.setText(mSelectedRofsBinFileName1568);
                    if (mSelectedRofsBinFileName1565 != null
                            && mSelectedRofsBinFileName1568 != null
                            && mTextViewAgentFwVersion.getText().toString().length() > 0) {
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
        Uri uri = data.getData();
        String path = uri.getPath();
        switch (requestCode) {
            case REQUEST_CHOOSE_FW_BIN_1565:
            case REQUEST_CHOOSE_ROFS_BIN_1565:
                if (path != null && path.length() > 0) {
                    gLogger.d(TAG, "uri.getPath() = " + path);
                    File tmpFile = uriToFileApiQ(uri);
                    if (tmpFile == null) {
                        showInvalidBinPathDialog();
                        return;
                    }
                    mTextOtaError.setText("");
                    if (requestCode == REQUEST_CHOOSE_FW_BIN_1565) {
                        mSelectedFwBinFileName1565 = tmpFile.getAbsolutePath();
                        mEditFwBinPath1565.setText(mSelectedFwBinFileName1565);
                    } else {
                        mSelectedRofsBinFileName1565 = tmpFile.getAbsolutePath();
                        mEditRofsBinPath1565.setText(mSelectedRofsBinFileName1565);
                    }
                    if (mFotaInfo != null) {
                        gLogger.d(TAG, "enable mBtn_Start");
                        mBtn_Start.setEnabled(true);
                    }
                } else {
                    showInvalidBinPathDialog();
                }
                break;

            case REQUEST_CHOOSE_FW_BIN_1568:
            case REQUEST_CHOOSE_ROFS_BIN_1568:
                if (path != null && path.length() > 0) {
                    gLogger.d(TAG, "uri.getPath() = " + path);
                    File tmpFile = uriToFileApiQ(uri);
                    if (tmpFile == null) {
                        showInvalidBinPathDialog();
                        return;
                    }
                    mTextOtaError.setText("");
                    if (requestCode == REQUEST_CHOOSE_FW_BIN_1568) {
                        mSelectedFwBinFileName1568 = tmpFile.getAbsolutePath();
                        mEditFwBinPath1568.setText(mSelectedFwBinFileName1568);
                    } else {
                        mSelectedRofsBinFileName1568 = tmpFile.getAbsolutePath();
                        mEditRofsBinPath1568.setText(mSelectedRofsBinFileName1568);
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
                        mTextOtaStatus.setText("");
                        if (mRadioBtnFwBin.isChecked()
                                && !mEditFwBinPath1565.getText().toString().isEmpty()
                                && !mEditFwBinPath1568.getText().toString().isEmpty()) {
                            gLogger.d(TAG, "enable mBtn_Start");
                            mBtn_Start.setEnabled(true);
                        }
                        if (mRadioBtnRofsBin.isChecked()
                                && !mEditRofsBinPath1565.getText().toString().isEmpty()
                                && !mEditRofsBinPath1568.getText().toString().isEmpty()) {
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

        final int mode = mSpinnerFotaMode.getSelectedItemPosition();
        final String level = mEditBatteryThrdLevel.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    FotaSettings.FotaTypeEnum fotaType = FotaSettings.FotaTypeEnum.Typical;
                    FotaSettings.FotaTargetEnum fotaTarget = FotaSettings.FotaTargetEnum.Dual;
                    if (mRadioBtnFwBin.isChecked()
                            && mSelectedFwBinFileName1565 != null
                            && mSelectedFwBinFileName1568 != null) {
                        mUpdatingBinFileName1565 = mSelectedFwBinFileName1565;
                        mUpdatingBinFileName1568 = mSelectedFwBinFileName1568;
                    } else if (mRadioBtnRofsBin.isChecked()
                            && mSelectedRofsBinFileName1565 != null
                            && mSelectedRofsBinFileName1568 != null) {
                        mUpdatingBinFileName1565 = mSelectedRofsBinFileName1565;
                        mUpdatingBinFileName1568 = mSelectedRofsBinFileName1568;
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
                    FotaSettings fotaSettings = new FotaSettings(fotaType, fotaTarget, mUpdatingBinFileName1565, mUpdatingBinFileName1568);
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
                    boolean isStarted = AirohaSDK.getInst().getAirohaFotaControl().startDataTransfer(fotaSettings, null);
                    if (!isStarted) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextOtaError.setText("FOTA bin is invalid!");
                                mActivity.updateMsg(MainActivity.MsgType.ERROR, "FOTA bin is invalid!");
                                mBtn_Cancel.setEnabled(false);
                                delayedEnableAllBtn(false);
                            }
                        });
                        return;
                    }

                    mIsFotaRunning = true;
                }catch (final Exception e){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextOtaError.setText(e.getMessage());
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, e.getMessage());
                            mBtn_Cancel.setEnabled(false);
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
                    mBtnFwBinFilePicker1565.setEnabled(true);
                    mBtnFwBinFilePicker1568.setEnabled(true);
                } else {
                    mBtnRofsBinFilePicker1565.setEnabled(true);
                    mBtnRofsBinFilePicker1568.setEnabled(true);
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
            mSpinnerFotaMode.setSelection(2);
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
                    mSpinnerFotaMode.setSelection(2);
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
                            mTextViewAgentBatteryLevel.setText("");
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