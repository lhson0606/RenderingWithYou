package com.dy.app.gameplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInventory {
    private Map<String, Object> data;
    public static final String KEY_COIN = "coin";
    public static final String KEY_GEMS = "gems";
    public static final String KEY_PIECE_SKIN_INDEX = "pieceSkinIndex";
    public static final String KEY_TERRAIN_SKIN_INDEX = "terrainSkinIndex";
    public static final String KEY_BOARD_SKIN_INDEX = "boardSkinIndex";
    public static final String KEY_TILE_SKIN_INDEX = "tileSkinIndex";
    public static final String KEY_PIECE_SKIN = "pieceSkin";
    public static final String KEY_TERRAIN_SKIN = "terrainSkin";
    public static final String KEY_BOARD_SKIN = "boardSkin";
    public static final String KEY_TILE_SKIN = "tileSkin";

    public PlayerInventory(){
        data = new HashMap<>();
        data.put(KEY_COIN, 0L);
        data.put(KEY_GEMS, 0L);
        data.put(KEY_PIECE_SKIN_INDEX, 0L);
        data.put(KEY_TERRAIN_SKIN_INDEX, 0L);
        data.put(KEY_BOARD_SKIN_INDEX, 0L);
        data.put(KEY_TILE_SKIN_INDEX, 0L);
        List<Long> piece_skin = new ArrayList<>();
        piece_skin.add(0L);
        List<Long> terrain_skin = new ArrayList<>();
        terrain_skin.add(0L);
        List<Long> board_skin = new ArrayList<>();
        board_skin.add(0L);
        List<Long> tile_skin = new ArrayList<>();
        tile_skin.add(0L);
        data.put(KEY_PIECE_SKIN, piece_skin);
        data.put(KEY_TERRAIN_SKIN, terrain_skin);
        data.put(KEY_BOARD_SKIN, board_skin);
        data.put(KEY_TILE_SKIN, tile_skin);
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

    public void putAll(Map<String, Object> data){
        for(String key : data.keySet()){
            set(key, data.get(key));
        }
    }

}
