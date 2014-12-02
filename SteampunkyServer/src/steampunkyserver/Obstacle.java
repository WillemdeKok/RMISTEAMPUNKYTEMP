/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steampunkyserver;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 OK
 <p>
 @author Melanie
 */
public class Obstacle extends Object
{
    //************************datavelden*************************************
    private int interfaceID;
    private int obstacleID = 1;
    private final String type;
    private boolean broken;

    ArrayList<Integer> obstacleIDs = new ArrayList<>();

    //***********************constructoren***********************************     
     /**
      * The constructor of the Class Obstacle
      * @param type A string which holds the type of the Obstacle.
      * @param broken A boolean which holds if this obstacle can be destroyed or not.
      * @param position An Object of the Class Position, which holds the position of this Obstacle.
      * @param active A boolean which holds the state of this Obstacle.
      * @param movable A boolean which holds the state of this Obstacle.
      * @param game Game in which obstacle is used
      */
    public Obstacle(String type , boolean broken , Position position , boolean active , boolean movable, Game game)
    {
        super(position , active , movable , null, game);

        interfaceID = super.getInterfaceID();
        if (interfaceID == 0)
        {
            throw new IllegalArgumentException("InterfaceID and ObstacleID can't be 0");
        }

        if (obstacleIDs.contains(obstacleID))
        {
            throw new IllegalArgumentException("Obstacle already exists.");
        }

        this.type = type;
        
        if (this.type.equals("box"))
        {      
            super.setImage("obstacle.png");
        }
        else if (this.type.equals("cube"))
        {     
            super.setImage("box01.png");
        }
        
        this.broken = broken;

        this.obstacleID = interfaceID;
        obstacleIDs.add(obstacleID);
    }

    //**********************methoden****************************************
    /**
     The Getter of this Obstacles Type.
     <p>
     @return A string which holds the type of this Obstacle.
     */
    public String getType()
    {
        return this.type;
    }

    /**
     The Getter of this Obstacles Broken.
     <p>
     @return A boolean which holds if this Obstacle can be destroyed or not.
     */
    public boolean getBroken()
    {
        return this.broken;
    }

    /**
     The Setter of this Obstacles Broken.
     <p>
     @param broken A boolean which holds the new state of this Obstacle.
     */
    public void setBroken(boolean broken)
    {
        this.broken = broken;
    }
}
