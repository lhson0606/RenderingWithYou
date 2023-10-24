package com.dy.app.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.dy.app.manager.UIManager;
import com.dy.app.ui.view.FragmentCredits;

public class AdapterSkinPage extends FragmentStatePagerAdapter  {
    static final String TAG = "SkinPageAdapter";
    final String skin_pages[] = {"Piece", "Terrain", "Tile"};
    final UIManager uiManager = UIManager.getInstance();
    final UIManager.UIType types[] = {UIManager.UIType.SKIN_PIECE_SELECTION, UIManager.UIType.SKIN_TERRAIN_SELECTION, UIManager.UIType.SKIN_TILE_SELECTION};

    public AdapterSkinPage(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentCredits.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return skin_pages[position];
    }

    @Override
    public int getCount() {
        return skin_pages.length;
    }
}
