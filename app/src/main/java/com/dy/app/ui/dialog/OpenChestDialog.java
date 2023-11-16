package com.dy.app.ui.dialog;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;

public class OpenChestDialog extends DialogFragment implements View.OnClickListener {
    private int res;
    private String animationName = "animation/anim_opening_chest.json";
    private boolean isChestOpened = false;

    @Override
    public void onClick(View v) {
        if(isChestOpened){
            dismiss();
        }
    }

    public interface OpenChestDialogListener{
        void onChestStartOpening();
        void onChestOpened();
    }
    private OpenChestDialogListener listener;

    public OpenChestDialog(int res, OpenChestDialogListener listener) {
        this.res = res;
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the dialog style to the transparent style
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.bg_showing_item, container, false);
        LottieAnimationView lvAnim = v.findViewById(R.id.lvAnim);
        TextView tvTapToContinue = v.findViewById(R.id.tvTapToContinue);
        tvTapToContinue.setVisibility(View.INVISIBLE);
        lvAnim.setAnimation(animationName);
        setCancelable(false);
        isChestOpened = false;
        lvAnim.playAnimation();
        lvAnim.addAnimatorListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                listener.onChestStartOpening();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                setCancelable(true);
                ImageView imageView = v.findViewById(R.id.ivItem);
                imageView.setImageResource(res);
                tvTapToContinue.setVisibility(View.VISIBLE);
                listener.onChestOpened();
                isChestOpened = true;
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        v.setOnClickListener(this);

        return v;
    }

}
