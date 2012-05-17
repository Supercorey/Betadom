package com.matyas.game.Betadom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;

public class Player extends Entity{
    protected ImageIcon bulletSprite = null;
    protected float bulletSpeed = 0;
    protected String username = "Player";

    public Player(Point location, int direction, Image sprite, float speed,
            int rotateSpeed, float bulletSpeed, Image bulletSprite, String username){
        super(location, direction, speed, rotateSpeed, sprite);
        this.bulletSprite = new ImageIcon(bulletSprite);
        this.bulletSpeed = bulletSpeed;
        this.username = username;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public void draw(Graphics gfx, Point offset){
        super.draw(gfx,offset);
        Graphics2D g = (Graphics2D)gfx;
        g.setColor(Color.decode(SettingsManager.getSetting("color_nametag")));
        g.drawChars(getUsername().toCharArray(), 0, getUsername().length(),
                x - offset.x - getDistanceOffset(), y - offset.y - getDistanceOffset());
    }

    public void shoot(){
        EntityManager.addEntity(new Bullet(new Point(x,y), direction, bulletSpeed, bulletSprite.getImage()));
    }
}
