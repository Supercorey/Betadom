package com.matyas.game.Betadom.util;

import java.util.ArrayList;

public class PacketBuilder {
    public static Packet ping(int token){
        ArrayList params = new ArrayList();
        params.add(token);
        return new Packet((byte)0x00, params);
    }
    public static Packet playerMove(boolean forward){
        ArrayList params = new ArrayList();
        params.add(forward);
        return new Packet((byte)0x03, params);
    }
    public static Packet playerRotate(boolean clockwise){
        ArrayList params = new ArrayList();
        params.add(clockwise);
        return new Packet((byte)0x04, params);
    }
    public static Packet chat(String message){
        ArrayList params = new ArrayList();
        params.add(message);
        params.add((short)0);
        return new Packet((byte)0x06, params);
    }
}
