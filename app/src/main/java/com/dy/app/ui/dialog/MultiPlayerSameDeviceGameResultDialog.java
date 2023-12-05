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

public class MultiPlayerSameDeviceGameResultDialog extends DialogFragment implements View.OnClickListener{

    public interface IGameResultDialogListener{
        void onBtnSavePGNClick(MultiPlayerSameDeviceGameResultDialog dialog);
        void onBtnRematchClick(MultiPlayerSameDeviceGameResultDialog dialog);
        void onBtnExitClick(MultiPlayerSameDeviceGameResultDialog dialog);
        void onBtnShareAsImageClick(MultiPlayerSameDeviceGameResultDialog dialog);
        void onBtnShareAsPGNClick(MultiPlayerSameDeviceGameResultDialog dialog);
    }


    public MultiPlayerSameDeviceGameResultDialog(boolean isWhiteWin, IGameResultDialogListener listener){
        this.listener = listener;
        this.isWhiteWin = isWhiteWin;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_multiplayer_same_device_game_result, container, false);
        setCancelable(false);
        btnSavePGN = v.findViewById(R.id.btnSavePGN);
        btnRematch = v.findViewById(R.id.btnRematch);
        btnExit = v.findViewById(R.id.btnExit);
        tvShareAsPGN = v.findViewById(R.id.tvShareAsPGN);
        tvShareAsImage = v.findViewById(R.id.tvShareAsImage);
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
        if(isWhiteWin) {
            setGameResultTitle("White win");
        }else{
            setGameResultTitle("Black win");
        }
    }

    private void setGameResultTitle(String title){
        tvGameResultTitle.setText(title);
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
            listener.onBtnSavePGNClick(this);
        }else if(v == btnRematch){
            listener.onBtnRematchClick(this);
        }else if(v == btnExit){
            listener.onBtnExitClick(this);
        }else if(v == tvShareAsPGN){
            listener.onBtnShareAsPGNClick(this);
        }else if(v == tvShareAsImage){
            listener.onBtnShareAsImageClick(this);
        }
    }

    public void dismissDialog(){
        dismiss();
    }

    private Button btnSavePGN, btnRematch, btnExit;
    private TextView tvShareAsPGN, tvShareAsImage;
    private TextView tvGameResultTitle;
    private IGameResultDialogListener listener;
    private boolean isWhiteWin;
}
