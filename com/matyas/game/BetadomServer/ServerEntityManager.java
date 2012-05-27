package com.matyas.game.BetadomServer;

import com.matyas.game.Betadom.*;
import com.matyas.game.Betadom.util.PacketBuilder;

public class ServerEntityManager extends EntityManager{
    public static void removeEntity(int uid){
        EntityManager.removeEntity(uid);
        ServerMain.sendToAll(PacketBuilder.removeEntity(uid));
    }
    
    public static void moveEntity(boolean forward, int uid){
        EntityManager.moveEntity(forward, uid);
        ServerMain.sendToAll(PacketBuilder.entityMove(uid, entities.get(uid).getLocation().x,
                    entities.get(uid).getLocation().y, entities.get(uid).getDirection()));
    }
    
    public static void rotateEntity(boolean clockwise, int uid){
        EntityManager.rotateEntity(clockwise, uid);
        ServerMain.sendToAll(PacketBuilder.entityMove(uid, entities.get(uid).getLocation().x,
                    entities.get(uid).getLocation().y, entities.get(uid).getDirection()));
    }
    
    public static void sendLocations(){
        for(Integer id : entities.keySet()){
            ServerMain.sendToAll(PacketBuilder.entityMove(id, entities.get(id).getLocation().x,
                    entities.get(id).getLocation().y, entities.get(id).getDirection()));
        }
    }
}
