package com.matyas.game.BetadomServer;

import java.util.Scanner;

public class ConsoleCommandListener extends Thread{
    private Scanner input = null;
    private boolean running = true;
    
    public ConsoleCommandListener(){
        input = new Scanner(System.in);
        start();
    }
    
    public void run(){
        while(running){
            if(input.hasNextLine()){
                ChatProcessor.getInstance().processChat(input.nextLine());
            }
        }
    }
}
