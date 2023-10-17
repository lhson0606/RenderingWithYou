package com.dy.app.gameplay.terrain;

import com.dy.app.core.GameEntity;
import com.dy.app.graphic.Skin;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.manager.AssetManger;
import com.dy.app.manager.ObjManager;
import com.dy.app.utils.DyConst;

public class Terrain implements GameEntity {

    public static Terrain newInstance(){
        Terrain terrain = new Terrain();
        terrain.obj = ObjManager.getInstance().getObj(DyConst.terrain);
        Skin skin = AssetManger.getInstance().getSkin(AssetManger.SkinType.TERRAIN_TEXTURE);
        terrain.obj.setTex(skin.getTexture());
        terrain.obj.setMaterial(skin.getMaterial());
        return terrain;
    }

    private Obj3D obj;

    @Override
    public void init() {
        obj.init();
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void draw() {
        obj.draw(Camera.getInstance().mViewMat, Camera.getInstance().mProjMat);
    }

    @Override
    public void destroy() {
        obj.destroy();
    }
}
