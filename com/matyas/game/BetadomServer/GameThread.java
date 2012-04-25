package com.matyas.game.BetadomServer;

import com.matyas.game.Betadom.EntityManager;
import com.matyas.game.Betadom.Map;

public class GameThread extends Thread{
    private Map map = null;
    
    public GameThread(ServerMain server, byte[][] tiles){
        ChatProcessor.getInstance().setServer(server);
        EntityManager.getInstance();
        map = new Map(tiles);
    }
    public void run(){
        EntityManager.getInstance().updateEntities();
        try{
            sleep(5);
        }catch(Exception ex) {
            BetadomLogger.log("Game thread interrupted while sleeping.");
        }
    }
}
