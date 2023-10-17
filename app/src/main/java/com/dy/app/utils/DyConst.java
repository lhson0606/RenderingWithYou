package com.dy.app.utils;

import com.dy.app.common.maths.Vec3;
import com.dy.app.graphic.Material;
import com.dy.app.graphic.Light;

public class DyConst {
    public static final String loading_bg_path = "loading_background/";
    public static final String loading_bg_name = "bg_";//bg_x.jpg
    public static final String piece_model_path = "model/piece/";
    public static final String black_piece_tex_path = "texture/piece/black/";
    public static final String white_piece_tex_path = "texture/piece/white/";
    public static final String black_tile_tex_path = "texture/tile/black/";
    public static final String white_tile_tex_path = "texture/tile/white/";
    public static final String board_tex_path = "texture/board/";
    public static final String background_tex_path = "texture/background/";
    public static final String terrain_model_path = "model/terrain/";
    public static final String piece_skins[] ={"basic.png"};
    public static final String board_skins[] ={"basic.jpg"};
    public static final String tile_skins[] = {"basic.png"};
    public static final String back_ground_skins[] = {"basic.jpg"};
    public static final String terrain_tex[] = {"cozy_room.jpg", "galaxy.jpg"};
    public static final String terrain_models[] = {"cozy_room.obj", "galaxy.obj"};
    public static final String king = "king";
    public static final String queen = "queen";
    public static final String bishop = "bishop";
    public static final String rook = "rook";
    public static final String knight = "knight";
    public static final String pawn = "pawn";
    public static final String tile = "tile";
    public static final String terrain = "terrain";
    public static final Material default_material = new Material(1.0f, 0.2f);
    public static final Light defulat_light = new Light(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(0.0f, 10.0f, 0.0f), 100.0f);
    public static final Vec3 default_black_cam_pos = new Vec3(0.0f, 20.0f, 20.0f);
    public static final Vec3 default_white_cam_pos = new Vec3(0.0f, 20.0f, -20.0f);
    public static final String board = "board";
    public static final Vec3 black_target = new Vec3(0.0f, 0.0f, 1.0f);
    public static final Vec3 white_target = new Vec3(0.0f, 0.0f, -1.0f);
    public static final int row_count = 8;
    public static final int col_count = 8;
    public static final float default_ambient_factor = 0.2f;
    public static final Vec3 light_color = new Vec3(1.0f, 1.0f, 1.0f);
    public static final Vec3 light_pos = new Vec3(0.0f, 20.0f, 0.0f);
    public static final float light_intensity = 1.0f;
    public static final float tile_size = 0.649908f;
    public static final float board_height = 0.117954f;
    public static float default_attenuation_constant = 0.0f;
    public static float default_attenuation_linear = 0f;
    public static float default_attenuation_quadratic = 0f;
    public static final String piece_ver_glsl_path = "glsl/piece/ver.glsl";
    public static final String piece_frag_glsl_path = "glsl/piece/frag.glsl";
    public static final String terrain_ver_glsl_path = "glsl/terrain/ver.glsl";
    public static final String terrain_frag_glsl_path = "glsl/terrain/frag.glsl";
    public static final String tile_ver_glsl_path = "glsl/tile/ver.glsl";
    public static final String tile_frag_glsl_path = "glsl/tile/frag.glsl";
    public static final String piece_model[] = {"king.obj", "queen.obj", "bishop.obj", "rook.obj", "knight.obj", "pawn.obj"};
    public static final String tile_model_path ="model/board/tile.obj";
}
