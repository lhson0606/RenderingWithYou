package com.dy.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dy.app.R;
import com.dy.app.core.MainCallback;
import com.dy.app.gameplay.Player;
import com.dy.app.manager.SoundManager;
import com.dy.app.manager.UIManager;
import com.dy.app.ui.view.FragmentCreateAccount;
import com.dy.app.ui.view.FragmentCredits;
import com.dy.app.ui.view.FragmentLoginForm;
import com.dy.app.ui.view.FragmentLogoutForm;
import com.dy.app.ui.view.FragmentMainMenu;
import com.dy.app.ui.view.FragmentPieceSelection;
import com.dy.app.ui.view.FragmentSetting;
import com.dy.app.ui.view.FragmentSkinSelection;
import com.dy.app.utils.ImageLoader;

public class MainActivity extends FragmentHubActivity
        implements MainCallback, View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        fm = getSupportFragmentManager();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        menuScreen = findViewById(R.id.mainScreen);
        btnConfig = findViewById(R.id.btnConfig);
        btnAccount = findViewById(R.id.btnAccount);
        tvUsername = findViewById(R.id.tvUsername);
        btnChooseSkin = findViewById(R.id.btnChooseSkin);

        menuScreen.setBackground(ImageLoader.loadImage(getResources().openRawResource(R.raw.chess_wallpaper)));
        handler = new Handler(getMainLooper());
        attachFragment();

        attachListener();
        initManager();
    }

    @Override
    protected void onPause() {
//        FragmentTransaction ft = fm.beginTransaction();
//        //ft.remove(mainMenuFragment);
//        ft.remove(settingFragment);
//        ft.remove(creditsFragment);
//        ft.remove(loginFormFragment);
//        ft.remove(logoutFormFragment);
//        ft.remove(createAccountFragment);
//        ft.remove(skinSelectionFragment);
//        ft.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        //attachFragment();
        super.onResume();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void initManager() {
        soundManager = SoundManager.getInstance().initInContext(this);
    }

    private void attachListener() {
        btnConfig.setOnClickListener(this);
        btnAccount.setOnClickListener(this);
        btnChooseSkin.setOnClickListener(this);
    }

    private void attachFragment() {
        FragmentTransaction ft = fm.beginTransaction();

        mainMenuFragment = (FragmentMainMenu) UIManager.getInstance().getUI(UIManager.UIType.MENU);
        ft.add(R.id.flStage, mainMenuFragment, FragmentMainMenu.TAG);
        ft.show(mainMenuFragment);

        settingFragment = (FragmentSetting) UIManager.getInstance().getUI(UIManager.UIType.CONFIG);
        //ft.add(R.id.flStage, settingFragment, FragmentSetting.TAG);
        //ft.hide(settingFragment);

        creditsFragment = (FragmentCredits) UIManager.getInstance().getUI(UIManager.UIType.CREDITS);
        //ft.add(R.id.flStage, creditsFragment, FragmentCredits.TAG);
        //ft.hide(creditsFragment);

        loginFormFragment = (FragmentLoginForm) UIManager.getInstance().getUI(UIManager.UIType.LOGIN);
        //ft.add(R.id.flStage, loginFormFragment, FragmentLoginForm.TAG);
        //ft.hide(loginFormFragment);

        logoutFormFragment = (FragmentLogoutForm) UIManager.getInstance().getUI(UIManager.UIType.LOGOUT);
        //ft.add(R.id.flStage, logoutFormFragment, FragmentLogoutForm.TAG);
        //ft.hide(logoutFormFragment);

        createAccountFragment = (FragmentCreateAccount) UIManager.getInstance().getUI(UIManager.UIType.CREATE_ACCOUNT);
        //ft.add(R.id.flStage, createAccountFragment, FragmentCreateAccount.TAG);
        //ft.hide(createAccountFragment);

        skinSelectionFragment = (FragmentSkinSelection) UIManager.getInstance().getUI(UIManager.UIType.SKIN_SELECTION);
        //ft.add(R.id.flStage, skinSelectionFragment, FragmentSkinSelection.TAG);
        //ft.hide(skinSelectionFragment);

        ft.commit();

        currentFragment = mainMenuFragment;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public void onMsgToMain(String TAG, int type, Object o1, Object o2) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                handle(TAG, type, o1, o2);
            }
        });
    }

    private void handle(String tag, int type, Object o1, Object o2) {
        switch (tag){
            case FragmentMainMenu.TAG:
            {
                handleMsgFromMainMenu(type, o1, o2);
                break;
            }

            case FragmentSetting.TAG:
            {
                handleMsgFromSetting(type, o1, o2);
            }

            case FragmentCredits.TAG:
            {
                handleMsgFromCredits(type, o1, o2);
                break;
            }

            case FragmentLoginForm.TAG:
            {
                handleMsgFromLoginForm(type, o1, o2);
                break;
            }

            case FragmentLogoutForm.TAG:
            {
                handleMsgFromLogoutForm(type, o1, o2);
                break;
            }

            case FragmentCreateAccount.TAG:
            {
                handleMsgFromCreateAccount(type, o1, o2);
                break;
            }

            case FragmentSkinSelection.TAG:
                handleMsgFromSkinSelection(type, o1, o2);
                break;

        }
    }

    private void handleMsgFromSkinSelection(int type, Object o1, Object o2) {
        switch (type){
            case 0:
            {
                showFragment(mainMenuFragment);
                break;
            }
        }
    }

    private void handleMsgFromCreateAccount(int type, Object o1, Object o2) {
        switch (type){
            case 0:
            {
                showFragment(mainMenuFragment);
                break;
            }

            case 1:
            {
                showFragment(loginFormFragment);
                break;
            }

            case 2:
            {
                Player.getInstance().setLoginStatus(true);
                tvUsername.setText(Player.getInstance().getName());
                showFragment(logoutFormFragment);
                break;
            }
        }
    }

    private void handleMsgFromLogoutForm(int type, Object o1, Object o2) {
        switch (type){
            case 0:
            {
                showFragment(mainMenuFragment);
                break;
            }

            case 1:
            {
                showFragment(loginFormFragment);
                Player.getInstance().setLoginStatus(false);
                loginFormFragment.onMsgFromMain(FragmentLoginForm.TAG, 0, null, null);
                tvUsername.setText("Login");
                break;
            }
        }
    }

    private void handleMsgFromLoginForm(int type, Object o1, Object o2) {
        switch (type){
            case 0:
            {
                showFragment(mainMenuFragment);
                break;
            }

            case 1:
            {
                Player.getInstance().setLoginStatus(true);
                showFragment(logoutFormFragment);
                tvUsername.setText(Player.getInstance().getName());
                break;
            }

            case 2:
            {
                showFragment(createAccountFragment);
                break;
            }
        }
    }

    private void handleMsgFromCredits(int type, Object o1, Object o2) {
        switch (type){
            case 0:
            {
                showFragment(mainMenuFragment);
                break;
            }
        }
    }

    private void handleMsgFromSetting(int type, Object o1, Object o2) {
        switch (type){
            case 0:
            {
                showFragment(mainMenuFragment);
                break;
            }
        }
    }

    private void handleMsgFromMainMenu(int type, Object o1, Object o2) {
        switch (type) {
            case 0: {
                startFindingLobby();
                break;
            }
            case 1: {
                showFragment(creditsFragment);
                break;
            }
            case 2: {
                finish();
                break;
            }
        }
    }

    private void startFindingLobby(){
        Intent intent = new Intent(this, MatchMakingActivity.class);
        startActivity(intent);
    }

    private void showFragment(Fragment fragment){
        if(currentFragment == fragment){
            return;
        }

        if(fragment == null) return;

        FragmentTransaction ft = fm.beginTransaction();
        //ft.remove(currentFragment);
        ft.replace(R.id.flStage, fragment);
//        ft.hide(currentFragment);

        ft.show(fragment);
        ft.commit();
        currentFragment = fragment;
    }

    @Override
    public void onClick(View v) {
        soundManager.playSound(this, SoundManager.SoundType.BTN_BLOP);
        if(v.getId() == R.id.btnConfig) {
            showFragment(settingFragment);
        }else if(v.getId() == R.id.btnAccount){

            if(Player.getInstance().hasLogin())
                showFragment(logoutFormFragment);
            else
                showFragment(loginFormFragment);

        }else if (v.getId() == R.id.btnChooseSkin){
            showFragment(skinSelectionFragment);
        }
    }

    private SoundManager soundManager;
    public final String TAG = getClass().getSimpleName();
    private FragmentMainMenu mainMenuFragment;
    private FragmentSetting settingFragment;
    private FragmentCredits creditsFragment;
    private FragmentLoginForm loginFormFragment;
    private FragmentLogoutForm logoutFormFragment;
    private FragmentCreateAccount createAccountFragment;
    private FragmentSkinSelection skinSelectionFragment;
    TextView tvUsername;
    private View menuScreen;
    private FragmentManager fm;
    private Handler handler;
    private Button btnConfig, btnAccount, btnChooseSkin;
    private Fragment currentFragment;
}
