package com.matyas.game.Betadom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Properties;

public class SettingsManager {
    private static final Properties config = new Properties();
    private static final File CONFIG_FILE = new File("config.txt");
    
    public static void initialize(){
        try {
            if(!CONFIG_FILE.exists()){
                config.load(Main.class.getClass().getResourceAsStream("/com/matyas/game/Betadom/res/configdefault.properties"));
                CONFIG_FILE.createNewFile();
                FileOutputStream out = new FileOutputStream(CONFIG_FILE);
                config.store(out, "");
                out.close();
            }else{
                FileInputStream in = new FileInputStream(CONFIG_FILE);
                config.load(in);
                in.close();
            }
        } catch (Exception ex) {
            System.out.println("Could not open config file.");
            System.exit(-1);
        }
    }
    
    public static String getSetting(String setting){
        return config.getProperty(setting);
    }
    
    public static void setSetting(String setting, String value){
        config.setProperty(setting, value);
        try{
            FileOutputStream out = new FileOutputStream(CONFIG_FILE);
            config.store(out, "");
            out.close();
        }catch(Exception ex){
            System.out.println("Could not save to config file.");
        }
    }
}
