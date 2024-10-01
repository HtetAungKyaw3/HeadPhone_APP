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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.airoha.libutils.Converter;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.device.AirohaDevice;
import com.airoha.sdk.api.message.AirohaA2DPConnectionMsg;
import com.airoha.sdk.api.message.AirohaAncSettings;
import com.airoha.sdk.api.message.AirohaAncStatusMsg;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaBatteryInfo;
import com.airoha.sdk.api.message.AirohaDeviceInfoMsg;
import com.airoha.sdk.api.message.AirohaGestureMsg;
import com.airoha.sdk.api.message.AirohaGestureSettings;
import com.airoha.sdk.api.message.AirohaLinkDeviceStatus;
import com.airoha.sdk.api.message.AirohaLinkHistoryInfo;
import com.airoha.sdk.api.message.AirohaMyBudsInfo;
import com.airoha.sdk.api.message.AirohaMyBudsMsg;
import com.airoha.sdk.api.message.AirohaOTAInfo;
import com.airoha.sdk.api.message.AirohaOTAInfoMsg;
import com.airoha.sdk.api.message.AirohaSealingInfo;
import com.airoha.sdk.api.message.AirohaShareModeInfo;
import com.airoha.sdk.api.message.AirohaSidetoneInfo;
import com.airoha.sdk.api.utils.AirohaAiIndex;
import com.airoha.sdk.api.utils.AirohaMessageID;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.sdk.api.utils.ChipType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EmpFragment extends BaseFragment {
    private String TAG = EmpFragment.class.getSimpleName();
    private String LOG_TAG = "[EMP] ";
    private EmpFragment mFragment;
    private View mView;

    private ListView mListViewEmpLinkHistory;
    private Button mBtnEmpGetPairingModeState;
    private RadioButton mRadioBtnEmpPairingModeEnable;
    private RadioButton mRadioBtnEmpPairingModeDisable;
    private Button mBtnEmpRetrieveLinkHistory;
    private Button mBtnEmpConnectDevice;
    private Button mBtnEmpDisconnectDevice;
    private LinkHistoryListAdapter mLinkHistoryListAdapter;
    private int mSelectedDeviceIndex = -1;

    public EmpFragment(){
        mTitle = "EMP UT";
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
        mView = inflater.inflate(R.layout.fragment_emp, container,false);
        initUImember();
        enableAllButton(false);
        return mView;
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
                            if (msg.getMsgID().getCmdName().equals(AirohaMessageID.LINK_DEVICE_STATUS.toString())) {
                                strMsg = "[Push Msg] statusCode = "
                                        + code.getValue() + "(" + code.getDescription() + "), msg = "
                                        + msg.getMsgID().getCmdName();
                                AirohaLinkDeviceStatus deviceStatus = (AirohaLinkDeviceStatus)msg.getMsgContent();
                                mLinkHistoryListAdapter.updateDeviceList(deviceStatus);
                                mLinkHistoryListAdapter.notifyDataSetChanged();
                                mActivity.updateMsg(MainActivity.MsgType.GENERAL, strMsg);
                            } else if (msg.getMsgID().getCmdName().equals(AirohaMessageID.PAIRING_MODE_STATE.toString())) {
                                strMsg = "[Push Msg] statusCode = "
                                        + code.getValue() + "(" + code.getDescription() + "), msg = "
                                        + msg.getMsgID().getCmdName();
                                byte isOn = (byte)msg.getMsgContent();
                                if (isOn == 0) {
                                    mRadioBtnEmpPairingModeDisable.setChecked(true);
                                } else {
                                    mRadioBtnEmpPairingModeEnable.setChecked(true);
                                }
                                mActivity.updateMsg(MainActivity.MsgType.GENERAL, strMsg);
                            }
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                }
            });
        }
    }


    private boolean mIsHidden = false;
    @Override
    public void onStatusChanged(int status) {
        switch (status) {
            case AirohaConnector.DISCONNECTED:
                if(!mIsHidden) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enableAllButton(false);
                            showMessageDialog("BT disconnected. Please reconnect BT.");
                        }
                    });
                }
                break;
            case AirohaConnector.CONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enableAllButton(true);
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

    private void enableAllButton(boolean isEnable) {

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

    private void initUImember() {
        mListViewEmpLinkHistory = mView.findViewById(R.id.listViewEmpLinkHistory);
        mListViewEmpLinkHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedDeviceIndex = i;
            }
        });
        mLinkHistoryListAdapter = new LinkHistoryListAdapter();
        mListViewEmpLinkHistory.setAdapter(mLinkHistoryListAdapter);

        mBtnEmpGetPairingModeState = mView.findViewById(R.id.btnEmpGetPairingModeState);
        mBtnEmpGetPairingModeState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnEmpGetPairingModeState.setEnabled(false);
                mRadioBtnEmpPairingModeDisable.setEnabled(false);
                mRadioBtnEmpPairingModeEnable.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "mBtnEmpGetPairingModeState...");
                AirohaSDK.getInst().getAirohaDeviceControl().getPairingModeState(new PairingModeStateListener());
            }
        });
        mRadioBtnEmpPairingModeDisable = mView.findViewById(R.id.radioButtonEmpPairingDisable);
        mRadioBtnEmpPairingModeDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnEmpGetPairingModeState.setEnabled(false);
                mRadioBtnEmpPairingModeDisable.setEnabled(false);
                mRadioBtnEmpPairingModeEnable.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "mRadioBtnEmpPairingModeDisable...");
                AirohaSDK.getInst().getAirohaDeviceControl().setPairingModeState(0, new PairingModeStateListener());
            }
        });
        mRadioBtnEmpPairingModeEnable = mView.findViewById(R.id.radioButtonEmpPairingEnable);
        mRadioBtnEmpPairingModeEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnEmpGetPairingModeState.setEnabled(false);
                mRadioBtnEmpPairingModeDisable.setEnabled(false);
                mRadioBtnEmpPairingModeEnable.setEnabled(false);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "mRadioBtnEmpPairingModeEnable...");
                AirohaSDK.getInst().getAirohaDeviceControl().setPairingModeState(1, new PairingModeStateListener());
            }
        });

        mBtnEmpRetrieveLinkHistory = mView.findViewById(R.id.btnEmpRetrieveLinkHistory);
        mBtnEmpRetrieveLinkHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnEmpRetrieveLinkHistory.setEnabled(false);
                mBtnEmpConnectDevice.setEnabled(false);
                mBtnEmpDisconnectDevice.setEnabled(false);
                mLinkHistoryListAdapter.clear();
                mLinkHistoryListAdapter.notifyDataSetChanged();
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "mBtnEmpRetrieveLinkHistory...");
                AirohaSDK.getInst().getAirohaDeviceControl().getLinkHistory(new LinkHistoryListener());
            }
        });

        mBtnEmpConnectDevice = mView.findViewById(R.id.btnEmpConnectDevice);
        mBtnEmpConnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnEmpRetrieveLinkHistory.setEnabled(false);
                mBtnEmpConnectDevice.setEnabled(false);
                mBtnEmpDisconnectDevice.setEnabled(false);
                LinkHistoryListAdapter.DeviceInfo deviceInfo = mLinkHistoryListAdapter.getDeviceInfo(mSelectedDeviceIndex);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "mBtnEmpConnectDevice...");
                AirohaSDK.getInst().getAirohaDeviceControl().setDeviceLink(deviceInfo.mAddr, 1, new LinkDeviceStatusListener());
            }
        });

        mBtnEmpDisconnectDevice = mView.findViewById(R.id.btnEmpDisconnectDevice);
        mBtnEmpDisconnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnEmpRetrieveLinkHistory.setEnabled(false);
                mBtnEmpConnectDevice.setEnabled(false);
                mBtnEmpDisconnectDevice.setEnabled(false);
                LinkHistoryListAdapter.DeviceInfo deviceInfo = mLinkHistoryListAdapter.getDeviceInfo(mSelectedDeviceIndex);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "mBtnEmpDisconnectDevice...");
                AirohaSDK.getInst().getAirohaDeviceControl().setDeviceLink(deviceInfo.mAddr, 2, new LinkDeviceStatusListener());
            }
        });
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
        if (gFilePrinter != null) {
            gLogger.removePrinter(gFilePrinter.getPrinterName());
        }
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mIsHidden = true;
        } else {
            mIsHidden = false;
        }
    }

    private int convertToAiIndex(int ui_index){
        switch(ui_index){
            case 0:
                return AirohaAiIndex.GOOGLE_AI.getValue();
            case 1:
                return AirohaAiIndex.AMAZON_AI.getValue();
            case 3:
                return AirohaAiIndex.XIAOWEI_AI.getValue();
            case 2:
            default:
                return AirohaAiIndex.SIRI_AI.getValue();
        }
    }

    private int convertToUiIndex(int ai_index){
        switch(ai_index){
            case 0: //AMAZON_AI
                return 1;
            case 1://GOOGLE_AI
                return 0;
            case 2://XIAOWEI_AI
                return 3;
            case -1://SIRI_AI
            default:
                return 2;
        }
    }

    class PairingModeStateListener implements AirohaDeviceListener {
        void updateUI(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS) {
                            byte isOn = (byte)msg.getMsgContent();
                            if (isOn == 0) {
                                mRadioBtnEmpPairingModeDisable.setChecked(true);
                            } else {
                                mRadioBtnEmpPairingModeEnable.setChecked(true);
                            }
                            mRadioBtnEmpPairingModeDisable.setEnabled(true);
                            mRadioBtnEmpPairingModeEnable.setEnabled(true);
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "PairingModeState: " + isOn);
                        } else {
                            mActivity.updateMsg(MainActivity.MsgType.ERROR, "PairingModeState: " + code.getDescription());
                        }
                    } catch (Exception e) {
                        gLogger.e(e);
                    }
                    mBtnEmpGetPairingModeState.setEnabled(true);
                }
            });
        }

        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI(code, msg);
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI(code, msg);
        }
    }

    class LinkHistoryListener implements AirohaDeviceListener {
        void updateUI(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            try {
                if (code == AirohaStatusCode.STATUS_SUCCESS) {
                    AirohaLinkHistoryInfo linkHistoryInfo = (AirohaLinkHistoryInfo) msg.getMsgContent();
                    for (AirohaLinkDeviceStatus deviceStatus : linkHistoryInfo.getLinkHistory()) {
                        mLinkHistoryListAdapter.updateDeviceList(deviceStatus);
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLinkHistoryListAdapter.notifyDataSetChanged();
                            mBtnEmpConnectDevice.setEnabled(true);
                            mBtnEmpDisconnectDevice.setEnabled(true);
                        }
                    });
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "getLinkHistory: Done");
                } else {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "getLinkHistory: " + code.getDescription());
                }
            } catch (Exception e) {
                gLogger.e(e);
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBtnEmpRetrieveLinkHistory.setEnabled(true);
                }
            });
        }

        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI(code, msg);
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI(code, msg);
        }
    }

    void notifyListViewChanged() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLinkHistoryListAdapter.notifyDataSetChanged();
            }
        });
    }

    class LinkDeviceStatusListener implements AirohaDeviceListener {
        void updateUI(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            try {
                if (code == AirohaStatusCode.STATUS_SUCCESS) {
                    AirohaLinkDeviceStatus deviceStatus = (AirohaLinkDeviceStatus) msg.getMsgContent();
                    mLinkHistoryListAdapter.updateDeviceList(deviceStatus);
                    notifyListViewChanged();
                    if (msg.isPush()) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "DeviceLinkStatus is changed");
                    } else {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "setDeviceLink: Done");
                    }
                } else {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, "LinkDeviceStatus: " + code.getDescription());
                }
            } catch (Exception e) {
                gLogger.e(e);
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBtnEmpConnectDevice.setEnabled(true);
                    mBtnEmpDisconnectDevice.setEnabled(true);
                    mBtnEmpRetrieveLinkHistory.setEnabled(true);
                }
            });
        }

        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI(code, msg);
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            updateUI(code, msg);
        }
    }

    private class LinkHistoryListAdapter extends BaseAdapter {
        private ArrayList<DeviceInfo> mListDeviceInfo;
        private LayoutInflater mInflator;

        public LinkHistoryListAdapter() {
            mListDeviceInfo = new ArrayList<>();
            mInflator = mFragment.getLayoutInflater();
        }

        public void updateDeviceList(AirohaLinkDeviceStatus deviceStatus) {
            try {
                DeviceInfo info = null;
                for (DeviceInfo deviceInfo:mListDeviceInfo) {
                    if (Arrays.equals(deviceInfo.mAddr, deviceStatus.getAddress())) {
                        info = deviceInfo;
                        break;
                    }
                }
                if (info == null) {
                    info = new DeviceInfo();
                    mListDeviceInfo.add(info);
                }
                info.mStatus = deviceStatus.getNewSatatus();
                gLogger.d(TAG, "mStatus= " + info.mStatus);
                info.mAddr = deviceStatus.getAddress();
                gLogger.d(TAG, "mAddr= " + Converter.byte2HexStr(info.mAddr, ":"));
                info.mName = deviceStatus.getName();
                gLogger.d(TAG, "mName= " + Converter.hexToAsciiString(info.mName));
            } catch (Exception e) {
                gLogger.e(e);
            }
        }

        public DeviceInfo getDeviceInfo(int position) {
            return mListDeviceInfo.get(position);
        }

        public void clear() {
            mListDeviceInfo.clear();
        }

        public void reverse(byte[] data) {
            for (int left = 0, right = data.length - 1; left < right; left++, right--) {
                // swap the values at the left and right indices
                byte temp = data[left];
                data[left]  = data[right];
                data[right] = temp;
            }
        }

        @Override
        public int getCount() {
            return mListDeviceInfo.size();
        }

        @Override
        public Object getItem(int i) {
            return mListDeviceInfo.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolderSource viewHolderSource;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_link_history_device, viewGroup, false);
                viewHolderSource = new ViewHolderSource();
                viewHolderSource.mTextViewDeviceName = view.findViewById(R.id.listitemEmpDeviceName);
                viewHolderSource.mTextViewBDA = view.findViewById(R.id.listitemEmpDeviceAddr);
                viewHolderSource.mTextViewStatus = view.findViewById(R.id.listitemEmpDeviceStatus);
                view.setTag(viewHolderSource);
            } else {
                viewHolderSource = (ViewHolderSource) view.getTag();
            }

            DeviceInfo report = mListDeviceInfo.get(position);
            if (report != null) {
                viewHolderSource.mTextViewDeviceName.setText(Converter.hexToAsciiString(report.mName));
                viewHolderSource.mTextViewBDA.setText(Converter.byte2HexStr(report.mAddr, ":"));
                viewHolderSource.mTextViewStatus.setText(report.mStatus.getName());
            }

            return view;
        }

        public class DeviceInfo {
            AirohaLinkDeviceStatus.STATUS mStatus;
            byte[] mAddr;
            byte[] mName;
        }

        public class ViewHolderSource {
            TextView mTextViewBDA;
            TextView mTextViewDeviceName;
            TextView mTextViewStatus;
        }
    }
}

