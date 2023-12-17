package com.dy.app.ui.dialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.activity.RunScriptsActivity;
import com.dy.app.core.thread.ScriptsRunner;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.ui.adapter.MoveCellAdapter;
import com.dy.app.utils.Utils;

public class MoveControlPanel extends DialogFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String TAG = "MoveControlPanel";
    public static final int JUMP_TO_MOVE = 0;
    public static final int SHARE_GAME_IMAGE = 1;
    private FragmentHubActivity activity;
    public MoveControlPanel(){
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    public static MoveControlPanel newInstance(PGNFile pgnFile) {
            Bundle args = new Bundle();
            args.putSerializable("pgnFile", pgnFile);
            MoveControlPanel fragment = new MoveControlPanel();
            fragment.setArguments(args);
            return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PGNFile pgnFile = (PGNFile) getArguments().getSerializable("pgnFile");
        activity = (FragmentHubActivity) getActivity();
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_game_replay_control_panel, container, true);
        setCancelable(false);

        btnClose = v.findViewById(R.id.btnClose);
        gvMovesTable = v.findViewById(R.id.gvMovesTable);
        sbProgress = v.findViewById(R.id.sbProgress);
        tvShareAsImage = v.findViewById(R.id.tvShareAsImage);
        rootView = v.findViewById(R.id.clMovesTable);

        moveCellAdapter = new MoveCellAdapter(pgnFile.getMoveNotations());

        gvMovesTable.setAdapter(moveCellAdapter);

        btnClose.setOnClickListener(this);
        gvMovesTable.setOnItemClickListener(this);
        sbProgress.setProgress(1);
        sbProgress.setMax(pgnFile.getBothSideMoveCount());
        tvShareAsImage.setOnClickListener(this);

        return v;
    }

    private LottieAnimationView btnClose;

    @Override
    public void onClick(View v) {
        if(v == btnClose){
            dismiss();
        }else if(v == tvShareAsImage) {
            Bitmap bitmap = Utils.getScreenShot(rootView);
            activity.onMsgToMain(TAG, SHARE_GAME_IMAGE, bitmap, null);
        }
    }

    public void updateMoveIndex(int index){
        if(getContext() == null){
            return;
        }

        if(moveCellAdapter == null){
            return;
        }

        if(previousSelectedView != null){
            previousSelectedView.setBackgroundColor(getResources().getColor(R.color.transparent));
        }

        previousSelectedView = moveCellAdapter.getCellView(index);
        if(previousSelectedView == null){
            return;
        }
        previousSelectedView.setBackgroundColor(getResources().getColor(R.color.light_green));
        sbProgress.setProgress(index);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        activity.onMsgToMain(TAG, JUMP_TO_MOVE, position, null);
    }

    public void unhighlight() {
        if(previousSelectedView != null){
            previousSelectedView.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
    }

    private View previousSelectedView = null;

    private GridView gvMovesTable = null;
    private MoveCellAdapter moveCellAdapter;
    private SeekBar sbProgress;
    private TextView tvShareAsImage;
    private View rootView;
}
