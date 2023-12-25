package com.dy.app.ui.dialog;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dy.app.R;

public class P2pGameResultDialog extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "P2pGameResultDialog";
    public static final int DRAW = 0;
    public static final int WIN = 1;
    public static final int LOSE = 2;

    public interface P2pGameResultDialogListener{
        void onBtnSavePGNClick();
        void onBtnRematchClick();
        void onBtnExitClick();
        void onBtnShareAsImageClick();
        void onBtnShareAsPGNClick();
    }

    private P2pGameResultDialogListener listener;

    public P2pGameResultDialog(int gameResult, long playerTrophyDiff, long playerGoldDiff, long playerGemDiff, long opponentTrophyDiff, P2pGameResultDialogListener listener){
        this.listener = listener;
        this.gameResult = gameResult;
        this.playerTrophyDiff = playerTrophyDiff;
        this.playerGoldDiff = playerGoldDiff;
        this.playerGemDiff = playerGemDiff;
        this.opponentTrophyDiff = opponentTrophyDiff;
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        trophyAnimator = ValueAnimator.ofInt(0, (int)playerTrophyDiff);
        goldAnimator = ValueAnimator.ofInt(0, (int)playerGoldDiff);
        gemAnimator = ValueAnimator.ofInt(0, (int)playerGemDiff);
        oppoTrophyAnimator = ValueAnimator.ofInt(0, (int)opponentTrophyDiff);

        trophyAnimator.setDuration(ANIMATION_DURATION);
        goldAnimator.setDuration(ANIMATION_DURATION);
        gemAnimator.setDuration(ANIMATION_DURATION);
        oppoTrophyAnimator.setDuration(ANIMATION_DURATION);

        trophyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int)animation.getAnimatedValue();
                setSingleValue(tvTrophyDifference, currentValue);
            }
        });

        goldAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int)animation.getAnimatedValue();
                setSingleValue(tvGoldDifference, currentValue);
            }
        });

        gemAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int)animation.getAnimatedValue();
                setSingleValue(tvGemDifference, currentValue);
            }
        });

        oppoTrophyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int)animation.getAnimatedValue();
                setSingleValue(tvOppoPlayerTrophyDifference, currentValue);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                skipAnimation();
                return false;
            }
        });


        animatorSet.playSequentially(trophyAnimator, goldAnimator, gemAnimator, oppoTrophyAnimator);

        super.onViewCreated(view, savedInstanceState);
    }

    private void skipAnimation(){
        if(animatorSet.isRunning()){
            animatorSet.end();
            //update final value
            setSingleValue(tvTrophyDifference, (int)playerTrophyDiff);
            setSingleValue(tvGoldDifference, (int)playerGoldDiff);
            setSingleValue(tvGemDifference, (int)playerGemDiff);
            setSingleValue(tvOppoPlayerTrophyDifference, (int)opponentTrophyDiff);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        animatorSet.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        skipAnimation();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_p2p_game_result, container, false);
        setCancelable(false);
        btnSavePGN = v.findViewById(R.id.btnSavePGN);
        btnRematch = v.findViewById(R.id.btnRematch);
        btnExit = v.findViewById(R.id.btnExit);
        tvShareAsPGN = v.findViewById(R.id.tvShareAsPGN);
        tvShareAsImage = v.findViewById(R.id.tvShareAsImage);
        tvTrophyDifference = v.findViewById(R.id.tvTrophyDifference);
        tvGoldDifference = v.findViewById(R.id.tvGoldDifference);
        tvGemDifference = v.findViewById(R.id.tvGemDifference);
        tvOppoPlayerTrophyDifference = v.findViewById(R.id.tvOppoPlayerTrophyDifference);
        tvGameResultTitle = v.findViewById(R.id.tvGameResultTitle);

        btnSavePGN.setOnClickListener(this);
        btnRematch.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        tvShareAsPGN.setOnClickListener(this);
        tvShareAsImage.setOnClickListener(this);

        updateDisplay();

        return v;
    }

    private void updateDisplay() {
        setTitle();
    }

    private void setTitle(){
        switch (gameResult){
            case DRAW:
                tvGameResultTitle.setText("Draw");
                break;
            case WIN:
                tvGameResultTitle.setText("You won!");
                break;
            case LOSE:
                tvGameResultTitle.setText("You lost!");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + gameResult);
        }
    }

    private void setSingleValue(TextView tv, int value){
        if(value == 0){
            tv.setText("+ 0");
            tv.setTextColor(Color.parseColor("#ffffff"));
        }else if(value > 0) {
            tv.setText("+ " + value);
            tv.setTextColor(Color.parseColor("#2050CA"));
        }else{
            tv.setText("- " + Math.abs(value));
            tv.setTextColor(Color.parseColor("#FF4040"));
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnSavePGN){
            listener.onBtnSavePGNClick();
        }else if(v == btnRematch){
            listener.onBtnRematchClick();
        }else if(v == btnExit){
            listener.onBtnExitClick();
        }else if(v == tvShareAsPGN){
            listener.onBtnShareAsPGNClick();
        }else if(v == tvShareAsImage){
            listener.onBtnShareAsImageClick();
        }
    }

    private final AnimatorSet animatorSet = new AnimatorSet();
    private Button btnSavePGN, btnRematch, btnExit;
    private TextView tvShareAsPGN, tvShareAsImage;
    private TextView tvTrophyDifference, tvGoldDifference, tvGemDifference, tvOppoPlayerTrophyDifference;
    long playerTrophyDiff, playerGoldDiff, playerGemDiff, opponentTrophyDiff;
    private int gameResult;
    private TextView tvGameResultTitle;
    private ValueAnimator trophyAnimator, goldAnimator, gemAnimator, oppoTrophyAnimator;
    public static final int ANIMATION_DURATION = 500;
}
