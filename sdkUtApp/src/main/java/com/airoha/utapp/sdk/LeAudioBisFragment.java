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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.libbase.RaceCommand.constant.RaceType;
import com.airoha.libcommon.AirohaCommonListener;
import com.airoha.libutils.Converter;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaCmdSettings;
import com.airoha.sdk.api.utils.AirohaStatusCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.airoha.libbase.RaceCommand.constant.RaceId.RACE_RELAY_PASS_TO_DST;

public class LeAudioBisFragment extends BaseFragment {

    public static final int RACE_LEAUDIO_SCAN_BROADCAST_SOURCE = 0x2200;
    public static final int RACE_LEAUDIO_SELECT_BROADCAST_SOURCE = 0x2201;
    public static final int RACE_LEAUDIO_PLAY_BIS = 0x2202;
    public static final int RACE_LEAUDIO_STOP_BIS = 0x2203;
    public static final int RACE_LEAUDIO_BIS_RESET = 0x2204;

    private String TAG = LeAudioBisFragment.class.getSimpleName();
    private String LOG_TAG = "[LEA BIS] ";
    private MainActivity mActivity;
    private LeAudioBisFragment mFragment;
    private View mView;

    private byte mPartnerDstID;

    private Spinner mSpinnerLeaBisScanMode;

//    private RadioGroup mRadioGroupScanMode;
//    private RadioButton mRadioButtonFirst;
//    private RadioButton mRadioButtonMaxRSSI;
//    private RadioButton mRadioButtonAll;

    private LinearLayout mLinearLayoutSelect;

    private Button mButtonReset;
    private Button mButtonSelect;
    private Button mButtonScanStop;
    private Button mButtonPlayPause;

    private ListView mListViewSRC;
    private ListView mListViewSubgroup;

    private SourceListAdapter mSourceListAdapter;
    private SubgroupListAdapter mSubgroupListAdapter;

    private int mScanMode = 3;
    private int mSelectedAddressIndex = 0;
    private int mSelectedSubgroupIndex = 0;

    private boolean mIsScanning = false;
    private boolean mIsPlaying = false;

    public LeAudioBisFragment() {
        mTitle = "LE Audio BIS";
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
        mActivity.getAirohaService().getHost().addHostDataListener(TAG, mFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_lea_bis, container, false);

//        mRadioGroupScanMode = mView.findViewById(R.id.radioGroupLeaBisScanMode);

        mSpinnerLeaBisScanMode = mView.findViewById(R.id.spinnerLeaBisScanMode);

        ArrayAdapter<CharSequence> arrAdapSpn
                = ArrayAdapter.createFromResource(mActivity,
                R.array.LEA_BIS_ScanMode, R.layout.spinner_item);

        mSpinnerLeaBisScanMode.setAdapter(arrAdapSpn);
        mSpinnerLeaBisScanMode.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinnerLeaBisScanMode.setSelection(2);

        mButtonReset = mView.findViewById(R.id.buttonLeaBisReset);
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUiEnabled(false);
//                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Relay Reset...");
//                sendResetCmd(true);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Reset...");
                sendResetCmd(false);

                mSourceListAdapter.clear();
                mSourceListAdapter.notifyDataSetChanged();
                mSubgroupListAdapter.clear();
                mSubgroupListAdapter.notifyDataSetChanged();
            }
        });

        mButtonSelect = mView.findViewById(R.id.buttonLeaBisSelect);
        mButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSourceListAdapter.mListReportEA.size() <= 0) {
                    return;
                }
                setUiEnabled(false);
//                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Relay Select...");
//                sendSelectCmd(mSelectedAddressIndex, true);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Select...");
                sendSelectCmd(mSelectedAddressIndex, false);
            }
        });

        mButtonScanStop = mView.findViewById(R.id.buttonLeaBisScanStop);
        mButtonScanStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsScanning) {
                    mScanMode = 0;
                } else {
                    mSourceListAdapter.clear();
                    mSourceListAdapter.notifyDataSetChanged();
                    mSubgroupListAdapter.clear();
                    mSubgroupListAdapter.notifyDataSetChanged();
                    mScanMode = mSpinnerLeaBisScanMode.getSelectedItemPosition()+1;
//                    int id = mRadioGroupScanMode.getCheckedRadioButtonId();
//                    switch (id) {
//                        case R.id.radioButtonLeaBisFirst:
//                            mScanMode = 1;
//                            break;
//                        case R.id.radioButtonLeaBisMaxRSSI:
//                            mScanMode = 2;
//                            break;
//                        case R.id.radioButtonLeaBisAll:
//                            mScanMode = 3;
//                            break;
//                    }
                }
                setUiEnabled(false);
                mListViewSRC.setEnabled(true);
//                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Relay Scan...");
//                sendScanCmd(mScanMode, true);
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Scan...");
                sendScanCmd(mScanMode, false);
            }
        });

        mButtonPlayPause = mView.findViewById(R.id.buttonLeaBisPlayPause);
        mButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUiEnabled(false);
                if (!mIsPlaying) {
//                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Relay Play...");
//                    sendPlayCmd(mSelectedSubgroupIndex, true);
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Play...");
                    sendPlayCmd(mSelectedSubgroupIndex, false);
                } else {
//                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Relay Stop...");
//                    sendStopCmd(true);
                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Stop...");
                    sendStopCmd(false);
                }
            }
        });

        mListViewSRC = mView.findViewById(R.id.listViewLeaBisScannedSRC);
        mListViewSRC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedAddressIndex = i;
            }
        });
        mSourceListAdapter = new SourceListAdapter();
        mListViewSRC.setAdapter(mSourceListAdapter);

        mListViewSubgroup = mView.findViewById(R.id.listViewLeaBisSubgroup);
        mListViewSubgroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedSubgroupIndex = i;
            }
        });
        mSubgroupListAdapter = new SubgroupListAdapter();
        mListViewSubgroup.setAdapter(mSubgroupListAdapter);

        return mView;
    }

    void setUiEnabled(boolean isEnabled) {
        mListViewSRC.setEnabled(isEnabled);
        mListViewSubgroup.setEnabled(isEnabled);
        mButtonReset.setEnabled(isEnabled);
        mButtonSelect.setEnabled(isEnabled);
        mButtonScanStop.setEnabled(isEnabled);
        mButtonPlayPause.setEnabled(isEnabled);
    }

    @Override
    public void onResume() {
        gLogger.d(TAG, "onResume");
        super.onResume();
        if (mActivity.getAirohaService() != null) {
            mActivity.getAirohaService().getHost().addHostDataListener(TAG, mFragment);
            mActivity.getAirohaService().getAirohaCommonMgr().addListener(TAG, mAirohaCommonListener);
            if (AirohaSDK.getInst().isPartnerExisting()) {
                mActivity.getAirohaService().getAirohaCommonMgr().getAvailableDstID();
            }
        }
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
        if (mActivity.getAirohaService() == null) {
            return;
        }

        if (hidden) {
            mActivity.getAirohaService().getHost().removeHostDataListener(TAG);
        } else {
            mActivity.getAirohaService().getHost().addHostDataListener(TAG, mFragment);
            if (mActivity.getAirohaService().getHost().isOpened()) {
                mButtonReset.performClick();
                mButtonScanStop.setText("Scan");
                mButtonPlayPause.setText("Play");
                mSourceListAdapter.clear();
                mSubgroupListAdapter.clear();
                notifyListViewChanged();
            }
        }
    }

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener
            = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    };

    @Override
    public boolean onHostPacketReceived(final byte[] packet) {
        gLogger.d(TAG, "onHostPacketReceived: " + Converter.byte2HexStr(packet));
        byte type = packet[1];
        if (type != RaceType.INDICATION)
            return false;

        final byte status = packet[6];
        gLogger.d(TAG, "status: " + Converter.byte2HexStr(status));

        final int raceID = Converter.bytesToU16(packet[5], packet[4]);
        gLogger.d(TAG, "raceID: " + Converter.byte2HexStr(packet[5]) + Converter.byte2HexStr(packet[4]));

        boolean ret = false;

        switch (raceID) {
            case RACE_LEAUDIO_SCAN_BROADCAST_SOURCE:
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "EA report status= " + status);
                if (status == 0) {
                    if (mSubgroupListAdapter.getCount() == 0) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mIsScanning = true;
                                mButtonSelect.setEnabled(true);
                                mButtonScanStop.setText("Stop");
                                mButtonScanStop.setEnabled(true);
                            }
                        });
                    }
                    mSourceListAdapter.addReportEA(packet);
                    notifyListViewChanged();
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIsScanning = false;
                            mButtonScanStop.setText("Scan");
                        }
                    });
                }
                ret = true;
                break;
            case RACE_LEAUDIO_SELECT_BROADCAST_SOURCE:
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "PA report status= " + status);
                if (status == 0) {
                    mSubgroupListAdapter.updateReportPA(packet);
                    notifyListViewChanged();
                } else {

                }
                ret = true;
                break;
            case RACE_LEAUDIO_PLAY_BIS:
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "BIG Sync status= " + status);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == 0) {
                                mButtonPlayPause.setText("Pause");
                                mIsPlaying = true;
                            } else {
                                mButtonPlayPause.setText("Play");
                                mIsPlaying = false;
                            }
                            mButtonPlayPause.setEnabled(true);
                        }
                    });
                ret = true;
                break;
            case RACE_LEAUDIO_STOP_BIS:
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Terminated status= " + status);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mButtonPlayPause.setText("Play");
                        mIsPlaying = false;
                        mButtonPlayPause.setEnabled(true);
                    }
                });
//                if (status == 0) {
//
//                } else {
//                    mActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mButtonPlayPause.setChecked(false);
//                        }
//                    });
//                }
                ret = true;
                break;
        }

        return ret;
    }

    void notifyListViewChanged() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSourceListAdapter.notifyDataSetChanged();
                mSubgroupListAdapter.notifyDataSetChanged();
            }
        });
    }

    class CustomCmdListener implements AirohaDeviceListener {
        @Override
        public void onRead(final AirohaStatusCode code, final AirohaBaseMsg msg) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (code == AirohaStatusCode.STATUS_SUCCESS || code == AirohaStatusCode.STATUS_FAIL) {
                            byte[] resp = (byte[]) msg.getMsgContent();
                            int raceID = Converter.bytesToU16(resp[5], resp[4]);
                            gLogger.d(TAG, "raceID: " + Converter.byte2HexStr(resp[5]) + Converter.byte2HexStr(resp[4]));
                            byte status = resp[6];
                            setUiEnabled(true);

                            switch (raceID) {
                                case RACE_LEAUDIO_SCAN_BROADCAST_SOURCE:
                                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Scan 5B status= " + Converter.byte2HexStr(status));
                                    if (status == 0) {
                                        if (!mIsScanning) {
                                            mButtonScanStop.setEnabled(false);
                                        } else {
                                            mButtonScanStop.setText("Scan");
                                            mIsScanning = false;
                                        }
                                    }
                                    break;
                                case RACE_LEAUDIO_SELECT_BROADCAST_SOURCE:
                                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Select 5B status= " + Converter.byte2HexStr(status));
                                    if (status == 0 && mIsScanning) {
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mButtonScanStop.setText("Scan");
                                                mIsScanning = false;
                                            }
                                        });
                                    }
                                    break;
                                case RACE_LEAUDIO_PLAY_BIS:
                                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Play 5B status= " + Converter.byte2HexStr(status));
                                    if (status == 0 && !mIsPlaying) {
                                        mButtonPlayPause.setEnabled(false);
                                    }
                                    break;
                                case RACE_LEAUDIO_STOP_BIS:
                                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Stop 5B status= " + Converter.byte2HexStr(status));
                                    if (status == 0 && mIsPlaying) {
                                        mButtonPlayPause.setEnabled(false);
                                    }
                                    break;
                                case RACE_LEAUDIO_BIS_RESET:
                                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Reset 5B status= " + Converter.byte2HexStr(status));
                                    break;
                                case RACE_RELAY_PASS_TO_DST:
                                    mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Relay 5B status= " + Converter.byte2HexStr(status));
                                    break;
                            }
                        }  else {
                            mActivity.updateMsg(MainActivity.MsgType.GENERAL, "Error: " + code.getDescription());
                        }

                    } catch (Exception e) {
                        gLogger.e(e);
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, "rsp timeout!!");
                        setUiEnabled(true);
                    }
                }
            });
        }

        @Override
        public void onChanged(final AirohaStatusCode code, final AirohaBaseMsg msg) {
        }
    }

    void send(List<Byte> cmd, boolean isToPartner) {
        Byte[] cmdArray = cmd.toArray(new Byte[cmd.size()]);

        byte[] cmdRaw = new byte[cmdArray.length];

        for (int i = 0; i < cmdRaw.length; i++) {
            cmdRaw[i] = cmdArray[i];
        }

        if (isToPartner) {
            byte[] payload = new byte[2 + cmdArray.length];

            byte[] dst = new byte[]{0x05, mPartnerDstID};
            System.arraycopy(dst, 0, payload, 0, 2);
            System.arraycopy(cmdRaw, 0, payload, 2, cmdRaw.length);

            byte[] relayRaceID = new byte[]{0x0D, 0x01};
            int relayCmdLength = relayRaceID.length + payload.length;

            List<Byte> cmdByteList = new ArrayList<>();
            cmdByteList.add((byte)0x05);
            cmdByteList.add((byte)0x5A);
            cmdByteList.add((byte) (relayCmdLength & 0xFF));
            cmdByteList.add((byte) ((relayCmdLength >> 8) & 0xFF));
            cmdByteList.add((byte) 0x01);
            cmdByteList.add((byte) 0x0D);
            for(byte b:payload) {
                cmdByteList.add(b);
            }

            cmdRaw = new byte[cmdByteList.size()];
            for (int i = 0; i < cmdRaw.length; i++) {
                cmdRaw[i] = cmdByteList.get(i);
            }
        }

        AirohaCmdSettings setting = new AirohaCmdSettings();
        setting.setRespType(RaceType.RESPONSE);
        setting.setCommand(cmdRaw);
        AirohaSDK.getInst().getAirohaDeviceControl().sendCustomCommand(setting, new CustomCmdListener());
    }

    void sendScanCmd(int mode, boolean isToPartner) {
        List<Byte> cmd = new ArrayList<>();
        cmd.add((byte)0x05);
        cmd.add((byte)0x5A);
        cmd.add((byte)0x03);
        cmd.add((byte)0x00);
        byte[] tmp = Converter.shortToBytes((short)RACE_LEAUDIO_SCAN_BROADCAST_SOURCE);
        cmd.add(tmp[0]);
        cmd.add(tmp[1]);
        cmd.add((byte)mode);
        send(cmd, isToPartner);
    }

    void sendSelectCmd(int index, boolean isToPartner) {
        SourceListAdapter.ReportEA reportEA = mSourceListAdapter.getReportEA(index);
        List<Byte> cmd = new ArrayList<>();
        cmd.add((byte)0x05);
        cmd.add((byte)0x5A);
        cmd.add((byte)0x0A);
        cmd.add((byte)0x00);
        byte[] tmp = Converter.shortToBytes((short)RACE_LEAUDIO_SELECT_BROADCAST_SOURCE);
        cmd.add(tmp[0]);
        cmd.add(tmp[1]);
        for (int i=reportEA.mAddr.length-1; i>=0; --i) {
            cmd.add(reportEA.mAddr[i]);
        }
        cmd.add(reportEA.mAddrType);
        cmd.add(reportEA.mAdvSid);
        send(cmd, isToPartner);
    }

    void sendPlayCmd(int index, boolean isToPartner) {
        List<Byte> cmd = new ArrayList<>();
        cmd.add((byte)0x05);
        cmd.add((byte)0x5A);
        cmd.add((byte)0x03);
        cmd.add((byte)0x00);
        byte[] tmp = Converter.shortToBytes((short)RACE_LEAUDIO_PLAY_BIS);
        cmd.add(tmp[0]);
        cmd.add(tmp[1]);
        cmd.add((byte)index);
        send(cmd, isToPartner);
    }

    void sendStopCmd(boolean isToPartner) {
        List<Byte> cmd = new ArrayList<>();
        cmd.add((byte)0x05);
        cmd.add((byte)0x5A);
        cmd.add((byte)0x02);
        cmd.add((byte)0x00);
        byte[] tmp = Converter.shortToBytes((short)RACE_LEAUDIO_STOP_BIS);
        cmd.add(tmp[0]);
        cmd.add(tmp[1]);
        send(cmd, isToPartner);
    }

    void sendResetCmd(boolean isToPartner) {
        List<Byte> cmd = new ArrayList<>();
        cmd.add((byte)0x05);
        cmd.add((byte)0x5A);
        cmd.add((byte)0x02);
        cmd.add((byte)0x00);
        byte[] tmp = Converter.shortToBytes((short)RACE_LEAUDIO_BIS_RESET);
        cmd.add(tmp[0]);
        cmd.add(tmp[1]);
        send(cmd, isToPartner);
    }

    public static void reverse(byte[] data) {
        for (int left = 0, right = data.length - 1; left < right; left++, right--) {
            // swap the values at the left and right indices
            byte temp = data[left];
            data[left]  = data[right];
            data[right] = temp;
        }
    }

    private class SourceListAdapter extends BaseAdapter {
        private HashMap<String, Integer> mAddrIndexMap;
        private List<ReportEA> mListReportEA;
        private LayoutInflater mInflator;

        public SourceListAdapter() {
            super();
            mAddrIndexMap = new HashMap<>();
            mListReportEA = new LinkedList<>();
            mInflator = mFragment.getLayoutInflater();
        }

        public void addReportEA(byte[] packet) {
            try {
                ReportEA info;
                int index = 7;
                byte type = packet[index];
                ++index;
                byte[] addr = Arrays.copyOfRange(packet, index, 8 + 6);
                reverse(addr);
                index += 6;
                String addrStr = Converter.byte2HexStr(addr).replace(" ", ",");
                if (mAddrIndexMap.containsKey(addrStr)) {
                    info = mListReportEA.get(mAddrIndexMap.get(addrStr));
                } else {
                    info = new ReportEA();
                    mAddrIndexMap.put(addrStr, mListReportEA.size());
                    mListReportEA.add(info);
                }
                info.mAddrType = type;
                gLogger.d(TAG, "mAddrType= " + Converter.byte2HexStr(info.mAddrType));
                info.mAddr = addr;
                gLogger.d(TAG, "addrStr= " + addrStr);
                info.mAdvSid = packet[index];
                gLogger.d(TAG, "mAdvSid= " + Converter.byte2HexStr(info.mAdvSid));
                ++index;
                info.mRssi = packet[index];
                gLogger.d(TAG, "mRssi= " + Converter.byte2HexStr(info.mRssi));
                ++index;
                info.mDataLength = packet[index];
                gLogger.d(TAG, "mDataLength= " + Converter.byte2HexStr(info.mDataLength));
                if (info.mDataLength > 0) {
                    ++index;
                    byte[] data = Arrays.copyOfRange(packet, index, packet.length);
                    gLogger.d(TAG, "data= " + Converter.byte2HexStr(data));
                    info.mData = new PBP(data);
                } else {
                    info.mData = null;
                }
            } catch (Exception e) {
                gLogger.e(e);
            }
        }

        public ReportEA getReportEA(int position) {
            return mListReportEA.get(position);
        }

        public void clear() {
            mAddrIndexMap.clear();
            mListReportEA.clear();
        }

        @Override
        public int getCount() {
            return mListReportEA.size();
        }

        @Override
        public Object getItem(int i) {
            return mListReportEA.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolderSource viewHolderSource;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_lea_bis_source, null);
                viewHolderSource = new ViewHolderSource();
                viewHolderSource.mTextViewAddr = view.findViewById(R.id.listitemLeaBisSourceAddr);
                viewHolderSource.mTextViewRssi = view.findViewById(R.id.listitemLeaBisSourceRSSI);
                view.setTag(viewHolderSource);
            } else {
                viewHolderSource = (ViewHolderSource) view.getTag();
            }

            ReportEA report = mListReportEA.get(i);
            if (report != null) {
                String tmp = "[";
                tmp += Converter.byte2HexStr(report.mAddr).replace(" ", ":");
                tmp += "] ";
                if (report.mData != null) {
                    tmp += report.mData.mBroadcastName;
                }
                viewHolderSource.mTextViewAddr.setText(tmp);
                viewHolderSource.mTextViewRssi.setText(String.valueOf(report.mRssi) + " dBm");
            }

            return view;
        }

        public class ReportEA {
            byte mAddrType;
            byte[] mAddr;
            byte mAdvSid;
            byte mRssi;
            byte mDataLength;
            PBP mData;
        }

        public class PBP {
            HashMap<Byte, byte[]> mTypeValueMap;
            String mBroadcastName = "";

            public PBP(byte[] raw) {
                mTypeValueMap = new HashMap<>();
                int index = 0;
                byte length = raw[index];
                ++index;
                byte type = raw[index];
                while (index < raw.length) {
                    byte[] tmpValue = Arrays.copyOfRange(raw, index + 1, index + length - 1);
                    gLogger.d(TAG, "L= " + length);
                    gLogger.d(TAG, "T= " + Converter.byte2HexStr(type));
                    gLogger.d(TAG, "V= " + Converter.byte2HexStr(tmpValue));
                    switch (type) {
                        case (byte)0xF1: /// BROADCAST_NAME
                            mBroadcastName = Converter.hexToAsciiString(tmpValue);
                            break;
                    }
                    index += length;
                    if (tmpValue != null) {
                        mTypeValueMap.put(type, tmpValue);
                        if (index < raw.length) {
                            length = raw[index];
                            ++index;
                            type = raw[index];
                        }
                    }
                }
            }
        }

        public class ViewHolderSource {
            TextView mTextViewAddr;
            TextView mTextViewRssi;
        }
    }

    private class SubgroupListAdapter extends BaseAdapter {
        private ReportPA mReportPA;
        private ArrayList<Subgroup> mListSubgroup;
        private LayoutInflater mInflator;

        public SubgroupListAdapter() {
            super();
            mListSubgroup = new ArrayList<>();
            mInflator = mFragment.getLayoutInflater();
        }

        public void updateReportPA(byte[] packet) {
            try {
                mReportPA = new ReportPA(packet);
                mListSubgroup.clear();
                for (Subgroup sg:mReportPA.mSubgroupArray) {
                    mListSubgroup.add(sg);
                }
            } catch (Exception e) {
                gLogger.e(e);
            }
        }

        public Subgroup getSubgroup(int position) {
            return mListSubgroup.get(position);
        }

        public void clear() {
            mListSubgroup.clear();
        }

        @Override
        public int getCount() {
            return mListSubgroup.size();
        }

        @Override
        public Object getItem(int i) {
            return mListSubgroup.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolderSubgroup viewHolderSubgroup;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_lea_bis_subgroup, null);
                viewHolderSubgroup = new ViewHolderSubgroup();
                viewHolderSubgroup.mTextViewIndex = (TextView) view.findViewById(R.id.listitemLeaBisSubgroupIndex);
                viewHolderSubgroup.mTextViewSamplingRate = (TextView) view.findViewById(R.id.listitemLeaBisSubgroupSamplingRate);
                viewHolderSubgroup.mTextViewFrameSize = (TextView) view.findViewById(R.id.listitemLeaBisSubgroupFrameSize);
                viewHolderSubgroup.mTextViewFrameDuration = (TextView) view.findViewById(R.id.listitemLeaBisSubgroupFrameDuration);
                view.setTag(viewHolderSubgroup);
            } else {
                viewHolderSubgroup = (ViewHolderSubgroup) view.getTag();
            }

            Subgroup subgroup = mListSubgroup.get(i);
            if (subgroup != null) {
                viewHolderSubgroup.mTextViewIndex.setText("[Index]: " + subgroup.mSubgroupIndex);
                viewHolderSubgroup.mTextViewSamplingRate.setText("[Sampling Rate]: " + subgroup.mCodecConfig.mSamplingRate);
                viewHolderSubgroup.mTextViewFrameSize.setText("[Frame Size]: " + subgroup.mCodecConfig.mFrameSize);
                viewHolderSubgroup.mTextViewFrameDuration.setText("[Frame Duration]: " + subgroup.mCodecConfig.mFrameDuration);
            }

            return view;
        }

        public class ReportPA {
            byte mNumSubgroup;
            Subgroup[] mSubgroupArray;

            public ReportPA(byte[] report) {
                int index = 7;
                mNumSubgroup = report[index];
                ++index;

                mSubgroupArray = new Subgroup[mNumSubgroup];
                for(int i=0; i<mNumSubgroup; ++i) {
                    Subgroup tmp = new Subgroup();
                    tmp.mSubgroupIndex = report[index];
                    ++index;
                    tmp.mCodecID = Arrays.copyOfRange(report, index, index+5);
                    index += 5;
                    tmp.mCodecConfigLength = report[index];
                    ++index;
                    if (tmp.mCodecConfigLength > 0) {
                        tmp.mCodecConfig = new CodecConfig(Arrays.copyOfRange(report, index, index+tmp.mCodecConfigLength));
                        index += tmp.mCodecConfigLength;
                    }

                    tmp.mMetadataLength = report[index];
                    ++index;
                    if (tmp.mMetadataLength > 0) {
                        tmp.mMetadata = Arrays.copyOfRange(report, index, index+tmp.mMetadataLength);
                        index += tmp.mMetadataLength;
                    }
                    mSubgroupArray[i] = tmp;
                }
            }
        }

        public class Subgroup {
            byte mSubgroupIndex;
            byte[] mCodecID;
            byte mCodecConfigLength;
            CodecConfig mCodecConfig;
            byte mMetadataLength;
            byte[] mMetadata;
        }

        public class CodecConfig {
            HashMap<Byte, byte[]> mTypeValueMap;
            String mSamplingRate;
            String mFrameSize;
            String mFrameDuration;

            public CodecConfig(byte[] raw) {
                mTypeValueMap = new HashMap<>();
                int index = 0;
                byte length = raw[index];
                ++index;
                byte type = raw[index];
                ++index;
                while(index < raw.length) {
                    byte[] tmpValue = null;
                    switch (type) {
                        case 1: /// sampling rate
                            tmpValue = Arrays.copyOfRange(raw, index, index+1);
                            mSamplingRate = getSamplingRate(tmpValue[0]);
                            ++index;
                            break;
                        case 2: /// frame duration
                            tmpValue = Arrays.copyOfRange(raw, index, index+1);
                            mFrameDuration = getFrameDuration(tmpValue[0]);
                            ++index;
                            break;
                        case 4: /// frame size
                            tmpValue = Arrays.copyOfRange(raw, index, index+2);
                            mFrameSize = getFrameSize(tmpValue);
                            index += 2;
                            break;
                        default:
                            index += raw.length;
                            break;
                    }
                    if (tmpValue != null) {
                        mTypeValueMap.put(type, tmpValue);
                        if (index < raw.length) {
                            length = raw[index];
                            ++index;
                            type = raw[index];
                            ++index;
                        }
                    }
                }
            }

            public String getSamplingRate(byte value) {
                switch (value) {
                    case 1:
                        return "8000 Hz";
                    case 2:
                        return "11025 Hz";
                    case 3:
                        return "16000 Hz";
                    case 4:
                        return "22050 Hz";
                    case 5:
                        return "24000 Hz";
                    case 6:
                        return "32000 Hz";
                    case 7:
                        return "44100 Hz";
                    case 8:
                        return "48000 Hz";
                    case 9:
                        return "88200 Hz";
                    case 10:
                        return "96000 Hz";
                    case 11:
                        return "176400 Hz";
                    case 12:
                        return "192000 Hz";
                    case 13:
                        return "384000 Hz";
                    default:
                        return "Unknown sampling rate";
                }
            }

            public String getFrameDuration(byte value) {
                switch (value) {
                    case 0:
                        return "7.5 ms";
                    case 1:
                        return "10 ms";
                    default:
                        return "RFU";
                }
            }

            public String getFrameSize(byte[] value) {
                String ret = "";

                int tmp = Converter.bytesToU16(value[1], value[0]);
                ret += tmp + " octets";

                return ret;
            }
        }

        public class ViewHolderSubgroup {
            TextView mTextViewIndex;
            TextView mTextViewSamplingRate;
            TextView mTextViewFrameSize;
            TextView mTextViewFrameDuration;
        }
    }

    AirohaCommonListener mAirohaCommonListener = new AirohaCommonListener() {

        @Override
        public void OnRespSuccess(String stageName) {

        }

        @Override
        public void onStopped(String stageName) {

        }

        @Override
        public void onResponseTimeout() {

        }

        @Override
        public void onNotifyReadChipName(boolean isOk, String chipName) {

        }

        @Override
        public void onNotifyReadDeviceType(byte type, byte awsMode) {

        }

        @Override
        public void onNotifyAvailableDstId(final byte type, final byte dstId) {
            if (dstId != (byte)0xFF) {
                mPartnerDstID = dstId;
            }
        }

        @Override
        public void onNotifyReadNvdmVersion(boolean isOk, byte version) {

        }
    };

}

