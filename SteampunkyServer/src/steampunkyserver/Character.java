/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steampunkyserver;

import java.util.*;

/**
 * OK
 * <p>
 * @author Linda
 */
public class Character extends Object {

    //************************datavelden*************************************
    private int characterID;
    private int score;
    private double speed;
    private boolean dead;
    private int maxBallistas;
    private int torchLight;
    private int shots;
    private Direction direction;
    //relaties
    private List<Ballista> ballistas;
    private List<PowerUp> powerups;

    /**
     * The Constructor of Character
     * <p>
     * @param speed The speed with which the character can move.
     * @param dead A boolean for showing if the player is alive or dead
     * @param maxBallista An int of the maximum amount of ballista you can drop
     * @param torch An int of the value of visible area
     * @param position A Position which holds the position object of this
     * Character
     * @param active A boolean if the character is active or not
     * @param movable A boolean if the character can move or not
     * @param direction A Direction to which the character is moving
     * @param game A instance of game that created this instance of Character.
     */
    public Character(double speed, boolean dead, int maxBallista, int torch,
            Position position, boolean active, boolean movable, Direction direction, Game game) {
        //todo
        super(position, active, movable, direction, game, "Character");

        //Checks if the value dead is false, since a Character can't be dead when starting.
        if (dead == true) {
            throw new IllegalArgumentException("You can't have a Character that starts dead");
        }
        this.characterID = super.getInterfaceID();
        this.score = 0;
        this.speed = speed;
        this.dead = dead;
        this.maxBallistas = maxBallista;
        this.torchLight = torch;
        this.direction = direction;
        if (!movable) {
            this.direction = null;
        }
        ballistas = new ArrayList<>();
        powerups = new ArrayList<>();

    }

    //**********************methoden****************************************
    /**
     * The Getter of Character ID.
     * <p>
     * @return An int for this Characters ID.
     */
    public int getCharacterID() {
        return this.characterID;
    }

    /**
     * The Getter of Speed.
     * <p>
     * @return A double for this Characters Speed.
     */
    public double getSpeed() {
        return this.speed;
    }

    /**
     * The Getter ofDead.
     * <p>
     * @return A boolean for the state of this Character.
     */
    public boolean getDead() {
        return this.dead;
    }

    /**
     * The Getter of Score.
     * <p>
     * @return An int with the current Score of this Character.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * The Getter of TorchRange.
     * <p>
     * @return An int with the current range of the Torch, also known as the
     * Field of Vision.
     */
    public int getTorchRange() {
        return this.torchLight;
    }

    /**
     * The Getter of Direction.
     * <p>
     * @return A Direction which holds the direction : Up, Down,Left or Right.
     */
    @Override
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * The Setter of Score.
     * <p>
     * @param score An int which is the new Score of this Character.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * The Setter of Speed.
     * <p>
     * @param speed A Double which is the new speed of this Character.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * The Setter of Dead.
     * <p>
     * @param dead A Boolean which is the current state of this Character.
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * The Setter of TorchLight.
     * <p>
     * @param range An int which is the new range of this TorchLight.
     */
    public void setTorch(int range) {
        this.torchLight = range;
    }

    public int getMaxBallistas() {
        return this.maxBallistas;
    }

    public void setMaxBallistas(int nr) {
        this.maxBallistas = nr;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int nr) {
        this.shots = nr;
    }

    /**
     * The Setter of Direction
     * <p>
     * @param direction A object of the Class Direction , which holds an
     * Up,Down,Left or Right value.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * A Method for setting down a new Ballista
     * <p>
     * @param direction A Direction object to set which sides of the Ballista
     * should fire first.
     * @param projectileSpeed A Double for the speed the Projectiles this
     * Ballista shoots can move with.
     */
    public void createBallista(Direction direction, double projectileSpeed) {
        //todo
        Game game = super.getGame();
        Position ballistaPosition = game.getPosition(super.getPositionX(), super.getPositionY());
        boolean active = this.getActive();

        //Create new Ballista
        if (this.ballistas.size() < this.maxBallistas) {
            Ballista newBallista = new Ballista("Normal", shots, projectileSpeed, ballistaPosition, active, direction, super.getGame());
            this.ballistas.add(newBallista);
            Position p = super.getPosition();
            p.addObject(newBallista);
            Timer t = new Timer();
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    p.removeObject(newBallista);
                    ballistas.remove(newBallista);
                }
            }, 3000);
        }
    }

}
