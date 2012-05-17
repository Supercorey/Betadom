package com.matyas.game.BetadomServer;

import com.matyas.game.Betadom.*;
import com.matyas.game.Betadom.util.PacketBuilder;

public class ServerEntityManager extends EntityManager{
    public static void sendLocations(){
        for(Entity entity : entities){
            ServerMain.sendToAll(PacketBuilder.entityMove(entities.indexOf(entity), entity.getLocation().x, entity.getLocation().y, entity.getDirection()));
        }
    }
}
