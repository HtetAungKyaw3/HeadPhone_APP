package com.airoha.utapp.sdk.ui.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.airoha.utapp.sdk.databinding.FragmentHomeMusicBinding;
import com.airoha.utapp.sdk.ui.base.BaseFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class HomeMusicFragment extends BaseFragment<FragmentHomeMusicBinding> {

    public enum TypeSeekBar {
        LO, LO_M, HI_M, HI
    }


    private LineChart lineChart;
    private Number[] series1Numbers = {3.5, 3.5, 0, 5, -2, -2};

    @Override
    protected void initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentHomeMusicBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        super.initView();
        initChart();
    }

    public void updateReceivedData(double newData, TypeSeekBar typeSeekBar) {
        switch (typeSeekBar) {
            case LO:
                series1Numbers[0] = newData;
                series1Numbers[1] = newData;
                break;
            case LO_M:
                series1Numbers[2] = newData;
                break;
            case HI_M:
                series1Numbers[3] = newData;
                break;
            case HI:
                series1Numbers[4] = newData;
                series1Numbers[5] = newData;
                break;
        }
        initChart();
    }

    public void updateReceivedData(double lo, double loM, double hiM, double hi) {
        updateReceivedData(lo, TypeSeekBar.LO);
        updateReceivedData(loM, TypeSeekBar.LO_M);
        updateReceivedData(hiM, TypeSeekBar.HI_M);
        updateReceivedData(hi, TypeSeekBar.HI);
    }

    private void initChart() {
        lineChart = binding.lineChart;
        setupLineChart();
        ArrayList<Entry> entries = generateDataEntries(series1Numbers);
        LineDataSet dataSet = new LineDataSet(entries, "Cubic Line Chart");
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet iLineDataSet, LineDataProvider lineDataProvider) {
                return 0f;
            }
        });
        lineChart.getXAxis().setLabelCount(entries.size());
        lineChart.setTouchEnabled(false);
        String fillColor = "#1A1E4396";
        String colorChart = "#001E78";
        int parseColorChart = Color.parseColor(colorChart);
        int fillParseColor = Color.parseColor(fillColor);
        dataSet.setDrawFilled(true);
        dataSet.setColor(parseColorChart);
        dataSet.setFillColor(fillParseColor);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);


        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setAxisMaximum(12f);
        lineChart.getAxisLeft().setAxisMinimum(-12f);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setLabelCount(40);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        lineChart.setVisibleYRangeMaximum(150, YAxis.AxisDependency.LEFT);
    }

    private ArrayList<Entry> generateDataEntries(Number[] numbers) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i < numbers.length; i++) {
            entries.add(new Entry(i, numbers[i].floatValue()));
        }

        return entries;
    }
}
