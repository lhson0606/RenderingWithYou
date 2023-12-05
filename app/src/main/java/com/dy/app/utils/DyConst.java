package com.dy.app.utils;

import android.os.Environment;

import com.dy.app.R;
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
    public static final String piece_skins[] ={"basic.png", "wooden.jpg", "fire_n_ice.jpg"};
    public static final int piece_skin_thumbnails[] ={R.raw.thumbnail_piece_basic, R.raw.thumbnail_piece_wooden, R.raw.thumbnail_piece_fire_n_ice};
    public static final String board_skins[] ={"basic.jpg"};
    public static final String tile_skins[] = {"basic.png", "wooden.jpg"};
    public static final int tile_skin_thumbnails[] ={R.raw.thumbnail_tile_basic, R.raw.thumbnail_tile_wooden};
    public static final String back_ground_skins[] = {"basic.jpg"};
    public static final String terrain_tex[] = {"galaxy.jpg", "cozy_room.jpg", "Booth2_lambert1_BaseColor.png", "PicnicTable_albedoM.tga.png"};
    public static final String terrain_models[] = {"galaxy.obj", "cozy_room.obj", "dinner_booth.obj", "picnic_table.obj"};
    public static final int terrain_skin_thumbnails[] = {R.raw.thumbnail_terrain_galaxy, R.raw.thumbnail_terrain_cozy_room, R.raw.thumbnail_terrain_booth, R.raw.thumbnail_terrain_pinic_table};
    public static final String king = "king";
    public static final String queen = "queen";
    public static final String bishop = "bishop";
    public static final String rook = "rook";
    public static final String knight = "knight";
    public static final String pawn = "pawn";
    public static final String tile = "tile";
    public static final String terrain = "terrain";
    public static final Material default_material = new Material(2f, 0.5f);
    public static final Light defulat_light = new Light(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(0.0f, 25.0f, 0.0f), 100.0f);
    public static final Vec3 default_black_cam_pos = new Vec3(-0.007419136f, 3.611991f, 0.6967087f);
    public static final Vec3 default_white_cam_pos = new Vec3(-1.2612155E-4f, 3.611991f, -0.6967087f);
    public static final String board = "board";
    public static final Vec3 black_target = new Vec3(0.0f, 0.0f, 1.0f);
    public static final Vec3 white_target = new Vec3(0.0f, 0.0f, -1.0f);
    public static final int row_count = 8;
    public static final int col_count = 8;
    public static final float default_ambient_factor = 0.4f;
    public static final Vec3 light_color = new Vec3(1.0f, 1.0f, 1.0f);
    public static final Vec3 light_pos = new Vec3(0.0f, 20.0f, 0.0f);
    public static final float light_intensity = 1.0f;
    public static final float tile_size = 0.649908f;
    public static final float board_height = 0.117954f;
    public static float default_attenuation_constant = 0.0f;
    public static float default_attenuation_linear = 1f;
    public static float default_attenuation_quadratic = 0f;
    public static final String piece_ver_glsl_path = "glsl/piece/ver.glsl";
    public static final String piece_frag_glsl_path = "glsl/piece/frag.glsl";
    public static final String terrain_ver_glsl_path = "glsl/terrain/ver.glsl";
    public static final String terrain_frag_glsl_path = "glsl/terrain/frag.glsl";
    public static final String tile_ver_glsl_path = "glsl/tile/ver.glsl";
    public static final String tile_frag_glsl_path = "glsl/tile/frag.glsl";
    public static final String piece_model[] = {"king.obj", "queen.obj", "bishop.obj", "rook.obj", "knight.obj", "pawn.obj"};
    public static final String tile_model_path ="model/board/tile.obj";
    public static final int REQUEST_CHOOSE_FILE_LOCATION = 1;
    public static final int REQUEST_TAKE_SCREENSHOT_AND_SHARE = 2;
    public static final int CHECK_FOR_PERMISSION_BEFORE_TAKE_SCREENSHOT_AND_SHARE = 3;
    final static String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DyChess/";

}
