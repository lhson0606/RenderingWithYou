package com.dy.app.gameplay.player;

import com.dy.app.ui.dialog.P2pGameResultDialog;

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
    public static final String KEY_TOTAL_DRAW_AS_WHITE = "totalDrawAsWhite";
    public static final String KEY_TOTAL_DRAW_AS_BLACK = "totalDrawAsBlack";

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
        data.put(KEY_TOTAL_DRAW_AS_WHITE, 0L);
        data.put(KEY_TOTAL_DRAW_AS_BLACK, 0L);
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

    public void addNewGameStats(int gameResult, boolean isWhite, long duration, long promotionCount) {
        Long totalGame = (Long) data.get(KEY_TOTAL_GAME);
        totalGame++;
        data.put(KEY_TOTAL_GAME, totalGame);
        Long totalTime = (Long) data.get(KEY_TOTAL_TIME);
        totalTime += duration;
        data.put(KEY_TOTAL_TIME, totalTime);
        Long totalPromotion = (Long) data.get(KEY_TOTAL_PROMOTION);
        totalPromotion += promotionCount;
        data.put(KEY_TOTAL_PROMOTION, totalPromotion);
        Long totalOnWhite = (Long) data.get(KEY_TOTAL_ON_WHITE);
        Long totalOnBlack = (Long) data.get(KEY_TOTAL_ON_BLACK);
        if (isWhite) {
            totalOnWhite++;
            data.put(KEY_TOTAL_ON_WHITE, totalOnWhite);
        } else {
            totalOnBlack++;
            data.put(KEY_TOTAL_ON_BLACK, totalOnBlack);
        }
        if (gameResult == P2pGameResultDialog.WIN) {
            Long win = (Long) data.get(KEY_WIN);
            win++;
            data.put(KEY_WIN, win);
            if (isWhite) {
                Long totalWinAsWhite = (Long) data.get(KEY_TOTAL_WIN_AS_WHITE);
                totalWinAsWhite++;
                data.put(KEY_TOTAL_WIN_AS_WHITE, totalWinAsWhite);
            } else {
                Long totalWinAsBlack = (Long) data.get(KEY_TOTAL_WIN_AS_BLACK);
                totalWinAsBlack++;
                data.put(KEY_TOTAL_WIN_AS_BLACK, totalWinAsBlack);
            }
        } else if (gameResult == P2pGameResultDialog.LOSE) {
            Long lose = (Long) data.get(KEY_LOSE);
            lose++;
            data.put(KEY_LOSE, lose);
            if (isWhite) {
                Long totalLoseAsWhite = (Long) data.get(KEY_TOTAL_LOSE_AS_WHITE);
                totalLoseAsWhite++;
                data.put(KEY_TOTAL_LOSE_AS_WHITE, totalLoseAsWhite);
            } else {
                Long totalLoseAsBlack = (Long) data.get(KEY_TOTAL_LOSE_AS_BLACK);
                totalLoseAsBlack++;
                data.put(KEY_TOTAL_LOSE_AS_BLACK, totalLoseAsBlack);
            }
        } else {
            if (isWhite) {
                Long totalDrawAsWhite = (Long) data.get(KEY_TOTAL_DRAW_AS_WHITE);
                totalDrawAsWhite++;
                data.put(KEY_TOTAL_DRAW_AS_WHITE, totalDrawAsWhite);
            } else {
                Long totalDrawAsBlack = (Long) data.get(KEY_TOTAL_DRAW_AS_BLACK);
                totalDrawAsBlack++;
                data.put(KEY_TOTAL_DRAW_AS_BLACK, totalDrawAsBlack);
            }
        }
    }
}
