package com.matyas.game.Betadom;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class MainFrame extends JFrame{
    public MainFrame(){
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        ResourceManager.getInstance().loadFromIndex();
        TileDataStore.addTile((byte)0, ResourceManager.getInstance().getImage("TILE_GRID"));
        
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        getContentPane().setCursor(blankCursor);
        
        setTitle("Betadom - Multiplayer Top Down Shooter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        GameCanvas canvas = new GameCanvas();
        
        add(canvas);
        
        setUndecorated(true);
        setBounds(0,0, size.width, size.height);
        setAlwaysOnTop(true);
        setSize(size);
        setResizable(false);
        setVisible(true);
        
        try{
            Robot robot = new Robot();
            robot.mousePress(MouseEvent.BUTTON1_MASK);
            robot.mouseRelease(MouseEvent.BUTTON1_MASK);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        canvas.createBufferStrategy(2);
        
        Thread thread = new Thread(canvas);
        thread.start();
    } 
}
