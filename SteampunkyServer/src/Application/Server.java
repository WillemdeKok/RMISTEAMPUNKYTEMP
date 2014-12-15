/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;

/**
 * OK
 *
 * @author Bart
 */
public class Server extends UnicastRemoteObject implements IGameServer,IServer {

    //************************datavelden*************************************
    private ArrayList<ILobby> lobbies;
    private ArrayList<User> users;
    private transient ObservableList<User> observableUsers;
    private transient ObservableList<ILobby> observableLobbies;

    private java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
    private ArrayList<IObserver> observers;

    private Connection con;
    private User newuser;
    private static Server server = null;

    //***********************constructoren***********************************
    /**
     * creates a server with ...
     *
     */
    private Server() throws RemoteException {
        this.lobbies = new ArrayList();
        this.users = new ArrayList();
        this.observers = new ArrayList();

        observableUsers = observableList(users);
        observableLobbies = observableList(lobbies);

    }

    @Override
    public ArrayList<ILobby> getLobbies() {
        return this.lobbies;
    }
    
    public ObservableList<User> getUsersAsUser() {
        return (ObservableList<User>) FXCollections.unmodifiableObservableList(observableUsers);
    }
    
    @Override
    public ArrayList<String> getUsers(){
        ArrayList<String> tempusers = new ArrayList();
        for(User U : observableUsers){
            tempusers.add(U.getUsername());
        }
        return tempusers;
    }
            

    @Override
    public void Connectionstring() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://stormhost.nl:3306/admin_bart", "admin_bart", "8IUAsf1E");
        } catch (Exception ex) {
            System.out.println("Geen verbinding met database mogelijk: " + ex);
        }
    }

 
    public void Userlogedin(User tempuser) {
        if (tempuser != null) {
            observableUsers.add(tempuser);
        }
    }

    //**********************methoden****************************************
    @Override
    public boolean createUser(String username, String password) {
        boolean adduser = true;
        try {
            Connectionstring();
            System.out.println("Verbing maken is geslaagd voor add user");
            Statement stat = con.createStatement();
            String queryread = "SELECT NAAM FROM USERS";
            ResultSet rs = stat.executeQuery(queryread);
            while (rs.next()) {
                if (rs.getString("Naam").equals(username)) {
                    adduser = false;
                    System.out.println("Dubbele gebruiker gevonden");
                }
            }
            con.close();
        } catch (Exception ex) {
            System.out.println("Dubbele gebruiker gevonden" + ex);
            return false;
        }

        if (adduser == true) {
            try {
                Connectionstring();
                String querywrite = "INSERT INTO USERS VALUES (2,?,?)";
                PreparedStatement stat2 = con.prepareStatement(querywrite);
                stat2.setString(1, username);
                stat2.setString(2, password);
                stat2.execute();
                System.out.println("Aanmaken van de user is gelukt: ");
                con.close();
                return true;
            } catch (Exception ex) {
                System.out.println("Aanmaken van de user is milsukt: " + ex);
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean loginUser(String username, String password) {
        try {
            Connectionstring();
            System.out.println("Verbing maken is geslaagd voor het in loggen van de user");
            String queryread = "SELECT NAAM,WACHTWOORD FROM USERS";
            PreparedStatement stat2 = con.prepareStatement(queryread);
            ResultSet rs = stat2.executeQuery();
            while (rs.next()) {
                if (rs.getString("NAAM").equals(username) && rs.getString("WACHTWOORD").equals(password)) {
                    System.out.println("Gebruiker mag inloggen");
                    this.Userlogedin(new User(username, password));
                    return true;
                }
            }
            con.close();
            
            
        } catch (Exception ex) {
            System.out.println("Gebruiker niet gevonden" + ex);
            return false;
        }
        System.out.println("Gebruiker mag niet inloggen");
        return false;
    }
    
    @Override
    public boolean createLobby(String lobbyName,String password,String username) {
        System.out.println("ik ben een lobby");
        User admin = null;
        for(User U: this.observableUsers)
        {
            if(U.getUsername().equals(username))
            {
                admin = U;
            }
        }
        System.out.println(lobbyName +" "+ username);
        if (lobbyName != null && admin != null) {
            Lobby lobby;
            this.observableLobbies.add(lobby = new Lobby(lobbyName, admin, password));
            try {
                this.NotifyObserversLobbies();
            } catch (RemoteException ex) {
                System.out.println("Lobby creation threw a remote Exception:    " + ex.getMessage());
            }
            return true;
        } else {
            System.out.println("Admin of lobbyname is null");
            return false;
        }
    }

    @Override
    public boolean joinLobby(ILobby lobby, String user, String password) throws RemoteException {
        if (lobby.addUser(user)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean leaveLobby(ILobby lobby, String user) throws RemoteException {

        if (lobby.removeUser(user) == 1) {
            return true;
        } else if (lobby.removeUser(user) == -1) {
            this.observableLobbies.remove(lobby);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteLobby(ILobby lobby) {
        ILobby templobby = null;
        for (ILobby lobbylist : observableLobbies) {
            if (lobbylist == lobby) {
                templobby = lobbylist;
            }
        }
        if (templobby != null) {
            observableLobbies.remove(templobby);
            return true;
        } else {
            return false;
        }
    }

    public static Server getServer() throws RemoteException {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    @Override
    public void AddObserver(IObserver observer) throws RemoteException {
        synchronized (lock) {
            this.observers.add(observer);
        }
    }

    @Override
    public void RemoveObserver(IObserver observer) throws RemoteException {
        synchronized (lock) {
            this.observers.remove(observer);
        }
    }

    @Override
    public void NotifyObserversLobbies() throws RemoteException {
        synchronized (lock) {
            observers.stream().forEach((observer) -> {
                try {
                    observer.updateLobbies((ArrayList<ILobby>) this.observableLobbies);
                } catch (RemoteException ex) {
                    System.out.println("Observer couldn't be notified");
                }
            });
        }
    }

    @Override
    public void NotifyObserversUsers() throws RemoteException {
        synchronized (lock) {
            observers.stream().forEach((observer) -> {
                ArrayList<String> tempUsers = new ArrayList();
                for(User U : observableUsers){
                    tempUsers.add(U.getUsername());
                }
                ObservableList temp = observableList(tempUsers);
                try {
                    observer.updateLobbies((ArrayList<ILobby>) temp);
                } catch (RemoteException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
}
