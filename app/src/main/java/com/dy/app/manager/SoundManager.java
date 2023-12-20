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
        GAME_START,
        GAME_OVER,
        CAPTURE,
        CASTLE,
        MOVE_CHECK,
        MOVE_SELF,
        NOTIFY,
        PROMOTE,
        BTN_BLOP,
        BTN_SKIN_PICKING,
        COIN_CLINK,
        FIREWORK_LONG,
        ANGELIC_CHORUS,
        WOOD_HIT,
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
        addSound(SoundType.ANGELIC_CHORUS, R.raw.se_angelic_chorus, 1f, 1f);
        addSound(SoundType.WOOD_HIT, R.raw.se_wood_hit, 1f, 1f);
        addSound(SoundType.CAPTURE, R.raw.se_capture, 1f, 1f);
        addSound(SoundType.CASTLE, R.raw.se_castle, 1f, 1f);
        addSound(SoundType.MOVE_CHECK, R.raw.se_move_check, 1f, 1f);
        addSound(SoundType.MOVE_SELF, R.raw.se_move_self, 1f, 1f);
        addSound(SoundType.NOTIFY, R.raw.se_notify, 1f, 1f);
        addSound(SoundType.PROMOTE, R.raw.se_promote, 1f, 1f);
        addSound(SoundType.GAME_START, R.raw.se_game_start, 1f, 1f);
        addSound(SoundType.GAME_OVER, R.raw.se_game_over, 1f, 1f);

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

    public void prepare(Context context){
        if(context!= currentContext){
            initInContext(context);
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
