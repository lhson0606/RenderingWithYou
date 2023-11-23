package com.dy.app.gameplay.player;

import java.util.Map;

public class PlayerPreferences {
    private Map<String, Object> data;
    public static final String KEY_SOUND_ON = "soundOn";
    public static final String KEY_GL_DRAW_MODE = "glDrawMode";

    public PlayerPreferences(){
        data = new java.util.HashMap<>();
        data.put(KEY_SOUND_ON, true);
        data.put(KEY_GL_DRAW_MODE, 0L);
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
        Map<String, Object> res = new java.util.HashMap<>();

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
