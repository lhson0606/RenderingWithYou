package com.dy.app.ui.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dy.app.R;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.gameplay.player.Player;
import com.dy.app.ui.adapter.PlayerMatchHistoryAdapter;
import com.dy.app.ui.dialog.PlayerHistoryDialog;

import java.util.Vector;

public class FragmentPvPHistory extends Fragment
        implements AdapterView.OnItemClickListener {
    PlayerHistoryDialog playerHistoryDialog;
    private ImageView ivEmpty;

    public FragmentPvPHistory(PlayerHistoryDialog playerHistoryDialog) {
        this.playerHistoryDialog = playerHistoryDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_history, container, false);
        lvGameHistory = v.findViewById(R.id.lvGameHistory);
        Vector<PGNFile> pgnFiles = Player.getInstance().getPvPHistory();
        if(pgnFiles.size() == 0) {
            ivEmpty.setVisibility(View.VISIBLE);
        }
        PlayerMatchHistoryAdapter adapter = new PlayerMatchHistoryAdapter(playerHistoryDialog, pgnFiles);
        lvGameHistory.setAdapter(adapter);
        lvGameHistory.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private ListView lvGameHistory;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View selectedView = parent.getChildAt(position);
        playerHistoryDialog.setCurrentSelectedView(selectedView);
        playerHistoryDialog.setReplayFile((PGNFile) parent.getItemAtPosition(position));
    }
}

