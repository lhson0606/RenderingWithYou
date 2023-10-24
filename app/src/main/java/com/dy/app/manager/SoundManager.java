package com.dy.app.manager;

import android.content.Context;
import android.media.MediaPlayer;

import com.dy.app.R;

import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    public enum SoundType {
        CLICK,
        MOVE,
        CAPTURE,
        CHECK,
        CHECKMATE,
        DRAW,
        WIN,
        LOSE,
        PROMOTION,
        CASTLING,
        ENPASSANT,
        STALEMATE,
        RESIGN,
        UNDO,
        REDO,
        NEWGAME,
        SAVE,
        LOAD,
        EXIT,
        ERROR,
        BTN_BLOP,
        BTN_SKIN_PICKING,
    }

    private Context currentContext;

    private Map<SoundType, MediaPlayer> soundMap;

    private SoundManager() {
        soundMap = new java.util.HashMap<>();
    }

    public SoundManager initInContext(Context context){
        if(currentContext == context){
            return this;
        }

        currentContext = context;

        MediaPlayer mp = MediaPlayer.create(context, R.raw.se_btn_blop);
        soundMap.put(SoundType.BTN_BLOP, mp); mp.setVolume(0.5f, 0.5f);
        soundMap.put(SoundType.BTN_SKIN_PICKING, MediaPlayer.create(context, R.raw.se_btn_skin_picking));

        return this;
    }

    public void playSound(Context context, SoundType type){
        if(context!= currentContext){
            initInContext(context);
        }

        MediaPlayer sound = soundMap.get(type);
        if(sound != null){
            sound.start();
        }
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
}
