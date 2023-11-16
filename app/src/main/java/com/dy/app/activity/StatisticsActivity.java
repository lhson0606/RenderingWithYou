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
        AnyChartView anyChartView = findViewById(R.id.anyChartView);
        anyChartView.setProgressBar(findViewById(R.id.progressBar));

        Radar radar = AnyChart.radar();

        radar.yScale().minimum(0d);
        radar.yScale().minimumGap(0d);
        radar.yScale().ticks().interval(50d);

        radar.xAxis().labels().padding(5d, 5d, 5d, 5d);

        radar.legend()
                .align(Align.CENTER)
                .enabled(true);

        List<DataEntry> data = new ArrayList<>();
        data.add(new CustomDataEntry("Strength", 136, 199, 43));
        data.add(new CustomDataEntry("Agility", 79, 125, 56));
        data.add(new CustomDataEntry("Stamina", 149, 173, 101));
        data.add(new CustomDataEntry("Intellect", 135, 33, 202));
        data.add(new CustomDataEntry("Spirit", 158, 64, 196));

        Set set = Set.instantiate();
        set.data(data);
        Mapping shamanData = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping warriorData = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping priestData = set.mapAs("{ x: 'x', value: 'value3' }");

        Line shamanLine = radar.line(shamanData);
        shamanLine.name("Shaman");
        shamanLine.markers()
                .enabled(true)
                .type(MarkerType.CIRCLE)
                .size(3d);

        Line warriorLine = radar.line(warriorData);
        warriorLine.name("Warrior");
        warriorLine.markers()
                .enabled(true)
                .type(MarkerType.CIRCLE)
                .size(3d);

        Line priestLine = radar.line(priestData);
        priestLine.name("Priest");
        priestLine.markers()
                .enabled(true)
                .type(MarkerType.CIRCLE)
                .size(3d);

        radar.tooltip().format("Value: {%Value}");

        anyChartView.setChart(radar);
        anyChartView.setBackgroundColor(Color.TRANSPARENT);
    }

    private class CustomDataEntry extends ValueDataEntry {
        public CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }
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
