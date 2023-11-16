package com.dy.app.gameplay.shop;

import com.dy.app.gameplay.item.GameItem;

import java.util.Vector;

public class Shop {
    Vector<ShopItem> shopItems;

    public Shop(){
        shopItems = new Vector<ShopItem>();
        shopItems.add(new ShopItem(new GameItem(), 100, "Piece skin", "A sword", ShopItem.RARITY_COMMON, 0.2f, ShopItem.GOLD));
        shopItems.add(new ShopItem(new GameItem(), 100, "Piece skin", "A sword", ShopItem.RARITY_COMMON, 0.2f, ShopItem.GOLD));
        shopItems.add(new ShopItem(new GameItem(), 100, "Piece skin", "A sword", ShopItem.RARITY_LEGENDARY, 0.2f, ShopItem.DIAMOND));
        shopItems.add(new ShopItem(new GameItem(), 100, "Piece skin", "A sword", ShopItem.RARITY_RARE, 0.2f, ShopItem.DIAMOND));
        shopItems.add(new ShopItem(new GameItem(), 100, "Piece skin", "A sword", ShopItem.RARITY_EPIC, 0.2f, ShopItem.DIAMOND));

    }

    public Vector<ShopItem> getShopItems() {
        return shopItems;
    }
}
