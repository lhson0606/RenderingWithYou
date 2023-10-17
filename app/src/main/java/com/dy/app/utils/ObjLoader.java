package com.dy.app.utils;

import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameCore;
import com.dy.app.graphic.Material;
import com.dy.app.graphic.model.Mesh;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.model.Texture;
import com.dy.app.graphic.shader.Shader;
import com.dy.app.graphic.shader.ShaderHelper;

import java.io.IOException;

public class ObjLoader {
    public static Obj3D load(String path) throws IOException {
        Obj3D res = null;

        Mesh mesh = MeshLoader.getInstance().load(GameCore.getInstance().getGameActivity(), path);
        String verCode = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.ver_glsl_path));
        String fragCode = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.frag_glsl_path));
        Shader shader = new Shader(verCode, fragCode);

        res = new Obj3D(
                mesh,
                shader
        );

        return res;
    }
}
