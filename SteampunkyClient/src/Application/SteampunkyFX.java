/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.io.InputStream;
import java.net.Inet4Address;
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
    private boolean uitloggen = false;
    private Scene scene;
    private AnchorPane page;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("CLIENT USING REGISTRY");
        Scanner input = new Scanner(System.in);
        //To be changed in case of deployment to the IP adres of the server you want to connect to.
        ipAddress = Inet4Address.getLocalHost().getHostAddress();
        portNumber = 1099;
        try {
            this.stage = stage;
            this.stage.setTitle("SteamPunky");
            this.stage.setMinWidth(100);
            this.stage.setMinHeight(100);
            stage.setResizable(false);
            gotoLoginselect();

            this.stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void gotoLoginselect() {
        try {
            SteampunkFXControllerLogin loginselect = (SteampunkFXControllerLogin) replaceSceneContent("LoginProftaak2.fxml");
            loginselect.setApp(this, ipAddress, portNumber, this.stage);
        } catch (Exception ex) {
            Logger.getLogger(SteampunkyFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void gotoLobbyselect(Client client, IGameServer ServerMock) {
        try {
            SteampunkFXControllerlobby lobbyselect = (SteampunkFXControllerlobby) replaceSceneContent("Lobby3.fxml");
            lobbyselect.setApp(this, client, ServerMock, this.stage);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            Logger.getLogger(SteampunkyFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void gotoGameRoomselect(Client client, ILobby l, IGameServer ServerMock) {
        try {
            GameRoomController GameRoomselect = (GameRoomController) replaceSceneContent("GameRoom.fxml");
            GameRoomselect.setApp(this, this.stage, client, l, ServerMock);
        } catch (Exception ex) {
            Logger.getLogger(SteampunkyFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = SteampunkyFX.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(SteampunkyFX.class.getResource(fxml));
        try {
            this.page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        }

        if (fxml.equals("LoginProftaak2.fxml")) {
            this.stage.setWidth(300);
            this.stage.setHeight(268);
            this.scene = new Scene(this.page, 300, 268);
        } else if (fxml.equals("Lobby3.fxml")) {
            this.stage.setWidth(700);
            this.stage.setHeight(425);
            this.scene = new Scene(this.page, 700, 400);
        } else if (fxml.equals("GameRoom.fxml")) {
            this.stage.setWidth(1048);
            this.stage.setHeight(590);
            this.stage.setX(100);
            this.stage.setY(100);
            this.scene = new Scene(this.page, 1048, 590);
        } else {
            System.out.println("FATAL GUI ERROR");
        }

        if (this.scene != null) {
            this.scene.getStylesheets().add(SteampunkyFX.class.getResource("style.css").toExternalForm());
            this.stage.setTitle("Steampunky");
            this.stage.getIcons().add(new Image(SteampunkyFX.class.getResourceAsStream("icon.png")));
            this.stage.setScene(this.scene);
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

    public Stage getstage() {
        return this.stage;
    }
}
