package com.dy.app.graphic.display;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dy.app.core.FragmentCallback;
import com.dy.app.core.GameCore;
import com.dy.app.gameplay.board.Board;
import com.dy.app.manager.EntityManger;

public class GameFragment extends Fragment implements FragmentCallback {
    public static final String TAG = "GameFragment";
    private GameSurface surface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return surface;
    }

    public GameFragment(Context context, EntityManger entityManger, Board board){
        surface = new GameSurface(context, entityManger, board);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }//onCreate
    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {
        if(TAG.equals(GameCore.TAG)){
            handleFromCore(type, o1, o2);
        }
    }

    private void handleFromCore(int type, Object o1, Object o2){    }

    public GameSurface getSurfaceView() {
        return surface;
    }
}
