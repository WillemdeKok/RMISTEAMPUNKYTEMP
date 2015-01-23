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
    private ScrollPane s1;
    private AnchorPane box;
    private Rectangle field;
    private Rectangle playfield;
    private Color backColor;

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

        this.slotsleft++;
        this.LBLRemaining.setText("Remaining slots: " + this.slotsleft);
        this.BTReady.setDisable(true);
        this.BTPlayer.setDisable(false);
        this.BTSpectator.setDisable(true);
        try {
            this.lobbyinstance.clearSlot(this.client.getUser());
        } catch (RemoteException ex) {
            System.out.println("User could not be cleared");
        }
        UpdateForms();
    }

    //Methode die er voor zorgt dat een speler een player wordt
    @FXML
    public void becomePlayer() {
        //      lobby.assignSlot(this.admin);
        this.slotsleft--;
        this.BTReady.setDisable(false);
        this.BTPlayer.setDisable(true);
        this.BTSpectator.setDisable(false);
        this.LBLRemaining.setText("Remaining slots: " + this.slotsleft);
        try {
            this.lobbyinstance.assignSlot(this.client.getUser());
        } catch (Exception ex) {
            System.out.println("User could not be assigned");
            ex.printStackTrace();
        }
        UpdateForms();
    }

    //Methode die de van de gameroom terug gaat naar de lobby
    @FXML
    public void ReturnToMenu() {
        //       this.main.gotoLobbyselect(admin);
    }

    //Stopt het starten van de game als op readt is geklikt
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
        box.getChildren().add(this.field);
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
            this.setKeyBindings();
        }
    }

    //clears the scene and draws new boxes for every object.
<<<<<<< HEAD
<<<<<<< HEAD
    public void DrawGame() throws InterruptedException, ExecutionException {
        
        if (PlayerNames.contains(client.getUser()))
        {
            
        }
        
        if (SpectatorNames.contains(client.getUser()))
=======
    public void DrawGame() throws InterruptedException, ExecutionException {
>>>>>>> 6375b4a1fc14d97db6a2d0225b31c78bdad60503
        {
            box.getChildren().clear();

            switch (level) {
                case 0:
<<<<<<< HEAD
                    this.backColor = Color.SADDLEBROWN;
                    this.field.setFill(backColor);                
                    this.playfield.setFill(Color.BURLYWOOD);
=======
    public void DrawGame() {
        box.getChildren().clear();
        
        switch (level) {
            case 0:
                this.field.setFill(Color.SADDLEBROWN);
                this.playfield.setFill(Color.BURLYWOOD);
                break;
            case 1:
                this.field.setFill(Color.DIMGRAY);
                this.playfield.setFill(Color.LIGHTGRAY);
                break;
            case 2:
                this.field.setFill(Color.PERU);
                this.playfield.setFill(Color.BEIGE);
                break;
        }
        
        box.getChildren().add(this.field);
        box.getChildren().add(this.playfield);
        
        ArrayList<String[]> information = null;
        
        try {
            information = this.lobbyinstance.GetInformation();
        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
                case 1:
                    this.backColor = Color.DIMGRAY;
                    this.field.setFill(backColor);
                    this.playfield.setFill(Color.LIGHTGRAY);
                    break;
                case 2:
                    this.backColor = Color.PERU;
                    this.field.setFill(backColor);
=======
                    this.field.setFill(Color.SADDLEBROWN);
                    this.playfield.setFill(Color.BURLYWOOD);
                    break;
                case 1:
                    this.field.setFill(Color.DIMGRAY);
                    this.playfield.setFill(Color.LIGHTGRAY);
                    break;
                case 2:
                    this.field.setFill(Color.PERU);
>>>>>>> 6375b4a1fc14d97db6a2d0225b31c78bdad60503
                    this.playfield.setFill(Color.BEIGE);
                    break;
            }

            box.getChildren().add(this.field);
            box.getChildren().add(this.playfield);

<<<<<<< HEAD
            //Informationthread.start();
            information = null;
        
=======
            information = null;

>>>>>>> 6375b4a1fc14d97db6a2d0225b31c78bdad60503
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
<<<<<<< HEAD
                }
            }
            );
            
            information = future.get();

            for (String[] s : information) {
                String object = "";
                String type = s[1];
                int xpos = Integer.parseInt(s[2]);
                int ypos = Integer.parseInt(s[3]);
                String direction = s[4];
                if(this.level != Integer.parseInt(s[5])){
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
                    //level nog niet geimplementeerd
                    image = selector.getImage(s, level);
                    img = new ImageView(image);
                    img.setScaleX(this.getScale());
                    img.setScaleY(this.getScale());
                    img.setX((xpos * 100 * this.getScale()) + (-50 * (1 - this.getScale())));
                    img.setY((ypos * 100 * this.getScale()) + (-50 * (1 - this.getScale())));

                    if (object.equals("Projectile") || object.equals("Character")) {
                        img.setRotate(getRotation(direction));
                    } else {
                        img.setRotate(0);
                    }

                    box.getChildren().add(img);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
=======
            
            Image image = null;
            ImageView img = null;
            
            try {
                //level nog niet geimplementeerd
                image = selector.getImage(s, level);
                img = new ImageView(image);
                img.setScaleX(this.getScale());
                img.setScaleY(this.getScale());
                img.setX((xpos * 100 * this.getScale()) + (-50 * (1 - this.getScale())));
                img.setY((ypos * 100 * this.getScale()) + (-50 * (1 - this.getScale())));
                
                if (object.equals("Projectile") || object.equals("Character")) {
                    img.setRotate(getRotation(direction));
                } else {
                    img.setRotate(0);
                }
                
                box.getChildren().add(img);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
>>>>>>> e8e63e9d5066a153c8a121b3a213a6b99380e403
=======
                }
>>>>>>> 6375b4a1fc14d97db6a2d0225b31c78bdad60503
            }
            );
            information = future.get();

            //Informationthread.start();
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
                    //level nog niet geimplementeerd
                    image = selector.getImage(s, level);
                    img = new ImageView(image);
                    img.setScaleX(this.getScale());
                    img.setScaleY(this.getScale());
                    img.setX((xpos * 100 * this.getScale()) + (-50 * (1 - this.getScale())));
                    img.setY((ypos * 100 * this.getScale()) + (-50 * (1 - this.getScale())));

                    if (object.equals("Projectile") || object.equals("Character")) {
                        img.setRotate(getRotation(direction));
                    } else {
                        img.setRotate(0);
                    }

                    box.getChildren().add(img);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
            try {
                this.lobbyinstance.move(this.client.getUser(), Direction.Right);
            } catch (RemoteException ex) {
                Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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

<<<<<<< HEAD
            //check scale for user
            if (SpectatorNames.contains(client.getUser()))
            {
                double hoogteScherm = 800;
                double hoogteSpel = this.lobbyinstance.getHeightPixels();
                scale = hoogteScherm / hoogteSpel;
            }

=======
            //check scale for admin
            /*for (String name : this.PlayerNames)
             {
             if (name.equals(this.admin.getUsername()))
             {
             scale = 1;
             }
             }
            
             for (String name : this.SpectatorNames)
             {
             if (name.equals(this.admin.getUsername()))
             {*/
            double hoogteScherm = 800;
            double hoogteSpel = this.lobbyinstance.getHeightPixels();
            scale = hoogteScherm / hoogteSpel;
            /*}
             }*/
<<<<<<< HEAD
            
>>>>>>> e8e63e9d5066a153c8a121b3a213a6b99380e403
=======

>>>>>>> 6375b4a1fc14d97db6a2d0225b31c78bdad60503
            return scale;

        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
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
            SetupDraw();
        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Sets up the settings needed to draw.

    public synchronized void SetupDraw() {
        //Teken code hier aan toevoegen
        //Moeten groter zijn dan 9; melding?!

        try {
            this.PlayerNames = this.lobbyinstance.getPlayers();
            this.SpectatorNames = this.lobbyinstance.getSpectators();
            
            this.widthPixels = this.lobbyinstance.getWidthPixels();
            this.widthCubes = this.lobbyinstance.getWidthCubes();
            this.heightPixels = this.lobbyinstance.getHeightPixels();
            this.heightCubes = this.lobbyinstance.getHeightCubes();
        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }

        root = new Group();
        Scene scene = new Scene(root, 1700, 900);

        s1 = new ScrollPane();
        s1.setLayoutX(50);
        s1.setLayoutY(50);
        s1.setPrefSize(1600, 800);

        box = new AnchorPane();
        s1.setContent(box);

        this.field = new Rectangle(this.widthPixels * this.getScale(), this.heightPixels * this.getScale());
        this.field.setFill(Color.GRAY);

        this.playfield = new Rectangle(100 * this.getScale(), 100 * this.getScale(), (this.widthCubes * 100 * this.getScale()), (this.heightCubes * 100 * this.getScale()));
        this.playfield.setFill(Color.WHITE);

        root.getChildren().add(s1);
        this.stage.setMinHeight(900);
        this.stage.setMinWidth(1700);
        this.stage.setScene(scene);
        this.GameUpdate();
    }
<<<<<<< HEAD
<<<<<<< HEAD
        

=======
    
>>>>>>> e8e63e9d5066a153c8a121b3a213a6b99380e403
=======

>>>>>>> 6375b4a1fc14d97db6a2d0225b31c78bdad60503
    public void drawTimer() {

    }

    //Set the keybindings for this Scene
    public synchronized void setKeyBindings() {
        this.stage.getScene().setOnKeyPressed((KeyEvent keyEvent) -> {
            try {
                if (keyEvent.getCode().toString().equals("W")) {
                    this.client.getIUser().move(Direction.Up);
                    System.out.println("moveup");
                    //this.game.getCharacter().move(Direction.Up);
                }

                if (keyEvent.getCode().toString().equals("A")) {
                    this.client.getIUser().move(Direction.Left);
                    //this.game.getCharacter().move(Direction.Left);
                }

                if (keyEvent.getCode().toString().equals("S")) {
                    this.client.getIUser().move(Direction.Down);
                    //this.game.getCharacter().move(Direction.Down);
                }

                if (keyEvent.getCode().toString().equals("D")) {
                    this.client.getIUser().move(Direction.Right);
                    //this.game.getCharacter().move(Direction.Right);
                }

                if (keyEvent.getCode().toString().equals("Q")) {
                    //c.createBallista(Direction.Right ,4);
                }

                if (keyEvent.getCode().toString().equals("E")) {
                    //ICharacter c = (ICharacter) game.getCharacter();
                    //c.createBallista(Direction.Up ,4);
                }
            } catch (RemoteException ex) {
                Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
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
                            StartGame();
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
                            DrawGame();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        }, 500, 500);
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
                        Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
}
