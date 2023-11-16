package com.dy.app.gameplay.item;

import com.dy.app.R;

public class GameItem {

    public ItemInfo itemInfo;
    public GameItem(){
        itemInfo = new ItemInfo(R.raw.thumbnail_piece_fire_n_ice, "Default", "Default");
    }
}
