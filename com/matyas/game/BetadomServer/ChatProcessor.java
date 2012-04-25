package com.matyas.game.BetadomServer;

import com.matyas.game.Betadom.util.PacketBuilder;

public class ChatProcessor {
    private static ChatProcessor instance = new ChatProcessor();
    private ServerMain server = null;
    
    public static ChatProcessor getInstance(){
        return instance;
    }
    
    public void setServer(ServerMain server){
        this.server = server;
    }
    
    public void processChat(String chat){
        if(chat.startsWith("/")){
            String command = chat.split(" ")[0].substring(1);
            if(command.equals("stop")){
                commandStop();
            }
        }else{
            ServerMain.sendToAll(PacketBuilder.chat(chat));
        }
    }
    
    private void commandStop(){
        server.stopServer();
    }
}
