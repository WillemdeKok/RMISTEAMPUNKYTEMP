/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.io.Serializable;
import java.util.*;

/**
 * OK
 * <p>
 * @author Melanie
 */
public class Bot implements Serializable {

    //************************datavelden*************************************
    private final int botID;
    private int nextBotID = 1;
    private final String name;
    private int difficulty;
    private CharacterPlayer character;
    private final Game game;

    //***********************constructoren***********************************
    /**
     * Constructor of this class
     * <p>
     * @param name The name of the Bot
     * @param difficulty The difficulty of the bot
     * @param game
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
     * @return an integer with the botID
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

    public CharacterPlayer getCharacter() {
        return this.character;
    }

    /**
     * The setter of the difficulty
     * <p>
     * @param difficulty an integer with the new difficulty
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setCharacter(CharacterPlayer character) {
        this.character = character;
    }

    public void AI() {
        if (!this.character.getDead()) {
            int X = this.character.getPosition().getX();
            int Y = this.character.getPosition().getY();
            List<Position> grid = this.game.getGrid();
            List<Position> movableGrid = getMovableGrid(X, Y, grid, null);

            // <editor-fold desc="difficulty 1." defaultstate="collapsed">
            if (this.difficulty == 1) {
                int i = 0;
                ArrayList<Direction> dir = new ArrayList<>();
                for (Position P : movableGrid) {
                    if (P.getX() == X && P.getY() == Y + 1) {
                        i++;
                        dir.add(Direction.Down);
                    }
                    if (P.getX() == X + 1 && P.getY() == Y) {
                        i++;
                        dir.add(Direction.Right);
                    }
                    if (P.getX() == X && P.getY() == Y - 1) {
                        i++;
                        dir.add(Direction.Up);
                    }
                    if (P.getX() == X - 1 && P.getY() == Y) {
                        i++;
                        dir.add(Direction.Left);
                    }
                }
                if (i == 1) {

                    this.character.createBallista(Direction.Right, this.character.getSpeed());
                    this.character.move(dir.get(0));
                } else if (i != 0) {
                    Random rand = new Random();
                    int randomNum = 0 + rand.nextInt(i);
                    this.character.move(dir.get(randomNum));
                }

            }
            // </editor-fold>

            // <editor-fold desc="difficulty 2." defaultstate="collapsed">
            if (this.difficulty == 2) {

                List<Direction> possibleDirections = new ArrayList<>();
                movableGrid.stream().map((P) -> {
                    if (P.getX() == X && P.getY() == Y + 1) {
                        possibleDirections.add(Direction.Down);
                    }
                    return P;
                }).map((P) -> {
                    if (P.getX() == X + 1 && P.getY() == Y) {
                        possibleDirections.add(Direction.Right);
                    }
                    return P;
                }).map((P) -> {
                    if (P.getX() == X && P.getY() == Y - 1) {
                        possibleDirections.add(Direction.Up);
                    }
                    return P;
                }).filter((P) -> (P.getX() == X - 1 && P.getY() == Y)).forEach((_item) -> {
                    possibleDirections.add(Direction.Left);
                });
                //find threats and set preferred direction
                movableGrid.stream().filter((P) -> (P.getX() == X || P.getY() == Y)).forEach((P) -> {
                    P.getObjects().stream().filter((O) -> (O instanceof Projectile)).map((O) -> (Projectile) O).forEach((projectile) -> {
                        if (Y == P.getY() && X > P.getX() && projectile.getDirection() == Direction.Left) {
                            possibleDirections.remove(Direction.Right);
                        } else if (Y == P.getY() && X < P.getX() && projectile.getDirection() == Direction.Right) {
                            possibleDirections.remove(Direction.Left);
                        } else if (X == P.getX() && Y < P.getY() && projectile.getDirection() == Direction.Down) {
                            possibleDirections.remove(Direction.Up);
                        } else if (X == P.getX() && Y > P.getY() && projectile.getDirection() == Direction.Up) {
                            possibleDirections.remove(Direction.Down);
                        }
                    });
                });

                if (possibleDirections.size() == 1) {
                    this.character.createBallista(Direction.Right, this.character.getSpeed());
                    this.character.move(possibleDirections.get(0));
                } else if (!possibleDirections.isEmpty()) {
                    Random rand = new Random();
                    int randomNum = rand.nextInt(possibleDirections.size()) + 0;
                    this.character.move(possibleDirections.get(randomNum));
                } else {
                    this.character.createBallista(Direction.Right, this.character.getSpeed());
                }
            }
            // </editor-fold>

            // <editor-fold desc="difficulty 3." defaultstate="collapsed">
            if (difficulty == 3) {
                if (!ballistaDropped()) {
                    if (shouldIDropBallista(movableGrid)) {
                        this.character.createBallista(Direction.Right, this.character.getSpeed());
                    }
                    List<Direction> threats = getThreats(movableGrid);
                    if (threats != null) {
                        Direction dir = MoveFrom(threats, movableGrid);
                        if (dir != null) {
                            this.character.move(dir);
                        }
                    } else if (getPowerUp(movableGrid) != null) {
                        this.character.move(getPowerUp(movableGrid));
                    } else {
                        randomMove(movableGrid);
                    }

                }
            }
            // </editor-fold>
        }
    }

    private boolean isVisible(int X, int Y) {
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

    private List<Position> getMovableGrid(int X, int Y, List<Position> grid, Direction D) {
        int x = this.character.getPosition().getX();
        int y = this.character.getPosition().getY();
        int t = this.character.getTorchRange();
        List<Position> tempList = new ArrayList<>();

        if (this.character.getMove()) {
            grid.stream().map((P) -> {
                if (P.getX() == X && P.getY() == Y) {
                    tempList.add(P);
                }
                return P;
            }).map((P) -> {
                if (P.getX() == X && P.getY() == Y + 1 && D != Direction.Up) {
                    if (this.isVisible(X, Y + 1)) {
                        if (!P.getObjects().isEmpty()) {
                            P.getObjects().stream().filter((O) -> ((O instanceof Ballista) == false && (O instanceof Obstacle) == false)).forEach((_item) -> {
                                this.getMovableGrid(X, Y + 1, grid, Direction.Down).stream().filter((Pos) -> (!tempList.contains(Pos))).forEach((Pos) -> {
                                    tempList.add(Pos);
                                });
                            });
                        } else {
                            this.getMovableGrid(X, Y + 1, grid, Direction.Down).stream().filter((Pos) -> (!tempList.contains(Pos))).forEach((Pos) -> {
                                tempList.add(Pos);
                            });
                        }

                    }
                }
                return P;
            }).map((P) -> {
                if (P.getX() == X + 1 && P.getY() == Y && D != Direction.Left) {
                    if (this.isVisible(X + 1, Y)) {
                        if (!P.getObjects().isEmpty()) {
                            P.getObjects().stream().filter((O) -> ((O instanceof Ballista) == false && (O instanceof Obstacle) == false)).forEach((_item) -> {
                                this.getMovableGrid(X + 1, Y, grid, Direction.Right).stream().filter((Pos) -> (!tempList.contains(Pos))).forEach((Pos) -> {
                                    tempList.add(Pos);
                                });
                            });
                        } else {
                            this.getMovableGrid(X + 1, Y, grid, Direction.Right).stream().filter((Pos) -> (!tempList.contains(Pos))).forEach((Pos) -> {
                                tempList.add(Pos);
                            });
                        }

                    }
                }
                return P;
            }).map((P) -> {
                if (P.getX() == X && P.getY() == Y - 1 && D != Direction.Down) {
                    if (this.isVisible(X, Y - 1)) {
                        if (!P.getObjects().isEmpty()) {
                            P.getObjects().stream().filter((O) -> ((O instanceof Ballista) == false && (O instanceof Obstacle) == false)).forEach((_item) -> {
                                this.getMovableGrid(X, Y - 1, grid, Direction.Up).stream().filter((Pos) -> (!tempList.contains(Pos))).forEach((Pos) -> {
                                    tempList.add(Pos);
                                });
                            });
                        } else {
                            this.getMovableGrid(X, Y - 1, grid, Direction.Up).stream().filter((Pos) -> (!tempList.contains(Pos))).forEach((Pos) -> {
                                tempList.add(Pos);
                            });
                        }
                    }
                }
                return P;
            }).filter((P) -> (P.getX() == X - 1 && P.getY() == Y && D != Direction.Right)).filter((P) -> (this.isVisible(X - 1, Y))).forEach((P) -> {
                if (!P.getObjects().isEmpty()) {
                    P.getObjects().stream().filter((O) -> ((O instanceof Ballista) == false && (O instanceof Obstacle) == false)).forEach((_item) -> {
                        this.getMovableGrid(X - 1, Y, grid, Direction.Left).stream().filter((Pos) -> (!tempList.contains(Pos))).forEach((Pos) -> {
                            tempList.add(Pos);
                        });
                    });
                } else {
                    this.getMovableGrid(X - 1, Y, grid, Direction.Left).stream().filter((Pos) -> (!tempList.contains(Pos))).forEach((Pos) -> {
                        tempList.add(Pos);
                    });
                }
            });
        }
        return tempList;
    }

    private boolean shouldIDropBallista(List<Position> grid) {
        int movable = 0;
        boolean neighbour = false;
        int X = this.character.getPositionX();
        int Y = this.character.getPositionY();
        for (Position P : grid) {
            if (P.getX() == X && P.getY() == Y + 1) {
                if (!P.getObjects().isEmpty()) {
                    for (Object O : P.getObjects()) {
                        if (O instanceof CharacterPlayer) {
                            neighbour = true;
                        }
                    }
                }
                movable++;
            }
            if (P.getX() == X + 1 && P.getY() == Y) {
                movable++;
                if (!P.getObjects().isEmpty()) {
                    for (Object O : P.getObjects()) {
                        if (O instanceof CharacterPlayer) {
                            neighbour = true;
                        }
                    }
                }
            }
            if (P.getX() == X && P.getY() == Y - 1) {
                movable++;
                if (!P.getObjects().isEmpty()) {
                    for (Object O : P.getObjects()) {
                        if (O instanceof CharacterPlayer) {
                            neighbour = true;
                        }
                    }
                }
            }
            if (P.getX() == X - 1 && P.getY() == Y) {
                movable++;
                if (!P.getObjects().isEmpty()) {
                    for (Object O : P.getObjects()) {
                        if (O instanceof CharacterPlayer) {
                            neighbour = true;
                        }
                    }
                }
            }
        }
        return movable < 2 || neighbour;
    }

    private List<Direction> getThreats(List<Position> movableGrid) {
        //find threats and set preferred direction
        List<Direction> threatPositions = new ArrayList<>();
        int X = this.character.getPositionX();
        int Y = this.character.getPositionY();
        for (int i = 0; i <= this.character.getTorchRange(); i++) {
            if (!this.game.getPosition(X + i, Y).getObjects().isEmpty()) {
                for (Object O : this.game.getPosition(X + i, Y).getObjects()) {
                    if (O instanceof Projectile && O.getDirection() == Direction.Left) {
                        threatPositions.add(Direction.Right);
                    }
                }
            }
            if (!this.game.getPosition(X - i, Y).getObjects().isEmpty()) {
                for (Object O : this.game.getPosition(X - i, Y).getObjects()) {
                    if (O instanceof Projectile && O.getDirection() == Direction.Right) {
                        threatPositions.add(Direction.Left);
                    }
                }
            }
            if (!this.game.getPosition(X, Y + i).getObjects().isEmpty()) {
                for (Object O : this.game.getPosition(X, Y + i).getObjects()) {
                    if (O instanceof Projectile && O.getDirection() == Direction.Down) {
                        threatPositions.add(Direction.Up);
                    }
                }
            }
            if (!this.game.getPosition(X, Y - i).getObjects().isEmpty()) {
                for (Object O : this.game.getPosition(X, Y - i).getObjects()) {
                    if (O instanceof Projectile && O.getDirection() == Direction.Up) {
                        threatPositions.add(Direction.Down);
                    }
                }
            }
            if (threatPositions.size() > 0) {
                return threatPositions;
            }
        }
        return null;
    }

    private boolean ballistaDropped() {
        if (!this.game.getPosition(this.character.getPositionX(), this.character.getPositionY()).getObjects().isEmpty()) {
            if (this.game.getPosition(this.character.getPositionX(), this.character.getPositionY()).getObjects().stream().anyMatch((O) -> (O instanceof Ballista))) {
                return true;
            }
        }
        return false;
    }

    private Direction MoveFrom(List<Direction> MoveFrom, List<Position> movableGrid) {
        int X = this.character.getPositionX();
        int Y = this.character.getPositionY();
        List<Direction> availableDirections = new ArrayList<>();
        movableGrid.stream().map((P) -> {
            if (P.getX() == X && P.getY() == Y + 1) {
                availableDirections.add(Direction.Down);
            }
            return P;
        }).map((P) -> {
            if (P.getX() == X + 1 && P.getY() == Y) {
                availableDirections.add(Direction.Right);
            }
            return P;
        }).map((P) -> {
            if (P.getX() == X && P.getY() == Y - 1) {
                availableDirections.add(Direction.Up);
            }
            return P;
        }).filter((P) -> (P.getX() == X - 1 && P.getY() == Y)).forEach((_item) -> {
            availableDirections.add(Direction.Left);
        });
        if (!MoveFrom.isEmpty() && !availableDirections.isEmpty()) {
            availableDirections.removeAll(MoveFrom);
        }
        if (availableDirections.isEmpty()) {
            return null;
        } else if (availableDirections.size() == 1) {
            return availableDirections.get(1);
        } else if (availableDirections.size() == 3) {
            if (MoveFrom.get(1) == Direction.Left) {
                availableDirections.remove(Direction.Right);
                Random rand = new Random();
                int randomNum = rand.nextInt(availableDirections.size()) + 0;
                return availableDirections.get(randomNum);
            }
        } else {
            return getFleeRoute(availableDirections, movableGrid);
        }
        return null;
    }

    private Direction getFleeRoute(List<Direction> Directions, List<Position> grid) {
        int X = this.character.getPositionX();
        int Y = this.character.getPositionY();
        for (int i = 0; i < 4; i++) {
            for (Direction D : Directions) {
                if (D == Direction.Up) {
                    for (Position P : grid) {
                        if ((P.getX() == X + 1 || P.getX() == X - 1) && P.getY() == Y + i) {
                            return Direction.Up;
                        }
                    }
                }
                if (D == Direction.Down) {
                    for (Position P : grid) {
                        if ((P.getX() == X + 1 || P.getX() == X - 1) && P.getY() == Y - i) {
                            return Direction.Down;
                        }
                    }
                }
                if (D == Direction.Right) {
                    for (Position P : grid) {
                        if ((P.getY() == Y + 1 || P.getY() == Y - 1) && P.getX() == X + i) {
                            return Direction.Right;
                        }
                    }
                }
                if (D == Direction.Left) {
                    for (Position P : grid) {
                        if ((P.getY() == Y + 1 || P.getY() == Y - 1) && P.getX() == X - i) {
                            return Direction.Left;
                        }
                    }

                }
            }
        }
        return null;
    }

    private Direction getPowerUp(List<Position> movableGrid) {
        int X = this.character.getPositionX();
        int Y = this.character.getPositionY();

        for (int i = 0; i <= this.character.getTorchRange(); i++) {
            if (!this.game.getPosition(X + i, Y).getObjects().isEmpty() && movableGrid.contains(this.game.getPosition(X + i, Y))) {
                for (Object O : this.game.getPosition(X + i, Y).getObjects()) {
                    if (O instanceof PowerUp) {
                        return Direction.Right;
                    }
                }
            }
            if (!this.game.getPosition(X - i, Y).getObjects().isEmpty() && movableGrid.contains(this.game.getPosition(X - i, Y))) {
                for (Object O : this.game.getPosition(X - i, Y).getObjects()) {
                    if (O instanceof PowerUp) {
                        return Direction.Left;
                    }
                }
            }
            if (!this.game.getPosition(X, Y + i).getObjects().isEmpty() && movableGrid.contains(this.game.getPosition(X, Y + i))) {
                for (Object O : this.game.getPosition(X, Y + i).getObjects()) {
                    if (O instanceof PowerUp) {
                        return Direction.Up;
                    }
                }
            }
            if (!this.game.getPosition(X, Y - i).getObjects().isEmpty() && movableGrid.contains(this.game.getPosition(X, Y - i))) {
                for (Object O : this.game.getPosition(X, Y - i).getObjects()) {
                    if (O instanceof PowerUp) {
                        return Direction.Down;
                    }
                }
            }

        }
        return null;
    }

    private void randomMove(List<Position> movableGrid) {
        int X = this.character.getPositionX();
        int Y = this.character.getPositionY();
        ArrayList<Direction> dir = new ArrayList<>();
        movableGrid.stream().map((P) -> {
            if (P.getX() == X && P.getY() == Y + 1) {
                dir.add(Direction.Down);
            }
            return P;
        }).map((P) -> {
            if (P.getX() == X + 1 && P.getY() == Y) {
                dir.add(Direction.Right);
            }
            return P;
        }).map((P) -> {
            if (P.getX() == X && P.getY() == Y - 1) {
                dir.add(Direction.Up);
            }
            return P;
        }).filter((P) -> (P.getX() == X - 1 && P.getY() == Y)).forEach((_item) -> {
            dir.add(Direction.Left);
        });
        if (dir.size() == 1) {
            this.character.move(dir.get(0));
        } else if (!dir.isEmpty()) {
            Random rand = new Random();
            int randomNum = rand.nextInt(dir.size()) + 0;
            this.character.move(dir.get(randomNum));
        }
    }

}
