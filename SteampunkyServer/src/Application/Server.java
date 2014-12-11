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
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;

/**
 * OK
 *
 * @author Bart
 */
public class Server extends UnicastRemoteObject implements ILogin {

    //************************datavelden*************************************
    private ArrayList<Lobby> lobbies;
    private ArrayList<User> users;
    private transient ObservableList<User> observableUsers;
    private transient ObservableList<Lobby> observableLobbies;

    private java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
    private ArrayList<IObserver> observers;

    private Connection con;
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

    public ObservableList<Lobby> getLobbies() {
        return (ObservableList<Lobby>) FXCollections.unmodifiableObservableList(observableLobbies);
    }

    public ObservableList<User> getUsers() {
        return (ObservableList<User>) FXCollections.unmodifiableObservableList(observableUsers);
    }

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

    public boolean createLobby(String lobbyName, String password, User admin) {
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

    public boolean joinLobby(Lobby lobby, User user, String password) {
        if (lobby.addUser(user)) {
            return true;
        }
        return false;
    }

    public boolean leaveLobby(Lobby lobby, User user) {

        if (lobby.removeUser(user) == 1) {
            return true;
        } else if (lobby.removeUser(user) == -1) {
            this.observableLobbies.remove(lobby);
            return true;
        }

        return false;
    }

    public boolean deleteLobby(Lobby lobby) {
        Lobby templobby = null;
        for (Lobby lobbylist : observableLobbies) {
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

    public void AddObserver(IObserver observer) throws RemoteException {
        synchronized (lock) {
            this.observers.add(observer);
        }
    }

    public void RemoveObserver(IObserver observer) throws RemoteException {
        synchronized (lock) {
            this.observers.remove(observer);
        }
    }

    public void NotifyObserversLobbies() throws RemoteException {
        synchronized (lock) {
            observers.stream().forEach((observer) -> {
                try {
                    observer.updateLobbies(this.observableLobbies);
                } catch (RemoteException ex) {
                    System.out.println("Observer couldn't be notified");
                }
            });
        }
    }

    public void NotifyObserversUsers() throws RemoteException {
        synchronized (lock) {
            observers.stream().forEach((observer) -> {
                try {
                    observer.updateUsers(this.observableUsers);
                } catch (RemoteException ex) {
                    System.out.println("Observer couldn't be notified");
                }
            });
        }
    }
}
