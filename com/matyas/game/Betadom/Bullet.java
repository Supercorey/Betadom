package com.matyas.game.Betadom;

import java.awt.Image;
import java.awt.Point;

public class Bullet extends Entity{   
    public Bullet(Point location, int direction, float speed, Image sprite){
        super(location, direction, speed, 0, sprite);
    }
    
    public void update(){
        move(speed);
    }
}
