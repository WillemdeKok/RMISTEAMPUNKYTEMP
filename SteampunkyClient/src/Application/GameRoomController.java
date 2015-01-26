/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import Application.FontysObserver.RemotePropertyListener;
import Application.FontysObserver.RemotePublisher;
import images.ImageSelector;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Bart
 */
public class GameRoomController extends UnicastRemoteObject implements Initializable, RemotePropertyListener {

    //JAVAFX CONTROLS
    @FXML
    private Font x1;
    @FXML
    private Button BTSpectator;
    @FXML
    private Button BTPlayer;
    @FXML
    private Button BTReturn;
    @FXML
    private Button BTstop;
    @FXML
    private Button BTReady;
    @FXML
    private Button BTNSend;
    @FXML
    private Label LBLusername;
    @FXML
    private Label LBLRemaining;
    @FXML
    private Label LBRating;
    @FXML
    private Label LBLGameState;
    @FXML
    private Label LBLPlayer1Status;
    @FXML
    private Label LBLPlayer2Status;
    @FXML
    private Label LBLPlayer3Status;
    @FXML
    private Label LBLPlayer4Status;
    @FXML
    private Label LBLsize;
    @FXML
    private Label LBLHeight;
    @FXML
    private Label LBLWidth;
    @FXML
    private Label LBLTime;
    @FXML
    private Label LBLRound;
    @FXML
    private Label LBLReadyToBegin;
    @FXML
    private TextField TFChatInsert;
    @FXML
    private ListView LBPlayers;
    @FXML
    private ListView LVChats;
    @FXML
    private ListView LBSpectators;
    @FXML
    private ComboBox CBlevelsizeHeight;
    @FXML
    private ComboBox CBlevelsizeWidth;
    @FXML
    private ComboBox CBMinutes;
    @FXML
    private ComboBox CBrounds;

    //JAVAFX referenties / mee gegeven objecten van andere forums
    private SteampunkyFX main;
    private Stage stage;

    //Game refereantie met game eigenschappen
    private int widthPixels;
    private int widthCubes;
    private int heightPixels;
    private int heightCubes;
    private Group root;
    //private ScrollPane s1;
    private AnchorPane box;
    private AnchorPane canvas;
    private Rectangle field;
    private Rectangle playfield;

    private Color fieldColor;
    private Color playfieldColor;

    //Listen die nodig zijn voor de gui te updaten
    private ArrayList<String> SpectatorNames;
    private ArrayList<String> PlayerNames;
    private ArrayList<String> Roomsizewidth;
    private ArrayList<String> Roomsizeheight;
    private ArrayList<String> Rounds;
    private ArrayList<String> Time;
    private transient ObservableList<String> observableRounds;
    private transient ObservableList<String> observableTime;
    private ArrayList<String[]> information;
    private transient ObservableList<String> observableRoomsizewidth;
    private transient ObservableList<String> observableRoomsizeheight;

    //Classe variable plus timer instantie
    private Timer timer;
    private Timer gameTickTimer;
    private int timercount = 6;
    private int countdown = 6;
    private int playernumber;
    private int slotsleft = 4;
    private Client client;
    private ILobby lobbyinstance;
    private IGameServer ServerMock;
    private ImageSelector selector;

    private int level = 0;

    public GameRoomController() throws RemoteException {
    }

    public void setApp(SteampunkyFX application, Stage stage, Client client, ILobby l, IGameServer ServerMock) {
        this.ServerMock = ServerMock;
        this.lobbyinstance = l;
        this.stage = stage;
        this.main = application;
        this.client = client;

        this.LBLusername.setText("Welcome: " + client.getUser());
        this.LBLRemaining.setText("Remaining slots: " + this.slotsleft);
        this.LBRating.setText("Rating: " + this.client.getRating());
        this.BTReady.setVisible(false);
        this.BTstop.setVisible(false);
        this.BTSpectator.setDisable(true);

        try {
            RemotePublisher publisher = (RemotePublisher) this.lobbyinstance;
            publisher.addListener(this, "lobby");
        } catch (Exception ex) {
            System.out.println("Publisher not initialized, consider changing the adress??");
            ex.printStackTrace();
        }

        this.LBLPlayer1Status.setVisible(false);
        this.LBLPlayer2Status.setVisible(false);
        this.LBLPlayer3Status.setVisible(false);
        this.LBLPlayer4Status.setVisible(false);

        this.LBLsize.setVisible(false);
        this.LBLHeight.setVisible(false);
        this.LBLWidth.setVisible(false);
        this.LBLTime.setVisible(false);
        this.LBLRound.setVisible(false);
        this.CBlevelsizeHeight.setVisible(false);
        this.CBlevelsizeWidth.setVisible(false);
        this.CBMinutes.setVisible(false);
        this.CBrounds.setVisible(false);
        this.LBLReadyToBegin.setVisible(false);

        //add level size 
        observableRounds = observableList(this.Rounds);
        observableTime = observableList(this.Time);
        observableRoomsizewidth = observableList(this.Roomsizewidth);
        observableRoomsizeheight = observableList(this.Roomsizeheight);

        //vult de ronde en game comboboxen
        for (int TimeRound = 1; TimeRound < 5; TimeRound++) {
            String temp = "" + TimeRound;
            observableRounds.add(temp);
            observableTime.add(temp);
        }

        // vult de hoogte en breedte lijst van het speelveld in de combobox
        for (int widthheight = 9; widthheight < 14; widthheight++) {
            if (widthheight % 2 != 0) {
                String temp = "" + widthheight;
                observableRoomsizewidth.add(temp);
                observableRoomsizeheight.add(temp);
            }
        }

        //Vult de comboboxen bij load en standaard waarde instellen
        InitCombos();
        this.CBMinutes.getSelectionModel().select(0);
        this.CBrounds.getSelectionModel().select(0);
        this.CBlevelsizeWidth.getSelectionModel().select(0);
        this.CBlevelsizeHeight.getSelectionModel().select(0);

        try {
            this.LVChats.setItems(FXCollections.observableArrayList(lobbyinstance.getChat()));
        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //Kijkt of de ingelogde speler een admin is
            if (this.client.getUser().equals(this.lobbyinstance.getAdminName())) {
                this.LBLReadyToBegin.setVisible(true);
                this.BTReady.setVisible(true);
                this.BTstop.setVisible(true);
                this.BTstop.setDisable(true);
                this.LBLsize.setVisible(true);
                this.LBLHeight.setVisible(true);
                this.LBLWidth.setVisible(true);
                this.LBLTime.setVisible(true);
                //this.LBLRound.setVisible(true);
                this.CBlevelsizeHeight.setVisible(true);
                this.CBlevelsizeWidth.setVisible(true);
                this.CBMinutes.setVisible(true);
                //this.CBrounds.setVisible(true);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int Calcfreeslots() {
        int result;
        try {
            this.playernumber = this.lobbyinstance.getPlayers().size();
            result = this.slotsleft - this.playernumber;
            return result;
        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        server = (IServer) getServer();
        SpectatorNames = new ArrayList<>();
        PlayerNames = new ArrayList<>();
        Roomsizewidth = new ArrayList<>();
        Roomsizeheight = new ArrayList<>();
        Rounds = new ArrayList<>();
        Time = new ArrayList<>();
        selector = new ImageSelector();
    }

    //Methode die er voor zorgt dat een speler een spectator wordt
    @FXML
    public void becomeSpectator() {
        //      this.lobby.clearSlot(this.admin);

        this.BTReady.setDisable(true);
        this.BTPlayer.setDisable(false);
        this.BTSpectator.setDisable(true);
        try {
            this.lobbyinstance.clearSlot(this.client.getUser());
            this.LBLRemaining.setText("Remaining slots: " + Calcfreeslots());
        } catch (RemoteException ex) {
            System.out.println("User could not be cleared");
        }
        UpdateForms();
    }

    //Methode die er voor zorgt dat een speler een player wordt
    @FXML
    public void becomePlayer() {
        //      lobby.assignSlot(this.admin);
        this.BTReady.setDisable(false);
        this.BTPlayer.setDisable(true);
        this.BTSpectator.setDisable(false);
        try {
            this.lobbyinstance.assignSlot(this.client.getUser());
            this.LBLRemaining.setText("Remaining slots: " + Calcfreeslots());
        } catch (Exception ex) {
            System.out.println("User could not be assigned");
            ex.printStackTrace();
        }
        UpdateForms();
    }

    //Methode die de van de gameroom terug gaat naar de lobby
    @FXML
    public void ReturnToMenu() {
        try {
            if (this.ServerMock.leaveLobby(lobbyinstance, this.client.getUser())) {
                UpdateForms();
                this.main.gotoLobbyselect(this.client, this.ServerMock);
            } else {
                System.out.println("Kan niet terug naar lobby");
            }
        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Stopt het starten van de game als op read is geklikt
    @FXML
    public void StopGame() {
        this.timer.cancel();
        this.timercount = 6;
        this.countdown = 6;
        this.LBLGameState.setText("Waiting for players");
        this.BTstop.setDisable(true);
        this.BTReady.setDisable(false);
    }

    public void SetupStage() {

        this.field = new Rectangle(this.widthPixels, this.heightPixels);
        this.field.setFill(Color.GRAY);
        canvas.getChildren().add(this.field);
    }

    //Start de game als de teller op 0 komt wordt het speel veld geladen
    public void Countdown() {
        this.countdown--;
        String number = "Game wil start in: " + this.countdown;
        this.LBLGameState.setText(number);

        if (this.countdown == 0) {
            //get random level from 1 to 3
            Random levelInt = new Random();
            level = levelInt.nextInt(3) + 1;
            this.StartGame();
        }
    }

    //clears the scene and draws new boxes for every object.
    public void DrawGame() throws InterruptedException, ExecutionException {

        //get game information (objects)
        //Informationthread.start();
        information = null;

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<ArrayList<String[]>> future = service.submit(new Callable() {
            @Override
            public Object call() throws Exception {
                try {
                    information = lobbyinstance.GetInformation();
                    return information;
                } catch (RemoteException ex) {
                    Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }

        }
        );

        try {
            information = future.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }

        canvas.getChildren().clear();
        int rotation = 0;

        if (PlayerNames.contains(client.getUser())) {
            int[] i = null;

            try {
                i = this.lobbyinstance.GetCharacter(client.getUser());
            } catch (RemoteException ex) {
                Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
            }

            int characterNr = i[0];
            int xpos = i[1];
            int ypos = i[2];
            int torchrange = i[3];

            switch (characterNr) {
                case 0:
                    rotation = 0;
                    break;
                case 1:
                    rotation = 270;
                    break;
                case 2:
                    rotation = 180;
                    break;
                case 3:
                    rotation = 90;
                    break;
            }

            Rectangle borderXleft = null;
            Rectangle borderXright = null;
            Rectangle borderYtop = null;
            Rectangle borderYbottom = null;

            //set range
            double maxrange = 5 * 100;
            double range = torchrange * 100;
            double maxfieldsize = 1100;
            double fieldsize = (range * 2) + 100;

            //draw playfield
            field = new Rectangle(0, 0, maxfieldsize, maxfieldsize);
            field.setFill(Color.BLACK);
            canvas.getChildren().add(field);

            playfield = new Rectangle(maxrange - range, maxrange - range, fieldsize, fieldsize);
            playfield.setFill(playfieldColor);
            canvas.getChildren().add(playfield);

            //Get player as object
            Rectangle player = new Rectangle(maxrange, maxrange, 100, 100);

            //set gamefield border
            double var1 = maxrange - range;
            double var2 = (widthCubes * 100) - (xpos * 100);
            double var3 = (heightCubes * 100) - (ypos * 100);

            if ((xpos * 100) <= range) {
                borderXleft = new Rectangle(var1, var1, range + 100 - (xpos * 100), fieldsize);
                borderXleft.setFill(fieldColor);
                canvas.getChildren().add(borderXleft);
            }

            if ((xpos * 100) >= (widthCubes * 100) - range + 100) {
                borderXright = new Rectangle(range + var1 + var2 + 100, var1, range - ((widthCubes * 100) - (xpos * 100)), fieldsize);
                borderXright.setFill(fieldColor);
                canvas.getChildren().add(borderXright);
            }

            if ((ypos * 100) <= range) {
                borderYtop = new Rectangle(var1, var1, fieldsize, range + 100 - (ypos * 100));
                borderYtop.setFill(fieldColor);
                canvas.getChildren().add(borderYtop);
            }

            if ((ypos * 100) >= (heightCubes * 100) - range + 100) {
                borderYbottom = new Rectangle(var1, range + var1 + var3 + 100, fieldsize, range - var3);
                borderYbottom.setFill(fieldColor);
                canvas.getChildren().add(borderYbottom);
            }

            //get min and max values
            double minX = (xpos * 100) - range;
            double maxX = (xpos * 100) + range;
            double minY = (ypos * 100) - range;
            double maxY = (ypos * 100) + range;

            //Draw objects in game within range
            for (String[] s : information) {
                String object = "";
                String type = s[1];
                int xpos2 = Integer.parseInt(s[2]);
                int ypos2 = Integer.parseInt(s[3]);
                String direction = s[4];
                if (this.level != Integer.parseInt(s[5])) {
                    this.level = Integer.parseInt(s[5]);
                }

                switch (s[0]) {
                    case "1":
                        object = "Character";
                        break;
                    case "2":
                        object = "Obstacle";
                        break;
                    case "3":
                        object = "PowerUp";
                        break;
                    case "4":
                        object = "Ballista";
                        break;
                    case "5":
                        object = "Projectile";
                        break;
                }

                double X = xpos2 * 100;
                double Y = ypos2 * 100;

                if (X >= minX && X <= maxX && Y >= minY && Y <= maxY) {
                    double changeX = X - (xpos * 100);
                    double changeY = Y - (ypos * 100);

                    Image image = null;
                    ImageView img = null;

                    try {
                        image = selector.getImage(s, level);
                        img = new ImageView(image);
                        img.setX(player.getX() + changeX);
                        img.setY(player.getY() + changeY);

                        if (object.equals("Projectile") || object.equals("Character")) {
                            img.setRotate(getRotation(direction));
                        } else {
                            img.setRotate(0);
                        }

                        canvas.getChildren().add(img);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }

        if (SpectatorNames.contains(client.getUser())) {
            this.field.setFill(this.fieldColor);
            this.playfield.setFill(this.playfieldColor);

            canvas.getChildren().add(this.field);
            canvas.getChildren().add(this.playfield);

            for (String[] s : information) {
                String object = "";
                String type = s[1];
                int xpos = Integer.parseInt(s[2]);
                int ypos = Integer.parseInt(s[3]);
                String direction = s[4];
                if (this.level != Integer.parseInt(s[5])) {
                    this.level = Integer.parseInt(s[5]);
                }

                switch (s[0]) {
                    case "1":
                        object = "Character";
                        break;
                    case "2":
                        object = "Obstacle";
                        break;
                    case "3":
                        object = "PowerUp";
                        break;
                    case "4":
                        object = "Ballista";
                        break;
                    case "5":
                        object = "Projectile";
                        break;
                }

                Image image = null;
                ImageView img = null;

                try {
                    image = selector.getImage(s, level);
                    img = new ImageView(image);
                    img.setX(xpos * 100);
                    img.setY(ypos * 100);

                    if (object.equals("Projectile") || object.equals("Character")) {
                        img.setRotate(getRotation(direction));
                    } else {
                        img.setRotate(0);
                    }

                    canvas.getChildren().add(img);

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        canvas.setRotate(rotation);
        canvas.setScaleX(this.getScale());
        canvas.setScaleY(this.getScale());
        canvas.setLayoutX(-40);
        canvas.setLayoutY(-120);
    }

    /**
     *
     * @param direction
     * @return
     */
    public double getRotation(String direction) {
        double rotation = 0;

        switch (direction) {
            case "Up":
                rotation = 0;
                break;
            case "Right":
                rotation = 90;
                break;
            case "Down":
                rotation = 180;
                break;
            case "Left":
                rotation = 270;
                break;
        }

        return rotation;
    }

    public double getScale() {
        try {
            double scale = 1;

            //check scale for user
            double hoogteScherm = 800;
            double hoogteSpel = 0;

            if (SpectatorNames.contains(client.getUser())) {
                hoogteSpel = this.lobbyinstance.getHeightPixels();
            }

            if (PlayerNames.contains(client.getUser())) {
                hoogteSpel = 1100;
            }

            scale = hoogteScherm / hoogteSpel;

            return scale;

        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class
                    .getName()).log(Level.SEVERE, null, ex);

            return 1;
        }
    }

    /**
     *
     */
    public void StartGame() {
        int width = Integer.parseInt(this.CBlevelsizeWidth.getValue().toString());
        int height = Integer.parseInt(this.CBlevelsizeHeight.getValue().toString());
        double time = Integer.parseInt(this.CBMinutes.getValue().toString()) * 60;
        int rounds = 1;

        try {
            this.lobbyinstance.createGame(time, 3, level, 1, width, height);
            //SetupDraw();

        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Sets up the settings needed to draw.

    public synchronized void SetupDraw() {
        //Teken code hier aan toevoegen
        //Moeten groter zijn dan 9; melding?!

        try {
            this.PlayerNames = this.lobbyinstance.getPlayers();
            this.SpectatorNames = this.lobbyinstance.getSpectators();

            this.level = this.lobbyinstance.getLevel();
            this.widthPixels = this.lobbyinstance.getWidthPixels();
            this.widthCubes = this.lobbyinstance.getWidthCubes();
            this.heightPixels = this.lobbyinstance.getHeightPixels();
            this.heightCubes = this.lobbyinstance.getHeightCubes();

        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        
        switch (level) {
            case 1:
                this.fieldColor = Color.SADDLEBROWN;
                this.playfieldColor = Color.BURLYWOOD;
                break;
            case 2:
                this.fieldColor = Color.DIMGRAY;
                this.playfieldColor = Color.LIGHTGRAY;
                break;
            case 3:
                this.fieldColor = Color.PERU;
                this.playfieldColor = Color.BEIGE;
                break;
        }

        root = new Group();
        Scene scene = new Scene(root, 900, 900);

        box = new AnchorPane();
        box.setPrefSize(800, 800);
        
        String url = "border.jpg";
        Image im = new Image(this.getClass().getResourceAsStream(url), 1800, 1125, false, false);
        //ImageView img = new ImageView(new Image(this.getClass().getResourceAsStream(url)));
        ImageView img = new ImageView(im);
        //img.setX(1280);
        //img.setY(800);
        box.getChildren().add(img);

        canvas = new AnchorPane();
        canvas.setMaxSize(800, 800);
        box.getChildren().add(canvas);

        this.field = new Rectangle(this.widthPixels, this.heightPixels);
        this.playfield = new Rectangle(100, 100, this.widthCubes * 100, this.heightCubes * 100);

        root.getChildren().add(box);
        this.stage.setHeight(900);
        this.stage.setWidth(1700);
        this.stage.setScene(scene);
        this.setKeyBindings();
        this.GameUpdate();
    }

    public void drawTimer() {

    }

    //Set the keybindings for this Scene
    public synchronized void setKeyBindings() {
        this.stage.getScene().setOnKeyPressed((KeyEvent keyEvent) -> {
            try {
                if (keyEvent.getCode().toString().equals("W")) {
                    this.lobbyinstance.move(this.client.getUser(), Direction.Up);
                }

                if (keyEvent.getCode().toString().equals("A")) {
                    this.lobbyinstance.move(this.client.getUser(), Direction.Left);

                }

                if (keyEvent.getCode().toString().equals("S")) {
                    this.lobbyinstance.move(this.client.getUser(), Direction.Down);
                }

                if (keyEvent.getCode().toString().equals("D")) {
                    this.lobbyinstance.move(this.client.getUser(), Direction.Right);
                }

                if (keyEvent.getCode() == KeyCode.SPACE) {
                    this.lobbyinstance.dropBallista(this.client.getUser());
                }
                if (keyEvent.getCode().toString().equals("E")) {
                    //ICharacter c = (ICharacter) game.getCharacter();
                    //c.createBallista(Direction.Up ,4);
                }
            } catch (RemoteException ex) {
                Logger.getLogger(GameRoomController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    //Zodra er op ready wordt geklikt start de timer die aftelt tot de game start
    @FXML
    public void Gameready() {
        this.timer = new Timer();
        this.BTstop.setDisable(false);
        this.BTReady.setDisable(true);
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Countdown();
                        if (countdown == 0) {
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    public void LVupdate() {
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        UpdateForms();
                    }
                });
            }
        }, 0, 2000);
    }

    public synchronized void GameUpdate() {
        this.gameTickTimer = new Timer();
        System.out.println("Fail");
        //Level opnieuw uittekenen met nieuwe posities      

        //Geeft momenteel ConcurrentModificationException error
        // Maar deze timer zou dus voor updaten moeten zijn.
        this.gameTickTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    {
                        try {
                            try {
                                if (lobbyinstance.getGameEnd()) {
                                    gameTickTimer.cancel();
                                    JOptionPane.showMessageDialog(null, "Game has ended");
                                    main.gotoGameRoomselect(client, lobbyinstance, ServerMock);
                                } else {
                                    DrawGame();
                                }
                            } catch (RemoteException ex) {
                                Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } catch (InterruptedException ex) {
                            Logger.getLogger(GameRoomController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            Logger.getLogger(GameRoomController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        }, 300, 300);
    }

    //Initialiseert de combo boxen
    public void InitCombos() {
        this.CBlevelsizeWidth.setItems(this.observableRoomsizewidth);
        this.CBlevelsizeHeight.setItems(this.observableRoomsizeheight);
        this.CBMinutes.setItems(this.observableTime);
        this.CBrounds.setItems(this.observableRounds);
        UpdateForms();
    }

    public void UpdateForms() {

        this.LBSpectators.getItems().clear();
        this.LBPlayers.getItems().clear();

        ArrayList<String> temp = new ArrayList<>();
        ArrayList<String> temp2 = new ArrayList<>();

        try {
            for (String s : this.lobbyinstance.getSpectators()) {
                temp.add(s);
            }
        } catch (RemoteException ex) {
            System.out.println("Remote Exception");
        }

        try {
            for (String p : this.lobbyinstance.getPlayers()) {
                temp2.add(p);
            }
        } catch (RemoteException ex) {
            System.out.println("Remote Exception");
        }

        this.LBSpectators.setItems(FXCollections.observableList(temp));
        this.LBPlayers.setItems(FXCollections.observableList(temp2));
        this.LBLRemaining.setText("Remaining slots: " + Calcfreeslots());
        if (Calcfreeslots() == 0) {
            this.BTPlayer.setDisable(true);
        } else {
            this.BTPlayer.setDisable(false);
        }
    }

    @FXML
    public void AddChatmessage() {
        if (TFChatInsert.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter a chat message");
        } else {
            try {
                String chatbericht;
                Calendar cal = Calendar.getInstance();
                cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                chatbericht = sdf.format(cal.getTime()) + " | " + client.getUser() + ": " + TFChatInsert.getText();
                this.lobbyinstance.Addchatmessage(chatbericht);
                TFChatInsert.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Server connection failed" + ex.getMessage());
                System.out.println("Failed" + ex.getMessage());
            }
        }
    }

    public void AdminReset() {
        try {
            //Kijkt of de ingelogde speler een admin is
            if (this.client.getUser().equals(this.lobbyinstance.getAdminName())) {
                this.LBLReadyToBegin.setVisible(true);
                this.BTReady.setVisible(true);
                this.BTstop.setVisible(true);
                this.BTstop.setDisable(true);
                this.LBLsize.setVisible(true);
                this.LBLHeight.setVisible(true);
                this.LBLWidth.setVisible(true);
                this.LBLTime.setVisible(true);
                //this.LBLRound.setVisible(true);
                this.CBlevelsizeHeight.setVisible(true);
                this.CBlevelsizeWidth.setVisible(true);
                this.CBMinutes.setVisible(true);
                //this.CBrounds.setVisible(true);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Property change event recorded");
                if (evt.getNewValue().equals("new")) {
                    System.out.println("Item added");
                    InitCombos();
                } else if (evt.getNewValue().equals("start")) {
                    System.out.println("Game started");
                    SetupDraw();
                } else if (evt.getNewValue().equals("Message")) {
                    try {
                        LVChats.setItems(FXCollections.observableArrayList(lobbyinstance.getChat()));
                        int count = LVChats.getItems().size();
                        LVChats.scrollTo(count);

                    } catch (RemoteException ex) {
                        Logger.getLogger(GameRoomController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (evt.getNewValue().equals("Admin")) {
                    AdminReset();
                }
            }
        });
    }
}
