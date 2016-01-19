/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amoebaclient;

import javafx.scene.Node;

/**
 *
 * @author Timothy
 */
public class Drawable {
    public Node node;
    public final long id;
    public int cacheMisses;
    
    public Drawable(long id, Node node){
        this.id = id;
        this.node = node;
    }
}
