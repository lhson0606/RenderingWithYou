package com.dy.app.manager;

import android.provider.Contacts;

import androidx.fragment.app.Fragment;

import com.dy.app.ui.view.FragmentCreateAccount;
import com.dy.app.ui.view.FragmentCredits;
import com.dy.app.ui.view.FragmentLoginForm;
import com.dy.app.ui.view.FragmentLogoutForm;
import com.dy.app.ui.view.FragmentMainMenu;
import com.dy.app.ui.view.FragmentSetting;

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
    }

    private static UIManager instance = null;

    public Fragment getUI(UIType type){
        return uiMap.get(type);
    }
}
