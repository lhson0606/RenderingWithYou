package com.dy.app.utils;

import com.dy.app.gameplay.item.ItemInfo;
import com.dy.app.manager.ItemsFinder;

public class GameItemsUtils {
    public static ItemInfo getRandomCoinsItem(){
        final int amount =  Utils.randomInt(1, 3);
        ItemInfo res = null;
        switch (amount){
            case 1:
                res = ItemsFinder.getInstance().getItemInfo(ItemsFinder.ItemType.COINx1);
                break;
            case 2:
                res = ItemsFinder.getInstance().getItemInfo(ItemsFinder.ItemType.COINx2);
                break;
            case 3:
                res = ItemsFinder.getInstance().getItemInfo(ItemsFinder.ItemType.COINx3);
                break;
        }

        return res;
    }
}
