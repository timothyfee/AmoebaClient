/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messages;

import DataTransferUnits.NetworkMessage;

/**
 *
 * @author Timothy
 */
public class ChatMessage extends NetworkMessage{
    public final String username;
    public final String text;
    
    public ChatMessage(String username, String text){
        this.username = username;
        this.text = text;
    }
}
