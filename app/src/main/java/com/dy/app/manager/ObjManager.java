package com.dy.app.manager;

import android.content.Context;

import com.dy.app.gameplay.player.Player;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.ObjLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ObjManager {
    private Map<String, Obj3D> obj3Ds;
    private Context context;

    public ObjManager(Context context){
        obj3Ds = new HashMap<>();
        this.context = context;
    }

    public void init(){
        loadPieceModel();
        loadTile();
        loadTerrain();
    }

    private void loadTerrain() {
        String path = Player.getInstance().getTerrainModelPath();
        try{
            Obj3D terrain = ObjLoader.load(context, path);
            obj3Ds.put(DyConst.terrain, terrain);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTile() {
        String path = DyConst.tile_model_path;
        try {
            Obj3D tile = ObjLoader.load(context, path);
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
                Obj3D obj3D =  ObjLoader.load(context, path);
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

    public Obj3D getObj(String name){
        Obj3D o = obj3Ds.get(name);
        return o.clone();
    }
}
