/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 *
 * @author bart
 */
public class SteampunkFXControllerLogin implements Initializable {

    // Loginproftaak
    @FXML
    Tab loginuser;
    @FXML
    Tab Createuser;
    @FXML
    TabPane Logintabs;
    //Login user
    @FXML
    Button BTloginuserlogin;
    @FXML
    Button BTExitlogin;
    @FXML
    TextField TFUsernamelogin;
    @FXML
    TextField TFWachtwoordlogin;
    @FXML
    Label LBUsernamelogin;
    @FXML
    Label LBWachtwoordlogin;

    //Create user
    @FXML
    Button BtCreatecreate;
    @FXML
    Button BTExitcreate;
    @FXML
    TextField TFUsernamecreate;
    @FXML
    TextField TFWachtwoordcreate;
    @FXML
    Label LBUsernamecreate;
    @FXML
    Label LBWachtwoordcreate;

    // Lobby
    @FXML
    Button Btcreatelobby;
    @FXML
    Button Btdeletelobby;
    @FXML
    Button btjoinlobby;
    @FXML
    TextField TfCreatename;
    @FXML
    TextField Tfvreatepassword;
    @FXML
    ListView Lblobby;
    @FXML
    ComboBox CBdeletelobby;
    @FXML
    ComboBox CBjoinlobby;

    private SteampunkyFX main;
    private int portNumber;
    private String ipAddress;
    // Set binding name for student administration
    private static final String bindingName = "serverMock";

    // References to registry and student administration
    private Registry registry = null;
    private IGameServer ServerMock;
    private Client clientInfo;

    public SteampunkFXControllerLogin() {

    }

    public void setApp(SteampunkyFX application, String ipAddress, int portNumber) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.main = application;

        clientInfo = new Client();
        // Print IP address and port number for registry
        System.out.println("Client: IP Address: " + this.ipAddress);
        System.out.println("Client: Port number " + this.portNumber);

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

//<editor-fold defaultstate="collapsed" desc="Outcommented initialize for push">
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        try {
//            RemotePublisher publisher = (RemotePublisher) Naming.lookup("rmi://localhost:1099/server");
//            publisher.addListener(this, "server");
//        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
//            Logger.getLogger(SteampunkFXControllerLogin.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
//</editor-fold>

    @FXML
    private void BTExitlogin() {
        Stage stage = (Stage) BTExitlogin.getScene().getWindow();
        stage.close();
    }

    //Kijk of de user kan inloggen met het opgeven wachtwoord en username
    @FXML
    private void Btlogin() throws IOException {
        boolean loggedin = false;

        for (String userlogedin : this.ServerMock.getUsers()) {
            if (userlogedin.equals(TFUsernamelogin.getText())) {
                loggedin = true;
            }
        }

        if (TFUsernamelogin.getText().isEmpty() && TFWachtwoordlogin.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password or username is empty");
        } else {
            if (loggedin == false) {
                if (ServerMock.loginUser(TFUsernamelogin.getText(), TFWachtwoordlogin.getText())) {
                    System.out.println("login succes");
                    try {
                        clientInfo.setUser(TFUsernamelogin.getText());
                        clientInfo.setPassword(TFWachtwoordlogin.getText());
                        clientInfo.setRating(this.ServerMock.GetRating(TFUsernamelogin.getText()));
                        clientInfo.setIUser(this.ServerMock.Getuser(clientInfo.getUser()));
                        main.gotoLobbyselect(this.clientInfo, this.ServerMock);
                    } catch (Exception ex) {
                        System.out.println("Error at starting lobby : " + ex);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Password or username are incorrect");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Already logged in");
            }
        }
    }

    //Maakt een user aan in de database
    @FXML
    private void BtCreatecreate() {
        if (TFUsernamecreate.getText().isEmpty() && TFWachtwoordcreate.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No username or password was filled in");
        } else {
            try {
                if (ServerMock.createUser(TFUsernamecreate.getText(), TFWachtwoordcreate.getText())) {
                    Logintabs.getSelectionModel().select(loginuser);
                    JOptionPane.showMessageDialog(null, "User created");
                } else {
                    JOptionPane.showMessageDialog(null, "User already registerd");
                }
            } catch (RemoteException ex) {
                System.out.println(ex);
            }
        }
    }
//<editor-fold defaultstate="collapsed" desc="outcommented propertyChange for push">

    public void propertyChange(PropertyChangeEvent pce) throws RemoteException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//</editor-fold>
}
