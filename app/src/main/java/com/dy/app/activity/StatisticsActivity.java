package com.dy.app.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Radar;
import com.anychart.core.radar.series.Line;
import com.anychart.data.Mapping;
import com.anychart.enums.Align;
import com.anychart.enums.MarkerType;
import com.anychart.graphics.vector.GradientKey;
import com.dy.app.R;

import java.util.ArrayList;
import java.util.List;
import com.anychart.data.Set;

public class StatisticsActivity extends FragmentActivity
implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
        attachListener();
        updateUI();
    }

    private void init(){
        btnClose = findViewById(R.id.btnClose);
    }

    private void attachListener() {
        btnClose.setOnClickListener(this);
    }

    private void updateUI() {
        updateWinRateChart();
        updateEloRecordChart();
    }

    private void updateWinRateChart() {
        AnyChartView anyChartView = findViewById(R.id.anyChartView);
        anyChartView.setProgressBar(findViewById(R.id.progressBar));

        Radar radar = AnyChart.radar();

        radar.yScale().minimum(0d);
        radar.yScale().minimumGap(0d);
        radar.yScale().ticks().interval(25d);

        radar.xAxis().labels().padding(5d, 5d, 5d, 5d);

        radar.legend()
                .align(Align.CENTER)
                .enabled(true);

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Black WR", 70));
        data.add(new ValueDataEntry("White WR", 40));
        data.add(new ValueDataEntry("Overall WR", 55));
        data.add(new ValueDataEntry("Draw", 10));
        data.add(new ValueDataEntry("Elo", 2500/30));

        Set set = Set.instantiate();
        set.data(data);

        Mapping priestData = set.mapAs("{ x: 'x', value: 'value3' }");


        Line priestLine = radar.line(priestData);
        //priestLine.name("Priest");
        priestLine.markers()
                .enabled(true)
                .type(MarkerType.CIRCLE)
                .size(1d);

        radar.tooltip().format("Value: {%Value}");

        anyChartView.setChart(radar);
        GradientKey key = new GradientKey("#2050CA", 0d, 1d);
        radar.background().enabled(true).fill(key, 0, true, 1);
    }

    private void updateEloRecordChart() {

    }

    @Override
    public void onClick(View v) {
        if(v == btnClose){
            btnClose.playAnimation();
            finish();
        }
    }

    private LottieAnimationView btnClose;
}
