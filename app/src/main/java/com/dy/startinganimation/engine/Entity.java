package com.dy.startinganimation.engine;

public interface Entity {
    public void init();
    public void update(float dt);
    public void draw();
    public void destroy();
}
