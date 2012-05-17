package com.matyas.game.Betadom;

import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;

public class Player extends Entity{
    protected ImageIcon bulletSprite = null;
    protected float bulletSpeed = 0;

    public Player(Point location, int direction, Image sprite, float speed, int rotateSpeed, float bulletSpeed, Image bulletSprite){
        super(location, direction, speed, rotateSpeed, sprite);
        this.bulletSprite = new ImageIcon(bulletSprite);
        this.bulletSpeed = bulletSpeed;
    }
    
    public void shoot(){
        EntityManager.addEntity(new Bullet(new Point(x,y), direction, bulletSpeed, bulletSprite.getImage()));
    }
}
