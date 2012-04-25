package com.matyas.game.Betadom;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.net.InetAddress;
import javax.swing.Timer;

public class GameCanvas extends Canvas implements Runnable{
    private Dimension size = null;
    private Timer timer = null;
    private final Color BG_COLOR = Color.BLACK;
    private final Color FONT_COLOR = Color.WHITE;
    private final Font MAIN_FONT = new Font("Monospaced", Font.PLAIN, 14);
    private boolean computationDone = false;
    private boolean doStop = false;
    
    private GameState gameState = GameState.MAIN_MENU;
    public static NetworkManager network = null;
    private int menuSelection = 0;
    private boolean menuSelected = false;
    private int menuTextLine = 0;
    private long menuTimeLast = 0;
    private final String[] menuItems = {"MENU_PLAY","MENU_OPTIONS","MENU_ABOUT","MENU_EXIT"};
    private final String[] aboutItems = {"MENU_BACK_GAME","MENU_DISCONNECT","MENU_EXIT"};
    private String[] aboutText = null;
    private String[] menuText = null;
    private boolean[] keysDown = new boolean[6]; //Up, Down, Left, Right, Space, T
    
    private long playerUpdateLast = 0;
    private String chatTemp = "";
    private Map map = new Map();
    
    public GameCanvas(){
        loadData();
        
        size = Toolkit.getDefaultToolkit().getScreenSize();
        addKeyListener(new KeyHandler());
        
        setSize(size);
        setIgnoreRepaint(true);
                
        timer = new Timer(15, new Chrono(this));
        timer.start();
    }
    
    protected synchronized void myRepaint(){
        if(!computationDone){
            return;
        }
        
        BufferStrategy strategy = getBufferStrategy();
        Graphics graphics = strategy.getDrawGraphics();
        graphics.setFont(MAIN_FONT);
        graphics.setColor(BG_COLOR);
        graphics.fillRect(0, 0, size.width, size.height);
        switch(gameState){
            case MAIN_MENU:
                graphics.drawImage(ResourceManager.getInstance().getImage("LOGO"), 0, 0, null);
                for(int t = 0; t < menuItems.length; ++t){
                    graphics.drawImage(ResourceManager.getInstance().getImage(menuItems[t]), size.width-375, t*125+25, null);
                }
                graphics.drawImage(ResourceManager.getInstance().getImage("MENU_ARROW"), size.width-425, menuSelection*125+50, this);
                graphics.setColor(FONT_COLOR);
                //TODO: Improve scrolling menu text
                drawText(menuText, graphics, 50, 400, 30, menuTextLine);
                break;
            case PAUSE:
                graphics.drawImage(ResourceManager.getInstance().getImage("LOGO"), 0, 0, null);
                for(int t = 0; t < aboutItems.length; ++t){
                    graphics.drawImage(ResourceManager.getInstance().getImage(aboutItems[t]), size.width-375, t*125+25, null);
                }
                graphics.drawImage(ResourceManager.getInstance().getImage("MENU_ARROW"), size.width-425, menuSelection*125+50, this);
                graphics.setColor(FONT_COLOR);
                break;
            case ABOUT:
                graphics.drawImage(ResourceManager.getInstance().getImage("LOGO"), 0, 0, null);
                graphics.drawImage(ResourceManager.getInstance().getImage("MENU_BACK"), size.width-375, size.height-150, null);
                graphics.drawImage(ResourceManager.getInstance().getImage("MENU_ARROW"), size.width-425, size.height-125, this);
                graphics.setColor(FONT_COLOR);
                drawText(aboutText,graphics, 800, 50, -1, 0);
                drawText(menuText, graphics, 50, 400, 30, menuTextLine);
                break;
            case OPTIONS:
                graphics.drawImage(ResourceManager.getInstance().getImage("LOGO"), 0, 0, null);
                graphics.drawImage(ResourceManager.getInstance().getImage("MENU_BACK"), size.width-375, size.height-150, null);
                graphics.drawImage(ResourceManager.getInstance().getImage("MENU_ARROW"), size.width-425, size.height-125, this);
                graphics.setColor(FONT_COLOR);
                drawText(menuText, graphics, 50, 400, 30, menuTextLine);
                //TODO: Options
                break;
            case SERVER_SELECT:
                //TODO: Server Select
                break;
            case GAME:
                map.drawMap(graphics, 
                        (short)((EntityManager.getInstance().getUser().getLocation().x)-(size.width/2)),
                        (short)((EntityManager.getInstance().getUser().getLocation().y)-(size.height/2)));
                EntityManager.getInstance().drawEntities(graphics, map.getLocation());
                graphics.setColor(FONT_COLOR);
                drawText(ChatManager.getRecentChat(), graphics, 100,size.height-300, -1, 0);
                break;
            default:
                break;
        }

        if(graphics != null) graphics.dispose();
        strategy.show();
        Toolkit.getDefaultToolkit().sync();
        computationDone = false;
    }

    public void run() {
        while(true){
            if(doStop){
                timer.stop();
                return;
            }
            if(computationDone){
                try {
                    Thread.sleep(1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
            if(gameState == GameState.MAIN_MENU || gameState == GameState.ABOUT || gameState == GameState.OPTIONS){
                if(System.currentTimeMillis() >= menuTimeLast + 300){
                    menuTextLine++;
                    if(menuTextLine == menuText.length){
                        menuTextLine -= 1;
                    }
                    menuTimeLast = System.currentTimeMillis();
                }
            }
            
            switch(gameState){
                case MAIN_MENU:
                    if(menuSelected){
                        switch(menuSelection){
                            case 0:
                                gameState = GameState.SERVER_SELECT;
                                //START TESTING CODE
                                EntityManager.getInstance().addEntity(
                                        new Player(new Point(2000,2000), 0, ResourceManager.getInstance().getImage("SHIP"), 5, 5, 3, ResourceManager.getInstance().getImage("BULLET"), true));
                                //END TESTING CODE
                                break;
                            case 1:
                                gameState = GameState.OPTIONS;
                                break;
                            case 2:
                                gameState = GameState.ABOUT;
                                break;
                            case 3:
                                System.exit(0);
                                break;
                            default:
                                break;
                        }
                        menuSelected = false;
                    }
                    break;
                case PAUSE:
                    if(menuSelected){
                        switch(menuSelection){
                            case 0:
                                gameState = GameState.GAME;
                                break;
                            case 1:
                                menuSelection = 0;
                                menuSelected = false;
                                network.disconnect();
                                gameState = GameState.MAIN_MENU;
                                break;
                            case 2:
                                System.exit(0);
                                break;
                            default:
                                break;
                        }
                        menuSelected = false;
                    }
                    break;
                case ABOUT:
                    menuSelection = 0;
                    if(menuSelected){
                        gameState = GameState.MAIN_MENU;
                        menuSelected = false;
                    }
                    break;
                case OPTIONS:
                    menuSelection = 0;
                    if(menuSelected){
                        gameState = GameState.MAIN_MENU;
                        menuSelected = false;
                    }
                    break;
                case SERVER_SELECT:
                    //START TESTING CODE
                    try{
                        network = new NetworkManager(InetAddress.getLocalHost(), 21220);
                    }catch(Exception ex){
                        System.out.println("Could not connect to localhost server.");
                        System.exit(-1);
                    }
                    gameState = GameState.GAME;
                    //END TESTING CODE
                    break;
                case GAME:
                    //TODO: Game
                    if(System.currentTimeMillis() >= playerUpdateLast + 25){
                        if(keysDown[2] && !keysDown[3]) EntityManager.getInstance().rotateUser(false);
                        else if(keysDown[3] && !keysDown[2]) EntityManager.getInstance().rotateUser(true);
                        
                        if(keysDown[0] && !keysDown[1]) EntityManager.getInstance().moveUser(true);
                        else if(keysDown[1] && !keysDown[0]) EntityManager.getInstance().moveUser(false);
                        
                        if(keysDown[4]){
                            EntityManager.getInstance().getUser().shoot();
                            keysDown[4] = false;
                        }
                        
                        playerUpdateLast = System.currentTimeMillis();
                    }
                    //TODO: REDUNDANT - MARK REMOVAL V
                    EntityManager.getInstance().updateEntities();
                    break;
                default:
                    break;
            }
            computationDone = true;
        }
    }
    
    private void loadData(){
        aboutText = JarFileUtility.loadTextFile("res/abouttext.txt").split("\n");
        menuText  = JarFileUtility.loadTextFile("res/menutext.txt") .split("\n");
    }
    
    private void drawText(String[] text, Graphics gfx, int x, int y, int numLines, int lineOffset){
        numLines = (numLines == -1 ? text.length : numLines);
        for(int t = lineOffset; t < numLines + lineOffset; ++t){
            if(t < text.length){
                gfx.drawChars(text[t].toCharArray(), 0, text[t].length(), x, y);
                y += 20;
            }
        }
    }
    
    public void stopGame(){
        doStop = true;
    }
    
    private class KeyHandler implements KeyListener{
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                if(gameState == GameState.GAME){
                    gameState = GameState.PAUSE;
                }else if(gameState == GameState.PAUSE){
                    gameState = GameState.GAME;
                }
            }
            if(gameState == GameState.MAIN_MENU || gameState == GameState.ABOUT || gameState == GameState.OPTIONS || gameState == GameState.PAUSE){
                switch(e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        menuSelection -= 1;
                        if(menuSelection < 0) menuSelection = (gameState == GameState.MAIN_MENU?menuItems:aboutItems).length-1;
                        break;
                    case KeyEvent.VK_DOWN:
                        menuSelection += 1;
                        if(menuSelection >= (gameState == GameState.MAIN_MENU?menuItems:aboutItems).length) menuSelection = 0;
                        break;
                    case KeyEvent.VK_ENTER:
                        menuSelected = true;
                        break;
                    default:
                        break;
                }
            }
            if(gameState == GameState.GAME){
                 switch(e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        keysDown[0] = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        keysDown[1] = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        keysDown[2] = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        keysDown[3] = true;
                        break;
                    case KeyEvent.VK_SPACE:
                        keysDown[4] = true;
                        break;
                    case KeyEvent.VK_T:
                        keysDown[5] = true;
                        break;
                }
            }
        }
        
        public void keyReleased(KeyEvent e) {
            if(gameState == GameState.GAME){
                 switch(e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        keysDown[0] = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        keysDown[1] = false;
                        break;
                    case KeyEvent.VK_LEFT:
                        keysDown[2] = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        keysDown[3] = false;
                        break;
                    case KeyEvent.VK_SPACE:
                        keysDown[4] = false;
                        break;
                }
            }
        }
        
        public void keyTyped(KeyEvent e) {}
    }
    
    private class Chrono implements ActionListener{
        private GameCanvas object = null;
        
        public Chrono(GameCanvas object){
            this.object = object;
        }

        public void actionPerformed(ActionEvent e) {
            object.myRepaint();
        }
    }
}