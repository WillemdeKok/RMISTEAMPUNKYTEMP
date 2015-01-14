/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Application;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;

/**
 * OK
 * @author Bart
 */
public class Lobby extends Observable implements ILobby, Serializable
{
    //************************datavelden*************************************
    private int lobbyID;
    private int nextLobbyID = 1;
    private String lobbyName;
    private String password;
    private String map;
    private ArrayList<String> chatMessages;
    private transient ObservableList<String> observableChat;
    private IUser admin;
    private ArrayList<IUser> spectators;
    private transient ObservableList<IUser> observableSpectators;
    private ArrayList<IUser> players;
    private transient ObservableList<IUser> observablePlayers;
    private ArrayList<Game> games;
    private int ratingDifference;
    private Game game;

    //***********************constructoren***********************************
    /**
     * creates a lobby with ...
     */
    public Lobby(String lobbyname, IUser addedByUser, String password)
    {      
        System.out.println("Lobby has been created");
        this.lobbyName = lobbyname;
        this.admin = addedByUser;
        this.lobbyID = this.nextLobbyID;
        this.nextLobbyID++;
        this.password = password;
        this.admin = addedByUser;
        
        this.games = new ArrayList<>();
        this.spectators = new ArrayList<>();
        observableSpectators = observableList(spectators);
        this.players = new ArrayList<>();
        observablePlayers = observableList(players);
        this.chatMessages = new ArrayList<>();
        observableChat = observableList(chatMessages);
        this.addUser(admin);

    }

    @Override
    public String GetLobbyname(){
       return this.lobbyName;       
    }
    
    public ObservableList<IUser> getPlayersAsUsers() {
        return (ObservableList<IUser>) FXCollections.unmodifiableObservableList(this.observablePlayers);
    }
    
    public ObservableList<IUser> getSpectatorsAsUsers() {
        return (ObservableList<IUser>) FXCollections.unmodifiableObservableList(this.observableSpectators);
    }
    
    @Override
    public ArrayList<String> getSpectators() {
        ArrayList<String> temp = new ArrayList();      
        for (IUser u : this.observableSpectators) {
            try {
                temp.add(u.getUsername());
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("temp size: " + temp.size());
        return temp;
    }
    
    @Override
    public ArrayList<String> getPlayers() {
        ArrayList<String> temp = new ArrayList();
        
        for (IUser u : this.observablePlayers) {
            try {
                temp.add(u.getUsername());
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return temp;
    }
    
    @Override
    public ArrayList<String> getChat() {
        return this.chatMessages;
    }
    
    @Override
    public void Addchatmessage(String message)
    {
        observableChat.add(message);
    }
    
    @Override
    public boolean checkPassword(String password)
    {
        if(this.password.equals(password))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public boolean createGame(double timelimit, int botDifficulty, String level, int rounds)
    {
        //todo
        if(timelimit != 0 && botDifficulty != 0 && level != null && rounds != 0)
        {
            games.add(game = new Game(9,9,timelimit,botDifficulty,rounds));
            return true;
        }
      
        return false;
    }
    
    @Override
    public boolean addUser(IUser user)
    {
        IUser Tempuser = null;
        
        for (IUser u : this.observablePlayers) {
            try {
                if (u.getUsername().equals(user)) {
                    Tempuser = u;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(Tempuser != null && !this.observableSpectators.contains(Tempuser))
        {
            this.observableSpectators.add(Tempuser);
            return true;   
        }
        return false;
    }
    /**
     * removes a user if this exists, if the user is an admin, check if there are more users. if there are more users give admin to the next user
     * if there are no new users, return a value that shows that the lobby has to be removed
     * @param user
     * @return if return -1 delete this lobby, if 0 no user has been removed if 1 user has been removed
     */
    @Override
    public int removeUser(String user)
    {
        IUser Tempuser = null;
        
        for (IUser u : this.observablePlayers) {
            try {
                if (u.getUsername().equals(user)) {
                    Tempuser = u;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        int removedUser = 0;
        
        if(this.observableSpectators.contains(Tempuser)){
            this.observableSpectators.remove(Tempuser);
            removedUser = 1;
        }else if (this.observablePlayers.contains(Tempuser)){
            this.observablePlayers.remove(Tempuser);
            removedUser = 1;
        }
        if (this.admin == Tempuser && this.observableSpectators.iterator().hasNext()){
            this.admin = this.observableSpectators.iterator().next();            
        } else if (this.admin == Tempuser){
            removedUser = -1;
        }
        return removedUser;
          
    }
    
    @Override
    public boolean assignSlot(String user)
    {        
        System.out.println("I arrived at AssignSlot");
        IUser Tempuser = null;
        
        System.out.println("Size: " + this.observableSpectators.size());
        
        for (IUser u : this.observableSpectators) {
            try {
                if (u.getUsername().equals(user)) {
                    Tempuser = u;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(Tempuser != null && this.observableSpectators.contains(Tempuser) && !this.observablePlayers.contains(Tempuser))
        {
            this.observablePlayers.add(Tempuser);
            this.observableSpectators.remove(Tempuser);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean clearSlot(String user)
    {
        IUser Tempuser = null;
        
        for (IUser u : this.observablePlayers) {
            try {
                if (u.getUsername().equals(user)) {
                    Tempuser = u;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(!this.observableSpectators.contains(Tempuser) && this.observablePlayers.contains(Tempuser))
        {
            this.observableSpectators.add(Tempuser);
            this.observablePlayers.remove(Tempuser);
            return true;
        }
        return false;
    }   
    
    @Override
    public String toString()
    {
       return this.lobbyName; 
    }
}
