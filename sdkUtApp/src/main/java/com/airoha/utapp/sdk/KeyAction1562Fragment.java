package com.airoha.utapp.sdk;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.utapp.sdk.MainActivity.MsgType;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.device.AirohaDevice;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaDeviceInfoMsg;
import com.airoha.sdk.api.message.AirohaGestureMsg;
import com.airoha.sdk.api.message.AirohaGestureSettings;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.sdk.api.utils.AudioChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.airoha.sdk.api.message.AirohaGestureSettings.DOUBLE_TAP_LEFT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.DOUBLE_TAP_RIGHT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.LONG_PRESS_LEFT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.LONG_PRESS_RIGHT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.SINGLE_TAP_LEFT;
import static com.airoha.sdk.api.message.AirohaGestureSettings.SINGLE_TAP_RIGHT;


public class KeyAction1562Fragment extends BaseFragment {
    private String TAG = KeyAction1562Fragment.class.getSimpleName();
    private String LOG_TAG = "[KeyAction] ";
    private KeyAction1562Fragment mFragment;
    private View mView;

    // Interaction
    private Button mBtnCheckChannel;
    private TextView mTextAgentChannel;
    private TextView mTextPartnerChannel;

    private Spinner mSpinLeftSingleClickKeyAction;
    private Spinner mSpinLeftDoubleClickKeyAction;
    private Spinner mSpinLeftTripleClickKeyAction;
    private Spinner mSpinLeftLongPressKeyAction;
    private Button mBtnSetLeftKepMap;

    private Spinner mSpinRightSingleClickKeyAction;
    private Spinner mSpinRightDoubleClickKeyAction;
    private Spinner mSpinRightTripleClickKeyAction;
    private Spinner mSpinRightLongPressKeyAction;
    private Button mBtnSetRightKepMap;

    //key action
    private boolean mIsAgentRight = true;
    private boolean mIsPartnerExist = false;
    private List<AirohaGestureSettings> mToSetGestureSettings = null;

    private ArrayAdapter<String> mSingleClickAdapter;
    private ArrayAdapter<String> mDoubleClickAdapter;
    private ArrayAdapter<String> mTripleClickAdapter;
    private ArrayAdapter<String> mLongPressAdapter;

    private static String SINGLE_CLICK = "SINGLE_CLICK";
    private static String DOUBLE_CLICK = "DOUBLE_CLICK";
    private static String TRIPLE_CLICK = "TRIPLE_CLICK";
    private static String LONG_PRESS = "LONG_PRESS";

    public KeyAction1562Fragment(){
        mTitle = "Key Action UT";
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
        AirohaSDK.getInst().getAirohaDeviceConnector().registerConnectionListener(mFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_keyaction_1562, container,false);
        initUImember();
        mBtnCheckChannel.performClick();
        return mView;
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
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getTwsConnectStatus: " + code.getDescription());
                    }
                    else{
                        mActivity.updateMsg(MsgType.ERROR, "getTwsConnectStatus: " + code.getDescription());
                    }
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }

    class DeviceInfoListener implements AirohaDeviceListener {

        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == AirohaStatusCode.STATUS_SUCCESS) {
                        final AirohaDeviceInfoMsg deviceInfoMessage = (AirohaDeviceInfoMsg)msg;
                        LinkedList<AirohaDevice> content = (LinkedList<AirohaDevice>)deviceInfoMessage.getMsgContent();
                        AirohaDevice airohaDevice = content.get(0);
                        if(AirohaSDK.getInst().isPartnerExisting())
                        {
                            AirohaDevice airohaDevicePartner = content.get(1);
                            mTextAgentChannel.setText(airohaDevice.getChannel().getName());
                            mTextPartnerChannel.setText(airohaDevicePartner.getChannel().getName());
                        }
                        else
                        {
                            mTextAgentChannel.setText(airohaDevice.getChannel().getName());
                            mTextPartnerChannel.setText("NA");
                        }
                        if(airohaDevice.getChannel() == AudioChannel.STEREO_RIGHT){
                            mIsAgentRight = true;
                        } else{
                            mIsAgentRight = false;
                        }
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getDeviceInfo: " + code.getDescription());
                    }
                    else{
                        mActivity.updateMsg(MainActivity.MsgType.ERROR, "getDeviceInfo: " + code.getDescription());
                    }
                    enableAllBtns();
                }
            });
        }

        @Override
        public void onChanged(AirohaStatusCode code, AirohaBaseMsg msg) {

        }
    }

//    class MultiAiListener implements AirohaDeviceListener {
//        @Override
//        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
//            mActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (code == AirohaStatusCode.STATUS_SUCCESS) {
//                        int MultiAiIndex = (int)msg.getMsgContent();
//                        mSpinVaIndex.setSelection(convertToUiIndex(MultiAiIndex));
//
//                        mVaIndex = MultiAiIndex;
//                        mActivity.updateMsg(MsgType.GENERAL, "getMultiAi: " + code.getDescription());
//                    }
//                    else {
//                        mActivity.updateMsg(MainActivity.MsgType.ERROR, "getMultiAi: " + code.getDescription());
//                    }
//                    enableAllBtns();
//                }
//            });
//        }
//
//        @Override
//        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
//            mActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (code == AirohaStatusCode.STATUS_SUCCESS) {
//                        int MultiAiIndex = (int)msg.getMsgContent();
//                        mSpinVaIndex.setSelection(convertToUiIndex(MultiAiIndex));
//
//                        mVaIndex = MultiAiIndex;
//                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setMultiAi: " + code.getDescription());
//                    }
//                    else{
//                        mActivity.updateMsg(MainActivity.MsgType.ERROR, "setMultiAi: " + code.getDescription());
//                    }
//                    enableAllBtns();
//                }
//            });
//        }
//    }

    class GestureListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == AirohaStatusCode.STATUS_SUCCESS) {
                        AirohaGestureMsg gestureMessage = (AirohaGestureMsg)msg;
                        updateKeyMapUI(gestureMessage.getMsgContent());
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getGestureStatus: " + code.getDescription());
                    }
                    else{
                        mActivity.updateMsg(MainActivity.MsgType.ERROR, "getGestureStatus: " + code.getDescription());
                    }
                    enableAllBtns();
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == AirohaStatusCode.STATUS_SUCCESS) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setGestureStatus: " + code.getDescription());
                    }
                    else{
                        mActivity.updateMsg(MainActivity.MsgType.ERROR, "setGestureStatus: " + code.getDescription());
                    }
                    enableAllBtns();
                }
            });
        }
    }

    private void initUImember() {
        mBtnCheckChannel = mView.findViewById(R.id.btnCheckAgentChannel);
        mBtnCheckChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllBtns();
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getTwsConnectStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getTwsConnectStatus(new TwsConnectStatusListener());
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getDeviceInfo...");
                AirohaSDK.getInst().getAirohaDeviceControl().getDeviceInfo(new DeviceInfoListener());
//                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getMultiAIStatus...");
//                AirohaSDK.getInst().getAirohaDeviceControl().getMultiAIStatus(new MultiAiListener());
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getGestureStatus...");
                AirohaSDK.getInst().getAirohaDeviceControl().getGestureStatus(AirohaGestureSettings.ALL, new GestureListener());
            }
        });

        mTextAgentChannel = mView.findViewById(R.id.textAgentChannel);
        mTextPartnerChannel = mView.findViewById(R.id.textPartnerChannel);

        mSpinLeftSingleClickKeyAction = mView.findViewById(R.id.spinLeftSingleClickKeyAction);
        mSpinLeftDoubleClickKeyAction = mView.findViewById(R.id.spinLeftDoubleClickKeyAction);
        mSpinLeftTripleClickKeyAction = mView.findViewById(R.id.spinLeftTripleClickKeyAction);
        mSpinLeftLongPressKeyAction = mView.findViewById(R.id.spinLeftLongPressKeyAction);

        mBtnSetLeftKepMap = mView.findViewById(R.id.buttonSetLeftKeyMap);
        mBtnSetLeftKepMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllBtns();
                updateToSetGestureList(true, false);
                AirohaSDK.getInst().getAirohaDeviceControl().setGestureStatus(mToSetGestureSettings, new GestureListener());
            }
        });

        mSpinRightSingleClickKeyAction = mView.findViewById(R.id.spinRightSingleClickKeyAction);
        mSpinRightDoubleClickKeyAction = mView.findViewById(R.id.spinRightDoubleClickKeyAction);
        mSpinRightTripleClickKeyAction = mView.findViewById(R.id.spinRightTripleClickKeyAction);
        mSpinRightLongPressKeyAction = mView.findViewById(R.id.spinRightLongPressKeyAction);

        initSpinnerSelections();

        mBtnSetRightKepMap = mView.findViewById(R.id.buttonSetRightKeyMap);
        mBtnSetRightKepMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllBtns();
                updateToSetGestureList(false, true);
                AirohaSDK.getInst().getAirohaDeviceControl().setGestureStatus(mToSetGestureSettings, new GestureListener());
            }
        });

        disableAllBtns();
    }

    private void disableAllBtns(){
        mBtnSetRightKepMap.setEnabled(false);
        mBtnSetLeftKepMap.setEnabled(false);
        mBtnCheckChannel.setEnabled(false);
    }

    private void enableAllBtns(){
        mBtnCheckChannel.setEnabled(true);

//        if((AirohaSDK.getInst().getDeviceType() == DeviceType.EARBUDS && mIsPartnerExist) ||
//                AirohaSDK.getInst().getDeviceType() == AirohaSDK.DEVICE_TYPE.Headset) {
//            mBtnSetVaIndex.setEnabled(true);
//        }
//
//        if(mVaIndex != convertToAiIndex(mSpinVaIndex.getSelectedItemPosition())){
//            mBtnSetRightKepMap.setEnabled(false);
//            mBtnSetLeftKepMap.setEnabled(false);
//            return;
//        }

        if(mIsPartnerExist){
            mBtnSetRightKepMap.setEnabled(true);
            mBtnSetLeftKepMap.setEnabled(true);
        }
        else{
            if(mIsAgentRight)
                mBtnSetRightKepMap.setEnabled(true);
            else
                mBtnSetLeftKepMap.setEnabled(true);
        }
    }

    private void updateToSetGestureList(boolean update_left, boolean update_right) {
        List<AirohaGestureSettings> infoList = new ArrayList<>();
        if(update_left) {
            infoList.add(new AirohaGestureSettings(SINGLE_TAP_LEFT, getGestureActionId(mSpinLeftSingleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(DOUBLE_TAP_LEFT, getGestureActionId(mSpinLeftDoubleClickKeyAction.getSelectedItem().toString())));
//            infoList.add(new AirohaGestureSettings(TRIPLE_TAP_LEFT, getGestureActionId(mSpinLeftTripleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(LONG_PRESS_LEFT, getGestureActionId(mSpinLeftLongPressKeyAction.getSelectedItem().toString())));
        }
        if(update_right) {
            infoList.add(new AirohaGestureSettings(SINGLE_TAP_RIGHT, getGestureActionId(mSpinRightSingleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(DOUBLE_TAP_RIGHT, getGestureActionId(mSpinRightDoubleClickKeyAction.getSelectedItem().toString())));
//            infoList.add(new AirohaGestureSettings(TRIPLE_TAP_RIGHT, getGestureActionId(mSpinRightTripleClickKeyAction.getSelectedItem().toString())));
            infoList.add(new AirohaGestureSettings(LONG_PRESS_RIGHT, getGestureActionId(mSpinRightLongPressKeyAction.getSelectedItem().toString())));
        }

        gLogger.d(TAG, "Update left = " + update_left);
        gLogger.d(TAG, "Update right = " + update_right);

        mToSetGestureSettings = infoList;
        gLogger.d(TAG, "From UI: gesture info = " + getGestureInfoListString(mToSetGestureSettings));

    }

    private String getGestureInfoListString(List<AirohaGestureSettings> gesture_info) {
        String rtn = "";
        if (gesture_info == null || gesture_info.size() == 0) {
            rtn = "null";
        } else {
            rtn += "<GestureID,ActionID> ";
            for (AirohaGestureSettings info : gesture_info) {
                rtn += "<" + info.getGestureId() + "," + info.getActionId() + "> ";
            }
        }
        return rtn;
    }

    private int getGestureActionId(String gestureActionString){
        if(gestureActionString.equalsIgnoreCase("DISABLE"))
            return AirohaGestureSettings.ACTION_NONE;
        else if(gestureActionString.equalsIgnoreCase("VOLUME_UP"))
            return AirohaGestureSettings.VOLUME_UP;
        else if(gestureActionString.equalsIgnoreCase("VOLUME_DOWN"))
            return AirohaGestureSettings.VOLUME_DOWN;
        else if(gestureActionString.equalsIgnoreCase("FORWARD"))
            return AirohaGestureSettings.NEXT_TRACK;
        else if(gestureActionString.equalsIgnoreCase("BACKWARD"))
            return AirohaGestureSettings.PREVIOUS_TRACK;
        else if(gestureActionString.equalsIgnoreCase("PLAY/PAUSE"))
            return AirohaGestureSettings.PLAY_PAUSE;
        else if (gestureActionString.equalsIgnoreCase("ANC/PASSTHROUGH"))
            return AirohaGestureSettings.SWITCH_ANC_AND_PASSTHROUGH;
        else if (gestureActionString.equalsIgnoreCase("ANC"))
            return AirohaGestureSettings.ANC;
        else if (gestureActionString.equalsIgnoreCase("PASS_THROUGH"))
            return AirohaGestureSettings.PASS_THROUGH;
        else if(gestureActionString.equalsIgnoreCase("WAKE_UP_SIRI"))
            return AirohaGestureSettings.WAKE_UP_SIRI;
        else
            return AirohaGestureSettings.ACTION_DEFAULT;
    }

    private String getGestureActionString(int gestureActionId) {
        switch (gestureActionId) {
            case AirohaGestureSettings.ACTION_NONE:
                return "DISABLE";
            case AirohaGestureSettings.VOLUME_UP:
                return "VOLUME_UP";
            case AirohaGestureSettings.VOLUME_DOWN:
                return "VOLUME_DOWN";
            case AirohaGestureSettings.NEXT_TRACK:
                return "FORWARD";
            case AirohaGestureSettings.PREVIOUS_TRACK:
                return "BACKWARD";
            case AirohaGestureSettings.PLAY_PAUSE:
                return "PLAY/PAUSE";
            case AirohaGestureSettings.SWITCH_ANC_AND_PASSTHROUGH:
                return "ANC/PASSTHROUGH";
            case AirohaGestureSettings.ANC:
                return "ANC";
            case AirohaGestureSettings.PASS_THROUGH:
                return "PASS_THROUGH";
            case AirohaGestureSettings.WAKE_UP_SIRI:
                return "WAKE_UP_SIRI";
            default:
                return "DEFAULT";
        }
    }

    private void initSpinnerSelections() {
        mSingleClickAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, getSpinnerDefaultSelections(SINGLE_CLICK));
        mDoubleClickAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, getSpinnerDefaultSelections(DOUBLE_CLICK));
        mTripleClickAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, getSpinnerDefaultSelections(TRIPLE_CLICK));
        mLongPressAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, getSpinnerDefaultSelections(LONG_PRESS));

        mSpinRightSingleClickKeyAction.setAdapter(mSingleClickAdapter);
        mSpinLeftSingleClickKeyAction.setAdapter(mSingleClickAdapter);
        mSpinRightDoubleClickKeyAction.setAdapter(mDoubleClickAdapter);
        mSpinLeftDoubleClickKeyAction.setAdapter(mDoubleClickAdapter);
        mSpinRightTripleClickKeyAction.setAdapter(mTripleClickAdapter);
        mSpinLeftTripleClickKeyAction.setAdapter(mTripleClickAdapter);
        mSpinRightLongPressKeyAction.setAdapter(mLongPressAdapter);
        mSpinLeftLongPressKeyAction.setAdapter(mLongPressAdapter);
        mSingleClickAdapter.notifyDataSetChanged();
        mDoubleClickAdapter.notifyDataSetChanged();
        mTripleClickAdapter.notifyDataSetChanged();
        mLongPressAdapter.notifyDataSetChanged();
    }

    private ArrayList getSpinnerDefaultSelections(String keyType){
        if(keyType.equalsIgnoreCase(SINGLE_CLICK) || keyType.equalsIgnoreCase(DOUBLE_CLICK) || keyType.equalsIgnoreCase(TRIPLE_CLICK)){
            return new ArrayList<>(Arrays.asList("DEFAULT", "DISABLE", "PLAY/PAUSE", "FORWARD", "BACKWARD", "VOLUME_UP", "VOLUME_DOWN", "ANC/PASSTHROUGH", "ANC", "PASS_THROUGH"));
        }
        else if(keyType.equalsIgnoreCase(LONG_PRESS)){
            return new ArrayList<>(Arrays.asList("DEFAULT", "DISABLE", "WAKE_UP_SIRI"));
        }
        return new ArrayList<>();
    }

    private void updateKeyMapUI(List<AirohaGestureSettings> list) {
        mSpinLeftSingleClickKeyAction.setSelection(0);
        mSpinRightSingleClickKeyAction.setSelection(0);
        mSpinLeftDoubleClickKeyAction.setSelection(0);
        mSpinRightDoubleClickKeyAction.setSelection(0);
        mSpinLeftLongPressKeyAction.setSelection(0);
        mSpinRightLongPressKeyAction.setSelection(0);
        mSpinLeftTripleClickKeyAction.setSelection(0);
        mSpinRightTripleClickKeyAction.setSelection(0);

        for(int i = 0; i < list.size(); i++){
            AirohaGestureSettings info = list.get(i);
            String actionStr = getGestureActionString(info.getActionId());

            int gesture = info.getGestureId();

            switch (gesture){
                case SINGLE_TAP_LEFT:
                    for(int j = 0; j < mSpinLeftSingleClickKeyAction.getCount(); j++){
                        if(mSpinLeftSingleClickKeyAction.getItemAtPosition(j).toString().equalsIgnoreCase(actionStr)){
                            mSpinLeftSingleClickKeyAction.setSelection(j);
                            break;
                        }
                    }
                    break;
                case SINGLE_TAP_RIGHT:
                    for(int j = 0; j < mSpinRightSingleClickKeyAction.getCount(); j++){
                        if(mSpinRightSingleClickKeyAction.getItemAtPosition(j).toString().equalsIgnoreCase(actionStr)){
                            mSpinRightSingleClickKeyAction.setSelection(j);
                            break;
                        }
                    }
                    break;
                case AirohaGestureSettings.DOUBLE_TAP_LEFT:
                    for(int j = 0; j < mSpinLeftDoubleClickKeyAction.getCount(); j++){
                        if(mSpinLeftDoubleClickKeyAction.getItemAtPosition(j).toString().equalsIgnoreCase(actionStr)){
                            mSpinLeftDoubleClickKeyAction.setSelection(j);
                            break;
                        }
                    }
                    break;
                case DOUBLE_TAP_RIGHT:
                    for(int j = 0; j < mSpinRightDoubleClickKeyAction.getCount(); j++){
                        if(mSpinRightDoubleClickKeyAction.getItemAtPosition(j).toString().equalsIgnoreCase(actionStr)){
                            mSpinRightDoubleClickKeyAction.setSelection(j);
                            break;
                        }
                    }
                    break;
                case AirohaGestureSettings.LONG_PRESS_LEFT:
                    for(int j = 0; j < mSpinLeftLongPressKeyAction.getCount(); j++){
                        if(mSpinLeftLongPressKeyAction.getItemAtPosition(j).toString().equalsIgnoreCase(actionStr)){
                            mSpinLeftLongPressKeyAction.setSelection(j);
                            break;
                        }
                    }
                    break;
                case AirohaGestureSettings.LONG_PRESS_RIGHT:
                    for(int j = 0; j < mSpinRightLongPressKeyAction.getCount(); j++){
                        if(mSpinRightLongPressKeyAction.getItemAtPosition(j).toString().equalsIgnoreCase(actionStr)){
                            mSpinRightLongPressKeyAction.setSelection(j);
                            break;
                        }
                    }
                    break;
                case AirohaGestureSettings.TRIPLE_TAP_LEFT:
                    for(int j = 0; j < mSpinLeftTripleClickKeyAction.getCount(); j++){
                        if(mSpinLeftTripleClickKeyAction.getItemAtPosition(j).toString().equalsIgnoreCase(actionStr)){
                            mSpinLeftTripleClickKeyAction.setSelection(j);
                            break;
                        }
                    }
                    break;
                case AirohaGestureSettings.TRIPLE_TAP_RIGHT:
                    for(int j = 0; j < mSpinRightTripleClickKeyAction.getCount(); j++){
                        if(mSpinRightTripleClickKeyAction.getItemAtPosition(j).toString().equalsIgnoreCase(actionStr)){
                            mSpinRightTripleClickKeyAction.setSelection(j);
                            break;
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onStatusChanged(final int status) {
        switch (status) {
            case AirohaConnector.CONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enableAllBtns();
                    }
                });
                break;
            case AirohaConnector.DISCONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disableAllBtns();
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
}

