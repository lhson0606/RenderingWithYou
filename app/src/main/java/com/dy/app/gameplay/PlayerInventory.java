package com.dy.app.gameplay;

import java.util.HashMap;
import java.util.Map;

public class PlayerInventory {
    private Map<String, Object> data;
    public static final String KEY_COIN = "coin";
    public static final String KEY_GEMS = "gems";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_EXP = "exp";
    public static final String KEY_ELO = "elo";
    public static final String KEY_PIECE_SKIN = "pieceSkin";
    public static final String KEY_TERRAIN_SKIN = "terrainSkin";
    public static final String KEY_TILE_SKIN = "tileSkin";

    public PlayerInventory(){
        data = new HashMap<>();
        data.put(KEY_COIN, 0);
        data.put(KEY_GEMS, 0);
        data.put(KEY_LEVEL, 0);
        data.put(KEY_EXP, 0);
        data.put(KEY_ELO, 0);
        data.put(KEY_PIECE_SKIN, new int[]{0});
        data.put(KEY_TERRAIN_SKIN, new int[]{0});
        data.put(KEY_TILE_SKIN, new int[]{0});
    }

    public Object get(String key){
        return data.get(key);
    }

    public void set(String key, Object value){
        if(!data.containsKey(key)) {
            throw new RuntimeException("Key not found");
        }
        data.put(key, value);
    }

    public Map<String, Object> getData() {
        Map<String, Object> res = new HashMap<>();

        for(String key : data.keySet()){
            res.put(key, data.get(key));
        }

        return res;
    }

}
