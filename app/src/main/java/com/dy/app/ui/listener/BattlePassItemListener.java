package com.dy.app.ui.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.paging.LoadState;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.db.Database;
import com.dy.app.gameplay.BattlePass;
import com.dy.app.gameplay.item.ItemInfo;
import com.dy.app.manager.ItemsFinder;
import com.dy.app.manager.SoundManager;
import com.dy.app.ui.dialog.LoadingDialog;
import com.dy.app.ui.dialog.OpenChestDialog;
import com.dy.app.utils.GameItemsUtils;
import com.dy.app.utils.Utils;

public class BattlePassItemListener implements View.OnClickListener{
    int itemId;
    int index;
    LottieAnimationView anim;
    ImageView btn;
    ImageView imgItem;
    BattlePass battlePass;
    FragmentActivity fragmentActivity;
    LoadingDialog loadingDialog;

    static int OBTAIN_FAILED = -1;
    static int OBTAIN_SUCCESS = 0;

    private interface OnItemObtainedListener{
        void onItemObtained(int res);
    }

    public BattlePassItemListener(FragmentActivity activity, BattlePass battlePass, LottieAnimationView anim, ImageView btn, ImageView imgItem,int itemId, int index){
        this.fragmentActivity = activity;
        this.battlePass = battlePass;
        this.anim = anim;
        this.btn = btn;
        this.imgItem = imgItem;
        this.itemId = itemId;
        this.index = index;
        this.loadingDialog = new LoadingDialog();
        this.loadingDialog.setCancelable(false);
    }

    private int getItemResource(int itemId){
        final ItemInfo itemInfo = ItemsFinder.getInstance().getItemInfoById(itemId);
        return itemInfo.res;
    }

    @Override
    public void onClick(View v) {
        if(fragmentActivity.isDestroyed() || fragmentActivity.isFinishing()){
            return;
        }

        btn.setEnabled(false);

        //if it's a chest
        if(itemId == 0){
            //get random coins 1-3
            int temp = Utils.randomInt(1,3);
            obtainItem(temp, (res)->{
                if(res == OBTAIN_FAILED){
                    return;
                }
                OpenChestDialog openChestDialog = new OpenChestDialog(getItemResource(temp), new OpenChestDialog.OpenChestDialogListener() {
                    @Override
                    public void onChestStartOpening() {
                        SoundManager.getInstance().playSound(fragmentActivity, SoundManager.SoundType.WOOD_HIT);
                    }

                    @Override
                    public void onChestOpened() {
                        SoundManager.getInstance().playSound(fragmentActivity, SoundManager.SoundType.ANGELIC_CHORUS);
                    }
                });
                openChestDialog.show(fragmentActivity.getSupportFragmentManager(), "OpenChestDialog");
            });
        }else{
            //if it's a normal item
            obtainItem(itemId, (res)->{
                if(res == OBTAIN_SUCCESS){
                    anim.playAnimation();
                }
            });
        }
    }

    private void obtainItem(int itemId, OnItemObtainedListener listener){
        loadingDialog.show(fragmentActivity.getSupportFragmentManager(), "loading");
        battlePass.obtainItem(index, itemId,(res, o)->{
            loadingDialog.dismiss();
            if(res == Database.RESULT_SUCCESS){
                btn.setImageResource(R.drawable.item_collected);
                SoundManager.getInstance().playSound(fragmentActivity, SoundManager.SoundType.COIN_CLINK);
                imgItem.setImageAlpha((int)(255*0.8));
                listener.onItemObtained(OBTAIN_SUCCESS);
            }else{
                btn.setEnabled(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
                builder.setTitle("Something went wrong, please check your internet connection and try again");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                });
                listener.onItemObtained(OBTAIN_FAILED);
            }
        });
    }
}
