package com.matyas.game.Betadom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Properties;

public class SettingsManager {
    private static final Properties config = new Properties();
    private static final Properties DEFAULT_CONFIG = new Properties();
    private static final File CONFIG_FILE = new File("config.txt");
    
    public static void initialize(){
        try {
            DEFAULT_CONFIG.load(Main.class.getClass().getResourceAsStream("/com/matyas/game/Betadom/res/configdefault.properties"));
            if(!CONFIG_FILE.exists()){
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
        String value = config.getProperty(setting);
        if(value == null){
            value = DEFAULT_CONFIG.getProperty(setting);
            setSetting(setting,value);
        }
        return value;
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
