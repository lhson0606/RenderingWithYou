package com.dy.app.gameplay.shop;

import com.dy.app.gameplay.item.GameItem;

public class ShopItem {
    public static final String RARITY_COMMON = "common";
    public static final String RARITY_RARE = "rare";
    public static final String RARITY_EPIC = "epic";
    public static final String RARITY_LEGENDARY = "legendary";
    public static final int GOLD = 0;
    public static final int DIAMOND = 1;
    long price;
    String name;
    String description;
    String rarity;
    float discount;
    GameItem gameItem;
    int currencyType;


    public ShopItem(GameItem gameItem, long price, String name, String description, String rarity, float discount, int currencyType) {
        this.gameItem = gameItem;
        this.price = price;
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.discount = discount;
        this.currencyType = currencyType;
    }

    public long getPrice(){
        return price;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getRarity(){
        return rarity;
    }

    public float getDiscount(){
        return discount;
    }

    public float getFinalPrice() {
        return price - price * discount;
    }

    public GameItem getGameItem() {
        return gameItem;
    }

    public int getCurrencyType() {
        return currencyType;
    }
}
