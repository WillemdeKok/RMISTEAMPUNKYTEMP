/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author Bart
 */
public class SteampunkFXControllerlobby extends UnicastRemoteObject implements Initializable {

    //Lobby
    @FXML
    Label Creatlobbynamelb;
    @FXML
    Label LBRating;
    @FXML
    Label Creatlobbypasswordlb;
    @FXML
    Label joinlobbynamelb;
    @FXML
    Label LBLLobbyWelcome;
    @FXML
    ComboBox CBjoinlobby;
    @FXML
    TextField TFchat;
    @FXML
    TextField TfCreatename;
    @FXML
    TextField Tfcreatepassword;
    @FXML
    Button Btcreatelobby;
    @FXML
    Button btjoinlobby;
    @FXML
    Button BTuitloggen;
    @FXML
    ListView Lblobby;
    @FXML
    ListView LVrating;
    @FXML
    ListView LBHasPasword;

    Timer LobbyTimer;
    ArrayList<ILobby> lobbies;
    private transient ObservableList<ILobby> observablelobbies;

    //list voor de gemaakte lobby's te laten zien
    ArrayList<String> lobbyName;

    //JAVAFX referenties / mee gegeven objecten van andere forums
    private SteampunkyFX main;
    private Client clientInfo;
    private int portNumber;
    private String ipAddress;
    private Registry registry = null;
    private IGameServer ServerMock;
    private Stage stage;

    private static final String bindingName = "serverMock";

    public SteampunkFXControllerlobby() throws RemoteException {
    }

    public void setApp(SteampunkyFX application, Client client, IGameServer ServerMock, Stage stage) throws RemoteException {
        this.stage = stage;
        this.ServerMock = ServerMock;
        this.clientInfo = client;
        this.main = application;
        LBLLobbyWelcome.setText("Welcome: " + client.getUser());
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.main = application;
        this.LBRating.setText("Rating: " + this.clientInfo.getRating());
        this.LobbyTimer();
        ArrayList<String> ratinglist = this.ServerMock.GetTotalrating();
        this.LVrating.setItems(FXCollections.observableArrayList(ratinglist).sorted());

        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("Exited lobby");

                        try {
                            boolean removeuser = ServerMock.RemoveUser(clientInfo.getUser());
                            if (removeuser == true) {
                                System.out.println("Loguit succesvol");
                            } else {
                                System.out.println("Loguit failed");
                            }
                        } catch (RemoteException ex) {
                            Logger.getLogger(SteampunkFXControllerlobby.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Loguit failed");
                        }
                        System.exit(0);
                    }
                });
            }
        });

        UpdateForms();

    }

    public void Logout(Event evt) {
        try {
            boolean removeuser = this.ServerMock.RemoveUser(this.clientInfo.getUser());
            if (removeuser == true) {
                System.out.println("Loguit succesvol");
                this.main.gotoLoginselect();
            } else {
                System.out.println("Loguit failed");
            }
        } catch (RemoteException ex) {
            Logger.getLogger(SteampunkFXControllerlobby.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Loguit failed");
        }
    }

    public void JoinLobbyFromLV(Event evt) {

        System.out.println("Event triggered");
        String s = "";
        try {
            s = this.Lblobby.getSelectionModel().getSelectedItem().toString();
        } catch (Exception ex) {
            System.out.println("Lobby string not valid");
        }
        System.out.println(s);
        try {
            for (ILobby l : this.ServerMock.getLobbies()) {
                System.out.println(l.GetLobbyname());
                if (l.GetLobbyname().equals(s)) {
                    if (l.checkPassword("")) {
                        l.addUser(this.clientInfo.getIUser());
                        this.main.gotoGameRoomselect(clientInfo, l, this.ServerMock);
                    } else {
                        String input = JOptionPane.showInputDialog("Wachtwoord van lobby: ");
                        if (l.checkPassword(input)) {
                            l.addUser(this.clientInfo.getIUser());
                            this.main.gotoGameRoomselect(clientInfo, l, this.ServerMock);
                        } else {
                            JOptionPane.showMessageDialog(null, "Wachtwoord van lobby is niet juist");
                        }
                    }
                }
            }
        } catch (RemoteException ex) {
            System.out.println("Remote Exception has been thrown");
        }
    }

    public void JoinLobbyFromCB(Event evt) {
        System.out.println("Event triggered");
        String s = "";
        try {
            s = this.CBjoinlobby.getSelectionModel().getSelectedItem().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Lobby string not valid in combobox" + ex.getMessage());
        }
        System.out.println(s);
        try {
            for (ILobby l : this.ServerMock.getLobbies()) {
                System.out.println(l.GetLobbyname());
                if (l.GetLobbyname().equals(s)) {
                    if (l.checkPassword("")) {
                        l.addUser(this.clientInfo.getIUser());
                        this.main.gotoGameRoomselect(clientInfo, l, this.ServerMock);
                    } else {
                        String input = JOptionPane.showInputDialog("Wachtwoord van lobby: ");
                        if (l.checkPassword(input)) {
                            l.addUser(this.clientInfo.getIUser());
                            this.main.gotoGameRoomselect(clientInfo, l, this.ServerMock);
                        } else {
                            JOptionPane.showMessageDialog(null, "Wachtwoord van lobby is niet juist");
                        }
                    }
                }
            }
        } catch (RemoteException ex) {
            System.out.println("Remote Exception has been thrown");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Clear();
        lobbyName = new ArrayList();
    }

    //Maakt een lobby 
    @FXML
    public void AddLobby() throws RemoteException {
        if (TfCreatename.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter a valid name.");
        } else {
            try {
                if (ServerMock.createLobby(TfCreatename.getText(), Tfcreatepassword.getText(), this.clientInfo.getUser(), ServerMock)) {
                    System.out.println("Succes!!!");
                    JOptionPane.showMessageDialog(null, "Lobby has been created");
                }
                for (ILobby L : this.ServerMock.getLobbies()) {
                    if (L.GetLobbyname().equals(TfCreatename.getText())) {
                        System.out.println("User in AddLobby method " + this.clientInfo.getUser());
                        this.ServerMock.joinLobby(L, this.clientInfo.getUser(), this.clientInfo.getPassword());
                        main.gotoGameRoomselect(this.clientInfo, L, this.ServerMock);
                    }
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lobby creation has failed " + ex.getMessage());
                ex.printStackTrace();
                System.out.println("Failed " + ex.getMessage());
            }
        }
        this.UpdateForms();
    }

    public void UpdateForms() throws RemoteException {
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<String> password = new ArrayList<>();

        try {
            for (ILobby i : ServerMock.getLobbies()) {
                if (i.checkPassword("")) {
                    temp.add(i.GetLobbyname());
                    password.add("Password: NO");
                } else {
                    temp.add(i.GetLobbyname());
                    password.add("Password: YES");
                }
            }
        } catch (RemoteException ex) {
            System.out.println("Remote Exception");
            ex.printStackTrace();
        }

        this.CBjoinlobby.setItems(FXCollections.observableArrayList(temp));
        this.Lblobby.setItems(FXCollections.observableArrayList(temp));
        this.LBHasPasword.setItems(FXCollections.observableArrayList(password));
    }

    public void Clear() {
        Lblobby.getItems().clear();
        CBjoinlobby.getItems().clear();
    }

    public void LobbyTimer() {

        this.LobbyTimer = new Timer();
        //Level opnieuw uittekenen met nieuwe posities      

        //Geeft momenteel ConcurrentModificationException error
        // Maar deze timer zou dus voor updaten moeten zijn.
        this.LobbyTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UpdateForms();
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }

                });
            }
        }, 0, 1000);
    }
}
