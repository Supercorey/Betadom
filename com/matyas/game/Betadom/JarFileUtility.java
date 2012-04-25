package com.matyas.game.Betadom;

import java.util.Scanner;

public class JarFileUtility {
    private static final String ABSOLUTE_TO_ROOT = "/com/matyas/game/Betadom/";
    
    public static String loadTextFile(String path){
        StringBuilder contents = new StringBuilder();
        
        try {
            Scanner scanner = new Scanner(Main.class.getClass().getResourceAsStream(ABSOLUTE_TO_ROOT+path));
            String line = "";
            while (scanner.hasNextLine()){
                line = scanner.nextLine();
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return contents.toString();
    }
}
