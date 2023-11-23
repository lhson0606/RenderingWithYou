package com.dy.app.gameplay.player;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatistics {
    private Map<String, Object> data;
    public static final String KEY_WIN = "win";
    public static final String KEY_LOSE = "lose";
    public static final String KEY_TOTAL_GAME = "totalGame";
    public static final String KEY_TOTAL_TIME = "totalTime";
    public static final String KEY_TOTAL_PROMOTION = "totalPromotion";
    public static final String KEY_TOTAL_ON_WHITE = "totalOnWhite";
    public static final String KEY_TOTAL_ON_BLACK = "totalOnBlack";
    public static final String KEY_TOTAL_WIN_AS_WHITE = "totalWinAsWhite";
    public static final String KEY_TOTAL_WIN_AS_BLACK = "totalWinAsBlack";
    public static final String KEY_TOTAL_LOSE_AS_WHITE = "totalLoseAsWhite";
    public static final String KEY_TOTAL_LOSE_AS_BLACK = "totalLoseAsBlack";
    public static final String KEY_TOTAL_WIN_AS_FIRST = "totalDrawAsWhite";
    public static final String KEY_TOTAL_WIN_AS_SECOND = "totalDrawAsBlack";

    public PlayerStatistics(){
        data = new HashMap<>();
        data.put(KEY_WIN, 0L);
        data.put(KEY_LOSE, 0L);
        data.put(KEY_TOTAL_GAME, 0L);
        data.put(KEY_TOTAL_TIME, 0L);
        data.put(KEY_TOTAL_PROMOTION, 0L);
        data.put(KEY_TOTAL_ON_WHITE, 0L);
        data.put(KEY_TOTAL_ON_BLACK, 0L);
        data.put(KEY_TOTAL_WIN_AS_WHITE, 0L);
        data.put(KEY_TOTAL_WIN_AS_BLACK, 0L);
        data.put(KEY_TOTAL_LOSE_AS_WHITE, 0L);
        data.put(KEY_TOTAL_LOSE_AS_BLACK, 0L);
        data.put(KEY_TOTAL_WIN_AS_FIRST, 0L);
        data.put(KEY_TOTAL_WIN_AS_SECOND, 0L);
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
            if(!this.data.containsKey(key)){
                throw new RuntimeException("Key not found");
            }
            this.data.put(key, data.get(key));
        }
    }
}
