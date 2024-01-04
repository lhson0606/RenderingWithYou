package com.dy.app.common.maths;

public class CubicBezier {
    private Vec3 p0;
    private Vec3 p1;
    private Vec3 p2;

    public CubicBezier(Vec3 p0, Vec3 p1, Vec3 p2){
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
    }

    public Vec3 getPoint(float t){
        float x = (1-t)*(1-t)*p0.x + 2*(1-t)*t*p1.x + t*t*p2.x;
        float y = (1-t)*(1-t)*p0.y + 2*(1-t)*t*p1.y + t*t*p2.y;
        float z = (1-t)*(1-t)*p0.z + 2*(1-t)*t*p1.z + t*t*p2.z;
        return new Vec3(x,y,z);
    }

}
