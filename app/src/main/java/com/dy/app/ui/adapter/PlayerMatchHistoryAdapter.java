package com.dy.app.ui.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dy.app.R;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.ui.dialog.PlayerHistoryDialog;
import com.dy.app.utils.DyConst;

import java.util.Vector;
import java.util.zip.Inflater;

public class PlayerMatchHistoryAdapter extends BaseAdapter {
    private Vector<PGNFile> pgnFiles;
    private PlayerHistoryDialog playerHistoryDialog;

    public PlayerMatchHistoryAdapter(PlayerHistoryDialog playerHistoryDialog, Vector<PGNFile> pgnFiles) {
        this.pgnFiles = pgnFiles;
        this.playerHistoryDialog = playerHistoryDialog;
    }

    @Override
    public int getCount() {
        return pgnFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return pgnFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView != null){
            return convertView;
        }
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_single_match_history, null);
        PGNFile pgnFile = pgnFiles.get(position);
        TextView gameResult = v.findViewById(R.id.tvMatchResult);
        switch (pgnFile.getGameResult()){
            case DyConst.GAME_NOT_END:
                gameResult.setText("Not end");
                gameResult.setTextColor(Color.parseColor("#0CC4ED"));
                break;
            case DyConst.GAME_BLACK_WIN:
                gameResult.setText("Black win");
                gameResult.setTextColor(Color.parseColor("#000000"));
                break;
            case DyConst.GAME_WHITE_WIN:
                gameResult.setText("White win");
                gameResult.setTextColor(Color.parseColor("#FFFFFF"));
                break;
                case DyConst.GAME_DRAW:
                gameResult.setText("Draw");
                gameResult.setTextColor(Color.parseColor("#00ffc3"));
                break;
        }

        TextView gameDate = v.findViewById(R.id.tvDate);
        gameDate.setText(pgnFile.getGameDate());
        TextView gameType = v.findViewById(R.id.tvEvent);
        gameType.setText(pgnFile.getEvent());

        return v;
    }

    public void updateData(Vector<PGNFile> pgnFiles) {
        this.pgnFiles = pgnFiles;
        notifyDataSetChanged();
    }
}
