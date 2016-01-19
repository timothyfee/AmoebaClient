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
public class MoveTowardCoordinatesMessage extends NetworkMessage {
    public final double x;
    public final double y;
    
    public MoveTowardCoordinatesMessage(double x, double y){
        this.x = x;
        this.y = y;
    }
}
