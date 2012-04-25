package com.matyas.game.BetadomServer;

import com.matyas.game.Betadom.util.Packet;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerMain extends Thread{
    private short serverPort = 21220;
    private ServerSocket serverSocket = null;
    private ConsoleCommandListener consoleListener = null;
    private GameThread gameThread = null;
    private static ArrayList<ClientHandler> users = new ArrayList<ClientHandler>();
    private boolean running = true;
    private int pingInterval = 10000;
    private byte[][] mapTiles = new byte[10000][10000];
    
    public ServerMain(){
        gameThread = new GameThread(this, mapTiles);
        gameThread.start();
        consoleListener = new ConsoleCommandListener();
        try {
            serverSocket = new ServerSocket(serverPort);
            BetadomLogger.log("Server started on port: "+serverPort);
        }catch (Exception ex) {
            BetadomLogger.log("Could not listen on port: "+serverPort);
            System.exit(-1);
        }
        start();
    }

    public void run(){
        Socket clientSocket = null;
        while(running){
            try {
                clientSocket = serverSocket.accept();
                ClientHandler newClient = new ClientHandler(clientSocket, pingInterval);
                users.add(newClient);
                newClient.start();
                BetadomLogger.log("Got new client from "+clientSocket.getInetAddress());
            }catch (Exception ex) {
                if(running){
                    BetadomLogger.log("Couldn't accept client on: "+serverPort);
                    System.exit(-1);
                }
            }
        }
    }
    
    public void stopServer(){
        running = false;
        for(ClientHandler client : users){
            client.disconnect();
        }
        try{
            serverSocket.close();
            BetadomLogger.log("Server socket closed. Shutting down...");
            BetadomLogger.close();
            System.exit(0);
        }catch(Exception ex){
            BetadomLogger.log("Unable to close server socket.");
            System.exit(-1);
        }
    }
    
    public static void sendToAll(Packet packet){
        for(ClientHandler client : users){
            client.addPacket(packet);
        }
    }
}
