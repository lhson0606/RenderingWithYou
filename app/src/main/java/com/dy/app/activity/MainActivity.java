package com.dy.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.core.MainCallback;
import com.dy.app.db.Database;
import com.dy.app.db.OnDBRequestListener;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerInventory;
import com.dy.app.gameplay.player.PlayerProfile;
import com.dy.app.manager.SoundManager;
import com.dy.app.manager.UIManager;
import com.dy.app.ui.dialog.LoadingDialog;
import com.dy.app.ui.dialog.PlayerHistoryDialog;
import com.dy.app.ui.view.FragmentCreateAccount;
import com.dy.app.ui.view.FragmentCredits;
import com.dy.app.ui.view.FragmentLoginForm;
import com.dy.app.ui.view.FragmentLogoutForm;
import com.dy.app.ui.view.FragmentMainMenu;
import com.dy.app.ui.view.FragmentSetting;
import com.dy.app.ui.view.FragmentSkinSelection;
import com.dy.app.ui.view.FragmentSplashScreen1;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentHubActivity
        implements MainCallback, View.OnClickListener, FirebaseAuth.AuthStateListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        handler = new Handler(getMainLooper());
        attachFragment();
        init();
        attachListener();
        initManager();
    }

    private FragmentSplashScreen1 fragmentSplashScreen1 = null;

    private void showSplashScreen(){
        fragmentSplashScreen1 = new FragmentSplashScreen1().newInstance();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.splashScreen, fragmentSplashScreen1);
        ft.show(fragmentSplashScreen1);
        ft.commit();
    }

    private void removeSplashScreen(){
        if(fragmentSplashScreen1 == null) return;
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fm.findFragmentById(R.id.splashScreen));
        fragmentSplashScreen1 = null;
        ft.commit();
    }

    private void init(){
        btnConfig = findViewById(R.id.btnConfig);
        btnHistory = findViewById(R.id.btnHistory);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnAbout = findViewById(R.id.btnAbout);
        btnAccount = findViewById(R.id.btnAccount);
        tvUsername = findViewById(R.id.tvUsername);
        btnChooseSkin = findViewById(R.id.btnChooseSkin);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        btnGamePass = findViewById(R.id.btnGamePass);
        btnShop = findViewById(R.id.btnShop);
        tvUserCoin = findViewById(R.id.tvUserCoin);
        tvUserElo = findViewById(R.id.tvUserElo);
        // Initialize EmojiCompat
        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
        EmojiCompat.init(config);
        loadingDialog = new LoadingDialog();
        //#todo loadingDialog.onDismiss();
        playerHistoryDialog = new PlayerHistoryDialog(this, playerHistoryDialogListener);

    }

    @Override
    protected void onPause() {
        Database.getInstance().removeAuthStateListener(this);
        savePlayerData();
        super.onPause();
    }

    @Override
    protected void onResume() {
        loadUserData();
        Database.getInstance().addAuthStateListener(this);

        if(firstEnterApp){
            firstEnterApp = false;
            showSplashScreen();
            handler.postDelayed(()->{
                removeSplashScreen();
            }, 5000);
        }

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
        btnHistory.setOnClickListener(this);
        btnStatistics.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnAccount.setOnClickListener(this);
        btnChooseSkin.setOnClickListener(this);
        btnSpeaker.setOnClickListener(this);
        btnGamePass.setOnClickListener(this);
        btnShop.setOnClickListener(this);
    }

    private void attachFragment() {
        FragmentTransaction ft = fm.beginTransaction();

        mainMenuFragment = FragmentMainMenu.newInstance();
        settingFragment = FragmentSetting.newInstance();
        creditsFragment = FragmentCredits.newInstance();
        loginFormFragment = FragmentLoginForm.newInstance();
        logoutFormFragment = FragmentLogoutForm.newInstance();
        createAccountFragment = FragmentCreateAccount.newInstance();
        skinSelectionFragment = FragmentSkinSelection.newInstance();
        ft.commit();
        currentFragment = null;
        showFragment(mainMenuFragment);
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
            //escape
            case 0:
            {
                showFragment(mainMenuFragment);
                break;
            }
            //cancel
            case 1:
            {
                showFragment(loginFormFragment);
                break;
            }
            //create account
            case 2:
            {
                registerInformation = (FragmentCreateAccount.RegisterInformation) o1;
                Database.getInstance().signUpWithEmailAndPassword(registerInformation, new OnDBRequestListener() {
                    @Override
                    public void onDBRequestCompleted(int result, Object object) {
                        if(result == Database.RESULT_SUCCESS){
                            displayAlertMessage((String)object);
                        }else{
                            displayAlertMessage((String)object);
                        }
                    }
                });
                break;
            }
        }
    }

    private void handleMsgFromLogoutForm(int type, Object o1, Object o2) {
        switch (type){
            //back
            case 0:
            {
                showFragment(mainMenuFragment);
                break;
            }
            //logout
            case 1:
            {
                if(Database.getInstance().isSignedInAsAnonymous()){
                    //if user is signed in as anonymous, we need to announce them data will be lost
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("You are signed in as guest, if you log out, all your data will be lost!");
                    builder.setPositiveButton("I understand!", (dialog, which) -> {
                        Database.getInstance().logOut();
                        showFragment(loginFormFragment);
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });

                    builder.create().show();

                }else{
                    Database.getInstance().logOut();
                    showFragment(loginFormFragment);
                }

                break;
            }
        }
    }


    private LoadingDialog loadingDialog;


    private void handleMsgFromLoginForm(int type, Object o1, Object o2) {
        switch (type){
            //back
            case 0:
            {
                showFragment(mainMenuFragment);
                break;
            }
            //sign in
            case 1:
            {
                showLoadingDialog(false);
                final String email = (String)o1;
                final String password = (String)o2;
                try {
                    Database.getInstance().signInWithEmailAndPassword(email, password, new OnDBRequestListener() {
                        @Override
                        public void onDBRequestCompleted(int result, Object object) {
                            hideLoadingDialog();
                            if(result == Database.RESULT_FAILED){
                                displayAlertMessage((String)object);
                            }
                        }
                    });
                } catch (ExecutionException e) {
                    displayAlertMessage(e.getMessage());
                } catch (InterruptedException e) {
                    //#todo
                    throw new RuntimeException(e);
                }
                break;
            }
            //create account
            case 2:
            {
                showFragment(createAccountFragment);
                break;
            }
            //sign in with
            case 3:
            {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.TwitterBuilder().build(),
                        new AuthUI.IdpConfig.PhoneBuilder().build(),
                        //new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.AnonymousBuilder().build());
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build();
                Database.getInstance().logOut();
                signInLauncher
                        .launch(signInIntent);
                BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                // Your server's client ID, not your Android client ID.
                                .setServerClientId(getString(R.string.default_web_client_id))
                                // Only show accounts previously used to sign in.
                                .setFilterByAuthorizedAccounts(true)
                                .build())
                        .build();
            }
        }
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult o) {
                    onSignInResult(o);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult o) {
        if(o.getResultCode() == RESULT_OK){
            try{
                signUserSignIn();
            }catch (Exception e) {
                displayAlertMessage("Something went wrong, we cannot log you in");
            }
        }
    }

    private synchronized void loadUserData(){
        //update UI
        tvUsername.setText(Player.getInstance().profile.get(PlayerProfile.KEY_USERNAME).toString());
        tvUserCoin.setText(Player.getInstance().inventory.get(PlayerInventory.KEY_COIN).toString());
        tvUserElo.setText(Player.getInstance().profile.get(PlayerProfile.KEY_ELO).toString());
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
                this.finishAffinity();
                break;
            }
            case 3:{
                startScriptsGame();
                break;
            }
            case 4:{
                startTwoPlayerSameDeviceGame();
            }
        }
    }

    private void startTwoPlayerSameDeviceGame() {
        Intent intent = new Intent(this, MultiPlayerOnSameDeviceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void startScriptsGame(){
        //these comments are uncommented for debug purpose
//        Intent intent = new Intent(this, RunScriptsActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(intent);
        Utils.openFile(this, "*/*", DyConst.REQUEST_IMPORT_PGN);
    }

    private void startFindingLobby(){
        Intent intent = new Intent(this, MatchMakingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void showFragment(Fragment fragment){
        if(currentFragment == fragment || isFinishing()){
            return;
        }

        if(fragment == null) return;

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flStage, fragment);

        ft.show(fragment);
        if(!isFinishing() && !fragment.isAdded()){
            ft.commit();
            currentFragment = fragment;
        }

    }

    @Override
    public void onClick(View v) {
        soundManager.playSound(this, SoundManager.SoundType.BTN_BLOP);
        if(v.getId() == R.id.btnConfig) {
            btnConfig.playAnimation();
            showFragment(settingFragment);
        } else if(v.getId() == R.id.btnHistory){
            btnHistory.playAnimation();
            if(!Player.getInstance().hasLogin()){
                displayAlertMessage("Please sign in first to use this feature");
                return;
            }
            //show player history dialog
            playerHistoryDialog.show(getSupportFragmentManager(), "PlayerHistoryDialog");
        } else if(v.getId() == R.id.btnStatistics){
            btnStatistics.playAnimation();
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        } else if(v.getId() == R.id.btnAbout){
            btnAbout.playAnimation();
            showFragment(creditsFragment);
        }
        else if(v.getId() == R.id.btnAccount){
            btnAccount.playAnimation();
            if(Player.getInstance().hasLogin())
                showFragment(logoutFormFragment);
            else
                showFragment(loginFormFragment);

        }else if (v.getId() == R.id.btnChooseSkin){

            if(!Database.getInstance().isSignedIn()){
                displayAlertMessage("Please sign in first");
                return;
            }

            btnChooseSkin.playAnimation();
            showFragment(skinSelectionFragment);
        }else if(v.getId() == R.id.btnSpeaker){
            btnSpeaker.playAnimation();
            if(SoundManager.getInstance().isSoundOn()){
                SoundManager.getInstance().setSoundOn(false);
                btnSpeaker.setAnimation("animated_ui/btn_speaker_mute.json");
            }
            else{
                SoundManager.getInstance().setSoundOn(true);
                btnSpeaker.setAnimation("animated_ui/btn_speaker_enable.json");
            }
        } else if(v.getId() == R.id.btnGamePass){
            if(!Database.getInstance().isSignedIn()){
                displayAlertMessage("Please sign in first");
                return;
            }

            btnGamePass.playAnimation();
            //start game pass activity
            Intent intent = new Intent(this, GamepassActivity.class);
            startActivity(intent);

        } else if(v.getId() == R.id.btnShop){
            btnShop.playAnimation();
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
        }
    }

    private void showLoadingDialog(boolean cancelable){
        //prevent UI leak
        if(loadingDialog == null || loadingDialog.isVisible() || isFinishing()){
            return;
        }

        loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(cancelable);
        loadingDialog.show(getSupportFragmentManager(), "LoadingDialog");
    }

    private void hideLoadingDialog(){
        if(loadingDialog != null && loadingDialog.isVisible() && !isFinishing()){
            loadingDialog.dismiss();
        }
    }

    public void displayAlertMessage(String message){
        //prevent UI leak
        if(message == null || message.isEmpty() || isFinishing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    public void pullPlayerData(PullCallback callback){
        Database.getInstance().fetchAllPlayerData((res, o)->{
            callback.onPullCompleted(res, o);
        });
    }

    public void savePlayerData(){
        if(Player.getInstance().hasLogin()){
            Database.getInstance().pushAllPlayerData(null);
        }
    }

    public void pushPlayerData(){
        Database.getInstance().pushAllPlayerData((res, o)->{
            if(res == Database.RESULT_SUCCESS){
                loadUserData();
            }else{
                displayAlertMessage((String)o);
            }
        });
    }

    private void initPlayer(){
        Player.getInstance().reset();
        if(registerInformation != null){
            //if user used email and password to register
            Player.getInstance().profile.set(PlayerProfile.KEY_USERNAME, registerInformation.username);
            //reset register information
            registerInformation = null;
        }else{
            //else we get user name from auth
            Player.getInstance().profile.set(PlayerProfile.KEY_USERNAME, Database.getInstance().getUserDisplayName());
        }
        Player.getInstance().profile.set(PlayerProfile.KEY_EMAIL, Database.getInstance().getUserEmail());
        Player.getInstance().profile.set(PlayerProfile.KEY_PHONE, Database.getInstance().getUserPhoneNumber());
        Player.getInstance().profile.set(PlayerProfile.KEY_PHOTO_URL, Database.getInstance().getUserPhotoUrl());
        Player.getInstance().profile.set(PlayerProfile.KEY_UID, Database.getInstance().getUserUID());
    }

    private interface PullCallback{
        void onPullCompleted(int result, Object object);
    }

    private void signUserSignIn(){
        showLoadingDialog(false);
        btnGamePass.setEnabled(false);
        Database.getInstance().checkForPlayerInitialization((result, object) -> {
            if(result == Database.RESULT_SUCCESS){
                if((boolean)object) {
                    //player is initialized
                    pullPlayerData((res, o)->{
                        hideLoadingDialog();
                        if(res == Database.RESULT_SUCCESS){
                            Player.getInstance().setLoginStatus(true);
                            onUserDataReady();
                            showFragment(logoutFormFragment);
                        }else{
                            displayAlertMessage((String)o);
                        }
                    });
                    Player.getInstance().setLoginStatus(true);
                }else{
                    //player is not initialized, we need to initialize it
                    initPlayer();
                    Player.getInstance().setLoginStatus(true);
                    pushPlayerData();
                    hideLoadingDialog();
                    onUserDataReady();
                    showFragment(logoutFormFragment);
                }

            }else{
                displayAlertMessage((String)object);
            }
        });
    }

    private void onUserDataReady() {
        //enable game pass button
        btnGamePass.setEnabled(true);
        loadUserData();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        //login scenario
        if(firebaseAuth.getCurrentUser() != null){
            if(!Player.getInstance().hasLogin()) {
                try{
                    signUserSignIn();
                }
                catch (Exception e){
                    displayAlertMessage("Something went wrong, we cannot log you in");
                }
            }
        //logout scenario
        }else{
            Player.getInstance().reset();
            Player.getInstance().setLoginStatus(false);
            loginFormFragment.onMsgFromMain(FragmentLoginForm.TAG, 0, null, null);
            loadUserData();
        }
    }

    private PlayerHistoryDialog.PlayerHistoryDialogListener playerHistoryDialogListener = new PlayerHistoryDialog.PlayerHistoryDialogListener() {
        @Override
        public void onBtnReplayClick(PGNFile selectedFile) {
            if(selectedFile == null){
                displayAlertMessage("Please select a game to replay");
                return;
            }

            //put pgn file into intent and start replay activity
            Intent intent = new Intent(MainActivity.this, ReplayActivity.class);
            intent.putExtra("pgn", selectedFile);
            startActivity(intent);
        }

        @Override
        public void onBtnSaveClick(PGNFile selectedFile) {
            if(selectedFile == null){
                displayAlertMessage("Please select a game to save");
                return;
            }

            //save pgn file to storage
            Utils.askForFileLocation(MainActivity.this, "gameplay.pgn", "", "*/*", DyConst.REQUEST_CHOOSE_FILE_LOCATION);
            currentPGNFile = selectedFile;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            displayAlertMessage("You didn't complete the operation!");
            return;
        }
        switch (requestCode){
            case DyConst.REQUEST_CHOOSE_FILE_LOCATION:
                // The result data contains a URI for the document or directory that
                // the user selected.
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    savePGN(uri);
                }
            break;
            case DyConst.REQUEST_IMPORT_PGN:
                Uri openingPGNuri = null;
                if (data != null) {
                    openingPGNuri = data.getData();
                    try{
                        PGNFile pgnFile = PGNFile.parsePGN(this, openingPGNuri);
                        Intent intent = new Intent(this, ReplayActivity.class);
                        intent.putExtra("pgn", pgnFile);
                        startActivity(intent);
                    }catch (Exception e){
                        displayAlertMessage("Invalid PGN file, or the file is corrupted.");
                    }
                }
            break;
        }

    }

    private PGNFile currentPGNFile;

    private void savePGN(Uri uri){
        showLoadingDialog(false);
        Thread worker = new Thread(()->{
            try {
                currentPGNFile.savePGN(this, uri, new PGNFile.IOnSavePGNListener() {
                    @Override
                    public void onSavePGN(String path) {
                        runOnUiThread(() -> {
                            hideLoadingDialog();
                            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } catch (IOException e) {
                hideLoadingDialog();
                displayAlertMessage(e.getMessage());
            }
        });
        worker.start();
    }

    @Override
    public void onBackPressed() {
        //disable back button
        if(false){
            super.onBackPressed();
        }
    }

    //store the register information if user sign up with email and password
    private FragmentCreateAccount.RegisterInformation registerInformation = null;
    private SoundManager soundManager;
    public final String TAG = getClass().getSimpleName();
    private FragmentMainMenu mainMenuFragment;
    private FragmentSetting settingFragment;
    private FragmentCredits creditsFragment;
    private FragmentLoginForm loginFormFragment;
    private FragmentLogoutForm logoutFormFragment;
    private FragmentCreateAccount createAccountFragment;
    private FragmentSkinSelection skinSelectionFragment;
    private TextView tvUsername, tvUserCoin, tvUserElo;
    private final FragmentManager fm = getSupportFragmentManager();;
    private Handler handler;
    private LottieAnimationView btnConfig, btnHistory, btnAbout, btnAccount, btnChooseSkin, btnSpeaker, btnShop, btnStatistics;
    private LottieAnimationView btnGamePass;
    private Fragment currentFragment;
    private PlayerHistoryDialog playerHistoryDialog;
    private boolean firstEnterApp = true;
}
