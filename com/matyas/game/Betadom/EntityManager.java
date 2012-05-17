package com.matyas.game.Betadom;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class EntityManager {
    protected static ArrayList<Entity> entities = new ArrayList<Entity>();
    //private int playerEntity = 0;
    
    public static int addEntity(Entity entity){
        return addEntity(entity, -1);
    }
    
    public static int addEntity(Entity entity, int index){
        if(index != -1){
            entities.add(index, entity);
        }else{
            entities.add(entity);
            index = entities.indexOf(entity);
        }
        /*if(entity instanceof Player){
            if(((Player)entity).isUser()) playerEntity = index;
        }*/
        return index;
    }
    
    public static void removeEntity(Entity entity){
        entities.remove(entity);
    }
    public static void removeEntity(int index){
        entities.remove(index);
    }
    
    public static void drawEntities(Graphics g, Point offset){
        Graphics2D gfx = (Graphics2D)g;
        AffineTransform trans = new AffineTransform();
        for(Entity entity : entities){
            trans.setToTranslation(entity.getLocation().x - offset.x - entity.getDistanceOffset(),
                    entity.getLocation().y - offset.y - entity.getDistanceOffset());
            trans.rotate(Math.toRadians(entity.getDirection()),entity.getSprite().getWidth(null)/2,entity.getSprite().getHeight(null)/2);
            gfx.drawImage(entity.getSprite(), trans, null);
        }
    }
    
    public static void updateEntities(){
        for(Entity entity : entities){
            entity.update();
        }
    }
    
    /*public Player getUser(){
        return (Player)entities.get(playerEntity);
    }
    
    public void rotateUser(boolean clockwise){
        //GameCanvas.network.addPacket(PacketBuilder.playerRotate(clockwise));
        getUser().rotate(getUser().getRotateSpeed()*(clockwise?1:-1));
    }
    
    public void moveUser(boolean forward){
        //GameCanvas.network.addPacket(PacketBuilder.playerMove(forward));
        getUser().move(getUser().getSpeed()*(forward?1:-1));
    }*/
    
    public static Entity getEntityById(int uid){
        return entities.get(uid);
    }
    
    public static void rotateEntity(boolean clockwise, int uid){
        getEntityById(uid).rotate(getEntityById(uid).getRotateSpeed()*(clockwise?1:-1));
    }
    
    public static void moveEntity(boolean forward, int uid){
        entities.get(uid).move(getEntityById(uid).getSpeed()*(forward?1:-1));
    }
}
