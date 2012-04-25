package com.matyas.game.Betadom;

import com.matyas.game.Betadom.util.Packet;
import com.matyas.game.Betadom.util.PacketBuilder;
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
    
    public NetworkManager(InetAddress address, int port){
        try{
            socket = new Socket(address, port);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        }catch(Exception ex){
            System.out.println("Unable to create client socket to server.");
            System.exit(-1);
        }
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
                case 0x06:
                    String text = (String)input.readObject();
                    input.readObject();
                    ChatManager.addChatLine(text);
                    break;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Could not process packet.");
        }
    }
    
}
