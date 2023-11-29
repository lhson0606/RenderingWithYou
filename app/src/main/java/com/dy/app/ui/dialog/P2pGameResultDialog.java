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
    public interface P2pGameResultDialogListener{
        void onBtnSavePGNClick();
        void onBtnRematchClick();
        void onBtnExitClick();
        void onBtnShareAsImageClick();
        void onBtnShareAsPGNClick();
    }

    private P2pGameResultDialogListener listener;

    public P2pGameResultDialog(String title, int playerTrophyDiff, int playerGoldDiff, int playerGemDiff, int opponentTrophyDiff, P2pGameResultDialogListener listener){
        this.listener = listener;
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
        tvGameResultTitle.setText(title);
        setSingleValue(tvTrophyDifference, playerTrophyDiff);
        setSingleValue(tvGoldDifference, playerGoldDiff);
        setSingleValue(tvGemDifference, playerGemDiff);
        setSingleValue(tvOppoPlayerTrophyDifference, opponentTrophyDiff);
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
    int playerTrophyDiff, playerGoldDiff, playerGemDiff, opponentTrophyDiff;
    private String title;
    private TextView tvGameResultTitle;
}
