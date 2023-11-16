package com.dy.app.manager;

import static com.dy.app.manager.ItemsFinder.ItemType.*;

import com.dy.app.R;
import com.dy.app.gameplay.item.ItemInfo;

import java.util.HashMap;
import java.util.Map;

public class ItemsFinder {
    private static ItemsFinder instance;

    private Map<ItemType, ItemInfo> itemInfoMap;

    public enum ItemType{
        COINx1,
        COINx2,
        COINx3,
        CHEST,
        SKIN,
    }

    public static ItemsFinder getInstance(){
        if(instance == null){
            instance = new ItemsFinder();
        }

        return instance;
    }

    private ItemsFinder(){
        itemInfoMap = new HashMap<>();
        itemInfoMap.put(COINx1, new ItemInfo(R.drawable.game_item_coins_x1, "1 Coin", "1 Coin"));
        itemInfoMap.put(COINx2, new ItemInfo(R.drawable.game_item_coins_x2, "2 Coins", "2 Coins"));
        itemInfoMap.put(COINx3, new ItemInfo(R.drawable.game_item_coins_x3, "3 Coins", "3 Coins"));
    }

    public ItemInfo getItemInfo(ItemType itemType){
        ItemInfo itemInfo = new ItemInfo(itemInfoMap.get(itemType));
        return itemInfo;
    }

    public ItemInfo getItemInfoById(int itemId){
        ItemInfo res = null;
        switch (itemId){
            case 1:
                res = getItemInfo(COINx1);
                break;
            case 2:
                res = getItemInfo(COINx2);
                break;
            case 3:
                res = getItemInfo(COINx3);
                break;
        }

        return res;
    }
}
