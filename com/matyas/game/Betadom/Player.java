package com.matyas.game.Betadom;

import java.awt.Image;
import java.awt.Point;

public class Player extends Entity{
    protected Image bulletSprite = null;
    protected float bulletSpeed = 0;
    protected boolean isUser = false;
    
    public Player(Point location, int direction, Image sprite, float speed, int rotateSpeed, float bulletSpeed, Image bulletSprite, boolean isUser){
        super(location, direction, speed, rotateSpeed, sprite);
        this.bulletSprite = bulletSprite;
        this.bulletSpeed = bulletSpeed;
        this.isUser = isUser;
    }
    
    public boolean isUser(){
        return isUser;
    }
    
    public void shoot(){
        EntityManager.getInstance().addEntity(new Bullet(new Point(x,y), direction, bulletSpeed, bulletSprite));
    }
}
