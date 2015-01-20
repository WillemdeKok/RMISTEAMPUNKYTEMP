/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import images.ImageSelector;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * FXML Controller class
 *
 * @author Bart
 */
public class GameRoomController implements Initializable {

    //JAVAFX CONTROLS
    @FXML private Font x1;
    @FXML private Button BTSpectator;
    @FXML private Button BTPlayer;
    @FXML private Button BTReturn;
    @FXML private Button BTstop;
    @FXML private Button BTReady;
    @FXML private Label LBLusername;
    @FXML private Label LBLRemaining;
    @FXML private Label LBLGameState;
    @FXML private Label LBLPlayer1Status;
    @FXML private Label LBLPlayer2Status;
    @FXML private Label LBLPlayer3Status;
    @FXML private Label LBLPlayer4Status;
    @FXML private Label LBLsize;
    @FXML private Label LBLHeight;
    @FXML private Label LBLWidth;
    @FXML private Label LBLTime;
    @FXML private Label LBLRound;
    @FXML private ListView LBPlayers;
    @FXML private ListView LBSpectators;
    @FXML private ComboBox CBlevelsizeHeight;
    @FXML private ComboBox CBlevelsizeWidth;
    @FXML private ComboBox CBMinutes;
    @FXML private ComboBox CBrounds;

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

    //Listen die nodig zijn voor de gui te updaten
    private ArrayList<String> SpectatorNames;
    private ArrayList<String> PlayerNames;
    private ArrayList<String> Roomsizewidth;
    private ArrayList<String> Roomsizeheight;
    private ArrayList<String> Rounds;
    private ArrayList<String> Time;
    private transient ObservableList<String> observableRounds;
    private transient ObservableList<String> observableTime;
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

    public void setApp(SteampunkyFX application, Stage stage, Client client, ILobby l, IGameServer ServerMock) {
        this.ServerMock = ServerMock;
        this.lobbyinstance = l;
        this.stage = stage;
        this.main = application;
        this.client = client;

        this.LBLusername.setText("Welcome: " + client.getUser());
        this.LBLRemaining.setText("Remaining slots: " + this.slotsleft);
        this.BTReady.setDisable(true);
        this.BTSpectator.setDisable(true);

        this.LBLsize.setDisable(true);
        this.LBLHeight.setDisable(true);
        this.LBLWidth.setDisable(true);
        this.LBLTime.setDisable(true);
        this.LBLRound.setDisable(true);
        this.CBlevelsizeHeight.setDisable(true);
        this.CBlevelsizeWidth.setDisable(true);
        this.CBMinutes.setDisable(true);
        this.CBrounds.setDisable(true);
        this.BTstop.setDisable(true);

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
        for (int widthheight = 9; widthheight < 20; widthheight++) {
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

        //Kijkt of de ingelogde speler een admin is
//        for (IUser u : lobby.getSpectators()) {
//            if (u == admin) {
//                this.LBLsize.setDisable(false);
//                this.LBLHeight.setDisable(false);
//                this.LBLWidth.setDisable(false);
//                this.LBLTime.setDisable(false);
//                this.LBLRound.setDisable(false);
//                this.CBlevelsizeHeight.setDisable(false);
//                this.CBlevelsizeWidth.setDisable(false);
//                this.CBMinutes.setDisable(false);
//                this.CBrounds.setDisable(false);
//            }
//        }
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
    
    public void SetupStage(){
            this.field = new Rectangle(this.widthPixels, this.heightPixels);
            this.field.setFill(Color.GRAY);
            box.getChildren().add(this.field);   
    }
    
    //Start de game als de teller op 0 komt wordt het speel veld geladen
    public void Countdown() {
        this.countdown--;
        String number = "Game wil start in: " + this.countdown;
        this.LBLGameState.setText(number);

        if (this.countdown == 0) 
        {            
            //get random level from 1 to 3
            Random levelInt = new Random();
            level = levelInt.nextInt(3) + 1;
            
            this.SetupDraw();
            this.setKeyBindings();                   
        }
    }
    //clears the scene and draws new boxes for every object.
    public void DrawGame(){
        box.getChildren().clear();        
        
        switch (level)
        {
            case 1:
                this.field.setFill(Color.SADDLEBROWN);
                this.playfield.setFill(Color.BURLYWOOD);
                break;
            case 2:
                this.field.setFill(Color.DIMGRAY);
                this.playfield.setFill(Color.LIGHTGRAY);
                break;
            case 3:
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
        
        for (String[] s : information)
        {
            String object = "";
            String type = s[1];
            int xpos = Integer.parseInt(s[2]);
            int ypos = Integer.parseInt(s[3]);
            String direction = s[4];
            
            switch(s[0])
            {
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

            try
            {
                //level nog niet geimplementeerd
                image = selector.getImage(s, level);
                img = new ImageView(image);
                img.setScaleX(this.getScale());
                img.setScaleY(this.getScale());
                img.setX((xpos*100*this.getScale()) + (-50 * (1-this.getScale())));
                img.setY((ypos*100*this.getScale()) + (-50 * (1-this.getScale())));  

                if (object.equals("Projectile") || object.equals("Character"))
                {
                    img.setRotate(getRotation(direction));
                }
                else
                {
                    img.setRotate(0);
                }

                box.getChildren().add(img);
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    public double getRotation(String direction)
    {
        double rotation = 0;

        switch(direction)
        {
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
    
    public double getScale()
    {
        try {
            double scale = 1;
            
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
            scale = hoogteScherm/hoogteSpel;
            /*}
            }*/
            
            return scale;
            
        } catch (RemoteException ex) {
            Logger.getLogger(GameRoomController.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
    }
    
    //Sets up the settings needed to draw.
    public void SetupDraw(){
        
        //Teken code hier aan toevoegen
        //Moeten groter zijn dan 9; melding?!
        int width = Integer.parseInt(this.CBlevelsizeWidth.getValue().toString());
        int height = Integer.parseInt(this.CBlevelsizeHeight.getValue().toString());
        double time = Integer.parseInt(this.CBMinutes.getValue().toString()) * 60;
        int rounds = Integer.parseInt(this.CBrounds.getValue().toString());
            
        try {
            this.PlayerNames = this.lobbyinstance.getPlayers();
            this.lobbyinstance.createGame(time, 1, level, rounds, width, height);
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

        this.field = new Rectangle(this.widthPixels, this.heightPixels);
        this.field.setFill(Color.GRAY);      

        this.playfield = new Rectangle(100, 100, (this.widthCubes*100), (this.heightCubes*100));
        this.playfield.setFill(Color.WHITE);

        root.getChildren().add(s1);
        this.stage.setMinHeight(900);
        this.stage.setMinWidth(1700);
        this.stage.setScene(scene);
    }
    
    //Set the keybindings for this Scene
    public void setKeyBindings(){
//        this.stage.getScene().setOnKeyPressed((KeyEvent keyEvent) -> {
//            if(keyEvent.getCode().toString().equals("W"))
//            {
//                this.game.getCharacter().move(Direction.Up);
//            }
//            
//            if(keyEvent.getCode().toString().equals("A"))
//            {
//                this.game.getCharacter().move(Direction.Left);
//            }
//            
//            if(keyEvent.getCode().toString().equals("S"))
//            {
//                this.game.getCharacter().move(Direction.Down);
//            }
//            
//            if(keyEvent.getCode().toString().equals("D"))
//            {
//                this.game.getCharacter().move(Direction.Right);
//            }
//            
//            if(keyEvent.getCode().toString().equals("Q"))
//            {
//                ICharacter c = (ICharacter) game.getCharacter();
//                c.createBallista(Direction.Right ,4);
//            }
//            
//            if(keyEvent.getCode().toString().equals("E"))
//            {
//                ICharacter c = (ICharacter) game.getCharacter();
//                c.createBallista(Direction.Up ,4);
//            }
//        });
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
        GameUpdate();
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

    public void GameUpdate()
    {
        this.gameTickTimer = new Timer();        
        //Level opnieuw uittekenen met nieuwe posities      
       
        //Geeft momenteel ConcurrentModificationException error
        // Maar deze timer zou dus voor updaten moeten zijn.
        
        this.gameTickTimer.scheduleAtFixedRate(new TimerTask()
        {
            
            @Override
            public void run()
            {
                javafx.application.Platform.runLater(() -> 
                {
                    try
                    {
                        lobbyinstance.updateGame();
                        DrawGame();
                    }
                    catch(NullPointerException | RemoteException ex)
                    {
                    
                    }
                    
                });
                
            }
           
        },500,500);
            
    }

    //Initialiseert de combo boxen
    public void InitCombos() {
        this.LBSpectators.getItems().clear();
        this.LBPlayers.getItems().clear();

//        for (IUser u : this.lobby.getPlayers()) {
//            this.PlayerNames.add(u.toString());
//        }
//
//        for (IUser u : this.lobby.getSpectators()) {
//            this.SpectatorNames.add(u.toString());
//        }
        
        this.CBlevelsizeWidth.setItems(this.observableRoomsizewidth);
        this.CBlevelsizeHeight.setItems(this.observableRoomsizeheight);
        this.CBMinutes.setItems(this.observableTime);
        this.CBrounds.setItems(this.observableRounds);
        UpdateForms();
    }
    
    public void UpdateForms() {
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
           
        //this.CBjoinlobby.setItems(FXCollections.observableArrayList(temp));
        //this.Lblobby.setItems(FXCollections.observableArrayList(temp));
    }
    
    
    
//    public void UpdateForms() {
//        try {
//            this.LBSpectators.setItems(FXCollections.observableList(this.lobbyinstance.getSpectators()));
//            this.LBPlayers.setItems(FXCollections.observableList(this.lobbyinstance.getPlayers()));
//        } catch (RemoteException ex) {
//            System.out.println("Remote exception with updating GUI");
//        }
//    }
}
