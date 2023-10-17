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

    public Vec4(float[] data) {
        this.x = data[0];
        this.y = data[1];
        this.z = data[2];
        this.w = data[3];
    }

    public float[] getData(){
        return new float[]{x, y, z, w};
    }

    @Override
    public String toString() {
        return "Vec4{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }

    public Vec3 xyz() {
        return new Vec3(x, y, z);
    }
}
