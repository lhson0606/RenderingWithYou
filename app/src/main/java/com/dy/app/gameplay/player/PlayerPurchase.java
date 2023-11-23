package com.dy.app.gameplay.player;

import java.util.HashMap;
import java.util.Map;

public class PlayerPurchase {
    private Map<String, Object> data;
    public static final String KEY_DISCOUNT = "discount";
    public static final String KEY_FIRST_PURCHASE = "firstPurchase";

    public PlayerPurchase(){
        data = new HashMap<>();
        data.put(KEY_DISCOUNT, 20L);
        data.put(KEY_FIRST_PURCHASE, true);
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
