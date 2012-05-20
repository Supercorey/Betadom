package com.matyas.game.Betadom;

import com.matyas.game.Betadom.util.Packet;
import com.matyas.game.Betadom.util.PacketBuilder;
import java.awt.Point;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class NetworkManager extends Thread{
    private Socket socket = null;
    private ObjectInputStream input = null;
    private ObjectOutputStream output = null;
    private boolean running = true;
    private LinkedList<Packet> outputQueue = new LinkedList<Packet>();
    private GameCanvas game = null;
    
    public NetworkManager(InetAddress address, int port, GameCanvas game){
        try{
            socket = new Socket(address, port);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        }catch(Exception ex){
            System.out.println("Unable to create client socket to server.");
            System.exit(-1);
        }
        this.game = game;
        start();
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
                System.out.println("Networking thread could not read and process data from socket.");
                System.exit(-1);
            }
            try{
                if(!outputQueue.isEmpty()){
                    Packet packet = outputQueue.pop();
                    output.writeByte(packet.getPacketType());
                    for(Object param : packet.getParameters()){
                        output.writeObject(param);
                    }
                    output.flush();
                    continue;
                }
            }catch(Exception ex){
                System.out.println("Unable to write packet to output stream.");
                System.exit(-1);
            }
            try{
                Thread.sleep(5);
            }catch(Exception ex){
                System.out.println("Networking thread was interrupted while sleeping.");
            }
        }
    }
    
    public void addPacket(Packet packet){
        outputQueue.add(packet);
    }
    
    public void disconnect(){
        running = false;
        try{
            input.close();
            output.close();
            socket.close();
        }catch(Exception ex){
           System.out.println("Could not close client sockets.");
           System.exit(-1);
        }
    }
    private void processPacket(byte packetType){
        try{
            switch(packetType){
                case 0x00:
                    int token = (Integer)input.readObject();
                    addPacket(PacketBuilder.ping(token));
                    break;
                case 0x02:
                    serverPingInterval = (Integer)input.readObject();
                    break;
                case 0x05:
                    int uid1 = (Integer)input.readObject();
                    int x = (Integer)input.readObject();
                    int y = (Integer)input.readObject();
                    int rotation = (Integer)input.readObject(); 
                    Entity entity1 = EntityManager.getEntity(uid1);
                    entity1.setLocation(new Point(x,y));
                    entity1.setDirection(rotation);
                    break;
                case 0x06:
                    String text = (String)input.readObject();
                    input.readObject();
                    ChatManager.addChatLine(text);
                    break;
                case 0x07:
                    int uid2 = (Integer)input.readObject();
                    Entity entity = (Entity)input.readObject();
                    EntityManager.addEntity(entity, uid2);
                    if(playerId == -1){
                        playerId = uid2;
                        game.playerId = uid2;
                        game.gameState = GameState.GAME;
                    }
                    break;
                case 0x08:
                    int uid = (Integer)input.readObject();
                    EntityManager.removeEntity(uid);
                    break;
                case (byte)0xFF:
                    String reason = (String)input.readObject();
                    disconnect();
                    break;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Could not process packet.");
        }
    }
    
    private int serverPingInterval = 10000;
    private int playerId = -1;
    
}
