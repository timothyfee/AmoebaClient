/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messages;

import DataTransferUnits.KeyValuePair;
import DataTransferUnits.NetworkMessage;
import java.util.ArrayList;

/**
 *
 * @author Timothy
 */
public class SetBlobPropertiesMessage extends NetworkMessage{
    public final ArrayList<KeyValuePair> properties;
    
    public SetBlobPropertiesMessage(ArrayList<KeyValuePair> properties){
        this.properties = properties;
    }
}
