package com.matyas.game.BetadomServer;

import com.matyas.game.Betadom.*;
import com.matyas.game.Betadom.util.*;
import java.awt.Point;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

public class ClientHandler extends Thread{
    private boolean running = true;
    private Socket clientSocket = null;
    private ObjectOutputStream output = null;
    private ObjectInputStream input = null;
    private LinkedList<Packet> outputQueue = new LinkedList<Packet>();
    
    public ClientHandler(Socket clientSocket, int pingInterval){
        this.clientSocket = clientSocket;
        this.pingInterval = pingInterval;
        try{
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        }catch(Exception ex){
            //BetadomLogger.log("Unable to open socket with "+clientSocket.getInetAddress());
            disconnect("Socket Could Not Be Opened", false);
        }
        client = new Player(new Point(2000,2000), 0, ResourceManager.getInstance().getImage("SHIP"),
                5, 5, 3, ResourceManager.getInstance().getImage("BULLET"), "Player");
        uid = ServerEntityManager.addEntity(client);
    }
    
    public void run(){
        while(running){
            try{
                if(input.available() > 0){
                    byte packetType = input.readByte();
                    processPacket(packetType);
                    if(outputQueue.isEmpty()){
                        continue;
                    }
                }
            }catch(Exception ex){
                //BetadomLogger.log("Unable to read packet from "+clientSocket.getInetAddress());
                disconnect("Broken Input Stream", true);
            }
            flushOutput();
            if(lastPing + pingInterval < System.currentTimeMillis()){
                if(pingReply){
                    pingToken = pingRnd.nextInt();
                    addPacket(PacketBuilder.ping(pingToken));
                    pingReply = false;
                    lastPing = System.currentTimeMillis();
                }else{
                    disconnect("Ping Time-Out", true);
                }
            }
            try {
                sleep(5);
            }catch(Exception ex) {
                System.out.printf("Thread handling %s was interrupted during sleep.\n", clientSocket.getInetAddress());
            }
        }
    }
    
    public void addPacket(Packet packet){
        outputQueue.add(packet);
    }
    
    public void flushOutput(){
        try{
            if(!outputQueue.isEmpty()){
                Packet packet = outputQueue.pop();
                output.writeByte(packet.getPacketType());
                for(Object param : packet.getParameters()){
                    output.writeObject(param);
                }
                output.flush();
            }
        }catch(Exception ex){
            //BetadomLogger.log("Unable to write packet to "+clientSocket.getInetAddress());
            disconnect("Broken Output Stream", false);
        }
    }
    
    public void disconnect(){
        disconnect("",true);
    }
    public void disconnect(String reason, boolean notifyClient){
        running = false;
        BetadomLogger.log(clientSocket.getInetAddress()+" disconnected: "+reason);
        if(notifyClient){
            addPacket(PacketBuilder.disconnect(reason));
            flushOutput();
        }
        try{
            input.close();
            output.close();
            clientSocket.close();
        }catch(Exception ex){
           //BetadomLogger.log("Could not close I/O to "+clientSocket.getInetAddress());
        }
    }
    
     private void processPacket(byte packetType){
         try{
            switch(packetType){
                case 0x00:
                    if(((Integer)input.readObject()) == pingToken){
                        pingReply = true;
                    }
                    break;
                case 0x01:
                    client.setUsername((String)input.readObject());
                    String password = (String)input.readObject();
                    addPacket(PacketBuilder.loginReply(pingInterval));
                    ServerMain.sendToAll(PacketBuilder.addEntity(uid, client));
                    break;
                case 0x03:
                    ServerEntityManager.moveEntity(((Boolean)input.readObject()).booleanValue(), uid);
                    break;
                case 0x04:
                    ServerEntityManager.rotateEntity(((Boolean)input.readObject()).booleanValue(), uid);
                    break;
                case 0x06:
                    String chat = (String)input.readObject();
                    input.readObject();
                    ChatProcessor.getInstance().processChat("<"+client.getUsername()+"> "+chat);
                    break;
                case (byte)0xFF:
                    String reason = (String)input.readObject();
                    disconnect(reason, false);
                    break;
            }
         }catch(Exception ex){
             BetadomLogger.log("Could not process packet: "+packetType);
             ex.printStackTrace();
         }
    }
    
    private Player client = null;
    private int uid = 0;
    
    private Random pingRnd = new Random(System.currentTimeMillis());
    private int pingInterval = 0;
    private long lastPing = 0;
    private int pingToken = 0;
    private boolean pingReply = true;
}
