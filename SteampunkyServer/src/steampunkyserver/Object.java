/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package steampunkyserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 <p>
 @author Melanie
 */
public abstract class Object
{
    //************************datavelden*************************************

    private int interfaceID = 0;
    private Position position;
    private boolean active;
    private boolean movable;
    private Direction direction;
    private Game myGame;
    //private Shape shape;
    private Image image;
    private ImageView imageview;

    //***********************constructoren***********************************
    /**
     Constructor of the SuperClass Object
     creates an abstract class object with ...
     <p>
     @param position  An object of the Class Position which holds a coordinate.
     @param Active    A Boolean which holds the current state of this Object.
     @param Movable   A boolean which holds the current state of this Object
     @param direction A Object of the Class Direction which holds the direction in which this Object is moving.
     @param game      A Object of the Class Game in which this object is placed.
     */
    public Object(Position position , boolean Active , boolean Movable , Direction direction, Game game)
    {
        this.position = position;
        this.active = Active;
        this.movable = Movable;
        this.direction = direction;
        this.myGame = game;

        if (!movable)
        {
            this.direction = null;
        }

        this.interfaceID++;
    }

    //**********************methoden****************************************
    /**
     The Getter of this Objects InterfaceID.
     <p>
     @return An int which is this objects InterfaceID.
     */
    public int getInterfaceID()
    {
        return this.interfaceID;
    }

    /**
     The Getter of this Objects PositionX.
     <p>
     @return An int with the current X p of this Object
     */
    public int getPositionX()
    {
        return this.position.getX();
    }

    /**
     The Getter of this Objects PositionY.
     <p>
     @return An int with the current Y p of this Object
     */
    public int getPositionY()
    {
        return this.position.getY();
    }

    public boolean getMove()
    {
        return this.movable;
    }
    /**
     The Getter of this Objects Position.
     <p>
     @return An Object of the Class Position which holds this Objects Position.
     */
    public Position getPosition()
    {
        return this.position;
    }
    
    public Direction getDirection()
    {
        return this.direction;
    }

    /**
     The Getter of this objects Active.
     <p>
     @return A boolean which holds the current state of this Object.
     */
    public boolean getActive()
    {
        return active;
    }

    /**
     The Setter of this Objects Position.
     <p>
     @param position A Object of the Class Position which holds the new p of this Object.
     */
    public void setPosition(Position position)
    {
        this.position = position;
        
        this.imageview.setX(position.getX()*100);
        this.imageview.setY(position.getY()*100);
        this.imageview.relocate(position.getX()*100,position.getY()*100);
    }

    /**
     The Setter of this Objects Active
     <p>
     @param active A boolean which holds the new state of this Object.
     */
    public void setActive(Boolean active)
    {
        this.active = active;
    }
    
    /**
     * The Getter of Game
     * 
     * @return An instance of the game this object is located in.
     */
    public Game getGame()
    {
        return this.myGame;
    }
    
    /**
     * The Getter of the image of the object
     * 
     * @return image
     */
    public Image getImage()
    {
        return this.image;
    }
    
    /**
     * The Getter of the imageview of the object
     * 
     * @return image
     */
    public ImageView getImageView()
    {
        return this.imageview;
    }
    
    /**
     * The Setter of the image of the object
     * @param imageurl of the object
     */
    public void setImage(String imageurl)
    {
        try
        {            
            //System.out.println(imageurl);
            //String url = "src/classes/images/" + imageurl;
            //System.out.println(url);
            this.image = new Image(Object.class.getResourceAsStream("player.png"));
            //this.image = new Image(url, 100, 100, false, true);
            this.imageview.setImage(image);
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    public void movement(Direction direction)
    {
        int nextX = this.getPositionX();
        int nextY = this.getPositionY();
        if (direction == Direction.Right)
        {
            nextX++;
        } else if(direction == Direction.Left){
            nextX--;
        } else if(direction == Direction.Up){
            nextY--;
        } else {
            nextY++;
        }
        
        if(this instanceof Projectile)
        {
            Projectile P = (Projectile)this;
            P.checkCollision(nextX, nextY);
        }
    }
    
    /**
     A Method for moving this Object
     <p>
     @param direction A Object of the Class Direction which holds the direction in which direction this Object is moving.
     */
    public void move(Direction direction)
    {
        /*boolean canMove = true;
        List<Position> allPosition = myGame.getGrid();
        Object checkObject;
        
        Position newPosition = null;
        if (movable)
            {
                if (direction == Direction.Right)
                {
                    newPosition = myGame.getPosition(this.position.getX() +1,this.position.getY());
                    if(newPosition != null)
                    {
                        if(newPosition.getX() != 0 && newPosition.getY() != 0)
                        {
                            if(this instanceof Character)
                               {
                                   Character c = (Character)this;
                                   checkObject = this.checkCollision(newPosition.getX(),newPosition.getY());
                                        if(checkObject != null)
                                        {
                                            if(checkObject instanceof Projectile)
                                            {
                                                Projectile p = (Projectile)checkObject;
                                                canMove = false;
                                                this.movable = false;
                                                p.setActive(false);
                                                c.setDead(true);
                                                Position positionP = p.getPosition();
                                                this.position.removeObject(this);
                                                positionP.removeObject(checkObject);
                                                this.getGame().getObjects().remove(p);
                                                this.getGame().getObjects().remove(this);
                                            }
                                            if(checkObject instanceof Obstacle)
                                            {
                                                canMove = false;
                                            }
                                            if(checkObject instanceof Ballista)
                                            {
                                                canMove = false;
                                            }
                                        }
                                   }
                              
                            else if(this instanceof Projectile)
                            {
                                checkObject = this.checkCollision(newPosition.getX(),newPosition.getY());
                                if(checkObject != null)
                                {
                                    if(checkObject instanceof Character)
                                    {
                                           Character c = (Character)checkObject;
                                           canMove = false;
                                           c.setDead(true);
                                           Projectile p = (Projectile)this;
                                           checkObject.movable = false;
                                           this.shape = null;
                                       
                                           p.getPosition().removeObject(p); this.getGame().getObjects().remove(p);
                                           c.getPosition().removeObject(checkObject); this.getGame().getObjects().remove(checkObject);
                                           
                                    }
                                    if(checkObject instanceof Obstacle)
                                    {
                                        if(checkObject.shape.getFill() != Color.BLACK)
                                        {
                                            this.position.removeObject(this);
                                            Obstacle o = (Obstacle)checkObject;
                                            o.setBroken(true);
                                            o.getPosition().removeObject(checkObject);
                                            
                                            this.shape = null;
                                        }
                                        else if(checkObject.shape.getFill() == Color.BLACK)
                                        {
                                            this.shape.setFill(Color.BLACK);
                                        }
                                        this.movable = false;
                                    }
                                }

                            }
                            if(canMove)
                            {
                               for(Position p :allPosition)
                               {                    
                                   if((p.getY() == newPosition.getY()) && (p.getX() == newPosition.getX()))
                                   {
                                       this.position.removeObject(this);
                                       this.setPosition(p);
                                       this.position.addObject(this);
                                   }
                               }
                            }
                        }
                    }
                    else
                    {
                        if(this instanceof Projectile)
                        {
                            this.position.removeObject(this);
                        }
                    }
                }
                else if (direction == Direction.Left)
                {
                    newPosition = myGame.getPosition(this.position.getX() -1,this.position.getY());
                    if(newPosition != null)
                    {
                        if(newPosition.getX() != 0 && newPosition.getY() != 0)
                        {
                            if(this instanceof Character)
                               { 
                                   Character c = (Character)this;
                                                                  
                                        checkObject = this.checkCollision(newPosition.getX(),newPosition.getY());
                                        if(checkObject != null)
                                        {
                                            if(checkObject instanceof Projectile)
                                            {
                                                Projectile p = (Projectile)checkObject;
                                                canMove = false;
                                                this.movable = false;
                                                p.setActive(false);
                                                c.setDead(true);
                                                Position positionP = p.getPosition();
                                                this.position.removeObject(this);
                                                positionP.removeObject(checkObject);
                                                this.getGame().getObjects().remove(p);
                                                this.getGame().getObjects().remove(this);
                                            }
                                            if(checkObject instanceof Obstacle)
                                            {
                                                canMove = false;
                                            }
                                            if(checkObject instanceof Ballista)
                                            {
                                                canMove = false;
                                            }
                                        }
                                   }
                               
                            else if(this instanceof Projectile)
                            {
                                checkObject = this.checkCollision(newPosition.getX(),newPosition.getY());
                                if(checkObject != null)
                                {
                                    if(checkObject instanceof Character)
                                    {
                                           Character c = (Character)checkObject;
                                           canMove = false;
                                           Projectile p = (Projectile)this;
                                           checkObject.movable = false;
                                           c.setDead(true);
                                           p.getPosition().removeObject(p); this.getGame().getObjects().remove(p);
                                           c.getPosition().removeObject(checkObject); this.getGame().getObjects().remove(checkObject);
                                           this.shape = null;
                                    }
                                    if(checkObject instanceof Obstacle)
                                    {
                                        if(checkObject.shape.getFill() != Color.BLACK)
                                        {
                                        Obstacle o = (Obstacle)checkObject;
                                        o.setBroken(true);
                                        Projectile p = (Projectile)this;
                                        o.getPosition().removeObject(checkObject);
                                        this.position.removeObject(this);
                                        this.shape = null;
                                        }
                                        else if(checkObject.shape.getFill() == Color.BLACK)
                                        {
                                            this.shape.setFill(Color.BLACK);
                                        }
                                        this.movable = false;
                                    }
                                }

                            }
                            if(canMove)
                            {
                               for(Position p :allPosition)
                               {                    
                                   if((p.getY() == newPosition.getY()) && (p.getX() == newPosition.getX()))
                                   {
                                       this.position.removeObject(this);
                                       this.setPosition(p);
                                       this.position.addObject(this);
                                   }
                               }
                            }
                        }
                    }
                    else
                    {
                        if(this instanceof Projectile)
                        {
                            this.position.removeObject(this);
                        }
                    }
                }
                else if (direction == Direction.Up)
                {
                    newPosition = myGame.getPosition(this.position.getX(),this.position.getY()-1);
                    if(newPosition != null)
                    {
                        if(newPosition.getX() != 0 && newPosition.getY() != 0)
                        {
                            if(this instanceof Character)
                               {
                                   Character c = (Character)this;
                                    
                                        checkObject = this.checkCollision(newPosition.getX(),newPosition.getY());
                                        if(checkObject != null)
                                        {
                                            if(checkObject instanceof Projectile)
                                            {
                                                Projectile p = (Projectile)checkObject;
                                                canMove = false;
                                                this.movable = false;
                                                c.setDead(true);
                                                p.setActive(false);
                                                Position positionP = p.getPosition();
                                                this.position.removeObject(this);
                                                positionP.removeObject(checkObject);
                                                this.getGame().getObjects().remove(p);
                                                this.getGame().getObjects().remove(this);
                                            }
                                            if(checkObject instanceof Obstacle)
                                            {
                                                canMove = false;
                                            }
                                            if(checkObject instanceof Ballista)
                                            {
                                                canMove = false;
                                            }
                                        }
                                   
                               }
                            else if(this instanceof Projectile)
                            {
                                checkObject = this.checkCollision(newPosition.getX(),newPosition.getY());
                                if(checkObject != null)
                                {
                                    if(checkObject instanceof Character)
                                    {
                                           Character c = (Character)checkObject;
                                           canMove = false;
                                           Projectile p = (Projectile)this;
                                           checkObject.movable = false;
                                           c.setDead(true);
                                           p.getPosition().removeObject(p); this.getGame().getObjects().remove(p);
                                           c.getPosition().removeObject(checkObject); this.getGame().getObjects().remove(checkObject);
                                           this.shape = null;
                                           
                                    }
                                    if(checkObject instanceof Obstacle)
                                    {
                                        if(checkObject.shape.getFill() != Color.BLACK)
                                        {
                                        Obstacle o = (Obstacle)checkObject;
                                        o.setBroken(true);
                                        Projectile p = (Projectile)this;o.getPosition().removeObject(checkObject);
                                        this.position.removeObject(this);
                                        this.shape = null;
                                        }
                                        else if(checkObject.shape.getFill() == Color.BLACK)
                                        {
                                            this.shape.setFill(Color.BLACK);
                                        }
                                        this.movable = false;
                                    }
                                }

                            }
                            if(canMove)
                            {
                               for(Position p :allPosition)
                               {                    
                                   if((p.getY() == newPosition.getY()) && (p.getX() == newPosition.getX()))
                                   {
                                       this.position.removeObject(this);
                                       this.setPosition(p);
                                       this.position.addObject(this);
                                   }
                               }
                            }
                        }
                    }
                    else
                    {
                        if(this instanceof Projectile)
                        {
                            this.position.removeObject(this);
                        }
                    }
                }
                else if(direction == Direction.Down)
                {
                    newPosition = myGame.getPosition(this.position.getX(),this.position.getY()+1);
                    if(newPosition != null)
                    {
                        if(newPosition.getX() != 0 && newPosition.getY() != 0)
                        {
                            if(this instanceof Character)
                               {
                                   Character c = (Character)this;
                                    
                                        checkObject = this.checkCollision(newPosition.getX(),newPosition.getY());
                                        if(checkObject != null)
                                        {
                                            if(checkObject instanceof Projectile)
                                            {
                                                Projectile p = (Projectile)checkObject;
                                                canMove = false;
                                                this.movable = false;
                                                c.setDead(true);
                                                p.setActive(false);
                                                Position positionP = p.getPosition();
                                                this.position.removeObject(this);
                                                positionP.removeObject(checkObject);
                                                this.getGame().getObjects().remove(p);
                                                this.getGame().getObjects().remove(this);
                                            }
                                            if(checkObject instanceof Obstacle)
                                            {
                                                canMove = false;
                                            }
                                            if(checkObject instanceof Ballista)
                                            {
                                                canMove = false;
                                            }
                                        }
                                   
                               }
                            else if(this instanceof Projectile)
                            {
                                checkObject = this.checkCollision(newPosition.getX(),newPosition.getY());
                                if(checkObject != null)
                                {
                                    if(checkObject instanceof Character)
                                    {
                                           Character c = (Character)checkObject;
                                           canMove = false;
                                           Projectile p = (Projectile)this;
                                           checkObject.movable = false;
                                           c.setDead(true);
                                           p.getPosition().removeObject(p); this.getGame().getObjects().remove(p);
                                           c.getPosition().removeObject(checkObject); this.getGame().getObjects().remove(checkObject);
                                           this.shape = null;
                                           
                                    }
                                    if(checkObject instanceof Obstacle)
                                    {
                                        if(checkObject.shape.getFill() != Color.BLACK)
                                        {
                                        Obstacle o = (Obstacle)checkObject;
                                        o.setBroken(true);
                                        Projectile p = (Projectile)this;o.getPosition().removeObject(checkObject);
                                        this.position.removeObject(this);
                                        }
                                        else if(checkObject.shape.getFill() == Color.BLACK)
                                        {
                                            this.shape.setFill(Color.BLACK);
                                        }
                                        this.movable = false;
                                    }
                                }

                            }
                            if(canMove)
                            {
                               for(Position p :allPosition)
                               {                    
                                   if((p.getY() == newPosition.getY()) && (p.getX() == newPosition.getX()))
                                   {
                                       this.position.removeObject(this);
                                       this.setPosition(p);
                                       this.position.addObject(this);
                                   }
                               }
                            }
                        }
                    }
                    else
                    {
                        if(this instanceof Projectile)
                        {
                            this.position.removeObject(this);
                        }
                    }
                }
            }
            else
            {
                System.out.println("Can't move an immovable object"+this.toString());
            }*/
        }

    /**
     An method to check if the next Position of this projectile will collide with another object.
     <p>
     @param objects An list of all object currently in the game.
     <p>
     @return Returns an object if the next p of this projectile collides with the object
     else it returns null.
     */
    public Object checkCollision(int posX,int posY)
    {
        List<Object> objects = this.myGame.getObjectsFromGrid(posX, posY);
        Object hitObject = null;
        
        if(this instanceof Character)
        {
            for (Object O : objects)
            {
                int oPosX = O.getPositionX();
                int oPosY = O.getPositionY();

                if ((posX == oPosX) && (oPosY == oPosY))
                {
                    if(O instanceof Character)
                    {
                        hitObject = null;
                    }
                    else
                    {
                        hitObject = O;
                        break;
                    }
                }
            }    
        }
        return hitObject;
    }

    
    private void PickUp(String type)
    {
        if(this instanceof Character)
        {
            Character c = (Character)this;
            int t;
            if(type.equals("Torch"))
            {              
              t = c.getTorchRange();
              c.setTorch(t++);
            }
            if(type.equals("Ballista"))
            {
                t = c.getMaxBallistas();
                c.setMaxBallistas(t++);
            }
            if(type.equals("Projectiles"))
            {
                t = c.getShots();
                c.setShots(t+4);
            }
        }
    }
}
