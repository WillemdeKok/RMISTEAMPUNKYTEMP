/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * @author Melanie
 */
public abstract class ObjectForGame implements Serializable {
    //************************datavelden*************************************

    private int interfaceID = 0;
    private Position position;
    private boolean active;
    private boolean movable;
    private Direction direction;
    private Game myGame;
    //private Shape shape;
    private String objectType;
    private boolean canMove;

    //***********************constructoren***********************************
    /**
     * Constructor of the SuperClass Object creates an abstract class object
     * with ...
     * <p>
     * @param position An object of the Class Position which holds a coordinate.
     * @param Active A Boolean which holds the current state of this Object.
     * @param Movable A boolean which holds the current state of this Object
     * @param direction A Object of the Class Direction which holds the
     * direction in which this Object is moving.
     * @param game A Object of the Class Game in which this object is placed.
     * @param objecttype A String indicating what type of object this is.
     */
    public ObjectForGame(Position position, boolean Active, boolean Movable, Direction direction, Game game, String objecttype) {
        this.position = position;
        this.active = Active;
        this.movable = Movable;
        this.direction = direction;
        this.myGame = game;
        this.objectType = objecttype;
        if (!movable) {
            this.direction = null;
        }

        this.interfaceID++;
    }

    //**********************methoden****************************************
    /**
     * The Getter of this Objects InterfaceID.
     * <p>
     * @return An int which is this objects InterfaceID.
     */
    public int getInterfaceID() {
        return this.interfaceID;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public boolean getCanMove() {
        return this.canMove;
    }

    public void setCanMove(boolean bool) {
        this.canMove = bool;
    }
    
    public void setMovable(boolean bool){
        this.movable = bool;
    }
    public boolean getMovable() {
        return this.movable;
    }

    /**
     * The Getter of this Objects PositionX.
     * <p>
     * @return An int with the current X p of this ObjectForGame
     */
    public int getPositionX() {
        return this.position.getX();
    }

    /**
     * The Getter of this Objects PositionY.
     * <p>
     * @return An int with the current Y p of this ObjectForGame
     */
    public int getPositionY() {
        return this.position.getY();
    }

    public boolean getMove() {
        return this.movable;
    }

    /**
     * The Getter of this Objects Position.
     * <p>
     * @return An ObjectForGame of the Class Position which holds this Objects
     * Position.
     */
    public Position getPosition() {
        return this.position;
    }

    public Direction getDirection() {
        return this.direction;
    }

    /**
     * The Getter of this objects Active.
     * <p>
     * @return A boolean which holds the current state of this ObjectForGame.
     */
    public boolean getActive() {
        return active;
    }

    /**
     * The Setter of this Objects Position.
     * <p>
     * @param position A ObjectForGame of the Class Position which holds the new
     * p of this ObjectForGame.
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * The Setter of this Objects Active
     * <p>
     * @param active A boolean which holds the new state of this ObjectForGame.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * The Getter of Game
     *
     * @return An instance of the game this object is located in.
     */
    public Game getGame() {
        return this.myGame;
    }

    //<editor-fold defaultstate="collapsed" desc="movement and collision code">
    /**
     * A Method for moving this ObjectForGame
     * <p>
     * @param direction A ObjectForGame of the Class Direction which holds the
     * direction in which direction this ObjectForGame is moving.
     */
    public synchronized void move(Direction direction) {
        if (movable) {
            int nextX = this.getPositionX();
            int nextY = this.getPositionY();
            if (direction == Direction.Right) {
                nextX++;
            } else if (direction == Direction.Left) {
                nextX--;
            } else if (direction == Direction.Up) {
                nextY--;
            } else {
                nextY++;
            }
            if (this.checkCollision(nextX, nextY)) {
                this.myGame.getObjectsFromGrid(nextX, nextY).add(this);
                this.position.removeObject(this);
                this.direction = direction;
                this.setPosition(this.myGame.getPosition(nextX, nextY));
                this.canMove = false;
            }
        }
    }

    /**
     * An method to check if the next Position of this object will collide with
     * another object or wall.
     * <p>
     * @param posX the X coordinate of the position to be checked.
     * @param posY the Y coordinate of the position to be checked.
     * <p>
     * @return Returns a boolean that states if the object can move to the
     * desired position.
     */
    public synchronized Boolean checkCollision(int posX, int posY) {
        Boolean Movable = true;
        if (this.myGame.getPosition(posX, posY) != null) {
            if (!this.myGame.getObjectsFromGrid(posX, posY).isEmpty()) {
                List<ObjectForGame> objects = this.myGame.getObjectsFromGrid(posX, posY);
                //if this is a CharacterPlayer
                if (this instanceof CharacterPlayer) {
                    //for every object on the desired position
                    List<ObjectForGame> removeObjects = new ArrayList<>();
                    for (ObjectForGame O : objects) {
                        //if the ObjectForGame is a Projectile
                        if (O instanceof Projectile) {
                            //if the direction of this character is opposite to the direciton of the projectile.
                            if ((this.direction == Direction.Up && O.getDirection() == Direction.Down) || (this.direction == Direction.Right && O.getDirection() == Direction.Left) || (this.direction == Direction.Down && O.getDirection() == Direction.Up) || (this.direction == Direction.Left && O.getDirection() == Direction.Right)) {
                                removeObjects.add(O);
                                removeObjects.add(this);
                                Movable = false;
                            }
                        } else if (O instanceof Ballista || O instanceof Obstacle) {
                            Movable = false;
                        } else if (O instanceof PowerUp) {
                            PowerUp tempPowerUp = (PowerUp) O;
                            PickUp(tempPowerUp.getPowerUpType());
                            removeObjects.add(O);
                        }
                    }
                    removeObjects.stream().forEach((O) -> {
                        O.RemoveFromGame();
                    });
                    // if this is Projectile
                } else if (this instanceof Projectile) {
                    //for every object on desired position
                    List<ObjectForGame> removeObjects = new ArrayList<>();
                    for (ObjectForGame O : objects) {
                        if (O instanceof Obstacle) {
                            Obstacle tempObstacle = (Obstacle) O;
                            if (tempObstacle.getType().matches("cube")) {
                                removeObjects.add(this);
                                Movable = false;
                            } else {
                                removeObjects.add(O);
                                removeObjects.add(this);
                                Movable = false;
                            }
                        } else {
                            removeObjects.add(O);
                            removeObjects.add(this);
                            Movable = false;
                        }
                    }
                    removeObjects.stream().forEach((O) -> {
                        O.RemoveFromGame();
                    });
                }
            }
        } else {
            if (this instanceof Projectile) {
                this.RemoveFromGame();
            }
            Movable = false;
        }
        return Movable;
    }

    public void RemoveFromGame() {
        if (this instanceof CharacterPlayer) {
            CharacterPlayer C = (CharacterPlayer) this;
            C.setDead(true);
            C.setCanMove(false);
            C.setMovable(false);
        } else if (this instanceof Obstacle) {
            Obstacle O = (Obstacle) this;
            O.setBroken(true);
        } else if (this instanceof Ballista) {
            Ballista B = (Ballista) this;
            CharacterPlayer C = B.getOwner();
            C.removeBallista(B);
        }
        this.setActive(false);
        this.position.removeObject(this);
        this.myGame.getObjects().remove(this);
    }

    private void PickUp(String type) {
        if (this instanceof CharacterPlayer) {
            CharacterPlayer c = (CharacterPlayer) this;
            int t;
            if (type.equals("torch")) {
                t = c.getTorchRange();
                t++;
                c.setTorch(t);
                System.out.println("torch+" + t);
            } else if (type.equals("ballista")) {
                t = c.getMaxBallistas();
                t++;
                c.setMaxBallistas(t);
                System.out.println("ballista+" + t);
            } else if (type.equals("projectile")) {
                t = c.getShots();
                t = t + 4;
                c.setShots(t);
                System.out.println("projectiles+" + t);
            }
        }
    }
    //</editor-fold>

}
