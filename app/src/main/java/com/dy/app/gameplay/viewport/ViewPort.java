package com.dy.app.gameplay.viewport;

import com.dy.app.common.maths.Vec3;
import com.dy.app.utils.DyConst;

public class ViewPort {
    private String name;
    private Vec3 pos;
    public ViewPort(String name, Vec3 pos){
        this.name = name;
        this.pos = pos;
    }

    public static final String[] VIEW_PORT_NAMES = {
            "White side",
            "Black side",
            "White black",
            "Black white"
    };

    public static ViewPort WHITE_SIDE = new ViewPort(VIEW_PORT_NAMES[0], DyConst.default_white_cam_pos);
    public static ViewPort BLACK_SIDE = new ViewPort(VIEW_PORT_NAMES[1], DyConst.default_black_cam_pos);
    public static ViewPort WHITE_BLACK = new ViewPort(VIEW_PORT_NAMES[2], new Vec3(-0.6760385f, 3.6159182f, -0.0022912857f));
    public static ViewPort BLACK_WHITE = new ViewPort(VIEW_PORT_NAMES[3], new Vec3(0.6760385f, 3.6159182f, 0.0022912857f));

    public String getName() {
        return name;
    }

    public Vec3 getPos() {
        return pos;
    }
}
