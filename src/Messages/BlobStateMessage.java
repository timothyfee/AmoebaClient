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
public class BlobStateMessage extends NetworkMessage{
    
    public final double x;
    public final double y;
    public final double size;
    public final String color;
    public final String username;
    public final long id;
    
    public BlobStateMessage(double x, double y, double size, String color, String username, long id){
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color; 
        this.username = username;
        this.id = id;
    }
}
