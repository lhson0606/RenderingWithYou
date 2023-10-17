package com.dy.app.gameplay.terrain;

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

    public static Terrain newInstance() throws IOException {
        Terrain terrain = new Terrain();
        terrain.obj = ObjManager.getInstance().getObj(DyConst.terrain);
        Skin skin = AssetManger.getInstance().getSkin(AssetManger.SkinType.TERRAIN_TEXTURE);
        terrain.obj.setTex(skin.getTexture());
        terrain.obj.setMaterial(skin.getMaterial());
        String verCode = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.terrain_ver_glsl_path));
        String fragCode = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.terrain_frag_glsl_path));
        TerrainShader shader = new TerrainShader(verCode, fragCode);
        shader.setTerrain(terrain);
        terrain.obj.setShader(shader);
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

    public Obj3D getObj() {
        return obj;
    }
}
