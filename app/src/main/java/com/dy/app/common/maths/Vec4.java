package com.dy.app.common.maths;

public class Vec4 {
    public float x, y, z, w;

    public Vec4(){
        x = y = z = w = 0;
    }

    public Vec4(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4(Vec4 v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
    }

    public Vec4(Vec3 v, float w){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = w;
    }

}
