package com.dy.app.utils;

import android.content.Context;

import com.dy.app.core.GameCore;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.model.Mesh;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.shader.PieceShader;
import com.dy.app.graphic.shader.ShaderHelper;

import java.io.IOException;

public class ObjLoader{
    public static Obj3D load(Context context, String path) throws IOException {
        Obj3D res = null;

        Mesh mesh = MeshLoader.getInstance().load(context, path);


        res = new Obj3D(
                mesh
        );

        return res;
    }

}
