package com.dy.app.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dy.app.R;
import com.dy.app.utils.DyConst;

public class MultiPlayerSameDeviceGameResultDialog extends DialogFragment implements View.OnClickListener{

    public interface IGameResultDialogListener{
        void onBtnSavePGNClick(MultiPlayerSameDeviceGameResultDialog dialog);
        void onBtnReplayClick(MultiPlayerSameDeviceGameResultDialog dialog);
        void onBtnRematchClick(MultiPlayerSameDeviceGameResultDialog dialog);
        void onBtnExitClick(MultiPlayerSameDeviceGameResultDialog dialog);
        void onBtnShareAsImageClick(MultiPlayerSameDeviceGameResultDialog dialog);
        void onBtnShareAsPGNClick(MultiPlayerSameDeviceGameResultDialog dialog);
    }


    public MultiPlayerSameDeviceGameResultDialog(IGameResultDialogListener listener){
        this.listener = listener;
    }

    public void setGameResult(int gameResult){
        this.gameResult = gameResult;
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
        btnReplay = v.findViewById(R.id.btnReplay);
        btnRematch = v.findViewById(R.id.btnRematch);
        btnExit = v.findViewById(R.id.btnExit);
        tvShareAsPGN = v.findViewById(R.id.tvShareAsPGN);
        tvShareAsImage = v.findViewById(R.id.tvShareAsImage);
        tvGameResultTitle = v.findViewById(R.id.tvGameResultTitle);

        btnSavePGN.setOnClickListener(this);
        btnReplay.setOnClickListener(this);
        btnRematch.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        tvShareAsPGN.setOnClickListener(this);
        tvShareAsImage.setOnClickListener(this);

        updateDisplay();

        return v;
    }

    private void updateDisplay() {
        switch (gameResult){
            case DyConst.GAME_DRAW:
                setGameResultTitle("Draw");
                break;
            case DyConst.GAME_WHITE_WIN:
                setGameResultTitle("White won");
                break;
            case DyConst.GAME_BLACK_WIN:
                setGameResultTitle("Black won");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + gameResult);
        }
    }

    private void setGameResultTitle(String title){
        tvGameResultTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        if(v == btnSavePGN){
            listener.onBtnSavePGNClick(this);
        } else if(v == btnReplay){
            listener.onBtnReplayClick(this);
        } else if(v == btnRematch){
            listener.onBtnRematchClick(this);
        }else if(v == btnExit){
            listener.onBtnExitClick(this);
        }else if(v == tvShareAsPGN){
            listener.onBtnShareAsPGNClick(this);
        }else if(v == tvShareAsImage){
            listener.onBtnShareAsImageClick(this);
        }
    }

    private Button btnSavePGN, btnReplay, btnRematch, btnExit;
    private TextView tvShareAsPGN, tvShareAsImage;
    private TextView tvGameResultTitle;
    private IGameResultDialogListener listener;
    private int gameResult;
}
