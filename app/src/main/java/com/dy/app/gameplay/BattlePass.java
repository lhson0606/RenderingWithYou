package com.dy.app.gameplay;

import java.util.HashMap;
import java.util.Map;

public class BattlePass {
    private Map<String,Object> data;
    public static final String KEY_LEVEL = "level";
    public static final String KEY_OBTAIN_ITEMS = "obtainedItems";
    public static final String KEY_IS_ACTIVATED = "isActivated";

    public BattlePass(){
        data = new HashMap<>();
        data.put(KEY_LEVEL, 0);
        data.put(KEY_OBTAIN_ITEMS, new int[]{});
        data.put(KEY_IS_ACTIVATED, false);
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
