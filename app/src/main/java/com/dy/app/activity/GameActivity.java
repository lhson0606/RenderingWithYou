package com.dy.app.activity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.core.GameCore;
import com.dy.app.core.MainCallback;
import com.dy.app.core.thread.GameLoop;
import com.dy.app.core.thread.MultiDeviceInGameHandler;
import com.dy.app.core.thread.Timer;
import com.dy.app.db.Database;
import com.dy.app.gameplay.loot.GameLootCalculator;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerGameHistory;
import com.dy.app.gameplay.player.PlayerProfile;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.gameplay.screenshot.ITakeScreenshot;
import com.dy.app.graphic.display.GameFragment;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.manager.SoundManager;
import com.dy.app.manager.UIManager;
import com.dy.app.network.Message;
import com.dy.app.network.MessageCode;
import com.dy.app.ui.dialog.LoadingDialog;
import com.dy.app.ui.dialog.MultiPlayerSameDeviceGameResultDialog;
import com.dy.app.ui.dialog.P2pEndGameOptionDialog;
import com.dy.app.ui.dialog.P2pGameResultDialog;
import com.dy.app.ui.dialog.PromotionSelectionDialog;
import com.dy.app.ui.view.FragmentChatLobby;
import com.dy.app.ui.view.FragmentSetting;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.MessageFactory;
import com.dy.app.utils.Utils;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class GameActivity extends FragmentHubActivity
        implements MainCallback, View.OnClickListener, P2pGameResultDialog.P2pGameResultDialogListener,
        ITakeScreenshot {

    private static final String TAG = "GameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        setContentView(R.layout.game_activity);
        //https://stackoverflow.com/questions/6922878/how-to-remove-the-battery-icon-in-android-status-bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainHandler = new Handler(getMainLooper());
        init();
        attachFragment();
        attachListener();
        initCore();
    }

    private void init(){
        btnOpenChat = findViewById(R.id.btnOpenChat);
        btnConfig = findViewById(R.id.btnConfig);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        btnEndGameOptions = findViewById(R.id.btnEndGameOptions);
        btnUndoRequest = findViewById(R.id.btnUndoRequest);

        fragmentSetting = FragmentSetting.newInstance();
        fragmentChatLobby = FragmentChatLobby.newInstance();
        TextView tvBlackPlayerName = findViewById(R.id.tvBlackPlayerName);
        TextView tvWhitePlayerName = findViewById(R.id.tvWhitePlayerName);
        TextView tvBlackPlayerElo = findViewById(R.id.tvBlackPlayerElo);
        TextView tvWhitePlayerElo = findViewById(R.id.tvWhitePlayerElo);

        if(Player.getInstance().isWhitePiece()) {
            tvWhitePlayerName.setText((String)Player.getInstance().profile.get(PlayerProfile.KEY_USERNAME));
            tvBlackPlayerName.setText(Rival.getInstance().getName());
            tvBlackPlayerElo.setText(String.valueOf(Rival.getInstance().getElo()));
            tvWhitePlayerElo.setText(String.valueOf(Player.getInstance().profile.get(PlayerProfile.KEY_ELO)));
        }else{
            tvWhitePlayerName.setText(Rival.getInstance().getName());
            tvBlackPlayerName.setText((String)Player.getInstance().profile.get(PlayerProfile.KEY_USERNAME));
            tvWhitePlayerElo.setText(String.valueOf(Rival.getInstance().getElo()));
            tvBlackPlayerElo.setText(String.valueOf(Player.getInstance().profile.get(PlayerProfile.KEY_ELO)));

        }

        tvLeftTimeRemain = findViewById(R.id.tvLeftTimeRemain);
        tvRightTimeRemain = findViewById(R.id.tvRightTimeRemain);
        tvPlayerTimeRemain = findViewById(R.id.tvPlayerTimeRemain);

        loadingDialog = new LoadingDialog();
    }

    private void attachFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.flStage, fragmentSetting);
        ft.add(R.id.flStage, fragmentChatLobby);
        ft.show(fragmentSetting);
        ft.show(fragmentChatLobby);
        ft.hide(fragmentSetting);
        ft.hide(fragmentChatLobby);
        ft.commit();
    }

    private void attachListener(){
        btnOpenChat.setOnClickListener(this);
        btnConfig.setOnClickListener(this);
        btnSpeaker.setOnClickListener(this);
        btnEndGameOptions.setOnClickListener(this);
        btnUndoRequest.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        gameLoop.shutDown();
        multiDeviceInGameHandler.shutDown();
        super.onDestroy();
    }

    private void initCore() {
        gameCore = new GameCore(this);
        gameCore.init();
    }

    public void restartGame(){
        //start new game
        gameLoop.shutDown();
        multiDeviceInGameHandler.shutDown();
        Intent intent = new Intent(this, GameLobbyActivity.class);
        startActivity(intent);
        finish();
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

    public void informMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });

        runOnUiThread(()->{
            builder.create().show();
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
        }
    }

    public void addPeerMessage(String msg){
        if(fragmentChatLobby.isHidden()){
            runOnUiThread(()->{
                btnOpenChat.setAnimation("animated_ui/btn_open_chat_with_red_dot.json");
            });
            hasUnreadMessage = true;
        }
        runOnUiThread(()->{
            fragmentChatLobby.onMsgFromMain(GameActivity.TAG, FragmentChatLobby.ADD_PLAYER_MESSAGE, msg, null);
        });
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
                ft.commit();
                gameLoop = new GameLoop(gameFragment.getSurfaceView(), gameCore.getEntityManger());
                Thread worker = new Thread(()->{
                    gameFragment.getSurfaceView().getRenderer().waitForGLInit();
                    gameLoop.start();
                    multiDeviceInGameHandler = new MultiDeviceInGameHandler(this, gameCore.getBoard(), ConnectionManager.getInstance(), gameFragment.getSurfaceView().getRenderer().getTilePicker());
                    multiDeviceInGameHandler.start();
                });
                worker.start();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        SoundManager.getInstance().playSound(this, SoundManager.SoundType.BTN_BLOP);

        if(v == btnOpenChat){
            btnOpenChat.playAnimation();
            if(fragmentChatLobby.isHidden()){
                showFragment(fragmentChatLobby);
                if(hasUnreadMessage){
                    btnOpenChat.setAnimation("animated_ui/btn_open_chat_default.json");
                    hasUnreadMessage = false;
                }
            }else{
                hideFragment(fragmentChatLobby);
            }
        }else if(v == btnConfig){
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
            btnSpeaker.playAnimation();
        }else if(v == btnEndGameOptions){
            P2pEndGameOptionDialog dialog = new P2pEndGameOptionDialog(this);
            dialog.show(getSupportFragmentManager(), "P2pEndGameOptionDialog");
        }else if(v == btnUndoRequest){
            btnUndoRequest.playAnimation();
            showUndoRequestConfirmDialog();
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

    public void updateTimeRemain(boolean isWhite, long timeRemaining) {
        TextView currentTextView = isWhite? tvRightTimeRemain:tvLeftTimeRemain;

        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) -
                TimeUnit.MINUTES.toSeconds(minutes);
        String timeRemain = String.format("%02d:%02d", minutes, seconds);
        runOnUiThread(()->{
            currentTextView.setText(timeRemain);

            //<3 mins, set text color to red
            if(timeRemaining < 3 * 60 * 1000){
                currentTextView.setTextColor(getResources().getColor(R.color.red));
            }else{
                currentTextView.setTextColor(getResources().getColor(isWhite? R.color.white:R.color.black));
            }
        });

    }

    public void updatePlayerTimeRemain(long timeRemaining){
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) -
                TimeUnit.MINUTES.toSeconds(minutes);
        String timeRemain = String.format("%02d:%02d", minutes, seconds);
        runOnUiThread(()->{
            tvPlayerTimeRemain.setText(timeRemain);

            //<3 mins, set text color to red
            if(timeRemaining < 3 * 60 * 1000){
                tvPlayerTimeRemain.setTextColor(getResources().getColor(R.color.red));
            }else{
                int color = Player.getInstance().isWhitePiece()? R.color.white:R.color.black;
                tvPlayerTimeRemain.setTextColor(getResources().getColor(color));
            }
        });
    }

    private PGNFile getPGNFile(){
        PGNFile file = new PGNFile(gameCore.getBoard().getMoveHistory());
        //put meta
        file.addWhitePlayer(Player.getInstance().isWhitePiece()? Player.getInstance().getDisplayName() : Rival.getInstance().getName());
        file.addBlackPlayer(Player.getInstance().isWhitePiece()? Rival.getInstance().getName() : Player.getInstance().getDisplayName());
        int resultNotationCode = -1;
        if(gameResult == P2pGameResultDialog.WIN) {
            resultNotationCode = Player.getInstance().isWhitePiece()? DyConst.GAME_WHITE_WIN : DyConst.GAME_BLACK_WIN;
        }else if(gameResult == P2pGameResultDialog.LOSE) {
            resultNotationCode = Player.getInstance().isWhitePiece() ? DyConst.GAME_BLACK_WIN : DyConst.GAME_WHITE_WIN;
        }else{
            resultNotationCode = DyConst.GAME_DRAW;
        }
        file.addResult(resultNotationCode);
        file.addDate(Utils.getCurrentDate());
        file.addEvent("Multiplayer on same device");
        file.addSite("Local");
        file.addWhiteElo(Long.toString(Player.getInstance().isWhitePiece()? (long)Player.getInstance().profile.get(PlayerProfile.KEY_ELO) : Rival.getInstance().getElo()));
        file.addBlackElo(Long.toString(Player.getInstance().isWhitePiece()? Rival.getInstance().getElo() : (long)Player.getInstance().profile.get(PlayerProfile.KEY_ELO)));
        return file;
    }

    private void saveGameResultToPlayerHistory(){
        Player.getInstance().history.newGame(PlayerGameHistory.KEY_P_V_P, getPGNFile());
    }

    private void showResultDialog(GameLootCalculator lootCalculator){
        resultDialog = new P2pGameResultDialog(gameResult, lootCalculator.getPlayerTrophyDiff(), lootCalculator.getPlayerGoldDiff(), lootCalculator.getPlayerGemDiff(), lootCalculator.getOpponentTrophyDiff(), this);

        runOnUiThread(()->{
            resultDialog.show(getSupportFragmentManager(), "P2pGameResultDialog");
        });
    }

    public void onGameResult(int gameResult){
        this.gameResult = gameResult;
        saveGameResultToPlayerHistory();
        GameLootCalculator lootCalculator = new GameLootCalculator(Player.getInstance(), Rival.getInstance(), gameResult);
        showResultDialog(lootCalculator);

        if(Database.getInstance().isSignedIn()){
            obtainLoot(lootCalculator);
        }else{
            informMessage("You are not signed in. Your data will not be saved.");
        }
    }

    private void obtainLoot(GameLootCalculator lootCalculator) {
        Player.getInstance().addTrophy(lootCalculator.getPlayerTrophyDiff());
        Player.getInstance().addGold(lootCalculator.getPlayerGoldDiff());
        Player.getInstance().addGem(lootCalculator.getPlayerGemDiff());
        Player.getInstance().addNewGameStats(gameResult, Player.getInstance().isWhitePiece(), multiDeviceInGameHandler.getGameDuration(), multiDeviceInGameHandler.getPromotionCount());

        if(gameResult == P2pGameResultDialog.WIN){
            Player.getInstance().addNewGamepassPoint(1);
        }
    }

    private void sendRematchRequest(){
        Message msg = MessageFactory.getInstance().createDataMessage(null, MessageCode.ON_REMATCH_REQUEST);
        ConnectionManager.getInstance().postMessage(msg);
        //show success dialog
        informMessage("Rematch request sent!");
    }

    private void sendRematchConfirm(){
        Message msg = MessageFactory.getInstance().createDataMessage(null, MessageCode.ON_REMATCH_ACCEPTED);
        ConnectionManager.getInstance().postMessage(msg);
    }

    private void sendRematchReject(){
        Message msg = MessageFactory.getInstance().createDataMessage(null, MessageCode.ON_REMATCH_REJECTED);
        ConnectionManager.getInstance().postMessage(msg);
    }

    public void showRematchConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your opponent wants to rematch. Do you agree?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            sendRematchConfirm();
            restartGame();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
            sendRematchReject();
        });
        runOnUiThread(()->{
            builder.create().show();
        });
    }

    @Override
    public void onBtnSavePGNClick() {
        Utils.askForFileLocation(GameActivity.this, "gameplay.pgn", "", "*/*", DyConst.REQUEST_CHOOSE_FILE_LOCATION);
    }

    @Override
    public void onBtnRematchClick() {
        sendRematchRequest();
    }

    @Override
    public void onBtnExitClick() {
        //go back to MatchMakingActivity
        Intent intent = new Intent(this, MatchMakingActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBtnShareAsImageClick() {
        Utils.shareScreenShot(GameActivity.this, GameActivity.this, "gameplay.png");
    }

    @Override
    public void onBtnShareAsPGNClick() {
        if(currentSavedFileUri == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
            builder.setTitle("Error");
            builder.setMessage("Please save the PGN file first");
            builder.setPositiveButton("OK", (d, which) -> {
                d.dismiss();
                Utils.askForFileLocation(GameActivity.this, "gameplay.pgn", "", "*/*", DyConst.REQUEST_SAVE_FILE_BEFORE_SHARE);
            });
            builder.setNegativeButton("Cancel", (d, which) -> {
                d.dismiss();
            });
            builder.show();
        }else{
            Utils.shareFile(GameActivity.this, currentSavedFileUri, "text/plain");
        }
    }

    public void onRematchAccepted() {
        restartGame();
        currentSavedFileUri = null;
    }

    public void onRematchRejected() {
        //send message to inform user
        informMessage("Your opponent rejected your rematch request!");
    }

    public void resign() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to resign?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            multiDeviceInGameHandler.resign();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        runOnUiThread(()->{
            builder.create().show();
        });
    }

    public void requestDraw() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to request a draw?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            multiDeviceInGameHandler.requestDraw();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        runOnUiThread(()->{
            builder.create().show();
        });
    }

    public void onDrawRejected() {
        informMessage("Your opponent rejected your draw request!");
    }

    public void showDrawConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your opponent wants to draw. Do you agree?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            multiDeviceInGameHandler.acceptDraw();
            onGameResult(P2pGameResultDialog.DRAW);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
            multiDeviceInGameHandler.rejectDraw();
        });
        runOnUiThread(()->{
            builder.create().show();
        });
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

        if(resultDialog.getView()!=null){
            resultBitmap = Utils.getScreenShot(resultDialog.getView());
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

    private void showLoadingDialog(boolean cancelable){
        //prevent UI leak
        if(loadingDialog == null || loadingDialog.isVisible() || isFinishing()){
            return;
        }

        loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(cancelable);
        loadingDialog.show(getSupportFragmentManager(), "LoadingDialog");
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
                            Toast.makeText(GameActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        });
                        currentSavedFileUri = uri;
                    }
                });
            } catch (IOException e) {
                hideLoadingDialog();
                informMessage("Error: " + e.getMessage());
            }
        });
        worker.start();
    }

    private void hideLoadingDialog(){
        loadingDialog.dismiss();
    }

    private void savePGNAndShare(Uri uri) {
        PGNFile file = getPGNFile();

        showLoadingDialog(false);
        Thread worker = new Thread(()->{
            try {
                file.savePGN(this, uri, new PGNFile.IOnSavePGNListener() {
                    @Override
                    public void onSavePGN(String path) {
                        runOnUiThread(() -> {
                            hideLoadingDialog();
                            Utils.shareFile(GameActivity.this, uri, "text/plain");
                        });
                        currentSavedFileUri = uri;
                    }
                });
            } catch (IOException e) {
                hideLoadingDialog();
                informMessage("Error: " + e.getMessage());
            }
        });
        worker.start();
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

    public void addSystemMessage(String strMsg) {
        runOnUiThread(()->{
            fragmentChatLobby.onMsgFromMain(GameActivity.TAG, FragmentChatLobby.ADD_SYSTEM_MESSAGE, strMsg, null);
        });
    }

    public void showUndoConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your opponent wants to undo. Do you agree?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            multiDeviceInGameHandler.acceptUndo();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
            multiDeviceInGameHandler.rejectUndo();
        });
        runOnUiThread(()->{
            builder.create().show();
        });
    }

    public void showUndoRequestConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Request opponent to undo a move?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            multiDeviceInGameHandler.checkAndRequestUndo();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        runOnUiThread(()->{
            builder.create().show();
        });
    }

    public void onUndoRejected() {
        informMessage("Your opponent rejected your undo request!");
    }

    public void onUndoAccepted() {
        informMessage("Your opponent accepted your undo request!");
    }

    private Fragment currentFragment;
    private final FragmentManager fm = getSupportFragmentManager();
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private GameFragment gameFragment;
    private GameLoop gameLoop;
    private GameCore gameCore;
    private MultiDeviceInGameHandler multiDeviceInGameHandler;
    private LottieAnimationView btnOpenChat, btnConfig, btnSpeaker, btnEndGameOptions, btnUndoRequest;
    private FragmentSetting fragmentSetting;
    private FragmentChatLobby fragmentChatLobby;
    private boolean hasUnreadMessage = false;
    private TextView tvLeftTimeRemain, tvRightTimeRemain, tvPlayerTimeRemain;
    private int gameResult;
    private Uri currentSavedFileUri = null;
    private LoadingDialog loadingDialog;
    P2pGameResultDialog resultDialog;
}