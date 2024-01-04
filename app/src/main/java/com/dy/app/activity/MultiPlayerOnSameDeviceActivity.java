package com.dy.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.core.GameCore;
import com.dy.app.core.thread.GameLoop;
import com.dy.app.core.thread.MultiPlayerSameDeviceHandler;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerGameHistory;
import com.dy.app.gameplay.player.PlayerInventory;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.gameplay.screenshot.ITakeScreenshot;
import com.dy.app.graphic.camera.CameraEntity;
import com.dy.app.graphic.display.GameFragment;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.manager.SoundManager;
import com.dy.app.setting.GameSetting;
import com.dy.app.ui.dialog.LoadingDialog;
import com.dy.app.ui.dialog.MultiPlayerSameDeviceGameResultDialog;
import com.dy.app.ui.dialog.PromotionSelectionDialog;
import com.dy.app.ui.view.FragmentSetting;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class MultiPlayerOnSameDeviceActivity extends FragmentHubActivity
implements View.OnClickListener, ITakeScreenshot {
    public static final String TAG = "MultiPlayerOnSameDeviceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_player_same_device_activity);
        //https://stackoverflow.com/questions/6922878/how-to-remove-the-battery-icon-in-android-status-bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainHandler = new Handler(getMainLooper());
        //set rival piece skin to the same as player
        Rival.getInstance().setPieceSkinIndex((long)Player.getInstance().inventory.get(PlayerInventory.KEY_PIECE_SKIN_INDEX));
        init();
        attachFragment();
        attachListener();
        initCore();
    }

    private void init(){
        btnConfig = findViewById(R.id.btnConfig);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        fragmentSetting = FragmentSetting.newInstance();
        tvWhiteTimeRemain = findViewById(R.id.tvWhiteTimeRemain);
        tvBlackTimeRemain = findViewById(R.id.tvBlackTimeRemain);

        //set player piece color
        Player.getInstance().setWhitePiece(true);
        Rival.getInstance().setWhitePiece(false);

        loadingDialog = new LoadingDialog();
        currentSavedFileUri = null;
    }

    private void attachFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.flStage, fragmentSetting);
        ft.show(fragmentSetting);
        ft.hide(fragmentSetting);
        ft.commit();
    }

    private void attachListener(){
        btnConfig.setOnClickListener(this);
        btnSpeaker.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        gameLoop.shutDown();
        multiPlayerSameDeviceHandler.stopGame();
        super.onDestroy();
    }

    private void initCore() {
        gameCore = new GameCore(this);
        Player.getInstance().setWhitePiece(true);
        Rival.getInstance().setWhitePiece(false);
        gameCore.init();
    }

    @Override
    public void onMsgToMain(String TAG, int type, Object o1, Object o2) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                handle(TAG, type, o1, o2);
            }
        });
    }

    private void handle(String TAG, int type, Object o1, Object o2){
        switch (TAG){
            case GameCore.TAG:
                handleGameCoreMsg(TAG, type, o1, o2);
                break;
            case FragmentSetting.TAG:
            {
                handleMsgFromSetting(type, o1, o2);
            }
        }
    }

    private void handleMsgFromSetting(int type, Object o1, Object o2) {
        switch (type){
            case 0:
                hideFragment(fragmentSetting);
                break;
            case FragmentSetting.CINEMATIC_CAMERA:
                boolean isEnabled = (boolean) o1;
                GameSetting.getInstance().isCinematicCameraEnabled = isEnabled;
                if(isEnabled){
                    gameCore.getEntityManger().newEntity(CameraEntity.getInstance());
                }else{
                    try{
                        gameCore.getEntityManger().removeEntity(CameraEntity.getInstance());
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case FragmentSetting.ENABLE_DRAG_MODE:
                boolean isDragModeEnabled = (boolean) o1;
                gameFragment.getSurfaceView().getRenderer().getTilePicker().setDragModeEnabled(isDragModeEnabled);
                break;
        }
    }

    private void handleGameCoreMsg(String TAG,int t, Object o1, Object o2) {
        GameCore.TaskType taskType = (GameCore.TaskType) o1;
        switch (taskType){
            case SET_LOADING_BACKGROUND:
                findViewById(R.id.loadingBackground).setBackground((BitmapDrawable) o2);
                break;
            case SET_GAME_BACKGROUND:
                gameFragment.onMsgFromMain(TAG, t, o1, o2);
                break;
            case START_GAME:
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                //Semaphore semaphore = new Semaphore(0);
                gameFragment = new GameFragment(this, gameCore.getEntityManger(), gameCore.getBoard());
                ft.replace(R.id.fl_game_surface, gameFragment);
                //check if the activity state is still valid
                if(isFinishing()){
                    return;
                }else{
                    ft.commit();
                }
                gameLoop = new GameLoop(gameFragment.getSurfaceView(), gameCore.getEntityManger());
                Thread worker = new Thread(()->{
                    gameFragment.getSurfaceView().getRenderer().waitForGLInit();
                    gameLoop.start();
                    multiPlayerSameDeviceHandler = new MultiPlayerSameDeviceHandler(this, gameCore.getBoard(), gameFragment.getSurfaceView().getRenderer().getTilePicker());
                    multiPlayerSameDeviceHandler.start();
                });
                worker.start();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        SoundManager.getInstance().playSound(this, SoundManager.SoundType.BTN_BLOP);
        if(v == btnConfig){
            btnConfig.playAnimation();
            if(fragmentSetting.isHidden()){
                showFragment(fragmentSetting);
            }else{
                hideFragment(fragmentSetting);
            }
        }else if(v == btnSpeaker) {
            if(SoundManager.getInstance().isSoundOn()) {
                SoundManager.getInstance().setSoundOn(false);
                btnSpeaker.setAnimation("animated_ui/btn_speaker_mute.json");
            }else{
                SoundManager.getInstance().setSoundOn(true);
                btnSpeaker.setAnimation("animated_ui/btn_speaker_enable.json");
            }
        }
    }

    public void hideFragment(Fragment fragment){
        if(fragment.isHidden()) return;

        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(fragment);
        ft.commit();
        currentFragment = null;
    }

    public void showFragment(Fragment fragment){
        if(currentFragment == fragment) return;

        FragmentTransaction ft = fm.beginTransaction();
        if(currentFragment != null) ft.hide(currentFragment);
        ft.show(fragment);
        ft.commit();

        currentFragment = fragment;
    }

    public void showPromotionDialog(PromotionSelectionDialog.PromotionSelectionDialogListener listener) {
        PromotionSelectionDialog dialog = new PromotionSelectionDialog(listener);
        dialog.show(getSupportFragmentManager(), "PromotionSelectionDialog");
    }

    public void updateTimeRemain(boolean isWhite, long timeRemainMS){
        TextView currentTextView = isWhite ? tvWhiteTimeRemain : tvBlackTimeRemain;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemainMS);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemainMS) -
                TimeUnit.MINUTES.toSeconds(minutes);
        String timeRemain = String.format("%02d:%02d", minutes, seconds);
        runOnUiThread(()->{
            currentTextView.setText(timeRemain);

            //<3 mins, set text color to red
            if(timeRemainMS < 3 * 60 * 1000){
                currentTextView.setTextColor(getResources().getColor(R.color.red));
            }else{
                currentTextView.setTextColor(getResources().getColor(isWhite? R.color.white:R.color.black));
            }
        });
    }

    private final MultiPlayerSameDeviceGameResultDialog dialog = new MultiPlayerSameDeviceGameResultDialog(new MultiPlayerSameDeviceGameResultDialog.IGameResultDialogListener() {
        @Override
        public void onBtnSavePGNClick(MultiPlayerSameDeviceGameResultDialog dialog) {
            Utils.askForFileLocation(MultiPlayerOnSameDeviceActivity.this, "gameplay.pgn", "", "*/*", DyConst.REQUEST_CHOOSE_FILE_LOCATION);
        }

        @Override
        public void onBtnReplayClick(MultiPlayerSameDeviceGameResultDialog dialog) {
            replay();
        }

        @Override
        public void onBtnRematchClick(MultiPlayerSameDeviceGameResultDialog dialog) {
            dialog.dismiss();
            gameLoop.shutDown();
            multiPlayerSameDeviceHandler.stopGame();
            currentSavedFileUri = null;
            initCore();
        }

        @Override
        public void onBtnExitClick(MultiPlayerSameDeviceGameResultDialog dialog) {
            finish();
        }

        @Override
        public void onBtnShareAsImageClick(MultiPlayerSameDeviceGameResultDialog dialog) {
            Utils.shareScreenShot(MultiPlayerOnSameDeviceActivity.this, MultiPlayerOnSameDeviceActivity.this, "gameplay.png");
        }

        @Override
        public void onBtnShareAsPGNClick(MultiPlayerSameDeviceGameResultDialog dialog) {
            if(currentSavedFileUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerOnSameDeviceActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Please save the PGN file first");
                builder.setPositiveButton("OK", (d, which) -> {
                    d.dismiss();
                    Utils.askForFileLocation(MultiPlayerOnSameDeviceActivity.this, "gameplay.pgn", "", "*/*", DyConst.REQUEST_SAVE_FILE_BEFORE_SHARE);
                });
                builder.setNegativeButton("Cancel", (d, which) -> {
                    d.dismiss();
                });
                builder.show();
            }else{
                Utils.shareFile(MultiPlayerOnSameDeviceActivity.this, currentSavedFileUri, "text/plain");
            }
        }
    });

    public void onGameResult(int gameResult) {
        this.gameResult = gameResult;
        saveGameResultToPlayerHistory();
        dialog.setGameResult(gameResult);
        dialog.show(getSupportFragmentManager(), "MultiPlayerSameDeviceGameResultDialog");
    }

    private void saveGameResultToPlayerHistory(){
        Player.getInstance().history.newGame(PlayerGameHistory.KEY_P_V_P, getPGNFile());
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
        loadingDialog.dismiss();
    }

    private PGNFile getPGNFile(){
        PGNFile file = new PGNFile(gameCore.getBoard().getMoveHistory());
        file.addWhitePlayer(Player.getInstance().isWhitePiece()? Player.getInstance().getDisplayName() : Rival.getInstance().getName());
        file.addBlackPlayer(Player.getInstance().isWhitePiece()? Rival.getInstance().getName() : Player.getInstance().getDisplayName());
        file.addResult(gameResult);
        file.addDate(Utils.getCurrentDate());
        file.addEvent("Multiplayer on same device");
        file.addSite("Local");
        return file;
    }

    private void replay(){
        PGNFile file = getPGNFile();
        Intent intent = new Intent(this, ReplayActivity.class);
        intent.putExtra("pgn", file);
        startActivity(intent);
    }

    private void savePGN(Uri uri){
        PGNFile file = getPGNFile();
        showLoadingDialog(false);
        Thread worker = new Thread(()->{
            try {
                file.savePGN(this, uri, new PGNFile.IOnSavePGNListener() {
                    @Override
                    public void onSavePGN(String path) {
                        runOnUiThread(() -> {
                            hideLoadingDialog();
                            Toast.makeText(MultiPlayerOnSameDeviceActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        });
                        currentSavedFileUri = uri;
                    }
                });
            } catch (IOException e) {
                hideLoadingDialog();
                showError(e.getMessage());
            }
        });
        worker.start();
    }

    private void savePGNAndShare(Uri uri) {
        PGNFile file =getPGNFile();
        showLoadingDialog(false);
        Thread worker = new Thread(()->{
            try {
                file.savePGN(this, uri, new PGNFile.IOnSavePGNListener() {
                    @Override
                    public void onSavePGN(String path) {
                        runOnUiThread(() -> {
                            hideLoadingDialog();
                            Utils.shareFile(MultiPlayerOnSameDeviceActivity.this, uri, "text/plain");
                        });
                        currentSavedFileUri = uri;
                    }
                });
            } catch (IOException e) {
                hideLoadingDialog();
                showError(e.getMessage());
            }
        });
        worker.start();
    }

    public void showError(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });

        runOnUiThread(()->{
            builder.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DyConst.REQUEST_CHOOSE_FILE_LOCATION
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                savePGN(uri);
            }
        }else if(requestCode == DyConst.REQUEST_SAVE_FILE_BEFORE_SHARE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                savePGNAndShare(uri);
            }
        } else if(requestCode == DyConst.REQUEST_TAKE_SCREENSHOT_AND_SHARE && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Utils.shareImageMedia(this, uri);
            }
        }
    }

    @Override
    public Bitmap getScreenshot() {
        Bitmap uiBitmap = Utils.getScreenShot(this.getWindow().getDecorView().getRootView());
        Bitmap gameBitmap = null;
        try {
            gameBitmap = gameFragment.getSurfaceView().getRenderer().getScreenShot();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Bitmap resultBitmap = null;

        if(dialog.getView()!=null){
            resultBitmap = Utils.getScreenShot(dialog.getView());
        }

        Bitmap bitmapParts[] = new Bitmap[3];
        bitmapParts[0] = gameBitmap;
        bitmapParts[1] = uiBitmap;
        bitmapParts[2] = resultBitmap;
        //exclude null bitmap
        int count = 0;
        for(Bitmap bitmap : bitmapParts){
            if(bitmap != null){
                count++;
            }
        }
        Bitmap[] finalBitmapParts = new Bitmap[count];
        int index = 0;
        for(Bitmap bitmap : bitmapParts){
            if(bitmap != null){
                finalBitmapParts[index++] = bitmap;
            }
        }

        Bitmap result = Utils.mergeBitmapCenter(finalBitmapParts);
        for(Bitmap bitmap : finalBitmapParts){
            bitmap.recycle();
        }

        return result;
    }

    private Fragment currentFragment;
    private final FragmentManager fm = getSupportFragmentManager();
    private Handler mainHandler;
    private GameFragment gameFragment;
    private GameLoop gameLoop;
    private GameCore gameCore;
    private MultiPlayerSameDeviceHandler multiPlayerSameDeviceHandler;
    private LottieAnimationView btnConfig, btnSpeaker;
    private FragmentSetting fragmentSetting;
    private TextView tvWhiteTimeRemain, tvBlackTimeRemain;
    private LoadingDialog loadingDialog;
    private Uri currentSavedFileUri = null;
    private int gameResult = -1;
}
