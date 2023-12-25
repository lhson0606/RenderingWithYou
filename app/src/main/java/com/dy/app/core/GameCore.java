package com.dy.app.core;

import androidx.fragment.app.DialogFragment;

import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.terrain.Terrain;
import com.dy.app.gameplay.viewport.ViewPort;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.manager.AssetManger;
import com.dy.app.manager.BackgroundManger;
import com.dy.app.manager.EntityManger;
import com.dy.app.manager.ObjManager;
import com.dy.app.manager.PieceManager;
import com.dy.app.setting.GameSetting;
import com.dy.app.ui.dialog.LoadingDialogWithText;
import com.dy.app.utils.DyConst;

import java.io.IOException;

public class GameCore {
    private FragmentHubActivity gameActivity;
    public static final String TAG = "GameCore";
    private BackgroundManger backgroundManger;
    private ObjManager objManager;
    private PieceManager pieceManager;
    private EntityManger entityManger;
    private AssetManger assetManger;
    private Board board;

    public enum TaskType{
        SET_LOADING_BACKGROUND,
        SET_GAME_BACKGROUND,
        START_GAME,
    }

    public GameCore(FragmentHubActivity gameActivity){
        this.gameActivity = gameActivity;
    }

    public void init() {
        LoadingDialogWithText loadingDialog = LoadingDialogWithText.newInstance();
        loadingDialog.doWork(gameActivity, ()->{

            //entity manager
            entityManger = new EntityManger();

            //background
            backgroundManger = new BackgroundManger();
            backgroundManger.load_loading_bg(gameActivity);
            gameActivity.onMsgToMain(TAG, -1, TaskType.SET_LOADING_BACKGROUND, backgroundManger.getRandomLoadingBackground());

            //assets
            assetManger = new AssetManger(gameActivity);
            try {
                assetManger.loadSkin();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            loadingDialog.setTvDisplayText(gameActivity, "Loading skin...");

            //Obj
            objManager = new ObjManager(gameActivity);
            objManager.init();
            loadingDialog.setTvDisplayText(gameActivity, "Loading game models...");

            //board
            board = new Board(gameActivity, entityManger, objManager, assetManger);
            board.load();
            loadingDialog.setTvDisplayText(gameActivity, "Initializing board...");

            //terrain
            Terrain terrain = new Terrain(gameActivity, objManager, assetManger);
            entityManger.newEntity(terrain);
            loadingDialog.setTvDisplayText(gameActivity, "Initializing terrain...");

            //load board boundary

            //load chess pieces
            pieceManager = new PieceManager(gameActivity, entityManger, board, objManager, assetManger, GameSetting.getInstance());
            try {
                pieceManager.init();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            board.setPieceManager(pieceManager);
            board.updateBoardState();

            startGame();
        });
    }

    public FragmentHubActivity getGameActivity() {
        return gameActivity;
    }

    public void startGame(){
        //setting game camera
        if(Player.getInstance().isWhitePiece()){
            GameSetting.getInstance().setSelectedViewPortIndex(ViewPort.WHITE_VIEWPORT_INDEX);
            Camera.getInstance().setPos(ViewPort.WHITE_SIDE.getPos());
        }else{
            GameSetting.getInstance().setSelectedViewPortIndex(ViewPort.BLACK_VIEWPORT_INDEX);
            Camera.getInstance().setPos(ViewPort.BLACK_SIDE.getPos());
        }
        gameActivity.onMsgToMain(TAG, -1, TaskType.START_GAME, null);
    }

    public BackgroundManger getBackgroundManger() {
        return backgroundManger;
    }

    public ObjManager getObjManager() {
        return objManager;
    }

    public PieceManager getPieceManager() {
        return pieceManager;
    }

    public EntityManger getEntityManger() {
        return entityManger;
    }

    public AssetManger getAssetManger() {
        return assetManger;
    }

    public Board getBoard() {
        return board;
    }
}
