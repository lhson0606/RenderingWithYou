package com.dy.app.gameplay.player;

import com.dy.app.gameplay.pgn.PGNFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerGameHistory {
    private Map<String, Object> data;
    public static final String KEY_P_V_P = "pvp";
    public static final String KEY_P_V_E = "pve";

    public PlayerGameHistory(){
        data = new HashMap<>();
        List<String> pvp = new ArrayList<>();
        List<String> pve = new ArrayList<>();
        data.put(KEY_P_V_P,pvp);
        data.put(KEY_P_V_E, pve);
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

    public void newGame(String gameType, String gameData){
        List<String> gameHistory = (List<String>) get(gameType);
        gameHistory.add(gameData);
        set(gameType, gameHistory);
    }

    public void newGame(String gameType, PGNFile gamePlay){
        newGame(gameType, gamePlay.toString());
    }
}
