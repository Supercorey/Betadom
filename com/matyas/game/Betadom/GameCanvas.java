package com.matyas.game.Betadom;

import com.matyas.game.Betadom.util.PacketBuilder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import javax.swing.Timer;

public class GameCanvas extends Canvas implements Runnable{
    //TODO: Modularize code
    private Dimension size = null;
    private Timer timer = null;
    private final Color BG_COLOR = Color.BLACK;
    private final Color FONT_COLOR = Color.WHITE;
    private final Font MAIN_FONT = new Font("Monospaced", Font.PLAIN, 14);
    protected final int SERVER_PORT = 21220;
    private boolean computationDone = false;
    private boolean doStop = false;
    
    protected GameState gameState = GameState.MAIN_MENU;
    public static NetworkManager network = null;
    protected int menuSelection = 0;
    protected boolean menuSelected = false;
    private int menuTextLine = 0;
    private long menuTimeLast = 0;
    protected final String[] menuItems = {"MENU_PLAY","MENU_OPTIONS","MENU_ABOUT","MENU_EXIT"};
    protected final String[] aboutItems = {"MENU_BACK_GAME","MENU_DISCONNECT","MENU_EXIT"};
    private String[] aboutText = null;
    private String[] menuText = null;
    protected boolean[] keysDown = new boolean[6]; //Up, Down, Left, Right, Space, T
    
    private long playerUpdateLast = 0;
    private boolean loginSent = false;
    protected String chatTemp = "";
    private Map map = new Map();
    protected int playerId = -1;
    
    public GameCanvas(){
        loadData();
        SettingsManager.initialize();
        
        size = Toolkit.getDefaultToolkit().getScreenSize();
        addKeyListener(new KeyHandler(this));
        
        setSize(size);
        setIgnoreRepaint(true);
                
        map.initializeGraphics();
        
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
                graphics.setColor(FONT_COLOR);
                graphics.drawChars("Enter Server Address: ".toCharArray(), 0, 20, size.width/2-200, size.height/2-50);
                graphics.drawChars(chatTemp.toCharArray(), 0, chatTemp.length(),size.width/2-200,size.height/2);
                break;
            case GAME:
                map.drawMap(graphics, 
                        (short)((EntityManager.getEntityById(playerId).getLocation().x)-(size.width/2)),
                        (short)((EntityManager.getEntityById(playerId).getLocation().y)-(size.height/2)));
                EntityManager.drawEntities(graphics, map.getLocation());
                graphics.setColor(FONT_COLOR);
                drawText(ChatManager.getRecentChat(), graphics, 100, size.height-300, -1, 0);
                graphics.drawChars(chatTemp.toCharArray(), 0, chatTemp.length(), 100, size.height-350);
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
                                //TODO: Move player entity creation serverside
                                //playerId = EntityManager.addEntity(
                                //        new Player(new Point(2000,2000), 0, ResourceManager.getInstance().getImage("SHIP"), 5, 5, 3, ResourceManager.getInstance().getImage("BULLET"), true));
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
                    if(network != null && !loginSent){
                        network.addPacket(PacketBuilder.clientLogin(SettingsManager.getSetting("username"), " "));
                        loginSent = true;
                    }
                    break;
                case GAME:
                    //TODO: Game
                    if(System.currentTimeMillis() >= playerUpdateLast + 25){
                        if(keysDown[2] && !keysDown[3]) {
                            EntityManager.rotateEntity(false,playerId);
                            network.addPacket(PacketBuilder.playerRotate(false));
                        }else if(keysDown[3] && !keysDown[2]) {
                            EntityManager.rotateEntity(true,playerId);
                            network.addPacket(PacketBuilder.playerRotate(true));
                        }
                        
                        if(keysDown[0] && !keysDown[1]) {
                            EntityManager.moveEntity(true,playerId);
                            network.addPacket(PacketBuilder.playerMove(false));
                        }
                        else if(keysDown[1] && !keysDown[0]) {
                            EntityManager.moveEntity(false,playerId);
                            network.addPacket(PacketBuilder.playerMove(false));
                        }
                        
                        if(keysDown[4]){
                            ((Player)EntityManager.getEntityById(playerId)).shoot();
                            keysDown[4] = false;
                        }
                        
                        playerUpdateLast = System.currentTimeMillis();
                    }
                    //TODO: REDUNDANT - MARK REMOVAL V
                    EntityManager.updateEntities();
                    //END BLOCK
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
    
    protected String getInput(KeyEvent e, String buffer){
        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
            buffer = buffer.substring(0, (buffer.length()-1) > 0 ? (buffer.length()-1) : 0);
        }else if(e.getKeyCode() != KeyEvent.VK_SHIFT){
            buffer += e.getKeyChar(); 
        }
        return buffer;
    }
    
    public void stopGame(){
        doStop = true;
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