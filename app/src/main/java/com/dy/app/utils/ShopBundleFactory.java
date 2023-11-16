package com.dy.app.utils;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dy.app.R;
import com.dy.app.activity.ShopActivity;
import com.dy.app.gameplay.shop.ShopItem;

public class ShopBundleFactory {
    public static FrameLayout createShopBundle(ShopActivity shopActivity, LinearLayout bundles, ShopItem item) {
        switch (item.getRarity()){
            case ShopItem.RARITY_COMMON:
                return ShopBundleFactory.createCommonBundle(shopActivity, bundles, item);
            case ShopItem.RARITY_RARE:
                return ShopBundleFactory.createRareBundle(shopActivity, bundles, item);
            case ShopItem.RARITY_EPIC:
                return ShopBundleFactory.createEpicBundle(shopActivity, bundles, item);
            case ShopItem.RARITY_LEGENDARY:
                return ShopBundleFactory.createLegendaryBundle(shopActivity, bundles, item);
            default:
                return null;
        }
    }

    private static FrameLayout createEpicBundle(ShopActivity shopActivity, LinearLayout bundles, ShopItem item) {
        FrameLayout frameLayout = new FrameLayout(shopActivity);
        LayoutInflater inflater = LayoutInflater.from(shopActivity);
        inflater.inflate(R.layout.shop_bundle, frameLayout, true);
        frameLayout.setBackgroundResource(R.drawable.bg_epic_bundle);
        addDetail(item, frameLayout);
        bundles.addView(frameLayout);
        return frameLayout;
    }

    private static FrameLayout createRareBundle(ShopActivity shopActivity, LinearLayout bundles, ShopItem item) {
        FrameLayout frameLayout = new FrameLayout(shopActivity);
        LayoutInflater inflater = LayoutInflater.from(shopActivity);
        inflater.inflate(R.layout.shop_bundle, frameLayout, true);
        frameLayout.setBackgroundResource(R.drawable.bg_rare_bundle);
        addDetail(item, frameLayout);
        bundles.addView(frameLayout);
        return frameLayout;
    }

    private static FrameLayout createCommonBundle(ShopActivity shopActivity, LinearLayout bundles, ShopItem item) {
        FrameLayout frameLayout = new FrameLayout(shopActivity);
        LayoutInflater inflater = LayoutInflater.from(shopActivity);
        inflater.inflate(R.layout.shop_bundle, frameLayout, true);
        frameLayout.setBackgroundResource(R.drawable.bg_common_bundle);
        addDetail(item, frameLayout);
        bundles.addView(frameLayout);
        return frameLayout;
    }

    private static FrameLayout createLegendaryBundle(ShopActivity shopActivity, LinearLayout bundles, ShopItem item) {
        FrameLayout frameLayout = new FrameLayout(shopActivity);
        LayoutInflater inflater = LayoutInflater.from(shopActivity);
        inflater.inflate(R.layout.shop_bundle, frameLayout, true);
        frameLayout.setBackgroundResource(R.drawable.bg_legendary_bundle);
        addDetail(item, frameLayout);
        bundles.addView(frameLayout);
        return frameLayout;
    }

    private static void addDetail(ShopItem item, View bundleView){
        ImageView ivDiscount = bundleView.findViewById(R.id.ivDiscount);

        if (item.getDiscount()>0){
            ivDiscount.setVisibility(ImageView.VISIBLE);
            switch (item.getRarity()){
                case ShopItem.RARITY_COMMON:
                    ivDiscount.setImageResource(R.drawable.decoration_common_discount_ribbon);
                    break;
                case ShopItem.RARITY_RARE:
                    ivDiscount.setImageResource(R.drawable.decoration_rare_discount_ribbon);
                    break;
                case ShopItem.RARITY_EPIC:
                    ivDiscount.setImageResource(R.drawable.decoration_epic_discount_ribbon);
                    break;
                case ShopItem.RARITY_LEGENDARY:
                    ivDiscount.setImageResource(R.drawable.decoration_legendary_discount_ribbon);
                    break;
            }
            TextView tvStartPrice = bundleView.findViewById(R.id.tvStartPrice);
            tvStartPrice.setText(String.valueOf(item.getPrice()));
            tvStartPrice.setPaintFlags(tvStartPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            ivDiscount.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        TextView tvName = bundleView.findViewById(R.id.tvName);
        tvName.setText(item.getName());
        TextView tvFinalPrice = bundleView.findViewById(R.id.tvFinalPrice);
        tvFinalPrice.setText(String.valueOf(item.getFinalPrice()));
        ImageView ivCurrency = bundleView.findViewById(R.id.ivCurrency);
        ivCurrency.setVisibility(ImageView.VISIBLE);
        if(item.getCurrencyType() == ShopItem.DIAMOND){
            ivCurrency.setBackgroundResource(R.drawable.item_gem);
        }else if(item.getCurrencyType() == ShopItem.GOLD){
            ivCurrency.setBackgroundResource(R.drawable.game_item_coins_x1);
        }
    }
}
