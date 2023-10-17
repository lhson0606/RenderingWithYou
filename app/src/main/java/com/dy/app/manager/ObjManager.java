package com.dy.app.manager;

import com.dy.app.core.GameCore;
import com.dy.app.gameplay.Player;
import com.dy.app.graphic.Skin;
import com.dy.app.graphic.model.Mesh;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.shader.Shader;
import com.dy.app.graphic.shader.ShaderHelper;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.MeshLoader;
import com.dy.app.utils.ObjLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ObjManager {
    private Map<String, Obj3D> obj3Ds;

    public static ObjManager getInstance(){
        return instance = (instance == null) ? new ObjManager() : instance;
    }

    private static ObjManager instance = null;

    public void init(){
        loadPieceModel();
        loadTile();
        loadTerrain();
    }

    private void loadTerrain() {
        String path = Player.getInstance().getTerrainModelPath();
        try{
            Obj3D terrain = ObjLoader.load(path);
            obj3Ds.put(DyConst.terrain, terrain);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTile() {
        String path = DyConst.tile_model_path;
        try {
            Obj3D tile = ObjLoader.load(path);
            tile.setMaterial(DyConst.default_material);
            obj3Ds.put(DyConst.tile, tile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadPieceModel(){
        for(String pieceName:DyConst.piece_model){
            String path = DyConst.piece_model_path + pieceName;
            try {
                Obj3D obj3D =  ObjLoader.load(path);
                obj3D.setMaterial(DyConst.default_material);

                if(pieceName.startsWith(DyConst.king)){
                    obj3Ds.put(DyConst.king, obj3D);
                }else if(pieceName.startsWith(DyConst.queen)) {
                    obj3Ds.put(DyConst.queen, obj3D);
                } else if (pieceName.startsWith(DyConst.bishop)) {
                    obj3Ds.put(DyConst.bishop, obj3D);
                } else if (pieceName.startsWith(DyConst.rook)) {
                    obj3Ds.put(DyConst.rook, obj3D);
                } else if (pieceName.startsWith(DyConst.knight)) {
                    obj3Ds.put(DyConst.knight, obj3D);
                } else if (pieceName.startsWith(DyConst.pawn)) {
                    obj3Ds.put(DyConst.pawn, obj3D);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ObjManager(){
        obj3Ds = new HashMap<>();
    }

    public Obj3D getObj(String name){
        return new Obj3D(obj3Ds.get(name));
    }
}
