package com.airoha.utapp.sdk;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.airoha.libanc.AirohaAncListener;
import com.airoha.libanc.model.AncUserTriggerSettings;
import com.airoha.libanc.model.AncUserTriggerUtils;
import com.airoha.libanc.model.EnvironmentDetectionInfo;
import com.airoha.libutils.Converter;
import com.airoha.sdk.AB1568DeviceControl;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.message.AirohaAncSettings;
import com.airoha.sdk.api.message.AirohaAncStatusMsg;
import com.airoha.sdk.api.message.AirohaAncUserTriggerSettings;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaEarCanalCompensationInfo;
import com.airoha.sdk.api.utils.AirohaAncUserTriggerStatusCode;
import com.airoha.sdk.api.utils.AirohaMessageID;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.utapp.sdk.MainActivity.MsgType;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class AncUserTriggerFragment extends BaseFragment implements OnChartValueSelectedListener {
    private String TAG = AncUserTriggerFragment.class.getSimpleName();
    private String LOG_TAG = "[AncUserTrigger] ";
    private AncUserTriggerFragment mFragment;
    private View mView;

    private static String FOLDER = "/sdcard/Documents/Airoha/Log/AB1568/AncUserTrigger/";
    private String mInputFolder = FOLDER;
    private String mOutputFolder = FOLDER;

    private Button mBtnStartAncUserTrigger;
    private Button mBtnStopAncUserTrigger;
    private Button mBtnRestoreAncUserTriggerCoef;
    private Button mBtnSetBypassFilter;
    public static final String BYPASS_FILTER_COEF = "1111090000800000FFFFFF7F00000000000000000000000000000000FFFFFF7F00000000000000000000000000000000FFFFFF7F00000000000000000000000000000000FFFFFF7F00000000000000000000000000000000FFFFFF7F00000000000000000000000000000000FFFFFF7F00000000000000000000000000000000FFFFFF7F00000000000000000000000000000000FFFFFF7F00000000000000000000000000000000FFFFFF7F00000000000000000000000000000000";

    private Spinner mSpinModel;
    private TextView mTextLibVersion;
    private TextView mTextAncUserTriggerTargetFilter;
    private TextView mTextAncUserTriggerResult;
    private TextView mTextAncUserTriggerNotice;
    private ArrayAdapter<String> mModelAdapter;

    private boolean mIsPartnerExist = false;
    private boolean mIsAgentRight = false;
    private boolean mIsConnected = true;

    private byte mAncFilterSelected = 0;
    private String mAncModeSelectedString = "";
    private int mAncModeSelected = 0;
    private int mAncPassthruFilterSelected = 0;
    private double mAncPassthruGainSelected = 0;

    private Button mBtnGetAncSetting;
    private List<RadioButton> mAncRadioButtonList;
    private RadioButton mRadioButtonAncFilter1;
    private RadioButton mRadioButtonAncFilter2;
    private RadioButton mRadioButtonAncFilter3;
    private RadioButton mRadioButtonAncFilter4;
    private RadioButton mRadioButtonPassThrough1;
    private RadioButton mRadioButtonPassThrough2;
    private RadioButton mRadioButtonPassThrough3;
    private RadioButton mRadioButtonAncPtOff;
    private AppCompatSeekBar mSeekBarAncGain;
    private TextView mTextViewAncGain;
    private AppCompatSeekBar mSeekBarPassThroughGain;
    private TextView mTextViewPassThroughGain;
    private ProgressDialogUtil mProgressDialog = null;

    private RadioButton mRadioButtonEarCanalCompensationON;
    private RadioButton mRadioButtonEarCanalCompensationOFF;
    private Button mBtnGetEarCanalCompensationStatus;
    private int mEarCanalCompensationStatus = 0x00;

    public AncUserTriggerFragment(){
        mTitle = "ANC USER TRIGGER";
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
        mView = inflater.inflate(R.layout.fragment_anc_user_trigger, container,false);
        initUImember();
        return mView;
    }

    private void initUImember() {
        Bundle bundle = getArguments();
        gTargetAddr = bundle.getString(BaseFragment.EXTRAS_DEVICE_BDA);

        mSpinModel = mView.findViewById(R.id.spinModel);
        refreshModelList();
        mSpinModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position < 0)
                    return;

                updateInputFolderPath();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTextLibVersion = mView.findViewById(R.id.textLibVersion);
        mTextAncUserTriggerTargetFilter = mView.findViewById(R.id.textAncUserTriggerTargetFilter);
        mTextAncUserTriggerResult = mView.findViewById(R.id.textAncUserTriggerResult);
        mTextAncUserTriggerNotice = mView.findViewById(R.id.textAncUserTriggerNotice);

        mBtnStartAncUserTrigger = mView.findViewById(R.id.btnStartAncUserTrigger);
        mBtnStartAncUserTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLogger.d(TAG, "mBtnStartAncUserTrigger onClick");
                mTextAncUserTriggerNotice.setVisibility(View.GONE);

                if(!updateInputFolderPath()){
                    mActivity.updateMsg(MsgType.ERROR, LOG_TAG + "Please select model.");
                    return;
                }
                String time_stamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(Calendar.getInstance().getTime());
                updateOutputFolderPath(mInputFolder + time_stamp + "/");

                mProgressDialog = new ProgressDialogUtil();
                mProgressDialog.showProgressDialog(mActivity, "Initializing...");
                mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "ANC user trigger start...(Agent" + ((mIsPartnerExist) ? "&Partner)" : ")"));

                mBtnStartAncUserTrigger.setEnabled(false);
                AirohaAncUserTriggerSettings settings = new AirohaAncUserTriggerSettings(mInputFolder, mOutputFolder);
                AirohaSDK.getInst().getAirohaDeviceControl().startAncUserTrigger(settings, new StartAncUserTriggerListener());
            }
        });

        mBtnStopAncUserTrigger = mView.findViewById(R.id.btnStopAncUserTrigger);
        mBtnStopAncUserTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnStopAncUserTrigger.setEnabled(false);
                gLogger.d(TAG, "mBtnStopAncUserTrigger onClick");
                AirohaSDK.getInst().getAirohaDeviceControl().stopAncUserTrigger(new StopAncUserTriggerListener());
            }
        });

        mBtnRestoreAncUserTriggerCoef = mView.findViewById(R.id.btnRestoreAncUserTriggerCoef);
        mBtnRestoreAncUserTriggerCoef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnRestoreAncUserTriggerCoef.setEnabled(false);
                gLogger.d(TAG, "mBtnRestoreAncUserTriggerCoef onClick");
                mActivity.updateMsg(MsgType.GENERAL,LOG_TAG + "restoreAncUserTriggerCoef...");
                AirohaSDK.getInst().getAirohaDeviceControl().restoreAncUserTriggerCoef(new RestoreAncUserTriggerCoefListener());
            }
        });

        mBtnSetBypassFilter = mView.findViewById(R.id.btnSetBypassFilter);
        mBtnSetBypassFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] coef = Converter.hexStringToByteArray(BYPASS_FILTER_COEF);
                AirohaAncUserTriggerSettings settings = new AirohaAncUserTriggerSettings();
                settings.setLeftAncOffset(0);
                settings.setRightAncOffset(0);
                settings.setLeftAncData(coef);
                settings.setRightAncData(coef);

                mBtnSetBypassFilter.setEnabled(false);
                mActivity.updateMsg(MsgType.GENERAL,LOG_TAG + "setAncUserTriggerCoefAsBypassFilter...");
                AirohaSDK.getInst().getAirohaDeviceControl().updateAncUserTriggerCoef(settings, new UpdateAncUserTriggerCoefListener());
            }
        });

        mBtnGetAncSetting = mView.findViewById(R.id.buttonGetAncStatus);
        mBtnGetAncSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAncModeSelected = 0;
                mBtnGetAncSetting.setEnabled(false);
                for (RadioButton btn : mAncRadioButtonList) {
                    btn.setEnabled(false);
                }
                mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Get ANC setting...");
                AirohaSDK.getInst().getAirohaDeviceControl().getAncSetting(new AncSettingListener());
            }
        });

        mAncRadioButtonList = new ArrayList<>();
        mRadioButtonAncFilter1 = mView.findViewById(R.id.radioButton_mmi_anc_filter1);
        mAncRadioButtonList.add(mRadioButtonAncFilter1);
        mRadioButtonAncFilter2 = mView.findViewById(R.id.radioButton_mmi_anc_filter2);
        mAncRadioButtonList.add(mRadioButtonAncFilter2);
        mRadioButtonAncFilter3 = mView.findViewById(R.id.radioButton_mmi_anc_filter3);
        mAncRadioButtonList.add(mRadioButtonAncFilter3);
        mRadioButtonAncFilter4 = mView.findViewById(R.id.radioButton_mmi_anc_filter4);
        mAncRadioButtonList.add(mRadioButtonAncFilter4);
        mRadioButtonPassThrough1 = mView.findViewById(R.id.radioButton_mmi_pass_through1);
        mAncRadioButtonList.add(mRadioButtonPassThrough1);
        mRadioButtonPassThrough2 = mView.findViewById(R.id.radioButton_mmi_pass_through2);
        mAncRadioButtonList.add(mRadioButtonPassThrough2);
        mRadioButtonPassThrough3 = mView.findViewById(R.id.radioButton_mmi_pass_through3);
        mAncRadioButtonList.add(mRadioButtonPassThrough3);
        mRadioButtonAncPtOff = mView.findViewById(R.id.radioButton_mmi_anc_pt_off);
        mAncRadioButtonList.add(mRadioButtonAncPtOff);

        mRadioButtonAncFilter1.setOnClickListener(mOnClickListener);
        mRadioButtonAncFilter2.setOnClickListener(mOnClickListener);
        mRadioButtonAncFilter3.setOnClickListener(mOnClickListener);
        mRadioButtonAncFilter4.setOnClickListener(mOnClickListener);
        mRadioButtonPassThrough1.setOnClickListener(mOnClickListener);
        mRadioButtonPassThrough2.setOnClickListener(mOnClickListener);
        mRadioButtonPassThrough3.setOnClickListener(mOnClickListener);
        mRadioButtonAncPtOff.setOnClickListener(mOnClickListener);

        mTextViewAncGain = mView.findViewById(R.id.textView_anc_gain);
        mTextViewPassThroughGain = mView.findViewById(R.id.textView_passthru_gain);
        mSeekBarAncGain = mView.findViewById(R.id.seekBar_anc_gain);
        mSeekBarPassThroughGain = mView.findViewById(R.id.seekBar_passthru_gain);
//        addToViewList(mSeekBarAncGain);
//        addToViewList(mSeekBarPassThroughGain);

        mSeekBarAncGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = (float) Math.round((progress - mSeekBarAncGain.getMax()) / 100);
                mTextViewAncGain.setText(String.format("%.2f", value));
                mAncPassthruGainSelected = Double.parseDouble(mTextViewAncGain.getText().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set ANC Gain...");
                AirohaAncSettings ancSetting = new AirohaAncSettings();
                ancSetting.setFilter(mAncPassthruFilterSelected);
                ancSetting.setGain(mAncPassthruGainSelected);
                ancSetting.setAncMode(mAncModeSelected);
                AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
            }
        });
        mSeekBarPassThroughGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = (float) Math.round((progress - mSeekBarPassThroughGain.getMax()) / 100);
                mTextViewPassThroughGain.setText(String.format("%.2f", value));
                mAncPassthruGainSelected = Double.parseDouble(mTextViewPassThroughGain.getText().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set Passthrough Gain...");
                AirohaAncSettings ancSetting = new AirohaAncSettings();
                ancSetting.setFilter(mAncPassthruFilterSelected);
                ancSetting.setGain(mAncPassthruGainSelected);
                AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
            }
        });

        mRadioButtonEarCanalCompensationON = mView.findViewById(R.id.radioButton_ear_canal_compensation_enable);
        mRadioButtonEarCanalCompensationON.setOnClickListener(mOnClickListener);
        mRadioButtonEarCanalCompensationOFF = mView.findViewById(R.id.radioButton_ear_canal_compensation_disable);
        mRadioButtonEarCanalCompensationOFF.setOnClickListener(mOnClickListener);
        mBtnGetEarCanalCompensationStatus = mView.findViewById(R.id.btnGetEarCanalCompensationStatus);
        mBtnGetEarCanalCompensationStatus.setOnClickListener(mOnClickListener);
    }

    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.radioButton_mmi_anc_filter1:
                    if (((RadioButton) v).isChecked()) {
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncFilterSelected = 0x01;
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.ANC1.ordinal();
                        mSeekBarAncGain.setEnabled(true);
                        mSeekBarPassThroughGain.setEnabled(false);
                        mAncPassthruGainSelected = Double.parseDouble(mTextViewAncGain.getText().toString());
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set ANC1...");
                        showAncModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_anc_filter2:
                    if (((RadioButton) v).isChecked()) {
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncFilterSelected = 0x02;
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.ANC2.ordinal();
                        mSeekBarAncGain.setEnabled(true);
                        mSeekBarPassThroughGain.setEnabled(false);
                        mAncPassthruGainSelected = Double.parseDouble(mTextViewAncGain.getText().toString());
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set ANC2...");
                        showAncModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_anc_filter3:
                    if (((RadioButton) v).isChecked()) {
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncFilterSelected = 0x03;
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.ANC3.ordinal();
                        mSeekBarAncGain.setEnabled(true);
                        mSeekBarPassThroughGain.setEnabled(false);
                        mAncPassthruGainSelected = Double.parseDouble(mTextViewAncGain.getText().toString());
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set ANC3...");
                        showAncModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_anc_filter4:
                    if (((RadioButton) v).isChecked()) {
                        resetAncRadioButtons();
                        ((RadioButton) v).setChecked(true);
                        mAncFilterSelected = 0x07;
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.ANC4.ordinal();
                        mSeekBarAncGain.setEnabled(true);
                        mSeekBarPassThroughGain.setEnabled(false);
                        mAncPassthruGainSelected = Double.parseDouble(mTextViewAncGain.getText().toString());
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set ANC4...");
                        showAncModeSelection();
                    }
                    break;
                case R.id.radioButton_mmi_pass_through1:
                    if (((RadioButton) v).isChecked()) {
                        resetAncRadioButtons();
                        setAncRadioButtonText(0, "");
                        ((RadioButton) v).setChecked(true);
                        mAncModeSelectedString = "";
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.PassThrough1.ordinal();
                        mSeekBarAncGain.setEnabled(false);
                        mSeekBarPassThroughGain.setEnabled(true);
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set Passthrough1...");
                        mAncPassthruGainSelected = Double.parseDouble(mTextViewPassThroughGain.getText().toString());
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mAncPassthruGainSelected);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                    }
                    break;
                case R.id.radioButton_mmi_pass_through2:
                    if (((RadioButton) v).isChecked()) {
                        resetAncRadioButtons();
                        setAncRadioButtonText(0, "");
                        ((RadioButton) v).setChecked(true);
                        mAncModeSelectedString = "";
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.PassThrough2.ordinal();
                        mSeekBarAncGain.setEnabled(false);
                        mSeekBarPassThroughGain.setEnabled(true);
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set Passthrough2...");
                        mAncPassthruGainSelected = Double.parseDouble(mTextViewPassThroughGain.getText().toString());
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mAncPassthruGainSelected);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                    }
                    break;
                case R.id.radioButton_mmi_pass_through3:
                    if (((RadioButton) v).isChecked()) {
                        resetAncRadioButtons();
                        setAncRadioButtonText(0, "");
                        ((RadioButton) v).setChecked(true);
                        mAncModeSelectedString = "";
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.PassThrough3.ordinal();
                        mAncPassthruGainSelected = Double.parseDouble(mTextViewPassThroughGain.getText().toString());
                        mSeekBarAncGain.setEnabled(false);
                        mSeekBarPassThroughGain.setEnabled(true);
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set Passthrough3...");
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mAncPassthruGainSelected);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                    }
                    break;
                case R.id.radioButton_mmi_anc_pt_off:
                    if (((RadioButton) v).isChecked()) {
                        resetAncRadioButtons();
                        setAncRadioButtonText(0, "");
                        ((RadioButton) v).setChecked(true);
                        mAncModeSelectedString = "";
                        mAncPassthruFilterSelected = AirohaAncSettings.UI_ANC_FILTER.OFF.ordinal();
                        mSeekBarAncGain.setEnabled(false);
                        mSeekBarPassThroughGain.setEnabled(false);
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Set ANC Off...");
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mAncPassthruGainSelected);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                    }
                    break;
                case R.id.radioButton_ear_canal_compensation_enable:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MsgType.GENERAL, "setEarCanalCompensationStatus...");
                        mEarCanalCompensationStatus = 0x01;
                        AirohaEarCanalCompensationInfo setting = new AirohaEarCanalCompensationInfo();
                        setting.setEarCanalCompensationOnOff(mEarCanalCompensationStatus);
                        AirohaSDK.getInst().getAirohaDeviceControl().setEarCanalCompensationStatus(setting, new EarCanalCompensationStatusListener());
                    }
                    break;
                case R.id.radioButton_ear_canal_compensation_disable:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MsgType.GENERAL, "setEarCanalCompensationStatus...");
                        mEarCanalCompensationStatus = 0x00;
                        AirohaEarCanalCompensationInfo setting = new AirohaEarCanalCompensationInfo();
                        setting.setEarCanalCompensationOnOff(mEarCanalCompensationStatus);
                        AirohaSDK.getInst().getAirohaDeviceControl().setEarCanalCompensationStatus(setting, new EarCanalCompensationStatusListener());
                    }
                    break;
                case R.id.btnGetEarCanalCompensationStatus:
                    mBtnGetEarCanalCompensationStatus.setEnabled(false);
                    mActivity.updateMsg(MsgType.GENERAL, "getEarCanalCompensationStatus...");
                    AirohaSDK.getInst().getAirohaDeviceControl().getEarCanalCompensationStatus(new EarCanalCompensationStatusListener());
                    break;
            }
        }
    };

    private void showAncModeSelection() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity)
                .setTitle("Select ANC Mode")
                .setItems(R.array.ANC_Filter_Control, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] Item = getResources().getStringArray(
                                R.array.ANC_Filter_Control
                        );
                        gLogger.d(TAG, "Select ANC Mode: " + Item[which]);
                        mAncModeSelected = which;
                        AirohaAncSettings ancSetting = new AirohaAncSettings();
                        ancSetting.setFilter(mAncPassthruFilterSelected);
                        ancSetting.setGain(mAncPassthruGainSelected);
                        ancSetting.setAncMode(mAncModeSelected);
                        AirohaSDK.getInst().getAirohaDeviceControl().setAncSetting(ancSetting, false, new AncSettingListener());
                        mAncModeSelectedString = Item[which];
                        setAncRadioButtonText(mAncFilterSelected, mAncModeSelectedString);
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void refreshModelList() {
        String path = FOLDER;
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files == null || files.length == 0){
            return;
        }
        ArrayList list = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                String name = files[i].getName();
                list.add(name);
                gLogger.d(TAG, "ModelName:" + name);
            }
        }
        if(list.size() != 0) {
            mModelAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, list);
            mSpinModel.setAdapter(mModelAdapter);
            mModelAdapter.notifyDataSetChanged();
        }
    }

    private boolean updateInputFolderPath() {
        if(mSpinModel.getSelectedItem() == null) {
            return false;
        }
        String modelName = mSpinModel.getSelectedItem().toString();
        mInputFolder = FOLDER + modelName + "/";
        return true;
    }

    private void setAncRadioButtonText(int filter_num, String mode_str) {
        mRadioButtonAncFilter1.setText("");
        mRadioButtonAncFilter2.setText("");
        mRadioButtonAncFilter3.setText("");
        mRadioButtonAncFilter4.setText("");

        switch (filter_num) {
            case 1:
                mRadioButtonAncFilter1.setText(mode_str);
                break;
            case 2:
                mRadioButtonAncFilter2.setText(mode_str);
                break;
            case 3:
                mRadioButtonAncFilter3.setText(mode_str);
                break;
            case 7:
                mRadioButtonAncFilter4.setText(mode_str);
                break;
        }
    }

    private void updateOutputFolderPath(String path){
        mOutputFolder = path;
    }

    private void initFlow(){
        int lib_version = AncUserTriggerUtils.getLibVersion();
        if (lib_version == -1){
            mTextLibVersion.setText("NA");
        } else{
            mTextLibVersion.setText("" + lib_version);
        }
        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "getTwsConnectStatus...");
        AirohaSDK.getInst().getAirohaDeviceControl().getTwsConnectStatus(new TwsConnectStatusListener());
        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "Get ANC setting...");
        AirohaSDK.getInst().getAirohaDeviceControl().getAncSetting(new AncSettingListener());
        mActivity.updateMsg(MsgType.GENERAL, "getEarCanalCompensationStatus...");
        AirohaSDK.getInst().getAirohaDeviceControl().getEarCanalCompensationStatus(new EarCanalCompensationStatusListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        gLogger.d(TAG, "onResume");

        if(mActivity != null && mActivity.getAirohaService() != null) {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(true);
            AirohaSDK.getInst().getAirohaDeviceConnector().registerConnectionListener(mFragment);
            AirohaSDK.getInst().getAirohaDeviceControl().registerGlobalListener(new UpdateDeviceStatusListener());
            ((AB1568DeviceControl) AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaAncMgr().addListener(TAG, mAirohaAncListener);

            initFlow();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        gLogger.d(TAG, "onPause");

        if(mActivity != null && mActivity.getAirohaService() != null){
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(false);
        }
    }

    @Override
    public void onDestroy() {
        gLogger.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
        super.onHiddenChanged(hidden);

        if (hidden) {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(false);
            AirohaSDK.getInst().getAirohaDeviceConnector().unregisterConnectionListener(mFragment);
            ((AB1568DeviceControl) AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr1568().removeListener(TAG);
        }
        else {
            AirohaSDK.getInst().getAirohaDeviceConnector().setReopenFlag(true);
            AirohaSDK.getInst().getAirohaDeviceConnector().registerConnectionListener(mFragment);
            ((AB1568DeviceControl) AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaAncMgr().addListener(TAG, mAirohaAncListener);
            initFlow();
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void changePrivilege(PrivilegeState state){
    }

    private void setViewEnable(boolean status){
        mBtnStartAncUserTrigger.setEnabled(status);
        mBtnStopAncUserTrigger.setEnabled(status);
        mBtnRestoreAncUserTriggerCoef.setEnabled(status);
        mBtnSetBypassFilter.setEnabled(status);

        mBtnGetAncSetting.setEnabled(status);
        for (RadioButton btn : mAncRadioButtonList) {
            btn.setEnabled(status);
        }
        mSeekBarAncGain.setEnabled(status);
        mSeekBarPassThroughGain.setEnabled(status);
    }

    @Override
    public void onStatusChanged(final int status) {
        switch (status) {
            case AirohaConnector.CONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(1000);

                        ((AB1568DeviceControl) AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaAncMgr().addListener(TAG, mAirohaAncListener);
                        initFlow();
                        setViewEnable(true);
                        mIsConnected = true;
                    }
                });
                break;
            case AirohaConnector.DISCONNECTED:
            case AirohaConnector.CONNECTING:
            case AirohaConnector.DISCONNECTING:
            case AirohaConnector.WAITING_CONNECTABLE:
            default:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mProgressDialog != null) {
                            mProgressDialog.dismiss();
                            gLogger.d(TAG, String.format("Dialog isShowing %s", mProgressDialog.isShowing()));
                        }
                        setViewEnable(false);
                        mIsConnected = false;
                    }
                });
                break;
        }
    }

    class TwsConnectStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == AirohaStatusCode.STATUS_SUCCESS) {
                        Boolean isTwsConnected = (Boolean)msg.getMsgContent();
                        mIsPartnerExist = isTwsConnected;
                        mIsAgentRight = AirohaSDK.getInst().isAgentRightSideDevice();
                        gLogger.d(TAG, "Partner exist status = " + mIsPartnerExist);
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + "getTwsConnectStatus: " + code.getDescription());
                    }
                    else{
                        mActivity.updateMsg(MsgType.ERROR, LOG_TAG + "getTwsConnectStatus: " + code.getDescription());
                    }
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }

    class AncSettingListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            final AirohaAncStatusMsg ancStatusMessage = (AirohaAncStatusMsg) msg;
                            updateAncUI(code, ancStatusMessage, "GetAirohaAncSettings");
                        } else {
                            mActivity.updateMsg(MsgType.ERROR, "GetAirohaAncSettings: " + code.getDescription());
                        }
                        mBtnGetAncSetting.setEnabled(true);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            final AirohaAncStatusMsg ancStatusMessage = (AirohaAncStatusMsg) msg;
                            updateAncUI(code, ancStatusMessage, "SetAirohaAncSettings");
                        } else {
                            mActivity.updateMsg(MsgType.ERROR, "SetAirohaAncSettings: " + code.getDescription());
                        }
                        mBtnGetAncSetting.setEnabled(true);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    private static final Object mUpdateAncUiLock = new Object();

    private void updateAncUI(final AirohaStatusCode code, final AirohaAncStatusMsg msg, final String title) {

        synchronized (mUpdateAncUiLock) {

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (msg != null) {
                            LinkedList<AirohaAncSettings> content = (LinkedList<AirohaAncSettings>) msg.getMsgContent();
                            AirohaAncSettings setting = content.get(0);
                            int filter = setting.getFilter();
                            double ancGain = setting.getAncGain();
                            double passthruGain = setting.getPassthruGain();

                            resetAncRadioButtons();
                            if(filter == mAncFilterSelected) {
                                setAncRadioButtonText(mAncFilterSelected, mAncModeSelectedString);
                            }
                            mAncModeSelectedString = "";

                            switch (AirohaAncSettings.UI_ANC_FILTER.values()[filter]) {
                                case OFF:
                                    mRadioButtonAncPtOff.setChecked(true);
                                    break;
                                case ANC1:
                                    mRadioButtonAncFilter1.setChecked(true);
                                    break;
                                case ANC2:
                                    mRadioButtonAncFilter2.setChecked(true);
                                    break;
                                case ANC3:
                                    mRadioButtonAncFilter3.setChecked(true);
                                    break;
                                case ANC4:
                                    mRadioButtonAncFilter4.setChecked(true);
                                    break;
                                case PassThrough1:
                                    mRadioButtonPassThrough1.setChecked(true);
                                    break;
                                case PassThrough2:
                                    mRadioButtonPassThrough2.setChecked(true);
                                    break;
                                case PassThrough3:
                                    mRadioButtonPassThrough3.setChecked(true);
                                    break;
                            }

                            if (filter == AirohaAncSettings.UI_ANC_FILTER.ANC1.ordinal()
                                    || filter == AirohaAncSettings.UI_ANC_FILTER.ANC2.ordinal()
                                    || filter == AirohaAncSettings.UI_ANC_FILTER.ANC3.ordinal()
                                    || filter == AirohaAncSettings.UI_ANC_FILTER.ANC4.ordinal()) {
                                mSeekBarAncGain.setEnabled(true);
                                mSeekBarPassThroughGain.setEnabled(false);
                            } else if (filter == AirohaAncSettings.UI_ANC_FILTER.PassThrough1.ordinal() ||
                                    filter == AirohaAncSettings.UI_ANC_FILTER.PassThrough2.ordinal() ||
                                    filter == AirohaAncSettings.UI_ANC_FILTER.PassThrough3.ordinal()) {
                                mSeekBarAncGain.setEnabled(false);
                                mSeekBarPassThroughGain.setEnabled(true);
                            } else {
                                mSeekBarAncGain.setEnabled(false);
                                mSeekBarPassThroughGain.setEnabled(false);
                            }
                            mSeekBarAncGain.setProgress((short) ancGain * 100 + mSeekBarAncGain.getMax());
                            mSeekBarPassThroughGain.setProgress((short) passthruGain * 100 + mSeekBarPassThroughGain.getMax());
                        }
                        mActivity.updateMsg(MsgType.GENERAL, LOG_TAG + title + ": " + code.getDescription());

                        mBtnGetAncSetting.setEnabled(true);
                        for (RadioButton btn : mAncRadioButtonList) {
                            btn.setEnabled(true);
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    private void updateAncUIAfterUserTrigger(final int filter) {
        synchronized (mUpdateAncUiLock) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resetAncRadioButtons();
                    switch (filter) {
                        case 0:
                            mRadioButtonAncPtOff.setChecked(true);
                            break;
                        case 1:
                            mRadioButtonAncFilter1.setChecked(true);
                            mRadioButtonAncFilter1.setText("Hybrid");
                            break;
                        case 2:
                            mRadioButtonAncFilter2.setChecked(true);
                            mRadioButtonAncFilter2.setText("Hybrid");
                            break;
                        case 3:
                            mRadioButtonAncFilter3.setChecked(true);
                            mRadioButtonAncFilter3.setText("Hybrid");
                            break;
                        case 4:
                            mRadioButtonAncFilter4.setChecked(true);
                            mRadioButtonAncFilter4.setText("Hybrid");
                            break;
                        default:
                            return;
                    }

                    if (filter == 1 || filter == 2 || filter == 3 || filter == 4) {
                        mSeekBarAncGain.setEnabled(true);
                    } else {
                        mSeekBarAncGain.setEnabled(false);
                    }
                    mSeekBarPassThroughGain.setEnabled(false);

                    for (RadioButton btn : mAncRadioButtonList) {
                        btn.setEnabled(true);
                    }
                }
            });
        }
    }

    private void resetAncRadioButtons() {
        mRadioButtonAncFilter1.setText("");
        mRadioButtonAncFilter2.setText("");
        mRadioButtonAncFilter3.setText("");
        mRadioButtonAncFilter4.setText("");

        for (RadioButton btn : mAncRadioButtonList) {
            btn.setChecked(false);
            btn.setEnabled(false);
        }
    }

    class StartAncUserTriggerListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, AirohaBaseMsg msg) {
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(mProgressDialog != null) {
                            mProgressDialog.dismiss();
                            gLogger.d(TAG, String.format("Dialog isShowing %s", mProgressDialog.isShowing()));
                        }
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            AirohaAncUserTriggerSettings settings = (AirohaAncUserTriggerSettings)msg.getMsgContent();
                            int result_status = settings.getStatus();
                            mTextAncUserTriggerResult.setText(convertToAirohaAncUserTriggerStatusCode(result_status).getDescription());
                            if(result_status == 1){
                                mTextAncUserTriggerNotice.setText("Please avoid swallowing, talking, walking or turning your head, etc.");
                                mTextAncUserTriggerNotice.setVisibility(View.VISIBLE);
                            }
                            else if(result_status == 11 || result_status == 12){
                                mTextAncUserTriggerNotice.setText("Please avoid playing music, calling or putting earbuds into the case. And keep the connection between both of earbuds.");
                                mTextAncUserTriggerNotice.setVisibility(View.VISIBLE);
                            }
                            int target_index = settings.getFilter();
                            if(target_index == -1){
                                mTextAncUserTriggerTargetFilter.setText(String.format("NA"));
                            }
                            else {
                                mTextAncUserTriggerTargetFilter.setText(String.format("%d", target_index));
                                updateAncUIAfterUserTrigger(target_index);
                            }
                            mActivity.updateMsg(MsgType.GENERAL, "startAncUserTrigger: " + code.getDescription());

                            if(mIsConnected && mActivity != null) {
                                Handler handler = new Handler(mActivity.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        gLogger.d(TAG, "initFlow after UT");
                                        initFlow();
                                    }
                                }, 700);
                            }
                        } else {
                            mTextAncUserTriggerTargetFilter.setText(String.format("NA"));
                            mTextAncUserTriggerResult.setText(String.format("NA"));
                            mActivity.updateMsg(MsgType.ERROR, String.format("startAncUserTrigger: %s, %s", code.getDescription(), (String)msg.getMsgContent()));
                        }
                    }
                    catch (Exception e) {
                        gLogger.e(e);
                    }

                    if(mIsConnected) {
                        mBtnStartAncUserTrigger.setEnabled(true);
                    }
                }
            });
        }
    }

    private AirohaAncUserTriggerStatusCode convertToAirohaAncUserTriggerStatusCode(int status){
        if(status == AirohaAncUserTriggerStatusCode.UPDATE_NEW_COEFFICIENT.getValue())
            return AirohaAncUserTriggerStatusCode.UPDATE_NEW_COEFFICIENT;
        else if(status == AirohaAncUserTriggerStatusCode.RESTORE_FACTORY_COEFFICIENT.getValue())
            return AirohaAncUserTriggerStatusCode.RESTORE_FACTORY_COEFFICIENT;
        else if(status == AirohaAncUserTriggerStatusCode.CHOOSE_TO_DEFAULT_FILTER.getValue())
            return AirohaAncUserTriggerStatusCode.CHOOSE_TO_DEFAULT_FILTER;
        else if(status == AirohaAncUserTriggerStatusCode.MISMATCHED_EAR_TIPS.getValue())
            return AirohaAncUserTriggerStatusCode.MISMATCHED_EAR_TIPS;
        else if(status == AirohaAncUserTriggerStatusCode.LOOSE_WEARING.getValue())
            return AirohaAncUserTriggerStatusCode.LOOSE_WEARING;
        else if(status == AirohaAncUserTriggerStatusCode.TERMINATE_BY_USER.getValue())
            return AirohaAncUserTriggerStatusCode.TERMINATE_BY_USER;
        else if(status == AirohaAncUserTriggerStatusCode.TERMINATE_BY_DEVICE.getValue())
            return AirohaAncUserTriggerStatusCode.TERMINATE_BY_DEVICE;
        else if(status == AirohaAncUserTriggerStatusCode.CHECK_SCENARIO_ERROR.getValue())
            return AirohaAncUserTriggerStatusCode.CHECK_SCENARIO_ERROR;
        else if(status == AirohaAncUserTriggerStatusCode.SILENCE_ENVIRONMENT.getValue())
            return AirohaAncUserTriggerStatusCode.SILENCE_ENVIRONMENT;
        else if(status == AirohaAncUserTriggerStatusCode.COMMAND_RESPONSE_ERROR.getValue())
            return AirohaAncUserTriggerStatusCode.COMMAND_RESPONSE_ERROR;
        else if(status == AirohaAncUserTriggerStatusCode.DATA_ERROR.getValue())
            return AirohaAncUserTriggerStatusCode.DATA_ERROR;
        else if(status == AirohaAncUserTriggerStatusCode.TIMEOUT.getValue())
            return AirohaAncUserTriggerStatusCode.TIMEOUT;
        else if(status == AirohaAncUserTriggerStatusCode.CALCULATE_FAIL.getValue())
            return AirohaAncUserTriggerStatusCode.CALCULATE_FAIL;
        else
            return AirohaAncUserTriggerStatusCode.UNKNOWN;
    }

    class StopAncUserTriggerListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, AirohaBaseMsg msg) {
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            mActivity.updateMsg(MsgType.GENERAL, "stopAncUserTrigger: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MsgType.ERROR, String.format("stopAncUserTrigger: %s, %s", code.getDescription(), (String)msg.getMsgContent()));
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                    mBtnStopAncUserTrigger.setEnabled(true);
                }
            });
        }
    }

    class RestoreAncUserTriggerCoefListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, AirohaBaseMsg msg) {
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            AirohaAncUserTriggerSettings settings = (AirohaAncUserTriggerSettings)msg.getMsgContent();
                            int target_index = settings.getFilter();
                            if(target_index == -1){
                                mTextAncUserTriggerTargetFilter.setText(String.format("NA"));
                            }
                            else {
                                mTextAncUserTriggerTargetFilter.setText(String.format("%d", target_index));
                            }
                            mActivity.updateMsg(MsgType.GENERAL, "restoreAncUserTriggerCoef: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MsgType.ERROR, "restoreAncUserTriggerCoef: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                    mBtnRestoreAncUserTriggerCoef.setEnabled(true);
                }
            });
        }
    }

    class UpdateAncUserTriggerCoefListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, AirohaBaseMsg msg) {
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            AirohaAncUserTriggerSettings settings = (AirohaAncUserTriggerSettings)msg.getMsgContent();
                            int target_index = settings.getFilter();
                            if(target_index == -1){
                                mTextAncUserTriggerTargetFilter.setText(String.format("NA"));
                            }
                            else {
                                mTextAncUserTriggerTargetFilter.setText(String.format("%d", target_index));
                            }
                            mActivity.updateMsg(MsgType.GENERAL, "updateAncUserTriggerCoef: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MsgType.ERROR, "updateAncUserTriggerCoef: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                    mBtnSetBypassFilter.setEnabled(true);
                }
            });
        }
    }

    class UpdateDeviceStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {

        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String strMsg = "";
                        if (msg.isPush()) {
                            strMsg = "[Push Msg] statusCode = "
                                    + code.getValue() + "(" + code.getDescription() + "), msg = "
                                    + msg.getMsgID().getCmdName();

                            if (msg.getMsgID().getCmdName().equals(AirohaMessageID.ANC_STATUS.toString())) {
                                final AirohaAncStatusMsg ancStatusMessage = (AirohaAncStatusMsg) msg;
                                updateAncUI(code, ancStatusMessage, "GetAirohaAncSettings");
                            }
                        } else {
                            strMsg = "[Global Msg] statusCode = "
                                    + code.getValue() + "(" + code.getDescription() + "), msg = "
                                    + msg.getMsgID().getCmdName();
                        }
                        mActivity.updateMsg(MsgType.GENERAL, strMsg);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class EarCanalCompensationStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int earCanalCompensationStatus = ((AirohaEarCanalCompensationInfo)msg.getMsgContent()).getEarCanalCompensationOnOff();
                            mEarCanalCompensationStatus = earCanalCompensationStatus;
                            mActivity.updateMsg(MsgType.GENERAL, "GetEarCanalCompensationStatus: " + code.getDescription());

                            if(mEarCanalCompensationStatus == 0) {
                                mRadioButtonEarCanalCompensationON.setChecked(false);
                                mRadioButtonEarCanalCompensationOFF.setChecked(true);
                            }
                            else{
                                mRadioButtonEarCanalCompensationON.setChecked(true);
                                mRadioButtonEarCanalCompensationOFF.setChecked(false);
                            }
                        } else{
                            mActivity.updateMsg(MsgType.ERROR, String.format("GetEarCanalCompensationStatus: %s, %s", code.getDescription(), (String)msg.getMsgContent()));
                        }
                        mBtnGetEarCanalCompensationStatus.setEnabled(true);
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int earCanalCompensationStatus = ((AirohaEarCanalCompensationInfo)msg.getMsgContent()).getEarCanalCompensationOnOff();
                            mEarCanalCompensationStatus = earCanalCompensationStatus;
                            mActivity.updateMsg(MsgType.GENERAL, "SetEarCanalCompensationStatus: " + code.getDescription());
                        } else{
                            mActivity.updateMsg(MsgType.ERROR, String.format("SetEarCanalCompensationStatus: %s, %s", code.getDescription(), (String)msg.getMsgContent()));
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    AirohaAncListener mAirohaAncListener = new AirohaAncListener(){

        @Override
        public void OnRespSuccess(String stageName) {

        }

        @Override
        public void onResponseTimeout() {

        }

        @Override
        public void onStopped(String stageName) {

        }

        @Override
        public void notifyUpdateDeviceStatus(int moduleId, int statusCode) {

        }

        @Override
        public void notifyUpdateDeviceData(int moduleId, Object obj) {

        }

        @Override
        public void notifyAncUserTriggerResult(AncUserTriggerSettings settings) {

        }

        @Override
        public void notifyAncUserTriggerState(final String state) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mProgressDialog != null) {
                        mProgressDialog.updateProgressMsg(state);
                    }
                }
            });
        }

        @Override
        public void notifyAncStatus(byte status, short gain) {

        }

        @Override
        public void notifyEarCanalCompensationStatus(byte status, byte target_index, byte default_index) {

        }

        @Override
        public void OnAncTurnOn(byte status) {

        }

        @Override
        public void notifySetAncPassThruGain(short gain) {

        }

        @Override
        public void notifyEnvironmentDetectionInfo(EnvironmentDetectionInfo info) {

        }

        @Override
        public void notifyEnvironmentDetectionStatus(byte status, byte enable) {

        }
    };
}
