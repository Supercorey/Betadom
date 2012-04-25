package com.matyas.game.Betadom;

import com.matyas.game.Betadom.util.PacketBuilder;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class EntityManager {
    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private int playerEntity = 0;
    
    private static EntityManager instance = new EntityManager();
    
    private EntityManager(){}
    
    public static EntityManager getInstance(){
        return instance;
    }
    
    public void addEntity(Entity entity){
        addEntity(entity, -1);
    }
    
    public void addEntity(Entity entity, int index){
        if(index != -1){
            entities.add(index, entity);
        }else{
            entities.add(entity);
        }
        if(entity instanceof Player){
            if(((Player)entity).isUser()) playerEntity = entities.indexOf(entity);
        }
    }
    
    public void removeEntity(Entity entity){
        entities.remove(entity);
    }
    public void removeEntity(int index){
        entities.remove(index);
    }
    
    public void drawEntities(Graphics g, Point offset){
        Graphics2D gfx = (Graphics2D)g;
        AffineTransform trans = new AffineTransform();
        for(Entity entity : entities){
            trans.setToTranslation(entity.getLocation().x - offset.x - entity.getDistanceOffset(),
                    entity.getLocation().y - offset.y - entity.getDistanceOffset());
            trans.rotate(Math.toRadians(entity.getDirection()),entity.getSprite().getWidth(null)/2,entity.getSprite().getHeight(null)/2);
            gfx.drawImage(entity.getSprite(), trans, null);
        }
    }
    
    public void updateEntities(){
        for(Entity entity : entities){
            entity.update();
        }
    }
    
    public Player getUser(){
        return (Player)entities.get(playerEntity);
    }
    
    public void rotateUser(boolean clockwise){
        GameCanvas.network.addPacket(PacketBuilder.playerRotate(clockwise));
        getUser().rotate(getUser().getRotateSpeed()*(clockwise?1:-1));
    }
    
    public void moveUser(boolean forward){
        GameCanvas.network.addPacket(PacketBuilder.playerMove(forward));
        getUser().move(getUser().getSpeed()*(forward?1:-1));
    }
}
