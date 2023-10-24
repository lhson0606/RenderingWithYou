package com.dy.app.manager;

import android.provider.Contacts;

import androidx.fragment.app.Fragment;

import com.dy.app.ui.view.FragmentCreateAccount;
import com.dy.app.ui.view.FragmentCredits;
import com.dy.app.ui.view.FragmentLoginForm;
import com.dy.app.ui.view.FragmentLogoutForm;
import com.dy.app.ui.view.FragmentMainMenu;
import com.dy.app.ui.view.FragmentPieceSelection;
import com.dy.app.ui.view.FragmentSetting;
import com.dy.app.ui.view.FragmentSkinSelection;
import com.dy.app.ui.view.FragmentTerrainSelection;
import com.dy.app.ui.view.FragmentTileSelection;

import java.util.Map;

public class UIManager {
    public enum UIType{
        MENU,
        CONFIG,
        CREDITS,
        CHAT,
        GAME_SUMMARY,
        LOGIN,
        LOGOUT,
        CREATE_ACCOUNT,
        SKIN_SELECTION,
        SKIN_PIECE_SELECTION,
        SKIN_TERRAIN_SELECTION,
        SKIN_TILE_SELECTION,
    }

    private Map<UIType, Fragment> uiMap;

    public static UIManager getInstance(){
        return instance = (instance == null) ? new UIManager() : instance;
    }

    private UIManager(){
        uiMap = new java.util.HashMap<>();
        uiMap.put(UIType.MENU, FragmentMainMenu.newInstance());
        uiMap.put(UIType.CONFIG, FragmentSetting.newInstance());
        uiMap.put(UIType.CREDITS, FragmentCredits.newInstance());
        uiMap.put(UIType.LOGIN, FragmentLoginForm.newInstance());
        uiMap.put(UIType.LOGOUT, FragmentLogoutForm.newInstance());
        uiMap.put(UIType.CREATE_ACCOUNT, FragmentCreateAccount.newInstance());
        uiMap.put(UIType.SKIN_SELECTION, FragmentSkinSelection.newInstance());
        uiMap.put(UIType.SKIN_PIECE_SELECTION, FragmentPieceSelection.newInstance());
        uiMap.put(UIType.SKIN_TERRAIN_SELECTION, FragmentTerrainSelection.newInstance());
        uiMap.put(UIType.SKIN_TILE_SELECTION, FragmentTileSelection.newInstance());
    }

    private static UIManager instance = null;

    public Fragment getUI(UIType type){
        return uiMap.get(type);
    }
}
