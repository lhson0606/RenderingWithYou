package com.dy.app.gameplay.item;

public class ItemInfo {
    public int res;
    public String name;
    public String description;

    public ItemInfo(int res, String name, String description) {
        this.res = res;
        this.name = name;
        this.description = description;
    }

    public ItemInfo(ItemInfo itemInfo) {
        this.res = itemInfo.res;
        this.name = itemInfo.name;
        this.description = itemInfo.description;
    }
}
