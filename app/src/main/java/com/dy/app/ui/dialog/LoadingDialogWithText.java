package com.dy.app.ui.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.utils.Utils;

public class LoadingDialogWithText extends DialogFragment {

    private Thread workerThread;
    public static final String TAG = "LoadingDialogWithText";

    public static final int BG_RES[] = {R.raw.chess_wallpaper_1, R.raw.chess_wallpaper_2, R.raw.chess_wallpaper_3, R.raw.chess_wallpaper_4, R.raw.chess_wallpaper_5};

    public LoadingDialogWithText(){
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    public static LoadingDialogWithText newInstance(){
        return new LoadingDialogWithText();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.loading_screen_with_text, container, false);
        //rootContainer
        View bg = v.findViewById(R.id.rootContainer);
        int randomBgIndex = Utils.randomInt(0, BG_RES.length - 1);
        bg.setBackgroundResource(BG_RES[randomBgIndex]);
        tvDisplayText = v.findViewById(R.id.tvDisplayText);
        setCancelable(false);

        return v;
    }

    public void doWork(FragmentActivity fragmentActivity, Runnable runnable){
        workerThread = new Thread(()->{
            show(fragmentActivity.getSupportFragmentManager(), TAG);
            runnable.run();
            fragmentActivity.runOnUiThread(()->{
                dismiss();
            });
        });
        workerThread.start();
    }

    public void setTvDisplayText(Activity activity, String text){
        activity.runOnUiThread(()->{
            tvDisplayText.setText(text);
        });
    }

    private TextView tvDisplayText;
}
