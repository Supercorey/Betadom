package com.matyas.game.Betadom;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import javax.swing.ImageIcon;

public class Entity implements Serializable, BetadomDrawable{
    protected int x = 0;
    protected int y = 0;
    protected int direction = 0;
    protected float speed = 0;
    protected int rotateSpeed = 0;
    protected ImageIcon sprite = null;
    
    protected int distanceOffset = 0;
    
    public Entity(Point location, int direction, float speed, int rotateSpeed, Image sprite){
        this.x = location.x;
        this.y = location.y;
        this.direction = direction;
        this.sprite = new ImageIcon(sprite);
        this.speed = speed;
        this.rotateSpeed = rotateSpeed;
        
        this.distanceOffset = sprite.getWidth(null)/2;
    }
    
    public void setDirection(int direction){
        this.direction = direction;
    }
    public int getDirection(){
        return direction;
    }
    
    public void setLocation(Point location){
        x = location.x;
        y = location.y;
    }
    public Point getLocation(){
        return new Point(x,y);
    }
    
    public void setSpeed(float speed){
        this.speed = speed;
    }
    public float getSpeed(){
        return speed;
    }
    
    public void setRotateSpeed(int rotateSpeed){
        this.rotateSpeed = rotateSpeed;
    }
    public int getRotateSpeed(){
        return rotateSpeed;
    }
    
    public void setSprite(Image sprite){
        this.sprite = new ImageIcon(sprite);
        this.distanceOffset = sprite.getWidth(null)/2;
    }
    public Image getSprite(){
        return sprite.getImage();
    }
    
    public int getDistanceOffset(){
        return distanceOffset;
    }
    
    public void rotate(int degrees){
        setDirection(direction+degrees);
        if(direction >= 360 || direction < 0){
            setDirection(Math.abs(Math.abs(direction)-360));
        }
    }

    public void move(float distance){
        int deltaX = (int) (distance*Math.cos(Math.toRadians(direction-90)));
        int deltaY = (int) (distance*Math.sin(Math.toRadians(direction-90)));
        setLocation(new Point(x+deltaX, y+deltaY));
    }
    
    public void update(){}

    public void draw(Graphics gfx, Point offset) {
        Graphics2D g = (Graphics2D)gfx;
        AffineTransform trans = new AffineTransform();

        trans.setToTranslation(getLocation().x - offset.x - getDistanceOffset(),
                getLocation().y - offset.y - getDistanceOffset());
        trans.rotate(Math.toRadians(getDirection()),getSprite().getWidth(null)/2,getSprite().getHeight(null)/2);
        g.drawImage(getSprite(), trans, null);
    }
}
