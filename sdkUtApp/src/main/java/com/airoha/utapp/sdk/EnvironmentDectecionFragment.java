package com.airoha.utapp.sdk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.sdk.AB1568DeviceControl;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaEnvironmentDetectionInfo;
import com.airoha.sdk.api.utils.AirohaMessageID;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.utapp.sdk.MainActivity.MsgType;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class EnvironmentDectecionFragment extends BaseFragment{
    private String TAG = EnvironmentDectecionFragment.class.getSimpleName();
    private String LOG_TAG = "[ED] ";
    private EnvironmentDectecionFragment mFragment;
    private View mView;

    private boolean mIsPartnerExist = false;
    private boolean mIsAgentRight = false;

    private RadioButton mRadioButtonEnvironmentDetectionON;
    private RadioButton mRadioButtonEnvironmentDetectionOFF;
    private TextView mTextLevel;
    private TextView mTextFFGain;
    private TextView mTextFBGain;
    private TextView mTextLeftStationaryNoise;
    private TextView mTextRightStationaryNoise;
    private Button mBtnGetEnvironmentDetectionInfo;
    private Button mBtnGetEnvironmentDetectionStatus;
    private int mEnvironmentDetectionStatus = 0x00;

    public EnvironmentDectecionFragment(){
        mTitle = "ED";
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
        AirohaSDK.getInst().getAirohaDeviceControl().registerGlobalListener(new UpdateDeviceStatusListener());
        AirohaSDK.getInst().getAirohaDeviceConnector().registerConnectionListener(mFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_environment_detection, container,false);
        initUImember();
        initFlow();
        return mView;
    }

    private void initUImember() {
        Bundle bundle = getArguments();
        gTargetAddr = bundle.getString(BaseFragment.EXTRAS_DEVICE_BDA);

        mRadioButtonEnvironmentDetectionON = mView.findViewById(R.id.radioButton_environment_detection_enable);
        mRadioButtonEnvironmentDetectionON.setOnClickListener(mOnClickListener);
        mRadioButtonEnvironmentDetectionOFF = mView.findViewById(R.id.radioButton_environment_detection_disable);
        mRadioButtonEnvironmentDetectionOFF.setOnClickListener(mOnClickListener);
        mTextLevel = mView.findViewById(R.id.textViewLevel);
        mTextFFGain = mView.findViewById(R.id.textViewFFGain);
        mTextFBGain = mView.findViewById(R.id.textViewFBGain);
        mTextLeftStationaryNoise = mView.findViewById(R.id.textViewLeftStationaryNoise);
        mTextRightStationaryNoise = mView.findViewById(R.id.textViewRightStationaryNoise);
        mBtnGetEnvironmentDetectionStatus = mView.findViewById(R.id.btnGetEnvironmentDetectionStatus);
        mBtnGetEnvironmentDetectionStatus.setOnClickListener(mOnClickListener);
        mBtnGetEnvironmentDetectionInfo = mView.findViewById(R.id.btnGetEnvironmentDetectionInfo);
        mBtnGetEnvironmentDetectionInfo.setOnClickListener(mOnClickListener);

    }

    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.radioButton_environment_detection_enable:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MsgType.GENERAL, "setEnvironmentDetectionStatus...");
                        mEnvironmentDetectionStatus = 0x01;
                        AirohaSDK.getInst().getAirohaDeviceControl().setEnvironmentDetectionStatus(mEnvironmentDetectionStatus, new EnvironmentDetectionStatusListener());
                    }
                    break;
                case R.id.radioButton_environment_detection_disable:
                    if (((RadioButton)v).isChecked()) {
                        mActivity.updateMsg(MsgType.GENERAL, "setEnvironmentDetectionStatus...");
                        mEnvironmentDetectionStatus = 0x00;
                        AirohaSDK.getInst().getAirohaDeviceControl().setEnvironmentDetectionStatus(mEnvironmentDetectionStatus, new EnvironmentDetectionStatusListener());
                    }
                    break;
                case R.id.btnGetEnvironmentDetectionStatus:
                    mBtnGetEnvironmentDetectionStatus.setEnabled(false);
                    mActivity.updateMsg(MsgType.GENERAL, "getEnvironmentDetectionStatus...");
                    AirohaSDK.getInst().getAirohaDeviceControl().getEnvironmentDetectionStatus(new EnvironmentDetectionStatusListener());
                    break;
                case R.id.btnGetEnvironmentDetectionInfo:
                    mBtnGetEnvironmentDetectionStatus.setEnabled(false);
                    mActivity.updateMsg(MsgType.GENERAL, "getEnvironmentDetectionStatus...");
                    AirohaSDK.getInst().getAirohaDeviceControl().getEnvironmentDetectionStatus(new EnvironmentDetectionStatusListener());
                    mBtnGetEnvironmentDetectionInfo.setEnabled(false);
                    mActivity.updateMsg(MsgType.GENERAL, "getEnvironmentDetectionInfo...");
                    AirohaSDK.getInst().getAirohaDeviceControl().getEnvironmentDetectionInfo(new EnvironmentDetectionInfoListener());
                    break;
            }
        }
    };

    private void initFlow(){
        if (mActivity == null || mActivity.getAirohaService() == null) {
            return;
        }
        mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "getTwsConnectStatus...");
        AirohaSDK.getInst().getAirohaDeviceControl().getTwsConnectStatus(new TwsConnectStatusListener());
        mActivity.updateMsg(MsgType.GENERAL, "getEnvironmentDetectionStatus...");
        AirohaSDK.getInst().getAirohaDeviceControl().getEnvironmentDetectionStatus(new EnvironmentDetectionStatusListener());
        mActivity.updateMsg(MsgType.GENERAL, "getEnvironmentDetectionInfo...");
        AirohaSDK.getInst().getAirohaDeviceControl().getEnvironmentDetectionInfo(new EnvironmentDetectionInfoListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        gLogger.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        gLogger.d(TAG, "onPause");
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
            mIsHidden = true;
        }
        else {
            mIsHidden = false;
            initFlow();
        }
    }

    @Override
    public void changePrivilege(PrivilegeState state){
    }

    private void setViewEnable(boolean status){
        mRadioButtonEnvironmentDetectionON.setEnabled(status);
        mRadioButtonEnvironmentDetectionOFF.setEnabled(status);
        mBtnGetEnvironmentDetectionStatus.setEnabled(status);
        mBtnGetEnvironmentDetectionInfo.setEnabled(status);
    }

    private boolean mIsHidden = false;
    @Override
    public void onStatusChanged(final int status) {
        switch (status) {
            case AirohaConnector.DISCONNECTED:
                if(!mIsHidden) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setViewEnable(false);
                            showMessageDialog("BT disconnected. Please reconnect BT.");
                        }
                    });
                }
                break;
            case AirohaConnector.CONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setViewEnable(true);
                    }
                });
                break;
            case AirohaConnector.CONNECTING:
            case AirohaConnector.DISCONNECTING:
            case AirohaConnector.WAITING_CONNECTABLE:
            default:
                break;
        }
    }

    private void showMessageDialog(String msg) {
        if(mActivity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Info.");
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mActivity.changeFragment(MainActivity.FragmentIndex.MENU);
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

                            if (msg.getMsgID().getCmdName().equals(AirohaMessageID.ENVIRONMENT_DETECTION_INFO.toString())) {
                                AirohaEnvironmentDetectionInfo info = (AirohaEnvironmentDetectionInfo) msg.getMsgContent();
                                mTextLevel.setText(info.getLevel().getName());
                                if(mEnvironmentDetectionStatus == 1 && info.getLevel().getValue() == 0){
                                    mTextLevel.setText("No Reduce Gain");
                                }
                                mTextFFGain.setText("" + info.getFFGain());
                                mTextFBGain.setText("" + info.getFBGain());
                                mTextLeftStationaryNoise.setText("" + info.getLeftStationaryNoise());
                                mTextRightStationaryNoise.setText("" + info.getRightStationaryNoise());
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

    class EnvironmentDetectionStatusListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            int environmentDetectionStatus = (int)msg.getMsgContent();
                            mEnvironmentDetectionStatus = environmentDetectionStatus;
                            mActivity.updateMsg(MsgType.GENERAL, "GetEnvironmentDetectionStatus: " + code.getDescription());

                            if(mEnvironmentDetectionStatus == 0) {
                                mRadioButtonEnvironmentDetectionON.setChecked(false);
                                mRadioButtonEnvironmentDetectionOFF.setChecked(true);
                            }
                            else{
                                mRadioButtonEnvironmentDetectionON.setChecked(true);
                                mRadioButtonEnvironmentDetectionOFF.setChecked(false);
                            }
                        } else{
                            mActivity.updateMsg(MsgType.ERROR, String.format("GetEnvironmentDetectionStatus: %s, %s", code.getDescription(), (String)msg.getMsgContent()));
                        }
                        mBtnGetEnvironmentDetectionStatus.setEnabled(true);
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
                            int environmentDetectionStatus = (int)msg.getMsgContent();
                            mEnvironmentDetectionStatus = environmentDetectionStatus;
                            mActivity.updateMsg(MsgType.GENERAL, "SetEnvironmentDetectionStatus: " + code.getDescription());
                        } else{
                            mActivity.updateMsg(MsgType.ERROR, String.format("SetEnvironmentDetectionStatus: %s, %s", code.getDescription(), (String)msg.getMsgContent()));
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }

    class EnvironmentDetectionInfoListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            if (msg != null) {
                                AirohaEnvironmentDetectionInfo info = (AirohaEnvironmentDetectionInfo) msg.getMsgContent();
                                mTextLevel.setText(info.getLevel().getName());
                                if(mEnvironmentDetectionStatus == 1 && info.getLevel().getValue() == 0){
                                    mTextLevel.setText("No Reduce Gain");
                                }
                                mTextFFGain.setText("" + info.getFFGain());
                                mTextFBGain.setText("" + info.getFBGain());
                                mTextLeftStationaryNoise.setText("" + info.getLeftStationaryNoise());
                                mTextRightStationaryNoise.setText("" + info.getRightStationaryNoise());
                            }
                            mActivity.updateMsg(MsgType.GENERAL, "GetEnvironmentDetectionInfo: " + code.getDescription());
                        } else {
                            mActivity.updateMsg(MsgType.ERROR, String.format("GetEnvironmentDetectionInfo: %s, %s", code.getDescription(), (String)msg.getMsgContent()));
                        }
                        mBtnGetEnvironmentDetectionInfo.setEnabled(true);
                    }  catch (Exception e) {
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
