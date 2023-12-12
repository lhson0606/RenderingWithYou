package com.dy.app.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.charts.Radar;
import com.anychart.core.radar.series.Line;
import com.anychart.data.Mapping;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.GradientKey;
import com.anychart.graphics.vector.Stroke;
import com.dy.app.R;

import java.util.ArrayList;
import java.util.List;
import com.anychart.data.Set;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerGameHistory;
import com.dy.app.gameplay.player.PlayerProfile;
import com.dy.app.gameplay.player.PlayerStatistics;
import com.dy.app.utils.Utils;

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
        Player player = Player.getInstance();
        ((TextView)findViewById(R.id.tvName)).setText(player.getDisplayName());
        Long elo = (Long)player.profile.get(PlayerProfile.KEY_ELO);
        String eloText = elo.toString();
        ((TextView)findViewById(R.id.tvRank)).setText(Utils.getTitle(elo));
        ((TextView)findViewById(R.id.tvElo)).setText(eloText);
        Long totalGames = (Long)player.statistics.get(PlayerStatistics.KEY_TOTAL_GAME);
        ((TextView)findViewById(R.id.tvTotalGames)).setText(totalGames.toString());
        String winRateStr = Utils.getWinRateDisplay((Long)player.statistics.get(PlayerStatistics.KEY_WIN), totalGames);
        ((TextView)findViewById(R.id.tvWinRate)).setText(winRateStr);
    }

    private void attachListener() {
        btnClose.setOnClickListener(this);
    }

    private void updateUI() {
        updateRatingCircle();
        updateEloRecordChart();
    }

    private void updateRatingCircle() {
        ProgressBar eloProgressBar = findViewById(R.id.eloProgressBar);
        Player player = Player.getInstance();
        Long elo = (Long)player.profile.get(PlayerProfile.KEY_ELO);
        float eloPercent = (float)elo / 3000;
        eloProgressBar.setProgress((int)(eloPercent * 100));
        TextView tvAlphabetRating = findViewById(R.id.tvAlphabetRating);
        String alphabetRating = Utils.getAlphabetRating(elo, 3000);
        tvAlphabetRating.setText(alphabetRating);
    }

    private void updateEloRecordChart() {
        AnyChartView anyChartView = findViewById(R.id.eloHistoryGraph);
        anyChartView.setProgressBar(findViewById(R.id.eloChartProgressBar));

        Cartesian cartesian = AnyChart.line();

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        String title = "Elo History";
        List<Long> eloValue = (List<Long>)Player.getInstance().history.get(PlayerGameHistory.KEY_ELO_HISTORY_VALUE);
        List<Long> eloDate = (List<Long>)Player.getInstance().history.get(PlayerGameHistory.KEY_ELO_HISTORY_DATE);
        if(eloDate.size() > 0){
            title += " from " + Utils.getDate(eloDate.get(0));
            title += " to " + Utils.getDate(eloDate.get(eloDate.size()-1));
        }

        cartesian.yAxis(0).title("Elo value");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();

        for(int i = 0; i < eloValue.size(); i++){
            seriesData.add(new ValueDataEntry(i, eloValue.get(i)));
        }

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");


        com.anychart.core.cartesian.series.Line series1 = cartesian.line(series1Mapping);
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        cartesian.title(title);
        anyChartView.setChart(cartesian);
        GradientKey key = new GradientKey("#2050CA", 0d, 1d);
        cartesian.background().enabled(true).fill(key, 0, true, 1);
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
