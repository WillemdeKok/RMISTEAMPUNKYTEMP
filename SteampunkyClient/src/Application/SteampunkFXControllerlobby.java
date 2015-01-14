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
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;

/**
 *
 * @author Bart
 */
public class SteampunkFXControllerlobby implements Initializable {

    //Lobby
    @FXML
    Label Creatlobbynamelb;
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
    TextField Tfvreatepassword;
    @FXML
    Button Btcreatelobby;
    @FXML
    Button btjoinlobby;
    @FXML
    ListView Lblobby;

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

    private static final String bindingName = "serverMock";

    public void setApp(SteampunkyFX application, Client client, IGameServer ServerMock) {
        this.ServerMock = ServerMock;
        this.clientInfo = client;
        this.main = application;
        LBLLobbyWelcome.setText("Welcome: " + client.getUser());
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.main = application;

        this.LobbyTimer();
        UpdateForms();
    }

    public void JoinLobbyFromLV(Event evt) {
        String s = (String) this.CBjoinlobby.getSelectionModel().getSelectedItem();
        System.out.println(s);
        try {
            for (ILobby l : this.ServerMock.getLobbies()) {
                System.out.println(l.toString());
                if (l.toString().equals(s)) {
                    l.addUser(this.clientInfo.getIUser());
                    this.main.gotoGameRoomselect(clientInfo, l, this.ServerMock);
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
    public void AddLobby() {
        if (TfCreatename.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter a valid name.");
        } else {
            try {
                if (ServerMock.createLobby(TfCreatename.getText(), Tfvreatepassword.getText(), this.clientInfo.getUser())) {
                    System.out.println("Succes!!!");
                    JOptionPane.showMessageDialog(null, "Lobby has been created");
                }
                for (ILobby L : this.ServerMock.getLobbies()) {
                    if (L.GetLobbyname().equals(TfCreatename.getText())) {
                        System.out.println(this.clientInfo.getUser());
                        main.gotoGameRoomselect(this.clientInfo, L, this.ServerMock);                        
                    }
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lobby creation has failed " + ex.getMessage());
                System.out.println("Failed " + ex.getMessage());
            }
        }
        this.UpdateForms();
    }

    @FXML
    public void AddChatmessage(ILobby l, String s) {
        if (TFchat.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter a chat message");
        } else {
            try {
                l.Addchatmessage(s);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Server connection failed" + ex.getMessage());
                System.out.println("Failed" + ex.getMessage());
            }
        }
    }
    
    public void UpdateForms() {
        ArrayList<String> temp = new ArrayList<>();
        
        try {
            for (ILobby i : ServerMock.getLobbies()) {
                temp.add(i.toString());
            }
        } catch (RemoteException ex) {
            System.out.println("Remote Exception");
        }
        
        this.CBjoinlobby.setItems(FXCollections.observableArrayList(temp));
        this.Lblobby.setItems(FXCollections.observableArrayList(temp));
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
