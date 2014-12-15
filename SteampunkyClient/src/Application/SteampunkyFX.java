/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 *
 * @author Bart
 */
public class SteampunkyFX extends Application {

    private Stage stage;
    private int portNumber;
    private String ipAddress;
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("CLIENT USING REGISTRY");
        Scanner input = new Scanner(System.in);
        System.out.print("CLIENT: ENTER IPADRESS:");
        ipAddress = input.nextLine();
        System.out.print("CLIENT: ENTER PORTNUMBER:");
        portNumber = input.nextInt();
        try {
            this.stage = stage;
            this.stage.setTitle("SteamPunky");
            this.stage.setMinWidth(100);
            this.stage.setMinHeight(100);
            gotoLoginselect();
            

            this.stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void gotoLoginselect() {
        try {
            SteampunkFXControllerLogin loginselect =(SteampunkFXControllerLogin) replaceSceneContent("LoginProftaak2.fxml");
            loginselect.setApp(this, ipAddress, portNumber); 
        } catch (Exception ex) {
            Logger.getLogger(SteampunkyFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void gotoLobbyselect(Client client,String ipAddress, int portNumber) {
        try {
            SteampunkFXControllerlobby lobbyselect = (SteampunkFXControllerlobby) replaceSceneContent("Lobby3.fxml");
            lobbyselect.setApp(this, client,ipAddress,portNumber);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            Logger.getLogger(SteampunkyFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void gotoGameRoomselect(Client client,String ipAddress, int portNumber) {
        try {
            GameRoomController GameRoomselect = (GameRoomController) replaceSceneContent("GameRoom.fxml");
  //          GameRoomselect.setApp(this,, stage);
        } catch (Exception ex) {
            Logger.getLogger(SteampunkyFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = SteampunkyFX.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(SteampunkyFX.class.getResource(fxml));
        AnchorPane page;
        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        }
        
        Scene scene = null;
        if(fxml.equals("LoginProftaak2.fxml"))
        {
            this.stage.setMinWidth(300);
            this.stage.setMinHeight(268);
            scene = new Scene(page, 300, 268); 
        }
        else if(fxml.equals("Lobby3.fxml"))
        {
            this.stage.setMinWidth(630);
            this.stage.setMinHeight(450);
            scene = new Scene(page, 630, 450); 
        }        
        else if(fxml.equals("GameRoom.fxml"))
        {
            this.stage.setMinWidth(600);
            this.stage.setMinHeight(400);
            scene = new Scene(page, 600, 400); 
        }
        else {
            System.out.println("FATAL GUI ERROR");
        }
        
        if (scene != null) {
            scene.getStylesheets().add(SteampunkyFX.class.getResource("style.css").toExternalForm());
        stage.setTitle("Steampunky");
        stage.getIcons().add(new Image(SteampunkyFX.class.getResourceAsStream("icon.png"))); 
        stage.setScene(scene);
        } else {
            System.out.println("Create of scene in steampinkfx faild");
        }
        
        
        //stage.sizeToScene();
        return (Initializable) loader.getController();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public Stage getstage()
    {
        return this.stage;
    }  
}
