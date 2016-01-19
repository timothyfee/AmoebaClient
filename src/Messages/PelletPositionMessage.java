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
public class PelletPositionMessage extends NetworkMessage{
    public final double x;
    public final double y;
    public final long id;
    
    public PelletPositionMessage(double x, double y, long id){
        this.x = x;
        this.y = y;
        this.id = id;
    }    
}
