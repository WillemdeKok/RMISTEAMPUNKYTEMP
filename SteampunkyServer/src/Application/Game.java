/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Melanie
 */
public class Game implements IGame, Serializable {

    //************************datavelden*************************************
    private int heightPixels;
    private int heightCubes;
    private int widthPixels;
    private int widthCubes;

    private int botDifficulty;
    private transient Timer timer;
    private transient TimerTask task;
    private double currentTime;
    private double totalTime;
    private Timer gameTickTimer;
    static boolean isRunning = false;

    private int totalRounds;
    private int currentRound;
    private boolean gameEnd;
    private int currentLevel;

    private double boxStartTime;
    private boolean fillUp;

    private ArrayList<Position> grid;
    private ArrayList<ObjectForGame> objects;
    private ArrayList<IUser> players;
    private ArrayList<Bot> bots;
    private ArrayList<CharacterPlayer> characters;

    //***********************constructoren***********************************
    /**
     * Constructor of Game
     *
     * @param height Uneven number of cubes, minimum of 9
     * @param width Uneven number of cubes, minimum of 9
     * @param timelimit The max amount of time a game can last in seconds.
     * @param botDifficulty The difficulty of the bots.
     * @param rounds The number of rounds that can be played.
     * @param level
     */
    public Game(int height, int width, double timelimit, int botDifficulty, int rounds, int level) {
        if ((height % 2 == 1 && width % 2 == 1) && (height >= 9 && width >= 9)) {
            this.heightCubes = height;
            this.heightPixels = (height * 100) + 200;
            this.widthCubes = width;
            this.widthPixels = (width * 100) + 200;
            this.objects = new ArrayList<>();

            this.botDifficulty = botDifficulty;
            this.totalTime = timelimit;
            this.currentTime = 0;
            this.totalRounds = rounds;
            this.currentLevel = level;
            this.currentRound = 1;
            this.boxStartTime = 0;
            this.fillUp = false;

            this.grid = new ArrayList<>();
            int row = 1;
            int col = 1;

            //<editor-fold defaultstate="collapsed" desc="Initiate Grid">
            while (row <= this.widthCubes) {
                col = 1;
                while (col <= this.heightCubes) {
                    Position p = new Position(col, row);
                    this.grid.add(p);
                    col++;
                }
                row++;
            }
            //</editor-fold>

            this.objects = new ArrayList<>();
            this.players = new ArrayList<>();
            this.bots = new ArrayList<>();
            this.characters = new ArrayList<>();

            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    setCurrentTime();
                }
            };
            timer.scheduleAtFixedRate(task, 0, 1000);
        } else {
            throw new IllegalArgumentException();
        }
    }

    //**********************methoden****************************************
    /**
     * Getter of height in pixels
     *
     * @return height in pixels
     */
    @Override
    public int getHeightPixels() {
        return this.heightPixels;
    }

    public CharacterPlayer getPlayerCharacter(String player) throws RemoteException {
        for (IUser I : this.players) {
            if (I.getUsername().matches(player)) {
                return this.characters.get(this.players.indexOf(I));
            }
        }
        return null;
    }

    public int getCharacterNumber(CharacterPlayer C) {
        return this.characters.indexOf(C);
    }

    /**
     * Getter of height in cubes
     *
     * @return height in cubes
     */
    @Override
    public int getHeightCubes() {
        return this.heightCubes;
    }

    /**
     * Getter of width in pixels
     *
     * @return width in pixels
     */
    @Override
    public int getWidthPixels() {
        return this.widthPixels;
    }

    /**
     * Getter of width in cubes
     *
     * @return width in cubes
     */
    @Override
    public int getWidthCubes() {
        return this.widthCubes;
    }

    /**
     * Getter of objects on the position with coordinates (x,y).
     *
     * @param x the x coordinate of the position.
     * @param y the y coordinate of the position.
     * @return the list of objects on the position(x,y), if there are no objects
     * returns null.
     */
    public ArrayList<ObjectForGame> getObjectsFromGrid(int x, int y) {
        for (Position p : grid) {
            if (p.getX() == x && p.getY() == y) {
                return p.getObjects();
            }
        }

        return null;
    }

    /**
     * Getter of position from grid with coordinates
     *
     * @param x the x coordinate of the position.
     * @param y the y coordinate of the position.
     * @return position with coordinates
     */
    public Position getPosition(int x, int y) {
        for (Position p : grid) {
            if (p.getX() == x && p.getY() == y) {
                return p;
            }
        }

        return null;
    }

    /**
     * Getter of grid
     *
     * @return grid; list of positions
     */
    public ArrayList<Position> getGrid() {
        return this.grid;
    }

    /**
     * Getter of botDifficulty
     *
     * @return an int representing the difficulty level of the bots
     */
    @Override
    public int getBotDifficulty() {
        return this.botDifficulty;
    }

    /**
     * Getter of TotalRounds
     *
     * @return an int with the amount of rounds
     */
    @Override
    public int getTotalRounds() {
        return this.totalRounds;
    }

    /**
     * Getter of CurrentRound
     *
     * @return an int with the value of current round
     */
    @Override
    public int getCurrentRound() {
        return this.currentRound;
    }

    /**
     * Getter of TotalTime
     *
     * @return a double with the value of the maximum time per round
     */
    @Override
    public double getTotalTime() {
        return this.totalTime;
    }

    /**
     * Getter of CurrentTime
     *
     * @return a double with the value of the current time
     */
    @Override
    public double getCurrentTime() {
        return this.currentTime;
    }

    /**
     * Getter of Characters
     *
     * @return a list of objects with the type CharacterPlayer
     */
    public ArrayList<ObjectForGame> getObjects() {
        return this.objects;
    }

    public ObjectForGame getCharacter() {
        return this.characters.get(0);
    }

    /**
     * Getter of current level
     *
     * @return current level
     */
    @Override
    public int getCurrentLevel() {
        return this.currentLevel;
    }

    /**
     * Getter of boolean game end
     *
     * @return if game ended
     */
    @Override
    public boolean getGameEnd() {
        return this.gameEnd;
    }

    /**
     * Setter of botDifficulty
     *
     * @param difficulty The difficuly of the bots
     * @return a boolean whether raising or lowering difficulty was succesfull
     */
    @Override
    public boolean setBotDifficulty(int difficulty) {
        this.botDifficulty = difficulty;

        //Check if this botDifficulty isn't to low or to high???
        return false;
    }

    /**
     * Add player to game
     *
     * @param player
     */
    @Override
    public void addPlayer(IUser player) {
        this.players.add(player);
    }

    /**
     * Add object to game
     *
     * @param object
     */
    public void addObject(ObjectForGame object) {
        this.objects.add(object);
    }

    /**
     * Generates middle cubes which can't be destroyed
     *
     * @return list of cubes
     */
    public ArrayList<ObjectForGame> getCubes() {
        //field op positie 200x200 pixels
        ArrayList<ObjectForGame> cubes = new ArrayList<>();
        int row = 2;
        int col = 2;

        while (row < this.heightCubes) {
            col = 2;

            while (col < this.widthCubes) {
                Position p = getPosition(col, row);
                ObjectForGame ob = new Obstacle("cube", false, p, true, false, this);
                p.addObject(ob);
                cubes.add(ob);
                col += 2;
            }

            row += 2;
        }

        return cubes;
    }

    /**
     * Generates boxes which ca be destroyed by players
     *
     * @return list of boxes
     */
    public ArrayList<ObjectForGame> getBoxes() {
        ArrayList<ObjectForGame> boxes = new ArrayList<>();
        int row = 1;
        int col = 1;

        //bigger field means more cubes
        double perc = 0.4;

        if (this.heightCubes > 15 || this.widthCubes > 15) {
            perc = 0.5;
        }

        //first boxes around player
        Position p = getPosition(3, 1);
        ObjectForGame ob = new Obstacle("box", false, p, true, false, this);
        boxes.add(ob);
        p = getPosition(1, 3);
        ob = new Obstacle("box", false, p, true, false, this);
        boxes.add(ob);

        p = getPosition(this.widthCubes - 2, 1);
        ob = new Obstacle("box", false, p, true, false, this);
        boxes.add(ob);
        p = getPosition(this.widthCubes, 3);
        ob = new Obstacle("box", false, p, true, false, this);
        boxes.add(ob);

        p = getPosition(1, this.heightCubes - 2);
        ob = new Obstacle("box", false, p, true, false, this);
        boxes.add(ob);
        p = getPosition(3, this.heightCubes);
        ob = new Obstacle("box", false, p, true, false, this);
        boxes.add(ob);

        p = getPosition(this.widthCubes - 2, this.heightCubes);
        ob = new Obstacle("box", false, p, true, false, this);
        boxes.add(ob);
        p = getPosition(this.widthCubes, this.heightCubes - 2);
        ob = new Obstacle("box", false, p, true, false, this);
        boxes.add(ob);

        //fill rest of field with random boxes
        while (row <= this.heightCubes) {
            col = 1;

            while (col <= this.widthCubes) {
                //can't place in the corners
                if (((row == 1 && col > 3 && col < (this.widthCubes - 2))
                        || (row == 2 && col > 2 && col < (this.widthCubes - 1))
                        || (row == 3 && col > 1 && col < this.widthCubes)
                        || (row == (this.heightCubes - 2) && col > 1 && col < this.widthCubes)
                        || (row == (this.heightCubes - 1) && col > 2 && col < (this.widthCubes - 1))
                        || (row == this.heightCubes && col > 3 && col < (this.widthCubes - 2))
                        || (row > 3 && row < (this.heightCubes - 2)))
                        && ((row % 2 == 1 && col % 2 == 0) || (row % 2 == 0 && col % 2 == 1) || (row % 2 == 1 && col % 2 == 1))) {

                    //place boxes random if random bool returns true
                    if (getRandomBool(perc)) {
                        p = getPosition(col, row);
                        ob = new Obstacle("box", false, p, true, false, this);
                        boxes.add(ob);
                    }
                }

                col++;
            }

            row++;
        }

        return boxes;
    }

    @Override
    public boolean placeFillupBoxes() {
        int round;
        int maxRounds = 0;
        int x = 1;
        int minWidth = 1;
        int maxWidth = this.widthCubes;
        int y = 1;
        int minHeight = 1;
        int maxHeight = this.widthCubes;

        if (maxWidth > maxHeight) {
            maxRounds = (maxHeight + 1) / 2;
        } else {
            maxRounds = (maxWidth + 1) / 2;
        }

        //place box every five seconds around field
        if ((this.getCurrentTime() - this.boxStartTime) % 5 == 0) {
            for (round = 1; round < maxRounds; round++) {
                while (x <= maxWidth) {
                    Position p = getPosition(x, y);
                    ObjectForGame ob = new Obstacle("cube", false, p, true, false, this);
                    this.objects.add(ob);
                    x++;
                }

                minHeight++;

                while (y <= maxHeight) {
                    Position p = getPosition(x, y);
                    ObjectForGame ob = new Obstacle("cube", false, p, true, false, this);
                    this.objects.add(ob);
                    y++;
                }

                maxWidth--;

                while (x >= minWidth) {
                    Position p = getPosition(x, y);
                    ObjectForGame ob = new Obstacle("cube", false, p, true, false, this);
                    this.objects.add(ob);
                    x--;
                }

                maxHeight--;

                while (y >= minHeight) {
                    Position p = getPosition(x, y);
                    ObjectForGame ob = new Obstacle("cube", false, p, true, false, this);
                    this.objects.add(ob);
                    y--;
                }

                minWidth--;

                return true;
            }
        }

        //When field is full of boxes, set game end
        this.setGameEnd();
        return false;
    }

    /**
     * Place powerup on random position
     *
     * @return if powerup is placed
     */
    @Override
    public boolean placeRandomPowerup() {
        Random rX = new Random();
        int col = rX.nextInt(this.widthCubes) + 1;
        Random rY = new Random();
        int row = rY.nextInt(this.heightCubes) + 1;
        Position p = getPosition(col, row);

        //get random type
        Random rType = new Random();
        int intType = rType.nextInt(4);
        String[] types = new String[4];
        types[0] = "runspeed";
        types[1] = "torch";
        types[2] = "shield";
        types[3] = "projectile";

        if (p.getObjects().size() <= 0) {
            if (((row == 1 && col > 3 && col < (this.heightCubes - 2))
                    || (row == 2 && col > 2 && col < (this.heightCubes - 1))
                    || (row == 3 && col > 1 && col < this.heightCubes)
                    || (row == (this.widthCubes - 2) && col > 1 && col < this.heightCubes)
                    || (row == (this.widthCubes - 1) && col > 2 && col < (this.heightCubes - 1))
                    || (row == this.widthCubes && col > 3 && col < (this.heightCubes - 2))
                    || (row > 3 && row < (this.widthCubes - 2)))
                    && ((row % 2 == 1 && col % 2 == 0) || (row % 2 == 0 && col % 2 == 1) || (row % 2 == 1 && col % 2 == 1))) {
                //place boxes random
                ObjectForGame ob = new PowerUp("test", types[intType], "test desc", true, p, true, false, Direction.Right, this);
                this.objects.add(ob);
                return true;
            }
        }

        return false;
    }

    /**
     * Getter of random boolean
     *
     * @param perc 0 < perc < 1
     * @return random boolean
     */
    @Override
    public boolean getRandomBool(double perc) {
        if (perc > 0 && perc < 1) {
            Random r = new Random();
            double b = r.nextDouble();

            return b <= perc;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Set object into the grid
     *
     * @param object object to be put into the grid
     */
    public void setObjectInGrid(ObjectForGame object) {
        this.grid.stream().filter((p) -> (p.getX() == object.getPositionX() && p.getY() == object.getPositionY())).forEach((p) -> {
            p.addObject(object);
        });
    }

    /**
     * Setter of current time in seconds
     */
    @Override
    public void setCurrentTime() {
        this.currentTime++;
    }

    /**
     * Set end of level and checks if there are more levels
     *
     * @return If total game is ended, so when there are no more rounds
     */
    @Override
    public boolean setGameEnd() {
        this.gameEnd = true;

        //Ga naar de volgende ronde mits die er is
        if (this.currentRound < this.totalRounds) {
            this.currentRound++;
            return false;
        }

        return true;
    }

    /**
     * Start round of game
     */
    @Override
    public void startRound() {
        /**
         * this creates positions and puts them in the grid.
         */
        for (Position p : this.grid) {
            p.clearAllObjects();
        }

        if (getCurrentRound() == 1) {
            setupGame();
        } else {
            setupLevel();
        }
        this.GameTimer();
    }

    /**
     * Update method of the game
     */
    @Override
    public synchronized void updateGame() {
        this.objects.clear();
        ArrayList<CharacterPlayer> tempCharacters = new ArrayList();
        ArrayList<Projectile> tempProjectiles = new ArrayList();
        ArrayList<PowerUp> tempPowerUps = new ArrayList();

        this.grid.stream().forEach((p) -> {
            p.getObjects().stream().map((o) -> {
                if (!this.objects.contains(o)) {
                    this.objects.add(o);
                }
                return o;
            }).map((o) -> {
                if (o instanceof CharacterPlayer && !tempCharacters.contains((CharacterPlayer) o)) {
                    tempCharacters.add((CharacterPlayer) o);
                }
                return o;
            }).map((o) -> {
                if (o instanceof Projectile && !tempProjectiles.contains((Projectile) o)) {
                    tempProjectiles.add((Projectile) o);
                }
                return o;
            }).filter((o) -> (o instanceof PowerUp && !tempPowerUps.contains((PowerUp) o))).forEach((o) -> {
                tempPowerUps.add((PowerUp) o);
            });
        });
        for (CharacterPlayer C : tempCharacters) {
            C.setCanMove(true);
        }
        tempProjectiles.stream().forEach((P) -> {
            P.move(P.getDirection());
        });
        bots.stream().forEach((B) -> {
            if (!B.getCharacter().getDead()) {
                B.AI();// starten van thread 
            } else {
                System.out.println("dead");
            }
        });
        int dead = 0;

        //Check if characters are dead
        dead = this.characters.stream().filter((c) -> (c.getDead())).map((_item) -> 1).reduce(dead, Integer::sum);

        //Stop game if all characters are dead
        if (dead == 3) {
            setGameEnd();
        }

        int count = 0;

        if (this.fillUp) {
            //Start methode placeFillupBoxes() one time
            if (count == 0) {
                placeFillupBoxes();
                count++;
            }
        }

        boolean ended = getGameEnd();

        if (!ended) {
            if (this.currentTime >= this.totalTime) {
                //Add boxes in circle in level
                this.fillUp = true;
                this.boxStartTime = getCurrentTime();
            }
        } else {
            boolean lastLevel = setGameEnd();

            if (!lastLevel) {
                startRound();
            } else {
                //einde van game
            }
        }
    }

    /**
     * Setup at begin of the game
     */
    @Override
    public void setupGame() {
        String[] namen = new String[4];
        namen[0] = "Hans";
        namen[1] = "Marcel";
        namen[2] = "Andre";
        namen[3] = "Nico";

        Position[] positions = new Position[4];
        positions[0] = getPosition(1, this.heightCubes);
        positions[1] = getPosition(1, 1);
        positions[2] = getPosition(this.widthCubes, 1);
        positions[3] = getPosition(this.widthCubes, this.heightCubes);

        Direction[] directions = new Direction[4];
        directions[0] = Direction.Right;
        directions[1] = Direction.Down;
        directions[2] = Direction.Left;
        directions[3] = Direction.Up;

        this.objects.addAll(this.getBoxes());
        this.objects.addAll(this.getCubes());

        int i = 0;

        //Add character to player
        for (IUser p : this.players) {
            CharacterPlayer c = new CharacterPlayer(1, false, 1, 3, positions[i], true, true, directions[i], this);
            try {
                p.setCharacter(c);
            } catch (RemoteException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.characters.add(c);
            this.objects.add(c);
            i++;
        }

        int count = this.players.size();

        //Add bots for missing players
        for (int k = i; k < 4; k++) {
            if (k >= count) {
                //Bot b = new Bot(namen[k], this.botDifficulty, this);
                //this.bots.add(b);
                ExecutorService service = Executors.newSingleThreadExecutor();
                Future<Bot> future = service.submit(new Bot(namen[k], this.botDifficulty, this));
                Bot b = null;
                try {
                    b = future.get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }

                this.bots.add(b);

                CharacterPlayer c = new CharacterPlayer(1, false, 1, 3, positions[k], true, true, directions[k], this);
                b.setCharacter(c);
                this.objects.add(c);
            }
        }

        int cubes = this.heightCubes * this.widthCubes;
        int powerups = Math.round((cubes / 10) / 2);
        boolean bool = false;

        //Add powerups
        for (int j = 0; j < powerups; j++) {
            bool = false;

            while (bool == false) {
                bool = placeRandomPowerup();
            }
        }

        //Add all objects to grid
        this.objects.stream().forEach((o) -> {
            this.setObjectInGrid(o);
        });
    }

    /**
     * Setup at begin of new level
     */
    @Override
    public void setupLevel() {
        this.objects.clear();

        Position[] positions = new Position[4];
        positions[0] = getPosition(1, this.heightCubes);
        positions[1] = getPosition(1, 1);
        positions[2] = getPosition(this.widthCubes, 1);
        positions[3] = getPosition(this.widthCubes, this.heightCubes);

        Direction[] directions = new Direction[4];
        directions[0] = Direction.Right;
        directions[1] = Direction.Down;
        directions[2] = Direction.Left;
        directions[3] = Direction.Up;

        int i = 0;

        //reset position of characters
        for (CharacterPlayer c : this.characters) {
            c.setPosition(positions[i]);
            c.setDirection(directions[i]);
            i++;
        }

        this.objects.addAll(this.characters);
        this.objects.addAll(this.getBoxes());
        this.objects.addAll(this.getCubes());

        //Add all objects to grid
        this.objects.stream().forEach((o) -> {
            this.setObjectInGrid(o);
        });
    }

    @Override
    public synchronized ArrayList<String[]> GetInformation() {
        ArrayList<String[]> information = new ArrayList();

        for (Position p : grid) {
            ArrayList<ObjectForGame> TempObjects = this.getObjectsFromGrid(p.getX(), p.getY());

            for (ObjectForGame o : TempObjects) {
                String[] objectinfo = new String[6];

                if (o instanceof CharacterPlayer) {
                    objectinfo[0] = "1";
                } else if (o instanceof Obstacle) {
                    objectinfo[0] = "2";
                } else if (o instanceof PowerUp) {
                    objectinfo[0] = "3";
                } else if (o instanceof Ballista) {
                    objectinfo[0] = "4";
                } else if (o instanceof Projectile) {
                    objectinfo[0] = "5";
                }

                objectinfo[1] = o.getObjectType();
                objectinfo[2] = o.getPosition().getX() + "";
                objectinfo[3] = o.getPosition().getY() + "";

                if (o.getDirection() != null) {
                    objectinfo[4] = o.getDirection().name() + "";
                } else {
                    objectinfo[4] = Direction.Up.name() + "";
                }
                objectinfo[5] = "" + this.currentLevel;
                information.add(objectinfo);
            }
        }
        return information;
    }

    public void GameTimer() {
        this.gameTickTimer = new Timer();
        System.out.println("Fail");
        //Level opnieuw uittekenen met nieuwe posities      

        //Geeft momenteel ConcurrentModificationException error
        // Maar deze timer zou dus voor updaten moeten zijn.
        this.gameTickTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (isRunning == false) {
                    isRunning = true;

                    {
                        updateGame();
                    }
                    isRunning = false;
                }
            }
        }, 500, 500);
    }
}
