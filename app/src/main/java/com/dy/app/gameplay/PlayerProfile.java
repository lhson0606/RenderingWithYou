package com.dy.app.gameplay;

import java.util.HashMap;
import java.util.Map;

public class PlayerProfile {
    private Map<String, Object> data;
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PHOTO_URL = "photoUrl";
    public static final String KEY_UID = "uID";

    public PlayerProfile(){
        data = new HashMap<>();
        data.put(KEY_USERNAME, "");
        data.put(KEY_EMAIL, "");
        data.put(KEY_PHONE, "");
        data.put(KEY_PHOTO_URL, "");
        data.put(KEY_UID, "");
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