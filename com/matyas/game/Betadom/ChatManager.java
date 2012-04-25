package com.matyas.game.Betadom;

import java.util.ArrayList;

public class ChatManager {
    private static final ArrayList<ChatLine> chatBuffer = new ArrayList<ChatLine>();
    
    static{
        for(int t = 0; t < 5; t++){
            chatBuffer.add(new ChatLine("", 0));
        }
    }
    
    public static void addChatLine(String chat){
        chatBuffer.add(new ChatLine(chat, (int)(System.currentTimeMillis()/1000)));
    }
    
    public static String[] getRecentChat(){
        String[] ret = new String[5];
        for(int t = 0; t < 5; t++){
            ret[t] = "";
        }
        for(int t = chatBuffer.size()-5; t < chatBuffer.size(); ++t){
            if(chatBuffer.get(t).getTimestamp() + 4000 > (System.currentTimeMillis()/1000)){
                ret[chatBuffer.size()-t] = chatBuffer.get(t).getText();
            }
        }
        return ret;
    }
    
    private static class ChatLine{
        private String chat = "";
        private int timestamp = 0;
        
        public ChatLine(String chat, int timestamp){
            this.chat = chat;
            this.timestamp = timestamp;
        }
        
        public String getText(){
            return chat;
        }
        
        public int getTimestamp(){
            return timestamp;
        }
    }
}
