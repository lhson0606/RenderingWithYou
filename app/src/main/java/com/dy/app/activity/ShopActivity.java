package com.dy.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.gameplay.shop.Shop;
import com.dy.app.gameplay.shop.ShopItem;
import com.dy.app.utils.ShopBundleFactory;
import com.dy.app.utils.ShopCategoryFactory;

import java.util.Vector;

public class ShopActivity extends FragmentActivity
implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
        attachListener();
        addShopItems();
    }

    private void init(){
        shop = new Shop();

        btnClose = findViewById(R.id.btnClose);
        shopItemsContainer = findViewById(R.id.shopItemsContainer);
    }

    private void attachListener(){
        btnClose.setOnClickListener(this);
    }

    private void addShopItems() {
        for(int i = 0; i<5; i++){
            //bundle
            Vector<ShopItem> items = shop.getShopItems();
            LinearLayout bundles;

            if(i==0){
                bundles = ShopCategoryFactory.createShopCategory(this, shopItemsContainer, "Bundle", ShopCategoryFactory.FIRST_ELEMENT, findViewById(R.id.chainDecoration));
            }else{
                bundles = ShopCategoryFactory.createShopCategory(this, shopItemsContainer, "Bundle", ShopCategoryFactory.MIDDLE_ELEMENT, findViewById(R.id.chainDecoration));
            }

            for(int j = 0; j < items.size(); j++){
                ShopItem item = items.get(j);
                FrameLayout bundle = ShopBundleFactory.createShopBundle(this, bundles, item);
            }
        }

    }

    @Override
    public void onClick(View v) {
        if(v == btnClose){
            btnClose.playAnimation();
            finish();
        }
    }

    private Shop shop;
    private LottieAnimationView btnClose;
    private LinearLayout shopItemsContainer;
}
