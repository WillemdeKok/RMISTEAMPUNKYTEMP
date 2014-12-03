/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steampunkyserver;

import java.util.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 <p>
 @author Melanie
 */
public class PowerUp extends Object
{
    //************************datavelden*************************************
    private int powerUpID;
    private String name;
    private String powerUpType;
    private String objectType;
    private String description;
    private String imgURL;
    private boolean available;

    //***********************constructoren***********************************
    /**
     Constructor of PowerUp
     <p>
     @param name        Name of this powerup
     @param powerUpType        Type of this powerup
     @param description Description of this powerup
     @param available   If this Powerup is available for the player
     @param position    An Object of the Class Position which holds the Position of this PowerUp.
     @param active      A boolean that holds the current state of this PowerUp.
     @param movable     A boolean that holds the current state of this PowerUp.
     @param direction   An Object of the Class Direction which holds the direction in which this PowerUp moves.
     */
    public PowerUp(String name , String powerUpType , String description , boolean available , Position position , boolean active , boolean movable , Direction direction,Game game)
    {
        super(position , active , movable , direction,game);

        // The Name, Type and Description of the constructor can't be null or empty.
        if ((name != null) || (powerUpType != null) || (description != null))
        {
            if (!name.isEmpty() && !powerUpType.isEmpty() && !description.isEmpty())
            {
                this.powerUpID = super.getInterfaceID();
                this.name = name;
                this.powerUpType = powerUpType;
                this.description = description;

                switch (powerUpType)
                {
                    case "runspeed":
                        this.imgURL = "powerup01.png";
                        break;
                    case "torch":
                        this.imgURL = "powerup02.png";
                        break;
                    case "shield":
                        this.imgURL = "powerup03.png";
                        break;
                    case "projectile":
                        this.imgURL = "powerup05.png";
                        break;
                }
                        
                super.setImage(this.imgURL);
            }
        } else
        {
            throw new IllegalArgumentException("You can't make a PowerUp without a Name,Type or Description");
        }
    }

    //**********************methoden****************************************
    /**
     Getter of Name
     <p>
     @return a name as String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     Getter of Type
     <p>
     @return a type as String
     */
    public String getPowerUpType()
    {
        return this.powerUpType;
    }

    /**
     Getter of Description
     <p>
     @return a description as String
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     Getter of Availablity
     <p>
     @return a boolean whether its available or not
     */
    public boolean getAvailable()
    {
        return this.available;
    }

    /**
     Setter of Available
     <p>
     @param available
     */
    public void setAvailable(boolean available)
    {
        this.available = available;
    }

}
