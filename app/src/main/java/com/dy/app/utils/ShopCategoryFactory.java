package com.dy.app.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.dy.app.R;

import java.util.zip.Inflater;

public class ShopCategoryFactory {
    public static final int FIRST_ELEMENT = 0;
    public static final int MIDDLE_ELEMENT = 1;
    public static final int LAST_ELEMENT = 2;
    private static boolean first = true;

    public static LinearLayout createShopCategory(Context context, LinearLayout shopCategoryContainer, String category, int elementType, LinearLayout chainView){
        ConstraintLayout constraintLayout = new ConstraintLayout(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.shop_category, constraintLayout, false);
        TextView tvCategory = v.findViewById(R.id.tvCategoryName);
        tvCategory.setText(category);
        //#todo add element type
        if(elementType!=FIRST_ELEMENT){
            ImageView doubleChains = new ImageView(context);
            doubleChains.setImageResource(R.drawable.decoration_double_chains);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            doubleChains.setScaleType(ImageView.ScaleType.FIT_XY);
            doubleChains.setLayoutParams(params);
            params.setMargins(0, -0, 0, 0);
            View rigid = new View(context);
            ViewTreeObserver vto = v.getViewTreeObserver();
            if(first){
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        //rigid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, v.getHeight() - doubleChains.getHeight()));
                        ViewTreeObserver vto = doubleChains.getViewTreeObserver();
                        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            public void onGlobalLayout() {
                                doubleChains.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                rigid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, v.getHeight() - doubleChains.getHeight()/2));
                            }
                        });
                    }
                });
                first = false;
            }else{
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        rigid.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, v.getMeasuredHeight() - doubleChains.getHeight()));
                    }
                });
            }

            chainView.addView(rigid);
            chainView.addView(doubleChains);
        }

        shopCategoryContainer.addView(v);

        LinearLayout shopItemsContainer = v.findViewById(R.id.categoryItemsContainer);
        return shopItemsContainer;
    }

}
