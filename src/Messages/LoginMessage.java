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
public class LoginMessage extends NetworkMessage {
    public final String username;
    public final String password;
    
    public LoginMessage(String username, String password){
        this.username = username;
        this.password = password;
    }
}
