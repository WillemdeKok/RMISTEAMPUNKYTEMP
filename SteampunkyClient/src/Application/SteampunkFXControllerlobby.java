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
public class SteampunkFXControllerlobby implements Initializable, Observer {

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

    public void setApp(SteampunkyFX application, Client client, String ipAddress, int portNumber) {
        this.clientInfo = client;
        this.main = application;
        LBLLobbyWelcome.setText("Welcome: " + client.getUser());
        this.lobbies = new ArrayList();
        this.observablelobbies = observableList(lobbies);
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.main = application;

        // Print IP address and port number for registry
        System.out.println("Client: IP Address: " + this.ipAddress);
        System.out.println("Client: Port number " + this.portNumber);

        this.LobbyTimer();
        Serverlogin();

    }

    public void Serverlogin() {
        // Locate registry at IP address and port number
        try {
            registry = LocateRegistry.getRegistry(this.ipAddress, this.portNumber);
        } catch (RemoteException ex) {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
            registry = null;
        }

        // Print result locating registry
        if (registry != null) {
            System.out.println("Client: Registry located");
        } else {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: Registry is null pointer");
        }
        // Bind student administration using registry
        if (registry != null) {
            try {
                ServerMock = (IGameServer) registry.lookup(bindingName);
                
                for (ILobby temp : ServerMock.getLobbies()) {
                    this.observablelobbies.add(temp);
                }
                
            } catch (RemoteException ex) {
                System.out.println("Client: Cannot bind ILogin");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                ServerMock = null;
            } catch (NotBoundException ex) {
                System.out.println("Client: Cannot bind ILogin");
                System.out.println("Client: NotBoundException: " + ex.getMessage());
                ServerMock = null;
            }
        }

        if (ServerMock != null) {
            System.out.println("Client: Login is bound");
        } else {
            System.out.println("Client: Login is null pointer");
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
                ServerMock.createLobby(TfCreatename.getText(), Tfvreatepassword.getText(), this.clientInfo.getUser());
                JOptionPane.showMessageDialog(null, "Lobby has been created");
                for (ILobby L : this.ServerMock.getLobbies()) {
                    if (L.GetLobbyname().equals(TfCreatename.getText())) {
                        this.clientInfo.setLobby(L);
                    }
                }
                main.gotoGameRoomselect(this.clientInfo, this.ipAddress, this.portNumber);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lobby creation has failed" + ex.getMessage());
                System.out.println("Failed " + ex.getMessage());
            }
        }
    }

    @FXML
    public void AddChatmessage() {
        if (TFchat.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter a chat message");
        } else {
            try {
                this.clientInfo.getLobby().Addchatmessage(Tfvreatepassword.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Server connection faild" + ex.getMessage());
                System.out.println("Failed" + ex.getMessage());
            }
        }
    }

    //update de rooms die worden aangemaakt in de listview
    public void update(Observable o, Object o1) {
        try {
            ILobby lobby = (ILobby) o1;
            for (ILobby L : ServerMock.getLobbies()) {
                lobbyName.add(L.toString());
            }
        } catch (Exception ex) {
            System.out.println("Not an lobby");
        }

        Lblobby.setItems(FXCollections.observableList(lobbyName));
        CBjoinlobby.setItems(FXCollections.observableList(lobbyName));
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
                            ArrayList<ILobby> listlobby;
                            listlobby = ServerMock.getLobbies();
                            if (listlobby != null) {
                                observablelobbies = FXCollections.observableArrayList(listlobby);
                            } else {
                                System.out.println("lobbies = null");
                            }
                           
                            System.out.println(observablelobbies.size());
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }

                });
            }
        }, 0, 1000);
    }

    @Override
    public void update(Observable o, java.lang.Object arg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
