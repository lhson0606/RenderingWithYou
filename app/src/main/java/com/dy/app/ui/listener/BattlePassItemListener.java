package com.dy.app.ui.listener;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.gameplay.BattlePass;
import com.dy.app.manager.SoundManager;

public class BattlePassItemListener implements View.OnClickListener{
    int itemId;
    int index;
    LottieAnimationView anim;
    ImageView img;
    BattlePass battlePass;
    Context context;

    public BattlePassItemListener(Context context, BattlePass battlePass, LottieAnimationView anim, ImageView img, int itemId, int index){
        this.context = context;
        this.battlePass = battlePass;
        this.anim = anim;
        this.img = img;
        this.itemId = itemId;
        this.index = index;
    }

    @Override
    public void onClick(View v) {
        anim.playAnimation();
        img.setEnabled(false);
        img.setImageResource(R.drawable.item_collected);
        SoundManager.getInstance().playSound(context, SoundManager.SoundType.COIN_CLINK);
        battlePass.obtainItem(index);
    }
}
