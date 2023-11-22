package com.dy.app.core;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.activity.GameActivity;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.thread.GameLoop;
import com.dy.app.gameplay.Player;
import com.dy.app.gameplay.Rival;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.terrain.Terrain;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.manager.AssetManger;
import com.dy.app.manager.BackgroundManger;
import com.dy.app.manager.EntityManger;
import com.dy.app.manager.ObjManager;
import com.dy.app.manager.PieceManager;
import com.dy.app.setting.GameSetting;
import com.dy.app.utils.DyConst;

import java.io.IOException;

public class GameCore {
    private FragmentHubActivity gameActivity;
    private GameSetting gameSetting;
    public static final String TAG = "GameCore";

    public enum TaskType{
        SET_LOADING_BACKGROUND,
        SET_GAME_SURFACE,
        SET_GAME_BACKGROUND,
        START_GAME,
    }

    private  void init() {
        gameSetting = new GameSetting();

        //background
        BackgroundManger bgm = BackgroundManger.getInstance();
        bgm.load_loading_bg(gameActivity);
        gameActivity.onMsgToMain(TAG, -1, TaskType.SET_LOADING_BACKGROUND, bgm.getRandomLoadingBackground());

        //assets
        try {
            AssetManger.getInstance().loadSkin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Obj
        ObjManager.getInstance().init();

        //board
        Board.getInstance().newBoard();

        //terrain
        try {
            EntityManger.getInstance().newEntity(Terrain.newInstance());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //load board boundary

        //load chess pieces
        try {
            PieceManager.getInstance().init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        startGame();
    }

    public static GameCore getInstance() {
        if(instance != null) return instance;
        instance = new GameCore();
        return instance;
    }

    private static GameCore instance = null;

    public void setActivity(FragmentHubActivity gameActivity) {
        if(this.gameActivity == gameActivity) return;
        this.gameActivity = gameActivity;
        instance.init();
    }

    public FragmentHubActivity getGameActivity() {
        return gameActivity;
    }

    public GameSetting getGameSetting() {
        return gameSetting;
    }

    public void startGame(){
        //set game background
        gameActivity.onMsgToMain(TAG, -1, TaskType.SET_GAME_SURFACE, null);
        //setting game camera
        if(Player.getInstance().isWhitePiece()){
            Camera.getInstance().setPos(DyConst.default_white_cam_pos);
        }else{
            Camera.getInstance().setPos(DyConst.default_black_cam_pos);
        }
        gameActivity.onMsgToMain(TAG, -1, TaskType.START_GAME, null);
    }
}
