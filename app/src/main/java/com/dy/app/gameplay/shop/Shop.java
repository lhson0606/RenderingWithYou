package com.dy.app.gameplay.shop;

import com.dy.app.gameplay.Player;
import com.dy.app.gameplay.PlayerPurchase;
import com.dy.app.gameplay.item.GameItem;

import java.util.Vector;
import java.util.regex.Pattern;

public class Shop {
    Vector<ShopItem> shopItems;

    public Shop(){
        shopItems = new Vector<ShopItem>();
        float discountValue = ((Long)Player.getInstance().purchase.get(PlayerPurchase.KEY_DISCOUNT)).floatValue() / 100;
        shopItems.add(new ShopItem(new GameItem(), 200, "Fire & Ice", "Piece skin", ShopItem.RARITY_EPIC, discountValue, ShopItem.GOLD));
        shopItems.add(new ShopItem(new GameItem(), 100, "Piece skin", "A sword", ShopItem.RARITY_COMMON, discountValue, ShopItem.GOLD));
        shopItems.add(new ShopItem(new GameItem(), 1000, "Piece skin", "A sword", ShopItem.RARITY_LEGENDARY, discountValue, ShopItem.DIAMOND));
        shopItems.add(new ShopItem(new GameItem(), 400, "Piece skin", "A sword", ShopItem.RARITY_RARE, discountValue, ShopItem.DIAMOND));
        shopItems.add(new ShopItem(new GameItem(), 200, "Piece skin", "A sword", ShopItem.RARITY_EPIC, discountValue, ShopItem.DIAMOND));

    }

    public Vector<ShopItem> getShopItems() {
        return shopItems;
    }
}
