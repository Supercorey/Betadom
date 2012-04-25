package com.matyas.game.BetadomServer;

import java.io.File;
import java.io.FileWriter;

public class BetadomLogger {
    private static FileWriter fileWriter = null;
    private final String LOG_NAME = "log.txt";
    
    private BetadomLogger(){
        try{
            fileWriter = new FileWriter(new File(LOG_NAME));
        }catch(Exception ex){
            System.out.println("Unable to open file for logging.");
            System.exit(-1);
        }
    }
    
    static{
        new BetadomLogger();
    }
    
    public static void log(String text){
        System.out.println("> "+text);
        if(fileWriter != null){
            try {
                fileWriter.write(text+"\n");
                fileWriter.flush();
            }catch(Exception ex) {
                System.out.println("Unable to log message.");
            }
        }
    }
    
    public static void close(){
        try{
           fileWriter.close();
           fileWriter = null;
        }catch(Exception ex){
            System.out.println("Unable to close log file.");
            System.exit(-1);
        }
    }
}
