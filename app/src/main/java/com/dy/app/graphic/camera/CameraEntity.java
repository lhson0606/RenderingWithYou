package com.dy.app.graphic.camera;

import com.dy.app.common.maths.CubicBezier;
import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.viewport.ViewPort;
import com.dy.app.manager.EntityManger;

public class CameraEntity implements GameEntity {
    private static CameraEntity instance = null;

    public static CameraEntity getInstance(){
        if(instance == null){
            instance = new CameraEntity();
        }
        return instance;
    }

    public enum CameraState{
        BLACK_TO_WHITE,
        WHITE_TO_BLACK,
        IDLE
    }

    final private CubicBezier blackToWhite = new CubicBezier(
            ViewPort.BLACK_SIDE.getPos(),
            ViewPort.BLACK_WHITE.getPos(),
            ViewPort.WHITE_SIDE.getPos()
    );

    final private CubicBezier whiteToBlack = new CubicBezier(
            ViewPort.WHITE_SIDE.getPos(),
            ViewPort.WHITE_BLACK.getPos(),
            ViewPort.BLACK_SIDE.getPos()
    );

    private final float duration = 0.5f;
    private float accumulatedTime = 0;
    private CameraState state = CameraState.IDLE;

    @Override
    public void init() {

    }

    @Override
    public void update(float dt) {
        if(state == CameraState.IDLE) return;

        accumulatedTime += dt;
        float t = accumulatedTime / duration;
        if(t > 1) t = 1;

        switch (state){
            case BLACK_TO_WHITE:
                Camera.getInstance().setPos(blackToWhite.getPoint(t));
                break;
            case WHITE_TO_BLACK:
                Camera.getInstance().setPos(whiteToBlack.getPoint(t));
                break;
        }

        Camera.getInstance().update();

        if(t == 1){
            state = CameraState.IDLE;
        }
    }

    @Override
    public void draw() {

    }

    @Override
    public void destroy() {

    }

    public void setState(CameraState state){
        this.state = state;
        accumulatedTime = 0;
    }
}
