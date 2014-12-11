/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


/**
 *
 * @author Bart
 */
public class SteampunkFXControllerlobby implements Initializable
{
    //Lobby
    @FXML Label Creatlobbynamelb;
    @FXML Label Creatlobbypasswordlb;
    @FXML Label joinlobbynamelb;
    @FXML Label LBLLobbyWelcome;
    @FXML ComboBox CBjoinlobby;
    @FXML TextField TFchat;
    @FXML TextField TfCreatename;
    @FXML TextField Tfvreatepassword;
    @FXML Button Btcreatelobby;
    @FXML Button btjoinlobby;
    @FXML ListView Lblobby;
    
    
    //list voor de gemaakte lobby's te laten zien
    ArrayList<String> lobbyName;

    //JAVAFX referenties / mee gegeven objecten van andere forums
    private SteampunkyFX main;
    private Client clientInfo;
    

    public void setApp(SteampunkyFX application,Client client)
    {
        this.clientInfo = client;
        this.main = application;
        LBLLobbyWelcome.setText("Welcome: " + client.getUser());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {   
        Clear();
        lobbyName = new ArrayList();
    }

    
    //Maakt een lobby 
    @FXML
    public void AddLobby() {
//        if (TfCreatename.getText().equals("")) {
//            JOptionPane.showMessageDialog(null,"Please enter a valid name.");
//        }
//        else {
//            try {
//                server.createLobby(TfCreatename.getText(), Tfvreatepassword.getText(), user);
//                JOptionPane.showMessageDialog(null,"Lobby has been created");
//                
//                
//                for (Lobby L : this.server.getLobbies()) {
//                    if (L.GetLobbyname().equals(TfCreatename.getText())) {
//                        L.addUser(user);
//                        main.gotoGameRoomselect(user, L);
//                    }
//                }
//            }
//            catch (Exception ex) {
//                JOptionPane.showMessageDialog(null,"Lobby creation has failed" + ex.getMessage());
//                        System.out.println("Failed" + ex.getMessage());
//            }
//        }
    }
    
    @FXML
    public void AddChatmessage() {
//        if (TFchat.getText().equals("")) {
//            JOptionPane.showMessageDialog(null,"Pleas enter a chat message");
//        }
//        else {
//            try {
//                lobby.Addchatmessage(Tfvreatepassword.getText());
//            }
//            catch (Exception ex) {
//                JOptionPane.showMessageDialog(null,"Server connection faild" + ex.getMessage());
//                        System.out.println("Failed" + ex.getMessage());
//            }
//        }
    }

    //update de rooms die worden aangemaakt in de listview
//    @Override
//    public void update(Observable o, Object o1) {
//        try {
//            Lobby lobby = (Lobby) o1;
//            for (Lobby l : server.getLobbies()) {
//                lobbyName.add(l.toString());
//            }
//        } catch(Exception ex) {
//            System.out.println("Not an lobby");
//        }
//        
//        
//        Lblobby.setItems(FXCollections.observableList(lobbyName));
//        CBjoinlobby.setItems(FXCollections.observableList(lobbyName));
//    }
    
    public void Clear() {
        Lblobby.getItems().clear();
        CBjoinlobby.getItems().clear();
    }
}
