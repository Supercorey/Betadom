package com.matyas.game.BetadomServer;

import com.matyas.game.Betadom.*;
import com.matyas.game.Betadom.util.PacketBuilder;

public class ServerEntityManager extends EntityManager{
    public static void sendLocations(){
        for(Integer id : entities.keySet()){
            ServerMain.sendToAll(PacketBuilder.entityMove(id, entities.get(id).getLocation().x,
                    entities.get(id).getLocation().y, entities.get(id).getDirection()));
        }
    }
}
