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
public class CurrentScoreMessage extends NetworkMessage{
    public final String text;
    
    public CurrentScoreMessage(String text){
        this.text = text;
    }
}
