package com.matyas.game.Betadom.util;

import com.matyas.game.Betadom.*;
import java.util.ArrayList;

public class PacketBuilder {
    //TODO: Finish networking code
    public static Packet ping(int token){
        ArrayList params = new ArrayList();
        params.add(token);
        return new Packet((byte)0x00, params);
    }
    public static Packet clientLogin(String username, String password){
        ArrayList params = new ArrayList();
        params.add(username);
        params.add(password);
        return new Packet((byte)0x01, params);
    }
    public static Packet loginReply(int pingInterval){
        ArrayList params = new ArrayList();
        params.add(pingInterval);
        return new Packet((byte)0x02, params);
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
    public static Packet entityMove(int uid, int x, int y, int rotation){
        ArrayList params = new ArrayList();
        params.add(uid);
        params.add(x);
        params.add(y);
        params.add(rotation);
        return new Packet((byte)0x05, params);
    }
    public static Packet chat(String message){
        ArrayList params = new ArrayList();
        params.add(message);
        params.add((short)0);
        return new Packet((byte)0x06, params);
    }
    public static Packet addEntity(int uid, Entity entity){
        ArrayList params = new ArrayList();
        params.add(uid);
        params.add(entity);
        return new Packet((byte)0x07, params);
    }
    public static Packet removeEntity(int uid){
        ArrayList params = new ArrayList();
        params.add(uid);
        return new Packet((byte)0x08, params);
    }
    public static Packet disconnect(String reason){
        ArrayList params = new ArrayList();
        params.add(reason);
        return new Packet((byte)0xFF, params);
    }
}
