package com.dy.app.common.maths;

import java.util.Vector;

public class Vec2 {
    public float x;
    public float y;

    public Vec2(){
        x = y = 0f;
    }

    public Vec2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 vec2) {
        x = vec2.x;
        y = vec2.y;
    }

    public Vec2 Scale(float s){
        Vec2 ret = new Vec2(this);
        ret.x *= s;
        ret.y *= s;
        return ret;
    }

    static public float[] toFloatArray(Vector<Vec2> data){
        float[] ret = new float[data.size()*2];

        for(int i = 0; i<data.size(); ++i){
            ret[i*3] = data.get(i).x;
            ret[i*3 + 1] = data.get(i).y;
        }

        return ret;
    }
}
