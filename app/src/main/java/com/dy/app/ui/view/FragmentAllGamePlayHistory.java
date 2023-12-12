package com.dy.app.ui.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dy.app.R;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.gameplay.player.Player;
import com.dy.app.ui.adapter.PlayerMatchHistoryAdapter;
import com.dy.app.ui.dialog.PlayerHistoryDialog;

import java.util.Vector;

public class FragmentAllGamePlayHistory extends Fragment
implements AdapterView.OnItemClickListener {
    private PlayerHistoryDialog playerHistoryDialog;
    private ImageView ivEmpty;
    private PlayerMatchHistoryAdapter adapter;

    public FragmentAllGamePlayHistory(PlayerHistoryDialog playerHistoryDialog) {
        this.playerHistoryDialog = playerHistoryDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_history, container, false);
        lvGameHistory = v.findViewById(R.id.lvGameHistory);
        ivEmpty = v.findViewById(R.id.ivEmpty);
        Vector<PGNFile> pgnFiles = Player.getInstance().getAllHistory();
        if(pgnFiles.size() == 0) {
            ivEmpty.setVisibility(View.VISIBLE);
        }else{
            ivEmpty.setVisibility(View.INVISIBLE);
        }
        adapter = new PlayerMatchHistoryAdapter(playerHistoryDialog, pgnFiles);
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



    public void updateView(){
        Vector<PGNFile> pgnFiles = Player.getInstance().getAllHistory();
        getActivity().runOnUiThread(()->{
            if(pgnFiles.size() == 0) {
                ivEmpty.setVisibility(View.VISIBLE);
            }else{
                ivEmpty.setVisibility(View.INVISIBLE);
            }
            adapter.updateData(pgnFiles);
        });
    }
}
