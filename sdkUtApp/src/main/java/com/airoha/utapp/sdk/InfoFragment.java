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
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.airoha.liblinker.physical.gatt.GattErrorCode;
import com.airoha.liblinker.physical.spp.SppErrorCode;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.utils.ConnectionProtocol;
import com.airoha.sdk.api.utils.ConnectionStatus;
import com.airoha.utapp.sdk.MainActivity.MsgType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InfoFragment extends BaseFragment {
    private String TAG = InfoFragment.class.getSimpleName();
    private InfoFragment mFragment;
    private View mView;
    private MyMsgAdapter mMsgAdapter;
    private List<String> mConnectionMsgList;
    private List<String> mGeneralMsgList;
    private List<String> mErrorMsgList;

    private LinearLayout mLinearLayoutInfo;
    private LinearLayout mLinearLayoutStatusMsg;
    private LinearLayout mLinearLayoutErrorMsg;
    private TextView mTextViewStatusMsg;
    private TextView mTextViewErrorMsg;
    private TextView mTextViewAddrTitle;
    private TextView mTextViewDeviceInfo;
    private Button mBtnHistory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;
        mMsgAdapter = new MyMsgAdapter(mActivity, R.layout.message);
        mConnectionMsgList = new ArrayList<>();
        mGeneralMsgList = new ArrayList<>();
        mErrorMsgList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_info, container,false);
        mTextViewAddrTitle = mView.findViewById(R.id.textViewAddrTitle);
        mTextViewDeviceInfo = mView.findViewById(R.id.textViewDeviceInfo);

        mLinearLayoutInfo = mView.findViewById(R.id.linearLayoutInfo);
        mLinearLayoutStatusMsg = mView.findViewById(R.id.linearLayoutStatusMsg);
        mLinearLayoutErrorMsg = mView.findViewById(R.id.linearLayoutErrorMsg);
        mBtnHistory = mView.findViewById(R.id.btnHistory);
        mBtnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMsgList();
            }
        });
        mTextViewStatusMsg = mView.findViewById(R.id.textViewStatusMsg);
        mTextViewErrorMsg = mView.findViewById(R.id.textViewErrorMsg);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mActivity.getAirohaService().getTargetAddr() == null || mActivity.getAirohaService().getTargetAddr().length() == 0)
            return;
        if (mActivity.getAirohaService().getTargetPhy() == null || mActivity.getAirohaService().getTargetPhy() == ConnectionProtocol.PROTOCOL_UNKNOWN)
            return;
        /*if (mTargetAddr == null || mTargetAddr.length() == 0)
            return;
        if (mTargetPhy == null || mTargetPhy.length() == 0)
            return;*/

        updateTargetAddr(mActivity.getAirohaService().getTargetAddr(), mActivity.getAirohaService().getTargetPhy().toString().replace("PROTOCOL_", ""));
        updateMsg(MsgType.GENERAL, "onResume APP");
        mLinearLayoutInfo.setVisibility(View.VISIBLE);

        if (mActivity.getAirohaService().getAirohaLinker() == null)
            return;
        if (mActivity.getAirohaService().getAirohaLinker().isConnected(mActivity.getAirohaService().getTargetAddr()))
            updateDeviceTextColor(Color.GREEN);
        else
            updateDeviceTextColor(Color.RED);
    }

    public void updateMsg(final MsgType msgType, final String msg){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS    ");
        String timeStr = sdf.format(new Date());
        mMsgAdapter.add(timeStr + msg);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (msgType){
                    case CONNECTION:
                        gLogger.d(TAG, "[Connection]" + msg);
                        mTextViewStatusMsg.setText(msg);
                        mLinearLayoutStatusMsg.setVisibility(View.VISIBLE);
                        mLinearLayoutErrorMsg.setVisibility(View.GONE);
                        mConnectionMsgList.add(Integer.toString(mMsgAdapter.getCount() - 1));
                        break;
                    case GENERAL:
                        gLogger.d(TAG, "[General]" + msg);
                        mTextViewStatusMsg.setText(msg);
                        mLinearLayoutStatusMsg.setVisibility(View.VISIBLE);
                        mLinearLayoutErrorMsg.setVisibility(View.GONE);
                        mGeneralMsgList.add(Integer.toString(mMsgAdapter.getCount() - 1));
                        break;
                    case ERROR:
                        gLogger.d(TAG, "[Error]" + msg);
                        mTextViewErrorMsg.setText(msg);
                        mLinearLayoutErrorMsg.setVisibility(View.VISIBLE);
                        mErrorMsgList.add(Integer.toString(mMsgAdapter.getCount() - 1));
                        break;
                }
            }
        });
    }

    public void updateTargetAddr(String addr, String phy){
        if (addr == null ||phy == null) {
            mLinearLayoutInfo.setVisibility(View.GONE);
            return;
        }

        mLinearLayoutInfo.setVisibility(View.VISIBLE);
        mTextViewDeviceInfo.setText(addr + "(" + phy+ ")");
        updateDeviceTextColor(Color.BLACK);
        mLinearLayoutStatusMsg.setVisibility(View.GONE);
        mLinearLayoutErrorMsg.setVisibility(View.GONE);
        mMsgAdapter.clear();
    }
    private void updateDeviceTextColor(int color){
        mTextViewAddrTitle.setTextColor(color);
        mTextViewDeviceInfo.setTextColor(color);
    }

    @Override
    public void onStatusChanged(final int status) {
        switch (status) {
            case AirohaConnector.CONNECTING:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMsg(MsgType.CONNECTION,"Connecting");
                        updateDeviceTextColor(Color.BLUE);
                    }
                });
                break;
            case AirohaConnector.CONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "";
                        msg += mActivity.getAirohaService().getTargetAddr();
                        msg += "(" + mActivity.getAirohaService().getTargetPhy().toString().replace("PROTOCOL_", "");
                        msg += ")[" + AirohaSDK.getInst().getDeviceType().getName() + "]";
                        mTextViewDeviceInfo.setText(msg);
                        updateDeviceTextColor(Color.rgb(0, 0xDD, 0));
                        updateMsg(MsgType.CONNECTION,"Connected");
                    }
                });
                break;
            case AirohaConnector.DISCONNECTING:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMsg(MsgType.CONNECTION,"Disconnecting");
                        updateDeviceTextColor(Color.BLUE);
                    }
                });
                break;
            case AirohaConnector.DISCONNECTED:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMsg(MsgType.CONNECTION,"Disconnected");
                        updateDeviceTextColor(Color.RED);
                    }
                });
                break;
            default:
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMsg(MsgType.CONNECTION, "Error: " + ConnectionStatus.getDescription(status));
                        updateDeviceTextColor(Color.RED);
                        if (status == ConnectionStatus.INITIALIZATION_FAILED.getValue()) {
                            showAlertDialog(mActivity, "Error", "Initialization Failed!");
                        }
                    }
                });
                break;
        }
    }


//    @Override
//    public void onHostConnected() {
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                updateMsg(MsgType.CONNECTION,"Connected");
//                updateDeviceTextColor(Color.GREEN);
//            }
//        });
//    }
//
//    @Override
//    public void onHostDisconnected() {
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                updateMsg(MsgType.CONNECTION,"Disconnected");
//                updateDeviceTextColor(Color.RED);
//            }
//        });
//    }
//
//    @Override
//    public void onHostInitialized() {
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                updateMsg(MsgType.CONNECTION,"Initialized");
//                updateDeviceTextColor(Color.GREEN);
//            }
//        });
//    }
//
//    @Override
//    public void onHostError(final int errorCode) {
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                updateMsg(MsgType.CONNECTION,"onHostError: " + errorCode);
//                updateDeviceTextColor(Color.RED);
//            }
//        });
//    }

    private void showMsgList() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);;
        View titleView = inflater.inflate(R.layout.custom_dialog_title, null, false);
        TextView tvTitle = titleView.findViewById(R.id.textView_dialog_title);
        tvTitle.setText("Message List");

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setCustomTitle(titleView);

        final ArrayAdapter<String> arrayAdapter = mMsgAdapter;

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class MyMsgAdapter extends ArrayAdapter<String>{

        private LayoutInflater mInflator;
        public MyMsgAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            mInflator = mActivity.getLayoutInflater();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
            view = super.getView(position, view, parent);
            TextView textView = view.findViewById(R.id.textView_msg);

//            if(mConnectionMsgList.contains(Integer.toString(position)))
//                textView.setTextColor(Color.BLUE);
//            if(mErrorMsgList.contains(Integer.toString(position)))
//                textView.setTextColor(Color.RED);

            return view;
        }
    }
}
