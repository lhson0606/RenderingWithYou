package com.dy.app.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.activity.GameActivity;
import com.dy.app.ui.adapter.EndGameOptionAdapter;

public class P2pEndGameOptionDialog extends DialogFragment
        implements View.OnClickListener{

    public P2pEndGameOptionDialog(GameActivity gameActivity){
        this.gameActivity = gameActivity;
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_p2p_end_game_options, container, false);
        setCancelable(false);

        lvEndGameOptions = v.findViewById(R.id.lvEndGameOptions);
        btnClose = v.findViewById(R.id.btnClose);

        String[] options = new String[]{"Resign", "Request a draw"};
        EndGameOptionAdapter adapter = new EndGameOptionAdapter(options);
        lvEndGameOptions.setAdapter(adapter);
        lvEndGameOptions.setOnItemClickListener((parent, view, position, id) -> {
            switch (position){
                case 0:
                    gameActivity.resign();
                    break;
                case 1:
                    gameActivity.requestDraw();
                    break;
            }
            dismiss();
        });

        btnClose.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if(v == btnClose){
            dismiss();
        }
    }

    private GameActivity gameActivity;
    private ListView lvEndGameOptions;
    private LottieAnimationView btnClose;
}
