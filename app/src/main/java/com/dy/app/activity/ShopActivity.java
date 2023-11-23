package com.dy.app.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerInventory;
import com.dy.app.gameplay.player.PlayerProfile;
import com.dy.app.gameplay.player.PlayerPurchase;
import com.dy.app.gameplay.shop.Shop;
import com.dy.app.gameplay.shop.ShopItem;
import com.dy.app.utils.ShopBundleFactory;
import com.dy.app.utils.ShopCategoryFactory;

import java.io.IOException;
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
        updateView();
    }

    private void init(){
        shop = new Shop();

        btnClose = findViewById(R.id.btnClose);
        shopItemsContainer = findViewById(R.id.shopItemsContainer);
        tvCoinAmount = findViewById(R.id.tvCoinAmount);
        tvGemAmount = findViewById(R.id.tvGemAmount);
        tvUsername = findViewById(R.id.tvUsername);
        tvDiscountValue = findViewById(R.id.tvDiscountValue);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        btnCoinPurchase = findViewById(R.id.btnCoinPurchase);
        btnGemPurchase = findViewById(R.id.btnGemPurchase);

    }

    private void updateView() {
        tvCoinAmount.setText(Player.getInstance().inventory.get(PlayerInventory.KEY_COIN).toString());
        tvGemAmount.setText(Player.getInstance().inventory.get(PlayerInventory.KEY_GEMS).toString());
        tvUsername.setText(Player.getInstance().profile.get(PlayerProfile.KEY_USERNAME).toString());

        Thread setAvatarWorker = new Thread(()->{
            try {
                Drawable avatar = Player.getInstance().getDrawableAvatar(this);
                runOnUiThread(()->{
                    ivUserAvatar.setImageDrawable(avatar);
                });
            } catch (IOException e) {
                //#todo handle exception
                throw new RuntimeException(e);
            }
        });

        tvDiscountValue.setText(Player.getInstance().purchase.get(PlayerPurchase.KEY_DISCOUNT).toString() + "%");

        setAvatarWorker.start();

    }

    private void attachListener(){
        btnClose.setOnClickListener(this);
        btnCoinPurchase.setOnClickListener(this);
        btnGemPurchase.setOnClickListener(this);
    }

    private void addShopItems() {
        Vector<ShopItem> items = shop.getShopItems();
        LinearLayout bundles;
        bundles = ShopCategoryFactory.createShopCategory(this, shopItemsContainer, "Bundle", ShopCategoryFactory.FIRST_ELEMENT, findViewById(R.id.chainDecoration));
        for(int j = 0; j < items.size(); j++){
            ShopItem item = items.get(j);
            FrameLayout bundle = ShopBundleFactory.createShopBundle(this, bundles, item);
        }

    }

    @Override
    public void onClick(View v) {
        if(v == btnClose){
            btnClose.playAnimation();
            finish();
        } else if(v == btnCoinPurchase){
            btnCoinPurchase.playAnimation();
        } else if(v == btnGemPurchase){
            btnGemPurchase.playAnimation();
        }
    }

    private Shop shop;
    private LottieAnimationView btnClose;
    private ImageView ivUserAvatar;
    private LottieAnimationView btnCoinPurchase, btnGemPurchase;
    private TextView tvGemAmount, tvCoinAmount, tvUsername, tvDiscountValue;
    private LinearLayout shopItemsContainer;
}
