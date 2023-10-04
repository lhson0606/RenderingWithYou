package com.dy.startinganimation.maths;

public class Vec3 {
    public float x;
    public float y;
    public float z;
    public Vec3(){
        x = y = z = 0f;
    }

    public Vec3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 cross(Vec3 v){
        Vec3 dest = new Vec3();
        /*
         * dest[0] = a2*b3 - a3*b2;
         * dest[1] = a3*b1 - a1*b3;
         * dest[2] = a1*b2 - a2*b1;
         * */
        dest.x = y*v.z - z*v.y;
        dest.y = z*v.x - x*v.z;
        dest.z = x*v.y - y*v.x;

        return dest;
    }

    public float dot(Vec3 v){
        return x*v.x + y*v.y + z*v.z;
    }
    public float length(){
        return (float)Math.sqrt(x*x + y*y + z*z);
    }
    public float sqrLength() {return x*x + y*y + z*z;}
    public Vec3 normalize(){
        Vec3 dst = new Vec3(x,y,z);
        float l = dst.length();

        if(l == 0){
            return dst;
        }

        dst.x /= l;
        dst.y /= l;
        dst.z /= l;

        return dst;
    }

    public Vec3 translate(Vec3 v){
        Vec3 dst = new Vec3();
        dst.x = x + v.x;
        dst.y = y + v.y;
        dst.z = z + v.z;
        return  dst;
    }
    public Vec3 scale(float s){
        Vec3 ret = new Vec3(x, y, z);
        ret.x *= s;
        ret.y *= s;
        ret.z *= s;
        return ret;
    }
    public float angle(Vec3 v){
        return (float)Math.acos(normalize().dot(v.normalize()));
    }
}
