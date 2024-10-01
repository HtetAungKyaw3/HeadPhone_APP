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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.libbase.constant.AgentPartnerEnum;
import com.airoha.liblogger.printer.FilePrinter;
import com.airoha.libmmi.AirohaMmiListener;
import com.airoha.libmmi.AirohaMmiMgr;
import com.airoha.libmmi.model.A2dpInfo;
import com.airoha.libmmi.model.AntennaInfo;
import com.airoha.libmmi.model.FieldTrialRelatedNV;
import com.airoha.libmmi158x.AirohaMmiListener158x;
import com.airoha.libutils.Common.AirohaGestureInfo;
import com.airoha.sdk.AB158xDeviceControl;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.utils.ChipType;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AntennaFragment extends BaseFragment implements OnChartValueSelectedListener {
    private String TAG = AntennaFragment.class.getSimpleName();
    private String LOG_TAG = "[FieldTrial] ";
    private AntennaFragment mFragment;
    private View mView;
    private Handler mHandler;

    private Button mBtnStartAntennaUT;
    private Button mBtnStopAntennaUT;
    private Spinner mSpinnerRptTime;
    private Spinner mSpinnerTestRole;
    private CheckBox mCbEnableStatistics;
    private EditText mEditStatisticsCount;

    private TextView mTextViewAgentStatus;
    private TextView mTextViewPartnerStatus;

    private Button mBtnGetNV;
    private CheckBox mCbGetA2dpInfoInLoop;
    private boolean mGetA2dpInfoInLoopEnabled = false;

    //log list
    private ListView mLogAgentView;
    private ListView mLogPartnerView;
    private static ArrayAdapter<String> gAgentLogAdapter;
    private static ArrayAdapter<String> gPartnerLogAdapter;

    //Antenna UT
    private boolean mIsPartnerExist = false;
    private boolean mIsReporting = false;
    private int mReportTime;
    private AgentPartnerEnum mTestRole;

    private boolean mReportDataStatus;
    private boolean mIsAgentResp;
    private boolean mIsPartnerResp;
    private boolean mHasPrintPartnerErrMsg;

    private static int RETRY_MAX_CNT = 5;
    private int mRetryCount = 0;
    private int mLogMaxCount = 50;

    private FilePrinter mAgentFilePrinter;
    private FilePrinter mPartnerFilePrinter;
    private FilePrinter mStatisticsReportFilePrinter;

    private boolean mStatisticsEnable = false;
    private int mStatisticsCount = 0;

    private int mAgentRssiCount = 0;
    private int mAgentHeadsetTotalRssi = 0;
    private int mAgentHeadsetMaxRssi = 0;
    private int mAgentHeadsetMinRssi = 0;
    private int mAgentPhoneTotalRssi = 0;
    private int mAgentPhoneMaxRssi = 0;
    private int mAgentPhoneMinRssi = 0;
    private LineDataSet mAgentHeadsetRssiLineDataSet;
    private LineDataSet mAgentPhoneRssiLineDataSet;

    private int mPartnerRssiCount = 0;
    private int mPartnerHeadsetTotalRssi = 0;
    private int mPartnerHeadsetMaxRssi = 0;
    private int mPartnerHeadsetMinRssi = 0;
    private int mPartnerPhoneTotalRssi = 0;
    private int mPartnerPhoneMaxRssi = 0;
    private int mPartnerPhoneMinRssi = 0;

    private int mMaxPointCountForScreenshot = 50;
    private int mChartPointCounter = 0;
    private LineChart mRssiChart;
    private LineDataSet mPartnerHeadsetRssiLineDataSet;
    private LineDataSet mPartnerPhoneRssiLineDataSet;

    private boolean mAgentIsRightDevice = false;

    private final String[] RSSI_LINE_NAME = new String[] {
            "agent_Device","agent_Phone", "partner_Device","partner_Phone"
    };

    private final int[] colors = new int[] {
            Color.BLUE,
            Color.rgb(0x66, 0xDD, 0x00),
            Color.RED,
            Color.rgb(0xDD, 0xAA, 0x00),
    };
    public AntennaFragment(){
        mTitle = "Field Trial";
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

        if(gAgentLogAdapter == null) {
            gAgentLogAdapter = new ArrayAdapter<>(mActivity, R.layout.message);
        }
        if(gPartnerLogAdapter == null) {
            gPartnerLogAdapter = new ArrayAdapter<>(mActivity, R.layout.message);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_antenna, container,false);
        initUImember();
        mRssiChart = mView.findViewById(R.id.chart);
        return mView;
    }

    private void initUImember() {
        mBtnStartAntennaUT = mView.findViewById(R.id.buttonStartAntennaUT);
        mBtnStopAntennaUT = mView.findViewById(R.id.buttonStopAntennaUT);
        mSpinnerRptTime = mView.findViewById(R.id.rpt_time_spinner);
        mSpinnerTestRole = mView.findViewById(R.id.test_role_spinner);
        mSpinnerTestRole.setSelection(2);
        mCbEnableStatistics = mView.findViewById(R.id.cb_enable_statistics);
        mEditStatisticsCount = mView.findViewById(R.id.edit_statistics_count);

        mTextViewAgentStatus = mView.findViewById(R.id.textView_AgentStatus);
        mTextViewPartnerStatus = mView.findViewById(R.id.textView_PartnerStatus);

        mLogAgentView = (ListView) mView.findViewById(R.id.listView_agent_log);
        mLogAgentView.setAdapter(gAgentLogAdapter);
        mLogPartnerView = (ListView) mView.findViewById(R.id.listView_partner_log);
        mLogPartnerView.setAdapter(gPartnerLogAdapter);

        mBtnStartAntennaUT.setEnabled(false);
        mBtnStartAntennaUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsReporting = true;
                if(mSpinnerTestRole.getSelectedItemPosition() == 0)
                    mTestRole = AgentPartnerEnum.AGENT;
                else if(mSpinnerTestRole.getSelectedItemPosition() == 1)
                    mTestRole = AgentPartnerEnum.PARTNER;
                else if(mSpinnerTestRole.getSelectedItemPosition() == 2)
                    mTestRole = AgentPartnerEnum.BOTH;

                mGetA2dpInfoInLoopEnabled = mCbGetA2dpInfoInLoop.isChecked();

                setUiStatus();
                initChart();

                if(mCbEnableStatistics.isChecked()){
                    int count = Integer.parseInt(mEditStatisticsCount.getText().toString());
                    startTest(count);
                }
                else{
                    startTest(0);
                }
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Start antenna test.");
            }
        });

        mBtnStopAntennaUT.setEnabled(false);
        mBtnStopAntennaUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsReporting = false;
                setUiStatus();
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Stop antenna test.");
            }
        });

        mSpinnerRptTime.setEnabled(false);
        mSpinnerTestRole.setEnabled(false);

        mCbGetA2dpInfoInLoop = mView.findViewById(R.id.cb_get_A2dp_info);
        mBtnGetNV = mView.findViewById(R.id.buttonGetA2dpInfo);
        mBtnGetNV.setEnabled(false);
        mBtnGetNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "get Field Trial Related NV");
                if(AirohaSDK.getInst().getChipType() == ChipType.AB158x
                        || AirohaSDK.getInst().getChipType() == ChipType.AB157x
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1568_V3
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1565_DUAL_V3
                        || AirohaSDK.getInst().getChipType() == ChipType.AB1568_DUAL_V3){
                    ((AB158xDeviceControl)AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr158x().getFieldTrialRelatedNV();
                }
                else {
                    ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).getFieldTrialRelatedNV();
                }
            }
        });

        setUiStatus();
    }

    private void initLineDataSet(LineDataSet set, int color) {
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setCircleColor(color);
        set.setLineWidth(2f);
        set.setCircleRadius(2.5f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
    }

    private void initChart(){
        mRssiChart.setOnChartValueSelectedListener(this);

        // enable description text
        mRssiChart.getDescription().setEnabled(true);
        Description description = new Description();
        description.setText("RSSI");
        mRssiChart.setDescription(description);

        // enable touch gestures
        mRssiChart.setTouchEnabled(true);

        // enable scaling and dragging
        mRssiChart.setDragEnabled(true);
        mRssiChart.setScaleEnabled(true);
        mRssiChart.setDrawGridBackground(true);

        // set an alternative background color
        mRssiChart.setBackgroundColor(Color.BLACK);

        mRssiChart.setMinimumHeight(400);

        resetRssiLineChart();

        // get the legend (only possible after setting data)
        Legend l = mRssiChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mRssiChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setSpaceMax(10);

        YAxis leftAxis = mRssiChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(0f);
        leftAxis.setAxisMinimum(-100f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mRssiChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void resetRssiLineChart() {
        int index = 0;
        mAgentHeadsetRssiLineDataSet =  new LineDataSet(null, RSSI_LINE_NAME[index]);
        initLineDataSet(mAgentHeadsetRssiLineDataSet, colors[index]);
        ++index;
        mAgentPhoneRssiLineDataSet =  new LineDataSet(null, RSSI_LINE_NAME[index]);
        initLineDataSet(mAgentPhoneRssiLineDataSet, colors[index]);
        ++index;
        mPartnerHeadsetRssiLineDataSet =  new LineDataSet(null, RSSI_LINE_NAME[index]);
        initLineDataSet(mPartnerHeadsetRssiLineDataSet, colors[index]);
        ++index;
        mPartnerPhoneRssiLineDataSet =  new LineDataSet(null, RSSI_LINE_NAME[index]);
        initLineDataSet(mPartnerPhoneRssiLineDataSet, colors[index]);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineData data = new LineData(dataSets);
        dataSets.add(mAgentHeadsetRssiLineDataSet);
        dataSets.add(mAgentPhoneRssiLineDataSet);
        dataSets.add(mPartnerHeadsetRssiLineDataSet);
        dataSets.add(mPartnerPhoneRssiLineDataSet);

        data.setValueTextColor(Color.WHITE);

        // add empty data
        mRssiChart.setData(data);
    }

    private void refreshChart() {
        LineData data = mRssiChart.getData();
        data.notifyDataChanged();

        // let the chart know it's data has changed
        mRssiChart.notifyDataSetChanged();
        // chart.setVisibleYRange(30, AxisDependency.LEFT);

        // limit the number of visible entries
        mRssiChart.setVisibleXRangeMaximum(60);

        // move to the latest entry
        mRssiChart.moveViewToX(data.getEntryCount());
    }

    private void saveLineChart() {
        String timeStamp = new SimpleDateFormat("_yyyyMMdd_HH_mm_ss_SSS").format(Calendar.getInstance().getTime());
        String filename = AirohaSDK.getInst().mChipType.getName() + timeStamp;
        mRssiChart.saveToGallery(filename);
    }

    private void initFlow(){
        setUiStatus();

        if(mActivity.getAirohaService().getAirohaLinker().isConnected(gTargetAddr)) {
            ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkPartnerExistence();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mActivity.getAirohaService() != null) {
            mActivity.getAirohaService().getAirohaLinker().removeHostListener(gTargetAddr, TAG);
            ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).removeListener(TAG);
            if(AirohaSDK.getInst().getChipType() == ChipType.AB158x
                    || AirohaSDK.getInst().getChipType() == ChipType.AB157x
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1568_V3
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1565_DUAL_V3
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1568_DUAL_V3){
                ((AB158xDeviceControl)AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr158x().removeListener(TAG);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler = new Handler();

        if(mActivity.getAirohaService() != null) {
            mActivity.getAirohaService().getAirohaLinker().addHostListener(gTargetAddr, TAG, mFragment);
            ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).addListener(TAG, mAirohaMmiListener);
            if(AirohaSDK.getInst().getChipType() == ChipType.AB158x
                    || AirohaSDK.getInst().getChipType() == ChipType.AB157x
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1568_V3
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1565_DUAL_V3
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1568_DUAL_V3){
                ((AB158xDeviceControl)AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr158x().addListener(TAG, mAirohaMmiListner158x);
            }
            initFlow();
        }
    }

    @Override
    public void onDestroy() {
        gLogger.d(TAG, "onDestroy");
        if (mAgentFilePrinter != null) {
            gLogger.removePrinter(mAgentFilePrinter.getPrinterName());
        }
        if (mPartnerFilePrinter != null) {
            gLogger.removePrinter(mPartnerFilePrinter.getPrinterName());
        }
        if (mStatisticsReportFilePrinter != null) {
            gLogger.removePrinter(mStatisticsReportFilePrinter.getPrinterName());
        }
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        gLogger.d(TAG, "onHiddenChanged: hidden=" + hidden);
        super.onHiddenChanged(hidden);
        if (mActivity.getAirohaService() == null) {
            return;
        }
        if(mIsReporting){
            return;
        }
        if (hidden) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            mActivity.getAirohaService().getAirohaLinker().removeHostListener(gTargetAddr, TAG);
            ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).removeListener(TAG);
            if(AirohaSDK.getInst().getChipType() == ChipType.AB158x
                    || AirohaSDK.getInst().getChipType() == ChipType.AB157x
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1568_V3
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1565_DUAL_V3
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1568_DUAL_V3){
                ((AB158xDeviceControl)AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr158x().removeListener(TAG);
            }
        }
        else {
            mHandler = new Handler();
            mActivity.getAirohaService().getAirohaLinker().addHostListener(gTargetAddr, TAG, mFragment);
            ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).addListener(TAG, mAirohaMmiListener);
            if(AirohaSDK.getInst().getChipType() == ChipType.AB158x
                    || AirohaSDK.getInst().getChipType() == ChipType.AB157x
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1568_V3
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1565_DUAL_V3
                    || AirohaSDK.getInst().getChipType() == ChipType.AB1568_DUAL_V3){
                ((AB158xDeviceControl)AirohaSDK.getInst().getAirohaDeviceControl()).getAirohaMmiMgr158x().addListener(TAG, mAirohaMmiListner158x);
            }
            initFlow();
        }
    }

    private void setUiStatus()
    {
        if (mActivity.getAirohaService() == null) {
            return;
        }
        if(!mActivity.getAirohaService().getAirohaLinker().isConnected(gTargetAddr)) {
            mBtnStartAntennaUT.setEnabled(false);
            mBtnStopAntennaUT.setEnabled(false);
            mBtnGetNV.setEnabled(false);
            mSpinnerRptTime.setEnabled(false);
            mSpinnerTestRole.setEnabled(false);
            mCbEnableStatistics.setEnabled(false);
            mEditStatisticsCount.setEnabled(false);
            mCbGetA2dpInfoInLoop.setEnabled(false);
        }
        else if(!mIsReporting) {
            mBtnStartAntennaUT.setEnabled(true);
            mBtnStopAntennaUT.setEnabled(false);
            mBtnGetNV.setEnabled(true);
            mSpinnerRptTime.setEnabled(true);
            mSpinnerTestRole.setEnabled(true);
            mCbEnableStatistics.setEnabled(true);
            mEditStatisticsCount.setEnabled(true);
            mCbGetA2dpInfoInLoop.setEnabled(true);
        }
        else{
            mBtnStartAntennaUT.setEnabled(false);
            mBtnStopAntennaUT.setEnabled(true);
            mBtnGetNV.setEnabled(false);
            mSpinnerRptTime.setEnabled(false);
            mSpinnerTestRole.setEnabled(false);
            mCbEnableStatistics.setEnabled(false);
            mEditStatisticsCount.setEnabled(false);
            mCbGetA2dpInfoInLoop.setEnabled(false);
        }
    }

    private void addLogMsg(final boolean is_Agent, final String msg) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS    ");
                String timeStr = sdf.format(new Date());
                if (is_Agent)
                    synchronized (gAgentLogAdapter) {
                        gAgentLogAdapter.add(timeStr + msg);
                        if (gAgentLogAdapter.getCount() >= mLogMaxCount) {
                            gAgentLogAdapter.remove(gAgentLogAdapter.getItem(0));
                        }
                    }
                else {
                    synchronized (gPartnerLogAdapter) {
                        gPartnerLogAdapter.add(timeStr + msg);
                        if (gPartnerLogAdapter.getCount() >= mLogMaxCount) {
                            gPartnerLogAdapter.remove(gPartnerLogAdapter.getItem(0));
                        }
                    }
                }
                writeLogMsgFile(is_Agent, timeStr + msg);
            }
        });
    }

    private void writeLogMsgFile(boolean is_Agent, String msg) {
        if (mAgentFilePrinter != null) {
            if (is_Agent) {
                gLogger.addLogToQueue(mAgentFilePrinter.getPrinterName(), TAG, msg);
            } else {
                gLogger.addLogToQueue(mPartnerFilePrinter.getPrinterName(), TAG, msg);
            }
        }
    }

    private void wirteStatisticsReportLogFile(String msg){
        gLogger.addLogToQueue(mStatisticsReportFilePrinter.getPrinterName(), TAG, msg);
    }

    private void initFlagsNParameters() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());

        if (mAgentFilePrinter != null) {
            gLogger.removePrinter(mAgentFilePrinter.getPrinterName());
        }
        mAgentFilePrinter = createAntennaLogFile("AntennaUT_Agent_" + timeStr + ".txt");
        if (mPartnerFilePrinter != null) {
            gLogger.removePrinter(mPartnerFilePrinter.getPrinterName());
        }
        mPartnerFilePrinter = createAntennaLogFile("AntennaUT_Partner_" + timeStr + ".txt");
        mReportDataStatus = false;
        mReportTime = 1000;
        mRetryCount = 0;
        mHasPrintPartnerErrMsg = false;
        mIsReporting = true;
    }

    private void initStatisticsParameters() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        if (mStatisticsReportFilePrinter != null) {
            gLogger.removePrinter(mStatisticsReportFilePrinter.getPrinterName());
        }
        mStatisticsReportFilePrinter = createAntennaLogFile("AntennaUT_StatisticsReport_" + timeStr + ".txt");
        mAgentRssiCount = 0;
        mAgentHeadsetTotalRssi = 0;
        mAgentHeadsetMaxRssi = 0;
        mAgentHeadsetMinRssi = 0;
        mAgentPhoneTotalRssi = 0;
        mAgentPhoneMaxRssi = 0;
        mAgentPhoneMinRssi = 0;

        mPartnerRssiCount = 0;
        mPartnerHeadsetTotalRssi = 0;
        mPartnerHeadsetMaxRssi = 0;
        mPartnerHeadsetMinRssi = 0;
        mPartnerPhoneTotalRssi = 0;
        mPartnerPhoneMaxRssi = 0;
        mPartnerPhoneMinRssi = 0;
    }

    private FilePrinter createAntennaLogFile(String fileName) {
        HashMap<String, String> map = new HashMap<>();
        map.put("file_name", fileName);

        // new file printer
        FilePrinter filePrinter = new FilePrinter(mActivity);
        if (!filePrinter.init(map)) {
            return null;
        }

        gLogger.addPrinter(filePrinter);
        gLogger.d(TAG, "Create log, file name: " + fileName);
        gLogger.d(TAG, "Ver:" + BuildConfig.VERSION_NAME);

        return filePrinter;
    }

    public void startTest(int times) {
        ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).checkAgentChannel();
        mRetryCount = 0;
        if(times != 0){
            mStatisticsEnable = true;
            mStatisticsCount = times;
            initStatisticsParameters();
        }
        else{
            mStatisticsEnable = false;
        }
        initFlagsNParameters();
        mReportTime = (mSpinnerRptTime.getSelectedItemPosition() + 1) * 1000;
        if(mHandler != null) {
            mTextViewAgentStatus.setText("");
            mTextViewPartnerStatus.setText("");
            if (mTestRole == AgentPartnerEnum.BOTH) {
                mMaxPointCountForScreenshot = 100;
                mHandler.postDelayed(new checkBothFlow(), 1000);
            } else if (mTestRole == AgentPartnerEnum.AGENT) {
                mMaxPointCountForScreenshot = 50;
                mHandler.postDelayed(new checkAgentOrPartnerFlow(), 1000);
            } else {
                mMaxPointCountForScreenshot = 50;
                mHandler.postDelayed(new checkAgentOrPartnerFlow(), 1000);
            }
        }
    }

    private void calcStatistics(boolean role, int headset_rssi, int phone_rssi)
    {
        if(role){
            mAgentRssiCount++;
            mAgentHeadsetTotalRssi += headset_rssi;
            mAgentPhoneTotalRssi += phone_rssi;

            if(mAgentRssiCount == 1){
                mAgentHeadsetMaxRssi = headset_rssi;
                mAgentHeadsetMinRssi = headset_rssi;
                mAgentPhoneMaxRssi = phone_rssi;
                mAgentPhoneMinRssi = phone_rssi;
                return;
            }
            if(headset_rssi > mAgentHeadsetMaxRssi){
                mAgentHeadsetMaxRssi = headset_rssi;
            }
            else if(headset_rssi <= mAgentHeadsetMinRssi){
                mAgentHeadsetMinRssi = headset_rssi;
            }
            if(phone_rssi > mAgentPhoneMaxRssi){
                mAgentPhoneMaxRssi = phone_rssi;
            }
            else if(phone_rssi <= mAgentPhoneMinRssi){
                mAgentPhoneMinRssi = phone_rssi;
            }
        }
        else{
            mPartnerRssiCount++;
            mPartnerHeadsetTotalRssi += headset_rssi;
            mPartnerPhoneTotalRssi += phone_rssi;

            if(mPartnerRssiCount == 1){
                mPartnerHeadsetMaxRssi = headset_rssi;
                mPartnerHeadsetMinRssi = headset_rssi;
                mPartnerPhoneMaxRssi = phone_rssi;
                mPartnerPhoneMinRssi = phone_rssi;
                return;
            }
            if(headset_rssi > mPartnerHeadsetMaxRssi){
                mPartnerHeadsetMaxRssi = headset_rssi;
            }
            else if(headset_rssi <= mPartnerHeadsetMinRssi){
                mPartnerHeadsetMinRssi = headset_rssi;
            }
            if(phone_rssi > mPartnerPhoneMaxRssi){
                mPartnerPhoneMaxRssi = phone_rssi;
            }
            else if(phone_rssi <= mPartnerPhoneMinRssi){
                mPartnerPhoneMinRssi = phone_rssi;
            }
        }
        updateDeviceStatus();
    }

    private void updateDeviceStatus() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DecimalFormat df =new DecimalFormat("#.##");
                if (mTestRole != AgentPartnerEnum.PARTNER) {
                    String tmp = "[Agent]: ";
                    tmp += (mAgentIsRightDevice ? "Right":"Left") + " device\n";
                    tmp += "device RSSI";
                    tmp += ": min= " + mAgentHeadsetMinRssi;
                    tmp += ", max= " + mAgentHeadsetMaxRssi;
                    tmp += ", div= " + Math.abs(mAgentHeadsetMaxRssi - mAgentHeadsetMinRssi);
                    tmp += ", avg= " + df.format((float)mAgentHeadsetTotalRssi / (float)mAgentRssiCount);
                    tmp += "\r\n";
                    tmp += "phone RSSI";
                    tmp += ": min= " + mAgentPhoneMinRssi;
                    tmp += ", max= " + mAgentPhoneMaxRssi;
                    tmp += ", div= " + Math.abs(mAgentPhoneMaxRssi - mAgentPhoneMinRssi);
                    tmp += ", avg= " + df.format((float)mAgentPhoneTotalRssi / (float)mAgentRssiCount);
                    mTextViewAgentStatus.setText(tmp);
                }

                if (mTestRole != AgentPartnerEnum.AGENT) {
                    String tmp = "[Partner]: ";
                    tmp += (mAgentIsRightDevice ? "Left":"Right") + " device\n";
                    tmp += "device RSSI";
                    tmp += ": min= " + mPartnerHeadsetMinRssi;
                    tmp += ", max= " + mPartnerHeadsetMaxRssi;
                    tmp += ", div= " + Math.abs(mPartnerHeadsetMaxRssi - mPartnerHeadsetMinRssi);
                    tmp += ", avg= " + df.format((float)mPartnerHeadsetTotalRssi / (float)mPartnerRssiCount);
                    tmp += "\r\n";
                    tmp += "phone RSSI";
                    tmp += ": min= " + mPartnerPhoneMinRssi;
                    tmp += ", max= " + mPartnerPhoneMaxRssi;
                    tmp += ", div= " + Math.abs(mPartnerPhoneMaxRssi - mPartnerPhoneMinRssi);
                    tmp += ", avg= " + df.format((float)mPartnerPhoneTotalRssi / (float)mPartnerRssiCount);
                    mTextViewPartnerStatus.setText(tmp);
                }
            }
        });
    }

    private void saveRssiInfo()
    {
        if(mTestRole != AgentPartnerEnum.BOTH) {
            if (mAgentRssiCount >= mStatisticsCount || mPartnerRssiCount >= mStatisticsCount) {
                mIsReporting = false;
                mStatisticsEnable = false;
                setUiStatus();
                saveLineChart();
                showAlertDialog(mActivity, "Statistics Report", genStatisticsReport());
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Finish antenna test.");
            }
        }
        else{
            if (mAgentRssiCount >= mStatisticsCount && mPartnerRssiCount >= mStatisticsCount) {
                mIsReporting = false;
                mStatisticsEnable = false;
                setUiStatus();
                saveLineChart();
                showAlertDialog(mActivity, "Statistics Report", genStatisticsReport());
                mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Finish antenna test.");
            }
        }
    }

    private String genStatisticsReport(){
        DecimalFormat df =new DecimalFormat("#.##");

        String msg = "";
        msg += mStatisticsCount + " Times" + ", Report Interval: " + mReportTime/1000 + "s" + "\r\n\r\n";
        if(mAgentRssiCount > 0) {
            msg += "Agent(" + (mAgentIsRightDevice ? "Right":"Left")
                    + " Device RSSI)\r\n"
                    + "RSSI_avg: " + df.format((float)mAgentHeadsetTotalRssi / (float)mStatisticsCount) + "\r\n"
                    + "RSSI_min: " + mAgentHeadsetMinRssi + "\r\n"
                    + "RSSI_max: " + mAgentHeadsetMaxRssi + "\r\n"
                    + "RSSI diviation: " + Math.abs(mAgentHeadsetMaxRssi - mAgentHeadsetMinRssi) + "\r\n\r\n"
                    + "Agent (Phone RSSI) \r\n"
                    + "RSSI_avg: " + df.format((float)mAgentPhoneTotalRssi / (float)mStatisticsCount) + "\r\n"
                    + "RSSI_min: " + mAgentPhoneMinRssi + "\r\n"
                    + "RSSI_max: " + mAgentPhoneMaxRssi + "\r\n"
                    + "RSSI diviation: " + Math.abs(mAgentPhoneMaxRssi - mAgentPhoneMinRssi) + "\r\n\r\n";
        }
        if(mPartnerRssiCount > 0){
            msg += "Partner("
                    + (mAgentIsRightDevice ? "Left":"Right")
                    + " Device RSSI)\r\n"
                    + "RSSI_avg: " + df.format((float)mPartnerHeadsetTotalRssi / (float)mStatisticsCount) + "\r\n"
                    + "RSSI_min: " + mPartnerHeadsetMinRssi + "\r\n"
                    + "RSSI_max: " + mPartnerHeadsetMaxRssi + "\r\n"
                    + "RSSI diviation: " + Math.abs(mPartnerHeadsetMaxRssi - mPartnerHeadsetMinRssi) + "\r\n\r\n"
                    + "Partner (Phone RSSI) \r\n"
                    + "RSSI_avg: " + df.format((float)mPartnerPhoneTotalRssi / (float)mStatisticsCount) + "\r\n"
                    + "RSSI_min: " + mPartnerPhoneMinRssi + "\r\n"
                    + "RSSI_max: " + mPartnerPhoneMaxRssi + "\r\n"
                    + "RSSI diviation: " + Math.abs(mPartnerPhoneMaxRssi - mPartnerPhoneMinRssi) + "\r\n\r\n";
        }
        wirteStatisticsReportLogFile(msg);
        return msg;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private class checkBothFlow implements Runnable {
        private boolean is_first_time = true;
        public void run() {
            if(!is_first_time && mRetryCount >= RETRY_MAX_CNT) {
                if (!mIsAgentResp) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "Agent no response.");
                    addLogMsg(true, "No Response.");
                }
                if (!mIsPartnerResp) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "Partner no response.");
                    addLogMsg(false, "No Response.");
                }
                if (!mIsAgentResp || !mIsPartnerResp) {
                    mIsReporting = false;
                    setUiStatus();
                    return;
                }
            }
            if (mIsReporting) {
                is_first_time = false;
                if(!mIsAgentResp || !mIsPartnerResp){
                    ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).getAntennaInfo(!mIsAgentResp, !mIsPartnerResp);
                    mHandler.postDelayed(this, 200);
                    mRetryCount++;
                }
                else {
                    mRetryCount = 0;
                    mIsAgentResp = false;
                    mIsPartnerResp = false;
                    if (mGetA2dpInfoInLoopEnabled) {
                        ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).getA2dpInfo();
                    }
                    ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).getAntennaInfo(true, true);
                    mHandler.postDelayed(this, mReportTime);
                }
            }
        }
    }

    private class checkAgentOrPartnerFlow implements Runnable {
        private boolean is_first_time = true;
        public void run() {
            if (mTestRole == AgentPartnerEnum.PARTNER && !mIsPartnerExist) {
                addLogMsg(false, "Partner is not connected.");
                mIsReporting = false;
                setUiStatus();
                return;
            }

            if(!is_first_time && mRetryCount >= RETRY_MAX_CNT) {
                if (mTestRole == AgentPartnerEnum.AGENT && !mIsAgentResp) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "Agent no response.");
                    addLogMsg(true, "No Response.");
                    mIsReporting = false;
                    setUiStatus();
                    return;
                }
                if (mTestRole == AgentPartnerEnum.PARTNER && !mIsPartnerResp) {
                    mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "Partner no response.");
                    addLogMsg(false, "No Response.");
                    mIsReporting = false;
                    setUiStatus();
                    return;
                }
            }

            if (mIsReporting) {
                is_first_time = false;
                if(!mIsAgentResp && mTestRole == AgentPartnerEnum.AGENT){
                    ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).getAntennaInfo(mTestRole == AgentPartnerEnum.AGENT, mTestRole == AgentPartnerEnum.PARTNER);
                    mHandler.postDelayed(this, 200);
                    mRetryCount++;
                }
                else if(!mIsPartnerResp && mTestRole == AgentPartnerEnum.PARTNER){
                    ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).getAntennaInfo(mTestRole == AgentPartnerEnum.AGENT, mTestRole == AgentPartnerEnum.PARTNER);
                    mHandler.postDelayed(this, 200);
                    mRetryCount++;
                }
                else {
                    mRetryCount = 0;
                    mIsAgentResp = false;
                    mIsPartnerResp = false;
                    if (mGetA2dpInfoInLoopEnabled) {
                        ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).getA2dpInfo();
                    }
                    ((AirohaMmiMgr) AirohaSDK.getInst().getAirohaDeviceControl().getAirohaMmiMgr()).getAntennaInfo(mTestRole == AgentPartnerEnum.AGENT, mTestRole == AgentPartnerEnum.PARTNER);
                    mHandler.postDelayed(this, mReportTime);
                }
            }
        }
    }

    AirohaMmiListener158x mAirohaMmiListner158x = new AirohaMmiListener158x(){

        @Override
        public void OnRespSuccess(String stageName) {

        }

        @Override
        public void onResponseTimeout() {

        }

        @Override
        public void onStopped(String stageName) {
            if (stageName != null) {
                mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "No rsp for " + stageName);
            }
        }

        @Override
        public void notifyUpdateDeviceStatus(int moduleId, int statusCode) {

        }

        @Override
        public void notifyShareModeState(byte state) {

        }

        @Override
        public void notifyFwInfo(byte role, String companyName, String modelName) {

        }

        @Override
        public void notifyFieldTrialRelatedNV(byte role, final com.airoha.libmmi158x.model.FieldTrialRelatedNV result) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAlertDialog(mActivity, "Field Trial Related NV", result.getReport());
                }
            });
        }

        @Override
        public void notifyGetKeyMap(byte role, boolean status, List<AirohaGestureInfo> gesture_info) {

        }

        @Override
        public void notifySetKeyMap(byte role, boolean status) {

        }

        @Override
        public void notifyReloadNv(byte role, boolean status) {

        }
    };

    AirohaMmiListener mAirohaMmiListener = new AirohaMmiListener() {

        @Override
        public void OnRespSuccess(String stageName) {

        }

        @Override
        public void OnFindMeState(byte state) {

        }

        @Override
        public void OnBattery(byte role, byte level) {

        }

        @Override
        public void OnAncTurnOn(byte status) {

        }

        @Override
        public void OnPassThrough(byte status) {

        }

        @Override
        public void OnAncTurnOff(byte status) {

        }

        @Override
        public void notifyLeakageDetectionStatus(byte mode, byte status, byte[] data, AgentPartnerEnum role) {

        }

        @Override
        public void OnAncCalibrationFailed(AgentPartnerEnum role) {

        }

        @Override
        public void notifyAgentIsRight(boolean isRight) {
            mAgentIsRightDevice = isRight;
        }

        @Override
        public void notifyPartnerIsExisting(final boolean isExisting) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIsPartnerExist = isExisting;
                    if (!isExisting) {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Partner doesn't exist.");
                    } else {
                        mActivity.updateMsg(MainActivity.MsgType.GENERAL, LOG_TAG + "Partner exists.");
                    }
                }
            });
        }

        @Override
        public void notifyQueryVpLanguage(List<String> vpList) {

        }

        @Override
        public void notifyGetVpIndex(byte index) {

        }

        @Override
        public void notifySetVpIndex(boolean status) {

        }

        @Override
        public void notifyAncStatus(byte status, short gain) {

        }

        @Override
        public void notifyGameModeState(byte state) {

        }

        @Override
        public void notifyGetPassThruGain(short gain) {

        }

        @Override
        public void notifySetAncPassThruGain(short gain) {

        }

        @Override
        public void notifyGetVaIndex(byte role, boolean status, byte index) {

        }

        @Override
        public void notifySetVaIndex(byte role, boolean status) {

        }

        @Override
        public void notifyGetKeyMap(byte role, boolean status, List<AirohaGestureInfo> gesture_info) {

        }

        @Override
        public void notifySetKeyMap(byte role, boolean status) {

        }

        @Override
        public void notifyReloadNv(byte role, boolean status) {

        }

        @Override
        public void notifySetAudioPath(byte status) {

        }

        @Override
        public void onGameModeStateChanged(boolean isEnabled) {

        }

        @Override
        public void onAudioPathChanged(byte audioPath) {

        }

        @Override
        public void onResponseTimeout() {

        }

        @Override
        public void onStopped(final String stageName) {
            if (stageName != null) {
                mActivity.updateMsg(MainActivity.MsgType.ERROR, LOG_TAG + "No rsp for " + stageName);
            }
        }

        @Override
        public void notifyGetAntennaInfo(final byte role, final AntennaInfo info) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean is_agent;
                    if(role == 0x00) {
                        is_agent = true;
                        mIsAgentResp = true;
                    }
                    else {
                        is_agent = false;
                        mIsPartnerResp = true;
                    }

                    if (info.getStatus() == 0) {
                        ++mChartPointCounter;
                        if (is_agent) {
                            mAgentHeadsetRssiLineDataSet.addEntry(new Entry(mAgentHeadsetRssiLineDataSet.getEntryCount(), info.getRssi(), 0));
                            mAgentHeadsetRssiLineDataSet.notifyDataSetChanged();
                            mAgentPhoneRssiLineDataSet.addEntry(new Entry(mAgentPhoneRssiLineDataSet.getEntryCount(), info.getPhoneRssi(), 0));
                            mAgentPhoneRssiLineDataSet.notifyDataSetChanged();
                        } else {
                            mPartnerHeadsetRssiLineDataSet.addEntry(new Entry(mPartnerHeadsetRssiLineDataSet.getEntryCount(), info.getRssi(), 0));
                            mPartnerHeadsetRssiLineDataSet.notifyDataSetChanged();
                            mPartnerPhoneRssiLineDataSet.addEntry(new Entry(mPartnerPhoneRssiLineDataSet.getEntryCount(), info.getPhoneRssi(), 0));
                            mPartnerPhoneRssiLineDataSet.notifyDataSetChanged();
                        }

                        if (mChartPointCounter >= mMaxPointCountForScreenshot) {
                            saveLineChart();
                            mChartPointCounter = 0;
                        }

                        refreshChart();

                        String msg = "Role:" + ((is_agent)? "Agent":"Partner")
                                + ", Rssi:" + info.getRssi()
                                + ", Phone Rssi:" + info.getPhoneRssi()
                                + ", IfpErrCnt:" + info.getIfpErrCnt()
                                + ", AclErrCnt:" + info.getAclErrCnt()
                                + ", AudioPktNum:" + info.getAudioPktNum()
                                + ", DspLostCnt:" + info.getDspLostCnt()
                                + ", AagcRssi:" + info.getAagcRssi()
                                + ", Phone AagcRssi:" + info.getPhoneAagcRssi()
                                + ", AagcGain:" + info.getAagcGain()
                                + ", Phone AagcGain:" + info.getPhoneAagcGain();

                        addLogMsg(is_agent, msg);

                        calcStatistics(is_agent, info.getRssi(), info.getPhoneRssi());

                        if(mStatisticsEnable) {
                            saveRssiInfo();
                        }
                    } else {
                        addLogMsg(is_agent, "It gets wrong status: " + info.getStatus());
                    }
                }
            });
        }

        @Override
        public void notifyFwInfo(byte role, String companyName, String modelName) {

        }

        @Override
        public void notifyFwVersion(byte role, String version) {

        }

        @Override
        public void notifySdkVersion(String sdkVersion) {

        }

        @Override
        public void notifyDeviceName(byte role, boolean isClassic, boolean isDefault, String deviceName) {

        }

        @Override
        public void notifySetDeviceName(byte role, boolean status) {

        }

        @Override
        public void notifyDeviceAddr(byte role, boolean isClassic, String deviceAddr) {

        }

        @Override
        public void notifyReadAncNv(byte[] ancData) {

        }

        @Override
        public void notifyUpdateDeviceStatus(int moduleId, int statusCode) {

        }

        @Override
        public void notifyIrOnOff(byte irStatus) {

        }

        @Override
        public void notifyTouchOnOff(byte touchStatus) {

        }

        @Override
        public void notifyCustomResp(byte[] resp) {

        }

        @Override
        public void notifyRhoDone(boolean status, int agentChannel) {

        }

        @Override
        public void notifyA2dpInfo(A2dpInfo info) {
            boolean is_agent = true;
            if (info.getStatus() == 0) {
                String msg = "[A2DP info] " + info.getReport();

                addLogMsg(is_agent, msg);

//                if(mStatisticsEnable) {
//                    saveRssiInfo(is_agent, info.getRssi(), info.getPhoneRssi());
//                }
            } else if (info.getStatus() == -1){
                addLogMsg(is_agent, "Failed to get A2DP info: " + info.getStatus());
            } else {
                mGetA2dpInfoInLoopEnabled = false;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCbGetA2dpInfoInLoop.setChecked(false);
                    }
                });
                String msg = "[A2DP info] Error status: " + info.getStatus() + ", please ensure music is playing.";
                addLogMsg(is_agent, msg);
                mActivity.updateMsg(MainActivity.MsgType.ERROR, msg);
            }
        }

        @Override
        public void notifyFieldTrialRelatedNV(byte role, final FieldTrialRelatedNV result) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAlertDialog(mActivity, "Field Trial Related NV", result.getReport());
                }
            });
        }
    };

    @Override
    public void onHostDisconnected() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsReporting = false;
                setUiStatus();
            }
        });
    }
}
