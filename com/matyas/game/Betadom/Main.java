package com.matyas.game.Betadom;

import com.matyas.game.BetadomServer.ServerMain;

public class Main {
    public static void main(String[] args){
        if(args.length == 0){
            new MainFrame();
        }else if(args[0].equals("-server")){
            new ServerMain();
        }
    }
}
