/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.util.*;

/**
 * OK
 * <p>
 * @author Melanie
 */
public class Bot {

    //************************datavelden*************************************
    private int botID;
    private int nextBotID = 1;
    private String name;
    private int difficulty;
    private Character character;
    private Game game;

    //***********************constructoren***********************************
    /**
     * Constructor of this class
     * <p>
     * @param name The name of the Bot
     * @param difficulty The difficulty of the bot
     */
    public Bot(String name, int difficulty, Game game) {
        this.name = name;
        this.difficulty = difficulty;
        this.botID = this.nextBotID;
        this.nextBotID++;
        this.game = game;
    }

    //**********************methoden****************************************
    /**
     * The getter of BotID
     * <p>
     * @return an int with the botID
     */
    public int getBotID() {
        return this.botID;
    }

    /**
     * Getter of name
     * <p>
     * @return a String with the name of the bot
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter of the Difficulty of the bot
     * <p>
     * @return the difficulty of the bot
     */
    public int getDifficulty() {
        return this.difficulty;
    }

    public Character getCharacter() {
        return this.character;
    }

    /**
     * The setter of the difficulty
     * <p>
     * @param difficulty an int with the new difficulty
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public void AI() {
        if(!this.character.getDead()){
            int X = this.character.getPosition().getX();
            int Y = this.character.getPosition().getY();
            List<Position> grid = this.game.getGrid();
            List<Position> movableGrid = new ArrayList<>();
//            movableGrid = getMovableGrid(X, Y, grid, null);

            // <editor-fold desc="difficulty 1." defaultstate="collapsed">
            if (this.difficulty == 1) {
                int i=0;
                ArrayList<Direction> dir = new ArrayList<>();
                for(Position P : movableGrid){
                    if(P.getX()== X && P.getY() == Y+1){
                        i++;
                        dir.add(Direction.Down);
                    }
                    if(P.getX() == X+1 && P.getY() ==Y){
                        i++;
                        dir.add(Direction.Right);
                    }
                    if(P.getX() == X && P.getY() ==Y-1){
                        i++;
                        dir.add(Direction.Up);
                    }
                    if(P.getX() == X-1 && P.getY() ==Y){
                        i++;
                        dir.add(Direction.Left);
                    }
                }
//                if(i==1){
//                    this.character.createBallista(Direction.Right,4,this.character.getSpeed());
//                    this.character.move(dir.get(0));
//                }else if(i!=0){
                Random rand = new Random();
                int randomNum = rand.nextInt(i) + 0;
                this.character.move(dir.get(randomNum));
//                }

            }
            // </editor-fold>

            if (this.difficulty == 2) {
                List<Position> threat = new ArrayList<>();
                for (Position P: movableGrid){
                    if(P.getX()== X || P.getY()==Y){
                        for(Object O: P.getObjects()){
                            if (O instanceof Projectile){
                                Projectile projectile = (Projectile) O;
                                if(X>P.getX() && projectile.getDirection() == Direction.Left){
                                    threat.add(P);
                                }else if (X<P.getX() && projectile.getDirection() == Direction.Right){
                                threat.add(P);
                                }
                                else if (Y<P.getY() && projectile.getDirection() == Direction.Down){
                                    threat.add(P);
                                }else if (Y>P.getY() && projectile.getDirection() == Direction.Up){
                                    threat.add(P);
                                }
                            }
                        }
                   }
                }
            }
        }
    }

   public boolean isVisible(int X, int Y) {
        int x = this.character.getPosition().getX();
        int y = this.character.getPosition().getY();
        int t = this.character.getTorchRange();
        //if X=x and Y is within torch range return true
        if (X == x) {
            if (y == Y) {
                return true;
            } else if ((Y >= y && Y <= (y + t)) || (Y <= y && Y >= (y - t))) {
                return true;
            }
            //if X>x or X<x but within torch range
        } else if ((X > x && X <= (x + t)) || (X < x && X >= (x - t))) {
            //if Y==y =true
            if (Y == y) {
                return true;
            }
            if (X > x) {
                //if X > x,Y is within range return true
                if ((Y > y && Y - y <= t - (X - x)) || (Y < y && y - Y <= t - (X - x))) {
                    return true;
                }
                //if X < x, Y is within range return true   
            } else {
                if ((Y > y && Y - y <= t - (x - X)) || (Y < y && y - Y <= t - (x - X))) {
                    return true;
                } 
            }
        }
        return false;
    }

    public List<Position> getDirectMovableGrid(List<Position> grid){
      int x = this.character.getPosition().getX();
        int y = this.character.getPosition().getY();
        int t = this.character.getTorchRange();
        List<Position> tempList = new ArrayList<>();        
        if(this.character.getMove())
        {
            boolean blockUp = false;
            boolean blockRight = false;
            boolean blockDown = false;
            boolean blockLeft = false;
            
            for (Position P : grid) {
                boolean breakdown = false;
                for (int i=0; i >= t ; i++){
                    if (P.getX() == x+i && P.getY()== y && !blockRight){
                        if (!P.getObjects().isEmpty()){
                            for (Object O : P.getObjects()) {
                                if ((O instanceof Ballista)==false && (O instanceof Obstacle)==false) {
                                    if(!tempList.contains(P)){
                                        tempList.add(P);
                                        breakdown = true;
                                    }    
                                }
                            if(breakdown){break;}
                            }
                        } else{
                            if(!tempList.contains(P)){
                                tempList.add(P);
                                breakdown=true;
                            }     
                        }  
                    }
                    if(breakdown){break;}     
                    if (P.getX() == x-i && P.getY() == y && !blockLeft){
                        if (!P.getObjects().isEmpty()){
                            for (Object O : P.getObjects()) {
                                if ((O instanceof Ballista)==false && (O instanceof Obstacle)==false) {
                                    if(!tempList.contains(P)){
                                        tempList.add(P);
                                        breakdown = true;
                                    }    
                                }
                            if(breakdown){break;}
                            }
                        } else{
                            if(!tempList.contains(P)){
                                tempList.add(P);
                                breakdown=true;
                            }     
                        }
                    }
                    if(breakdown){break;}
                    if (P.getX() == x && P.getY() == y+i && !blockUp){
                        if (!P.getObjects().isEmpty()){
                            for (Object O : P.getObjects()) {
                                if ((O instanceof Ballista)==false && (O instanceof Obstacle)==false) {
                                    if(!tempList.contains(P)){
                                        tempList.add(P);
                                        breakdown = true;
                                    }    
                                }
                            if(breakdown){break;}
                            }
                        } else{
                            if(!tempList.contains(P)){
                                tempList.add(P);
                                breakdown=true;
                            }     
                        }
                    }
                    if(breakdown){break;}
                    if (P.getX() == x && P.getY() == y-i && !blockDown){
                        if (!P.getObjects().isEmpty()){
                            for (Object O : P.getObjects()) {
                                if ((O instanceof Ballista)==false && (O instanceof Obstacle)==false) {
                                    if(!tempList.contains(P)){
                                        tempList.add(P);
                                        breakdown = true;
                                    }    
                                }
                                if(breakdown){break;}
                            }
                        } else{
                            if(!tempList.contains(P)){
                                tempList.add(P);
                                breakdown=true;
                            }     
                        }
                    }   
                }
            }
        }
        return tempList;
    }
   
    public List<Position> getMovableGrid(int X, int Y, List<Position> grid, Direction D) {
        int x = this.character.getPosition().getX();
        int y = this.character.getPosition().getY();
        int t = this.character.getTorchRange();
        List<Position> tempList = new ArrayList<>();
        
        if(this.character.getMove())
        {
        for (Position P : grid) {
            if (P.getX() == X && P.getY() == Y){
                tempList.add(P);
            }
            if (P.getX() == X && P.getY() == Y + 1 && D != Direction.Up ) {
                if (this.isVisible(X, Y + 1)) {
                    if (P.getObjects().size()!=0){
                        for (Object O : P.getObjects()) {
                            if ((O instanceof Ballista)==false && (O instanceof Obstacle)==false) {
                                for(Position Pos : this.getMovableGrid(X, Y + 1, grid,Direction.Down)){
                                    if(!tempList.contains(Pos)){
                                        tempList.add(Pos);
                                    }     
                                }  
                            }
                        }
                    } else{
                        for(Position Pos : this.getMovableGrid(X, Y + 1, grid,Direction.Down)){
                            if(!tempList.contains(Pos)){
                                tempList.add(Pos);
                            }     
                        }  
                    }
                    
                }
            }
            if (P.getX() == X + 1 && P.getY() == Y && D != Direction.Left) {
                if (this.isVisible(X + 1, Y)) {
                    if (!P.getObjects().isEmpty()){
                        for (Object O : P.getObjects()) {
                            if ((O instanceof Ballista)==false && (O instanceof Obstacle)==false) {
                                for(Position Pos :this.getMovableGrid(X + 1, Y, grid,Direction.Right)){
                                    if(!tempList.contains(Pos)){
                                        tempList.add(Pos);
                                    }
                                }
                            }
                        }
                    } else {
                        for(Position Pos :this.getMovableGrid(X + 1, Y, grid,Direction.Right)){
                            if(!tempList.contains(Pos)){
                            tempList.add(Pos);
                            }
                        }
                    }
                    
                }
            }
            if (P.getX() == X && P.getY() == Y - 1 && D != Direction.Down) {
                if (this.isVisible(X, Y - 1)) {
                    if(P.getObjects().size()!=0){
                        for (Object O : P.getObjects()) {
                            if ((O instanceof Ballista)==false && (O instanceof Obstacle)==false) {
                                for(Position Pos:this.getMovableGrid(X, Y - 1, grid,Direction.Up)){
                                    if(!tempList.contains(Pos)){
                                        tempList.add(Pos);
                                    }
                                }
                            }
                        }
                    }else {
                        for(Position Pos:this.getMovableGrid(X, Y - 1, grid,Direction.Up)){
                            if(!tempList.contains(Pos)){
                                tempList.add(Pos);
                            }
                        }
                    }
                }
            }
            if (P.getX() == X - 1 && P.getY() == Y && D != Direction.Right) {
                if (this.isVisible(X - 1, Y)) {
                    if(P.getObjects().size()!=0){
                        for (Object O : P.getObjects()) {
                            if ((O instanceof Ballista)==false && (O instanceof Obstacle)==false) {
                                for(Position Pos :this.getMovableGrid(X - 1, Y, grid,Direction.Left)){
                                    if(!tempList.contains(Pos)){
                                        tempList.add(Pos);
                                    }
                                }
                            }
                        }
                    } else {
                        for(Position Pos :this.getMovableGrid(X - 1, Y, grid,Direction.Left)){
                            if(!tempList.contains(Pos)){
                                tempList.add(Pos);
                            }
                        }
                    }
                }
            }
        }
        }
        return tempList;
    }
}