package com.matyas.game.Betadom.util;

import java.util.ArrayList;

public class Packet {
    private byte packetType = 0x00;
    private ArrayList parameters = new ArrayList();
    
    public Packet(byte packetType, ArrayList parameters){
        this.packetType = packetType;
        this.parameters = parameters;
    }
    
    public byte getPacketType(){
        return packetType;
    }
    
    public ArrayList getParameters(){
        return parameters;
    }
}
