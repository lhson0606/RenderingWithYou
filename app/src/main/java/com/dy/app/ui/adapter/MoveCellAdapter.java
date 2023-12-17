package com.dy.app.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dy.app.R;

import java.util.Map;
import java.util.Vector;

public class MoveCellAdapter extends BaseAdapter {
    private Vector<String> moveNotations;
    public static final String pieceNotationPattern = "KQRBN";

    public MoveCellAdapter(Vector<String> moveNotations){
        this.moveNotations = moveNotations;
    }
    public Map<Integer, View> moveViewMap = new java.util.HashMap<>();

    @Override
    public int getCount() {
        return moveNotations.size();
    }

    @Override
    public Object getItem(int position) {
        return moveNotations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.move_cell, parent, false);
        LinearLayout llMoveContainer = v.findViewById(R.id.llMoveContainer);
        final String currentMove = moveNotations.get(position);
        String temp = "";

        if(position % 2 == 0) {
            TextView tvMoveNumber = inflater.inflate(R.layout.tv_move, llMoveContainer, false).findViewById(R.id.tvMoveNotation);
            tvMoveNumber.setText((position / 2 + 1) + ".");
            tvMoveNumber.setTextColor(Color.parseColor("#000000"));
            llMoveContainer.addView(tvMoveNumber);
        }

        for(int i = 0; i < currentMove.length(); i++){

            char currentChar = currentMove.charAt(i);

            if(pieceNotationPattern.contains(currentChar + "")){
                TextView tv = inflater.inflate(R.layout.tv_move, llMoveContainer, false).findViewById(R.id.tvMoveNotation);
                tv.setText(temp);
                ImageView iv = new ImageView(llMoveContainer.getContext());

                switch (currentChar){
                    case 'K':
                        iv.setImageResource(R.drawable.ic_king_move);
                        break;
                    case 'Q':
                        iv.setImageResource(R.drawable.ic_queen_move);
                        break;
                    case 'R':
                        iv.setImageResource(R.drawable.ic_rook_move);
                        break;
                    case 'B':
                        iv.setImageResource(R.drawable.ic_bishop_move);
                        break;
                    case 'N':
                        iv.setImageResource(R.drawable.ic_knight_move);
                        break;
                }

                llMoveContainer.addView(tv);
                llMoveContainer.addView(iv);

                temp = "";
                continue;
            }

            temp += currentChar;
        }

        TextView tv = inflater.inflate(R.layout.tv_move, llMoveContainer, false).findViewById(R.id.tvMoveNotation);
        tv.setText(temp);

        llMoveContainer.addView(tv);

        moveViewMap.put(position, v);

        return v;
    }

    public View getCellView(int position){
        return moveViewMap.get(position);
    }
}
