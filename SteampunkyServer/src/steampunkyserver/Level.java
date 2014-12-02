/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steampunkyserver;

/**
 OK
 <p>
 @author Melanie
 */
public class Level
{
    //************************datavelden*************************************
    private int levelID;
    private int nextLevelID = 1;
    private int height;
    private int width;
    private String texture;
    private String name;

    //***********************constructoren***********************************
    /**
     Constructor of Level
     <p>
     @param height  The height of the level
     @param width   The width of the level
     @param texture The texture to be used as background image
     @param name    The name of the level
     */
    public Level(int height , int width , String texture , String name)
    {
        this.height = height;
        this.width = width;
        this.texture = texture;
        this.name = name;

        this.levelID = this.nextLevelID;
        this.nextLevelID++;
    }

    //**********************methoden****************************************
    /**
     Getter of LevelID
     <p>
     @return an int with the levelID
     */
    public int getLevelID()
    {
        return this.levelID;
    }

    /**
     Getter of Height
     <p>
     @return an int with the height of the level
     */
    public int getHeight()
    {
        return this.height;
    }

    /**
     Getter of Width
     <p>
     @return return the width of the level
     */
    public int getWidth()
    {
        return this.width;
    }

    /**
     Getter of Texture
     <p>
     @return a String that has the location of the texture
     */
    public String getTexture()
    {
        return this.texture;
    }

    /**
     Gettter of Name
     <p>
     @return a String with the name of the level
     */
    public String getName()
    {
        return this.name;
    }

}
