package com.dy.app.common.maths;

public class Vec2i {
    public int x;
    public int y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i(Vec2i v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Vec2i() {
        this.x = 0;
        this.y = 0;
    }

    public Vec2i add(Vec2i v) {
        return new Vec2i(this.x + v.x, this.y + v.y);
    }

    public Vec2i sub(Vec2i v) {
        return new Vec2i(this.x - v.x, this.y - v.y);
    }

    public Vec2i mul(int s) {
        return new Vec2i(this.x * s, this.y * s);
    }

    public Vec2i div(int s) {
        return new Vec2i(this.x / s, this.y / s);
    }

    public int dot(Vec2i v) {
        return this.x * v.x + this.y * v.y;
    }

    public int cross(Vec2i v) {
        return this.x * v.y - this.y * v.x;
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public boolean isEqual(Vec2i v){
        return this.x == v.x && this.y == v.y;
    }
}
