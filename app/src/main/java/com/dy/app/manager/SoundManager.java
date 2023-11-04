package com.dy.app.manager;

import android.content.Context;
import android.media.MediaPlayer;

import com.dy.app.R;

import java.io.IOException;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private boolean soundOn = true;

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

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
        COIN_CLINK,
        FIREWORK_LONG,
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

        for(MediaPlayer mp : soundMap.values()){
            if(mp!=null)
                mp.release();
        }

        currentContext = context;

        addSound(SoundType.BTN_BLOP, R.raw.se_btn_blop, 0.5f, 0.5f);
        soundMap.put(SoundType.BTN_SKIN_PICKING, MediaPlayer.create(context, R.raw.se_btn_skin_picking));
        addSound(SoundType.COIN_CLINK, R.raw.se_coins_clinking, 0.5f, 0.5f);
        addSound(SoundType.FIREWORK_LONG, R.raw.se_firework_long, 0.5f, 0.5f);

        return this;
    }

    private void addSound(SoundType type, int res, float leftVolume, float rightVolume){
        MediaPlayer mp = MediaPlayer.create(currentContext, res);
        mp.setVolume(leftVolume, rightVolume);
        soundMap.put(type, mp);
    }

    public void playSound(Context context, SoundType type){
        if(context!= currentContext){
            initInContext(context);
        }

        if(!soundOn){
            return;
        }

        MediaPlayer sound = soundMap.get(type);
        if(sound != null){
            sound.start();
        }
    }

    public void stopSound(SoundType type){
        final MediaPlayer sound = soundMap.get(type);

        if(sound != null){
            if(sound.isPlaying()){
                sound.stop();

                try {
                    sound.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
}
