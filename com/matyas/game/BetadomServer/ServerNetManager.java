package com.matyas.game.BetadomServer;

import com.matyas.game.Betadom.NetworkManager;
import java.net.InetAddress;

public class ServerNetManager extends NetworkManager{
    public ServerNetManager(InetAddress address, int port){
        super(address, port);
    }
}
