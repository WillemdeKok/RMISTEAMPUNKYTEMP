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
public class Server extends UnicastRemoteObject implements IGameServer {

    //************************datavelden*************************************
    private ArrayList<ILobby> lobbies;
    private ArrayList<IUser> users;
    private transient ObservableList<IUser> observableUsers;
    private transient ObservableList<ILobby> observableLobbies;

    private java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
    private ArrayList<IObserver> observers;

    private Connection con;
    private User newuser;
    private static Server server = null;
    private int StartRating = 100;
    private int GetRating = 0;

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

    public ObservableList<IUser> getUsersAsUser() {
        return (ObservableList<IUser>) FXCollections.unmodifiableObservableList(observableUsers);
    }

    @Override
    public ArrayList<String> getUsers() {
        ArrayList<String> tempusers = new ArrayList();
        for (IUser U : observableUsers) {
            try {
                tempusers.add(U.getUsername());
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return tempusers;
    }

    @Override
    public void Connectionstring() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://stormhost.nl:3306/admin_bart", "admin_bart", "8IUAsf1E");
        } catch (Exception ex) {
            System.out.println("Geen verbinding met database mogelijk: " + ex);
        }
    }

    @Override
    public boolean RemoveUser(String tempuser) {
        IUser temp = null;
        for (IUser tempacoount : observableUsers) {
            try {
                if (tempacoount.getUsername().equals(tempuser)) {
                    temp = tempacoount;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (temp != null) {
            observableUsers.remove(temp);
            return true;
        }
        return false;
    }

    public void Userlogedin(IUser tempuser) {
        if (tempuser != null) {
            observableUsers.add(tempuser);
            System.out.println("User has been added to the list");
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
                if (rs.getString("NAAM").equals(username)) {
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
                String querywrite = "INSERT INTO USERS VALUES (2,?,?,?)";
                PreparedStatement stat2 = con.prepareStatement(querywrite);
                stat2.setString(1, username);
                stat2.setString(2, password);
                stat2.setInt(3, StartRating);
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

    //Rating laten dalen met een waarde die hij binnenkrijgt in de methode
    @Override
    public int DecreaseRating(String username, int rating) {
        int oldrating = 0;
        int newrating = 0;
        try {
            Connectionstring();
            oldrating = GetRating(username);
            newrating = oldrating - rating;
            if (newrating >= 0) {
                System.out.println("Verbing maken is geslaagd voor het DecreaseRating van de Rating");
                String querywrite = "UPDATE USERS SET RATING = ? WHERE NAAM = ?";
                PreparedStatement stat2 = con.prepareStatement(querywrite);
                stat2.setInt(1, newrating);
                stat2.setString(2, username);
                stat2.execute();
                con.close();
            } else {
                newrating = 0;
            }
        } catch (Exception ex) {
            System.out.println("Gebruiker niet gevonden voor rating" + ex);
            return 0;
        }
        System.out.println("Rating ophalen is mislukt");
        return newrating;
    }

    //Rating laten stijgen met een waarde die hij binnenkrijgt in de methode
    @Override
    public int IncreaseRating(String username, int rating) {
        int oldrating = 0;
        int newrating = 0;
        try {
            Connectionstring();
            oldrating = GetRating(username);
            newrating = oldrating + rating;
            System.out.println("Verbing maken is geslaagd voor het DecreaseRating van de Rating");
            String querywrite = "UPDATE USERS SET RATING = ? WHERE NAAM = ?";
            PreparedStatement stat2 = con.prepareStatement(querywrite);
            stat2.setInt(1, oldrating + rating);
            stat2.setString(2, username);
            stat2.execute();
            con.close();
        } catch (Exception ex) {
            System.out.println("Gebruiker niet gevonden voor rating" + ex);
            return 0;
        }
        System.out.println("Rating ophalen is mislukt");
        return newrating;
    }

    //Rating van dit moment ophalen uit de database bij een username
    @Override
    public ArrayList<String> GetTotalrating() {
        ArrayList<String> ratinglist = new ArrayList();
        try {
            Connectionstring();
            System.out.println("Verbing maken is geslaagd voor het ophalen van de Rating");
            String queryread = "SELECT NAAM,RATING FROM USERS ORDER BY RATING DESC,NAAM";
            PreparedStatement stat2 = con.prepareStatement(queryread);
            ResultSet rs = stat2.executeQuery();
            while (rs.next()) {
                ratinglist.add(rs.getString("NAAM") + ": " + rs.getInt("RATING"));
            }
            con.close();
            return ratinglist;

        } catch (Exception ex) {
            System.out.println("Gebruiker niet gevonden voor rating" + ex);
            return null;
        }
    }

    //Rating van dit moment ophalen uit de database bij een username
    @Override
    public int GetRating(String username) {
        try {
            Connectionstring();
            System.out.println("Verbing maken is geslaagd voor het ophalen van de Rating");
            String queryread = "SELECT NAAM,RATING FROM USERS";
            PreparedStatement stat2 = con.prepareStatement(queryread);
            ResultSet rs = stat2.executeQuery();
            while (rs.next()) {
                if (rs.getString("NAAM").equals(username)) {
                    GetRating = rs.getInt("Rating");
                    System.out.println("Rating gevonden");
                    return GetRating;
                }
            }
            con.close();

        } catch (Exception ex) {
            System.out.println("Gebruiker niet gevonden voor rating" + ex);
            return 0;
        }
        System.out.println("Rating ophalen is mislukt");
        return 0;
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
                    User u = new User(username, password);
                    Userlogedin((IUser) u);
                    //AddUserToList((IUser) u);
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
    public synchronized boolean createLobby(String lobbyName, String password, String username, IGameServer server) {
        System.out.println("ik ben een lobby");
        IUser admin = null;

        for (IUser U : this.observableUsers) {
            try {
                if (U.getUsername().equals(username)) {
                    admin = U;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            System.out.println(lobbyName + " " + admin.getUsername());
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (lobbyName != null && admin != null) {
            try {
                Lobby lobby;
                this.observableLobbies.add(lobby = new Lobby(lobbyName, admin, password, server));
                return true;
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Admin of lobbyname is null");
            return false;
        }
        return false;
    }

    @Override
    public boolean joinLobby(ILobby lobby, String user, String password) throws RemoteException {
        if (lobby.addUser(Getuser(user))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean leaveLobby(ILobby lobby, String user) throws RemoteException {
        ILobby temp = null;
        if (lobby.removeUser(user) == 1) {
            return true;
        } else if (lobby.removeUser(user) == -1) {
            for (ILobby listlobby : observableLobbies) {
                if (listlobby.GetLobbyname().equals(lobby.GetLobbyname())) {
                    temp = listlobby;
                }
            }
            this.observableLobbies.remove(temp);
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

//    public void AddUserToList(IUser u) {
//        this.observableUsers.add(u);
//    }
    public IUser Getuser(String username) {
        IUser tempuser = null;
        for (IUser user : this.observableUsers) {
            try {
                if (user.getUsername().equals(username)) {
                    tempuser = user;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return tempuser;
    }
}
