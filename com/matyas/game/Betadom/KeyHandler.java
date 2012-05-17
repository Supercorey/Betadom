package com.matyas.game.Betadom;

import com.matyas.game.Betadom.util.PacketBuilder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;

public class KeyHandler implements KeyListener{
    private GameCanvas game = null;
    
    public KeyHandler(GameCanvas game){
        this.game = game;
    }
    
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            if(game.gameState == GameState.GAME){
                game.gameState = GameState.PAUSE;
            }else if(game.gameState == GameState.PAUSE){
                game.gameState = GameState.GAME;
            }
        }
        if(game.gameState == GameState.MAIN_MENU || game.gameState == GameState.ABOUT || game.gameState == GameState.OPTIONS || game.gameState == GameState.PAUSE){
            switch(e.getKeyCode()){
                case KeyEvent.VK_UP:
                    game.menuSelection -= 1;
                    if(game.menuSelection < 0) game.menuSelection = (game.gameState == GameState.MAIN_MENU?game.menuItems:game.aboutItems).length-1;
                    break;
                case KeyEvent.VK_DOWN:
                    game.menuSelection += 1;
                    if(game.menuSelection >= (game.gameState == GameState.MAIN_MENU?game.menuItems:game.aboutItems).length) game.menuSelection = 0;
                    break;
                case KeyEvent.VK_ENTER:
                    game.menuSelected = true;
                    break;
                default:
                    break;
            }
        }
        if(game.gameState == GameState.SERVER_SELECT){
            if(e.getKeyCode() == KeyEvent.VK_ENTER){
                try{
                    game.network = new NetworkManager(InetAddress.getByName(game.chatTemp), game.SERVER_PORT, game);
                }catch(Exception ex){
                    System.out.println("Unable to establish connection to server.");
                    System.exit(-1);
                }
                game.chatTemp = "";
            }else{
                game.chatTemp = game.getInput(e, game.chatTemp);
            }
        }
        if(game.gameState == GameState.GAME){
            if(game.keysDown[5]){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    game.network.addPacket(PacketBuilder.chat(game.chatTemp));
                    game.chatTemp = "";
                    game.keysDown[5] = false;
                }else{
                    game.chatTemp = game.getInput(e, game.chatTemp);
                }
            }
            switch(e.getKeyCode()){
                case KeyEvent.VK_UP:
                    game.keysDown[0] = true;
                    break;
                case KeyEvent.VK_DOWN:
                    game.keysDown[1] = true;
                    break;
                case KeyEvent.VK_LEFT:
                    game.keysDown[2] = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    game.keysDown[3] = true;
                    break;
                case KeyEvent.VK_SPACE:
                    game.keysDown[4] = true;
                    break;
                case KeyEvent.VK_T:
                    game.keysDown[5] = true;
                    break;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if(game.gameState == GameState.GAME){
             switch(e.getKeyCode()){
                case KeyEvent.VK_UP:
                    game.keysDown[0] = false;
                    break;
                case KeyEvent.VK_DOWN:
                    game.keysDown[1] = false;
                    break;
                case KeyEvent.VK_LEFT:
                    game.keysDown[2] = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    game.keysDown[3] = false;
                    break;
                case KeyEvent.VK_SPACE:
                    game.keysDown[4] = false;
                    break;
            }
        }
    }

    public void keyTyped(KeyEvent e) {}
}
