package com.matyas.game.Betadom;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class EntityManager {
    //TODO: Fix out-of-bounds on secondary connection
    protected static ArrayList<Entity> entities = new ArrayList<Entity>();
    
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
        return index;
    }
    
    public static void removeEntity(Entity entity){
        entities.remove(entity);
    }
    public static void removeEntity(int index){
        entities.remove(index);
    }
    
    public static void drawEntities(Graphics g, Point offset){
        for(Entity entity : entities){
            entity.draw(g, offset);
        }
    }
    
    public static void updateEntities(){
        for(Entity entity : entities){
            entity.update();
        }
    }
    
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
