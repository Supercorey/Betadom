package com.matyas.game.BetadomServer;

import com.matyas.game.Betadom.util.Packet;
import com.matyas.game.Betadom.util.PacketBuilder;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class ClientHandler extends Thread{
    private boolean running = true;
    private Socket clientSocket = null;
    private ObjectOutputStream output = null;
    private ObjectInputStream input = null;
    private BetadomPacketHandler protocol = new BetadomPacketHandler();
    private LinkedList<Packet> outputQueue = new LinkedList<Packet>();
    
    public ClientHandler(Socket clientSocket, int pingInterval){
        this.clientSocket = clientSocket;
        this.pingInterval = pingInterval;
        try{
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        }catch(Exception ex){
            System.out.println("Unable to open stream to client.");
            System.exit(-1);
        }
    }
    
    public void run(){
        while(running){
            try{
                if(input.available() > 0){
                    byte packetType = input.readByte();
                    ArrayList params = new ArrayList();
                    while(input.available() > 0){
                        params.add(input.readObject());
                    }
                    processPacket(new Packet(packetType, params));
                    if(outputQueue.isEmpty()){
                        continue;
                    }
                }
            }catch(Exception ex){
                System.out.println("Networking thread could not read from socket.");
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
            if(lastPing + pingInterval < System.currentTimeMillis()){
                if(pingReply){
                    pingToken = pingRnd.nextInt();
                    addPacket(PacketBuilder.ping(pingToken));
                    pingReply = false;
                    lastPing = System.currentTimeMillis();
                }else{
                    disconnect("Ping Time-Out");
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
    
    public void disconnect(){
        disconnect("");
    }
    public void disconnect(String reason){
        running = false;
        try{
            input.close();
            output.close();
            clientSocket.close();
            BetadomLogger.log(clientSocket.getInetAddress()+" disconnected: "+reason);
        }catch(Exception ex){
           System.out.println("Could not close sockets.");
           System.exit(-1);
        }
    }
    
     private void processPacket(Packet packet){
         try{
            switch(packet.getPacketType()){
                case 0x00:
                    if(((Integer)input.readObject()) == pingToken){
                        pingReply = true;
                    }
                    break;
            }
         }catch(Exception ex){
             BetadomLogger.log("Could not process packet.");
         }
    }
     
    private Random pingRnd = new Random(System.currentTimeMillis());
    private int pingInterval = 0;
    private long lastPing = 0;
    private int pingToken = 0;
    private boolean pingReply = true;
}
