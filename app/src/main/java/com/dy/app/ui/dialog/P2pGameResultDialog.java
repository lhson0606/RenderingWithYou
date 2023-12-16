package com.dy.app.ui.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
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
        setSingleValue(tvTrophyDifference, (int)playerTrophyDiff);
        setSingleValue(tvGoldDifference, (int)playerGoldDiff);
        setSingleValue(tvGemDifference, (int)playerGemDiff);
        setSingleValue(tvOppoPlayerTrophyDifference, (int)opponentTrophyDiff);
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

    private Button btnSavePGN, btnRematch, btnExit;
    private TextView tvShareAsPGN, tvShareAsImage;
    private TextView tvTrophyDifference, tvGoldDifference, tvGemDifference, tvOppoPlayerTrophyDifference;
    long playerTrophyDiff, playerGoldDiff, playerGemDiff, opponentTrophyDiff;
    private int gameResult;
    private TextView tvGameResultTitle;
}
