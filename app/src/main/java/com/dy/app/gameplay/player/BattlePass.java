package com.dy.app.gameplay.player;

import com.dy.app.db.Database;
import com.dy.app.db.OnDBRequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattlePass {
    private Map<String,Object> data;
    public static final String KEY_LEVEL = "level";
    public static final String KEY_OBTAIN_ITEMS = "obtainedItems";
    public static final String KEY_IS_ACTIVATED = "isActivated";

    public BattlePass(){
        data = new HashMap<>();
        data.put(KEY_LEVEL, 0L);
        List<Long> obtainedItems = new ArrayList<>();
        data.put(KEY_OBTAIN_ITEMS, obtainedItems);
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

    public void putAll(Map<String, Object> data){
        for(String key : data.keySet()){
            set(key, data.get(key));
        }
    }

    public boolean hasObtained(long i){
        List<Long> obtainedItems = (List<Long>)get(KEY_OBTAIN_ITEMS);
        return obtainedItems.contains(i);
    }

    public void obtainItem(long i, long itemId,OnDBRequestListener listener) {
        List<Long> obtainedItems = (List<Long>)get(KEY_OBTAIN_ITEMS);
        //will be used to perform rollback data if failed
        List<Long> newObtainedItems = new ArrayList<>();
        newObtainedItems.addAll(obtainedItems);
        newObtainedItems.add(i);
        set(KEY_OBTAIN_ITEMS, newObtainedItems);
        Database.getInstance().updateBattlePassOnDB((res, o)->{
            if(res == Database.RESULT_SUCCESS) {
                Player.getInstance().inventory.obtain(itemId, (res1, o1)-> {
                    if (res1 == Database.RESULT_SUCCESS) {
                        set(KEY_OBTAIN_ITEMS, newObtainedItems);
                    } else {
                        //rollback as if nothing happened
                        set(KEY_OBTAIN_ITEMS, obtainedItems);
                    }
                    listener.onDBRequestCompleted(res1, o1);
                });
            }else{
                //rollback as if nothing happened
                set(KEY_OBTAIN_ITEMS, obtainedItems);
                listener.onDBRequestCompleted(res, o);
            }
        });
    }

    public void addNewGamepassPoint(int i) {
        long level = ((Long)get(KEY_LEVEL)).longValue();
        level += i;
        set(KEY_LEVEL, level);
    }
}
