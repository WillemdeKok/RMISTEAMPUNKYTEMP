/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Application;

import java.util.*;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;

/**
 * OK
 * @author Bart
 */
public class Lobby extends Observable implements ILobby
{
    //************************datavelden*************************************
    private int lobbyID;
    private int nextLobbyID = 1;
    private String lobbyName;
    private String password;
    private String map;
    private ArrayList<String> chatMessages;
    private transient ObservableList<String> observableChat;
    private User admin;
    private ArrayList<User> spectators;
    private transient ObservableList<User> observableSpectators;
    private ArrayList<User> players;
    private transient ObservableList<User> observablePlayers;
    private ArrayList<Game> games;
    private int ratingDifference;
    private Game game;

    //***********************constructoren***********************************
    /**
     * creates a lobby with ...
     */
    public Lobby(String lobbyname, User addedByUser, String password)
    {        
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
        this.observableSpectators.add(admin);
    }

    @Override
    public String GetLobbyname(){
       return this.lobbyName;       
    }
    
    public ObservableList<User> getPlayersAsUsers() {
        return (ObservableList<User>) FXCollections.unmodifiableObservableList(this.observablePlayers);
    }
    
    public ObservableList<User> getSpectatorsAsUsers() {
        return (ObservableList<User>) FXCollections.unmodifiableObservableList(this.observableSpectators);
    }
    
    @Override
    public ObservableList<String> getSpectators() {
        ArrayList<String> temp = new ArrayList();
        
        for (User u : this.observableSpectators) {
            temp.add(u.getUsername());
        }
        
        ObservableList<String> ObservableTemp = observableList(temp);
        return (ObservableList<String>) FXCollections.unmodifiableObservableList(ObservableTemp);
    }
    
    @Override
    public ObservableList<String> getPlayers() {
        ArrayList<String> temp = new ArrayList();
        
        for (User u : this.observablePlayers) {
            temp.add(u.getUsername());
        }
        
        ObservableList<String> ObservableTemp = observableList(temp);
        return (ObservableList<String>) FXCollections.unmodifiableObservableList(ObservableTemp);
    }
    
    @Override
    public ObservableList<String> getChat() {
        return (ObservableList<String>) FXCollections.unmodifiableObservableList(observableChat);
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
    public boolean addUser(String user)
    {
        User Tempuser = null;
        
        for (User u : this.observablePlayers) {
            if (u.getUsername().equals(user)) {
                Tempuser = u;
            }
        }
        
        if(Tempuser != null && !this.observableSpectators.contains(Tempuser))
        {
            this.observableSpectators.add(Tempuser);
            this.setChanged();
            this.notifyObservers(user);
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
        User Tempuser = null;
        
        for (User u : this.observablePlayers) {
            if (u.getUsername().equals(user)) {
                Tempuser = u;
            }
        }
        
        int removedUser = 0;
        
        if(this.observableSpectators.contains(Tempuser)){
            this.observableSpectators.remove(Tempuser);
            this.setChanged();
            this.notifyObservers(Tempuser);
            removedUser = 1;
        }else if (this.observablePlayers.contains(Tempuser)){
            this.observablePlayers.remove(Tempuser);
            this.setChanged();
            this.notifyObservers(user);
            removedUser = 1;
        }
        if (this.admin == Tempuser && this.observableSpectators.iterator().hasNext()){
            this.admin = this.observableSpectators.iterator().next();            
        } else if (this.admin == Tempuser){
            removedUser = -1;
        }
        return removedUser;
          
    }
    
    public boolean assignSlot(User user)
    {        
        if(this.observableSpectators.contains(user) && !this.observablePlayers.contains(user))
        {
            this.observablePlayers.add(user);
            this.observableSpectators.remove(user);
            this.setChanged();
            this.notifyObservers(user);
            return true;
        }
        return false;
    }
    
    public boolean clearSlot(User user)
    {
        
        if(!this.observableSpectators.contains(user) && this.observablePlayers.contains(user))
        {
            this.observableSpectators.add(user);
            this.observablePlayers.remove(user);
            this.setChanged();
            this.notifyObservers(user);
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
