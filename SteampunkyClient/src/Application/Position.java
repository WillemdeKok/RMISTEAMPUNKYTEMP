/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 <p>
 @author Cyril
 */
public class Position implements Serializable
{
    private int x;
    private int y;
    private ArrayList<ObjectForGame> objects;

    /**
     The Constructor of Position
     <p>
     @param x An int for this Positions X value.
     @param y An int for this Positions Y value
     */
    public Position(int x , int y)
    {
        this.x = x;
        this.y = y;
        this.objects = new ArrayList<>();
    }

    /**
     The Getter of this Positions X.
     <p>
     @return the X coordinate of this instance
     */
    public int getX()
    {
        return this.x;
    }

    /**
     The Getter of this Positions Y
     <p>
     @return the Y coordinate of this instance
     */
    public int getY()
    {
        return this.y;
    }

    /**
     A Method for getting all Objects on this Position.
     <p>
     @return a list of objects in this instance
     */
    public ArrayList<ObjectForGame> getObjects()
    {
        return this.objects;
    }

    /**
     A Method that gets an object and removes that from the list of objects in this instance.
     <p>
     @param o A ObjectForGame of the Class ObjectForGame that will get removed.
     <p>
     @return true if this object is removed else return false.
     */
    public boolean removeObject(ObjectForGame o)
    {
        try
        {
            this.objects.remove(o);
            return true;
        } catch (NullPointerException e)
        {
            return false;
        }
    }

    /**
     A method that adds an object to the list of objects
     <p>
     @param o A ObjectForGame of the Class ObjectForGame that will be added.
     <p>
     @return the object that has been added.
     */
    public ObjectForGame addObject(ObjectForGame o)
    {
        if (o == null)
        {
            return null;
        } else if (this.objects.contains(o))
        {
            return o;
        } else
        {
            this.objects.add(o);
            return o;
        }
    }

    public void clearAllObjects()
    {
        ArrayList<ObjectForGame> templist = new ArrayList<>();
        for (ObjectForGame o : objects) {
            templist.add(o);
        }
        
        for (ObjectForGame o : templist) {
            objects.remove(o);
        }
    }
}
