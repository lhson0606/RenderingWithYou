package com.dy.app.gameplay.terrain;

import android.content.Context;

import com.dy.app.core.GameCore;
import com.dy.app.core.GameEntity;
import com.dy.app.graphic.Skin;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.shader.ShaderHelper;
import com.dy.app.graphic.shader.TerrainShader;
import com.dy.app.manager.AssetManger;
import com.dy.app.manager.ObjManager;
import com.dy.app.utils.DyConst;

import java.io.IOException;

public class Terrain implements GameEntity {

    public Terrain(Context context, ObjManager objManager, AssetManger assetManger) {
        this.obj = objManager.getObj(DyConst.terrain);
        Skin skin = assetManger.getSkin(AssetManger.SkinType.TERRAIN_TEXTURE);
        this.obj.setTex(skin.getTexture());
        this.obj.setMaterial(skin.getMaterial());
        String verCode = null;
        try {
            verCode = ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.terrain_ver_glsl_path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String fragCode = null;
        try {
            fragCode = ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.terrain_frag_glsl_path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TerrainShader shader = new TerrainShader(verCode, fragCode);
        shader.setTerrain(this);
        this.obj.setShader(shader);
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

    public Obj3D getObj() {
        return obj;
    }
}
