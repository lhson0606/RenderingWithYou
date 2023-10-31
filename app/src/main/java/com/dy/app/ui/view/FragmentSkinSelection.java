package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.activity.MainActivity;
import com.dy.app.core.FragmentCallback;
import com.dy.app.manager.UIManager;
import com.dy.app.ui.adapter.AdapterSkinPage;
import com.google.android.material.tabs.TabLayout;

public class FragmentSkinSelection extends Fragment
        implements FragmentCallback, View.OnClickListener {

    private LottieAnimationView btnClose;

    public final static String TAG = "FragmentSkinSelection";
    private FragmentHubActivity main;
    private TabLayout tabLayout;
    private FragmentManager fragmentManager;
    private FragmentPieceSelection fragmentPieceSelection;
    private FragmentTerrainSelection fragmentTerrainSelection;
    private FragmentTileSelection fragmentTileSelection;
    private Fragment currentFragment;

    public static FragmentSkinSelection newInstance(){
        FragmentSkinSelection fragment = new FragmentSkinSelection();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (FragmentHubActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skin_selection, container, false);
        btnClose = view.findViewById(R.id.btnClose);
        tabLayout = view.findViewById(R.id.tlStage);
        final UIManager uiManager = UIManager.getInstance();
        fragmentPieceSelection = (FragmentPieceSelection)uiManager.getUI(UIManager.UIType.SKIN_PIECE_SELECTION);
        fragmentTerrainSelection = (FragmentTerrainSelection)uiManager.getUI(UIManager.UIType.SKIN_TERRAIN_SELECTION);
        fragmentTileSelection = (FragmentTileSelection)uiManager.getUI(UIManager.UIType.SKIN_TILE_SELECTION);

        fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.flStage, fragmentPieceSelection, FragmentPieceSelection.TAG);
        transaction.add(R.id.flStage, fragmentTerrainSelection);
        transaction.add(R.id.flStage, fragmentTileSelection);

        transaction.addToBackStack(FragmentPieceSelection.TAG);
        transaction.addToBackStack(FragmentTerrainSelection.TAG);
        transaction.addToBackStack(FragmentTileSelection.TAG);

        transaction.hide(fragmentTerrainSelection);
        transaction.hide(fragmentTileSelection);

        transaction.show(fragmentPieceSelection); currentFragment = fragmentPieceSelection;

        transaction.commit();

        return view;
    }

    private void showFragment(Fragment fragment){
        if(fragment == currentFragment) return;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(currentFragment);
        transaction.show(fragment);
        transaction.commit();
        currentFragment = fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        exqListener();
    }

    private void exqListener() {
        btnClose.setOnClickListener(this);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position  = tab.getPosition();

                switch (position){
                    case 0:
                        showFragment(fragmentPieceSelection);
                        break;
                    case 1:
                        showFragment(fragmentTerrainSelection);
                        break;
                    case 2:
                        showFragment(fragmentTileSelection);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnClose){
            btnClose.playAnimation();
            main.onMsgToMain(TAG, 0, null, null);
        }
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {

    }

    public void setFragmentManager(FragmentManager supportFragmentManager) {
    }
}
