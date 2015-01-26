/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import Application.FontysObserver.BasicPublisher;
import Application.FontysObserver.RemotePropertyListener;
import Application.FontysObserver.RemotePublisher;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;
import jdk.nashorn.internal.codegen.CompilerConstants;

/**
 * OK
 *
 * @author Bart
 */
public class Lobby extends UnicastRemoteObject implements ILobby, RemotePublisher {

    //************************datavelden*************************************
    private int lobbyID;
    private int nextLobbyID = 1;
    private String lobbyName;
    private String password;
    private String map;
    private ArrayList<String> chatMessages;
    //private transient ObservableList<String> observableChat;
    private IUser admin;
    private ArrayList<IUser> spectators;
    //private transient ObservableList<IUser> observableSpectators;
    private ArrayList<IUser> players;
    //private transient ObservableList<IUser> observablePlayers;
    private int ratingDifference;
    private Game game;

    private String[] lobbyArray;
    private BasicPublisher publisher;

    //***********************constructoren***********************************
    /**
     * creates a lobby with ...
     */
    public Lobby(String lobbyname, IUser addedByUser, String password) throws RemoteException {
        System.out.println("Lobby has been created");
        this.lobbyName = lobbyname;
        this.admin = addedByUser;
        this.lobbyID = this.nextLobbyID;
        this.nextLobbyID++;
        this.password = password;
        this.admin = addedByUser;

        this.lobbyArray = new String[1];
        this.lobbyArray[0] = "lobby";
        this.publisher = new BasicPublisher(this.lobbyArray);

        this.spectators = new ArrayList<>();
        //observableSpectators = observableList(spectators);
        this.players = new ArrayList<>();
        //observablePlayers = observableList(players);
        this.chatMessages = new ArrayList<>();
        //observableChat = observableList(chatMessages);
        this.chatMessages.add("Welkom Bij Steampunky u bevindt zich in de volgende lobby: " + this.lobbyName);
    }

    @Override
    public String GetLobbyname() {
        return this.lobbyName;
    }

//    public ObservableList<IUser> getPlayersAsUsers() {
//        return (ObservableList<IUser>) FXCollections.unmodifiableObservableList(observablePlayers);
//    }
//    
//    public ObservableList<IUser> getSpectatorsAsUsers() {
//        return (ObservableList<IUser>) FXCollections.unmodifiableObservableList(observableSpectators);
//    }
    @Override
    public ArrayList<String> getSpectators() {
        ArrayList<String> temp = new ArrayList();
        for (IUser u : spectators) {
            try {
                temp.add(u.getUsername());
            } catch (Exception ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("temp size: " + temp.size());
        return temp;
    }

    @Override
    public ArrayList<String> getPlayers() {
        ArrayList<String> temp = new ArrayList();

        for (IUser u : players) {
            try {
                temp.add(u.getUsername());
            } catch (Exception ex) {
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
    public void Addchatmessage(String message) {
        chatMessages.add(message);
        this.publisher.inform(this, "lobby", "", "Message");
    }

    @Override
    public boolean checkPassword(String password) {
        if (this.password.equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean createGame(double timelimit, int botDifficulty, int level, int rounds, int width, int height) {
        //todo
        if (timelimit != 0 && botDifficulty != 0 && level > 0 && rounds != 0) {
            game = new Game(width, height, timelimit, botDifficulty, rounds, level);

            for (IUser u : players) {
                game.addPlayer(u);
            }

            game.startRound();
            publisher.inform(this, "lobby", "", "start");

            return true;
        }

        return false;
    }

    @Override
    public String getAdminName() {
        try {
            return this.admin.getUsername();
        } catch (RemoteException ex) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public boolean addUser(IUser user) {
        if (user != null && !spectators.contains(user)) {
            spectators.add(user);
            System.out.println("User has been added");
            publisher.inform(this, "lobby", "", "new");
            return true;
        }
        return false;
    }

    /**
     * removes a user if this exists, if the user is an admin, check if there
     * are more users. if there are more users give admin to the next user if
     * there are no new users, return a value that shows that the lobby has to
     * be removed
     *
     * @param user
     * @return if return -1 delete this lobby, if 0 no user has been removed if
     * 1 user has been removed
     */
    @Override
    public int removeUser(String user) {
        IUser TempuserSpec = null;
        IUser TempuserPlay = null;

        for (IUser u : players) {
            try {
                if (u.getUsername().equals(user)) {
                    TempuserPlay = u;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (IUser u : spectators) {
            try {
                if (u.getUsername().equals(user)) {
                    TempuserSpec = u;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        int removedUser = 0;

        if (spectators.contains(TempuserSpec)) {
            spectators.remove(TempuserSpec);
            publisher.inform(this, "lobby", "", "new");
            removedUser = 1;
        } else if (players.contains(TempuserPlay)) {
            players.remove(TempuserPlay);
            publisher.inform(this, "lobby", "", "new");
            removedUser = 1;
        }
        try {
            if (this.admin.getUsername().equals(user)) {
                if (players.iterator().hasNext()) {
                    this.admin = players.iterator().next();
                    publisher.inform(this, "lobby", "", "Admin");
                } else if (spectators.iterator().hasNext()) {
                    this.admin = spectators.iterator().next();
                    publisher.inform(this, "lobby", "", "Admin");
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.spectators.isEmpty() && this.players.isEmpty()) {
            removedUser = -1;
        }
        return removedUser;

    }

    @Override
    public boolean assignSlot(String user) {
        System.out.println("I arrived at AssignSlot");
        IUser Tempuser = null;

        System.out.println("Size: " + spectators.size());

        for (IUser u : spectators) {
            try {
                if (u.getUsername().equals(user)) {
                    Tempuser = u;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (Tempuser != null && spectators.contains(Tempuser) && !players.contains(Tempuser)) {
            players.add(Tempuser);
            spectators.remove(Tempuser);
            publisher.inform(this, "lobby", "", "new");
            return true;
        }
        return false;
    }

    @Override
    public boolean clearSlot(String user) {
        IUser Tempuser = null;

        for (IUser u : players) {
            try {
                if (u.getUsername().equals(user)) {
                    Tempuser = u;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!spectators.contains(Tempuser) && players.contains(Tempuser)) {
            spectators.add(Tempuser);
            players.remove(Tempuser);
            publisher.inform(this, "lobby", "", "new");
            return true;
        }
        return false;
    }

    @Override
    public void dropBallista(String user) {
        CharacterPlayer C = null;
        try {
            C = this.game.getPlayerCharacter(user);
            C.createBallista(Direction.Right, 1);
        } catch (RemoteException ex) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void move(String user, Direction d) {
        CharacterPlayer C = null;
        try {
            C = this.game.getPlayerCharacter(user);
        } catch (RemoteException ex) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (C.getCanMove()) {
            int i = this.game.getCharacterNumber(C);
            int move = 0;
            int nextMove = 0;
            switch (d) {
                case Up:
                    move = 0;
                    break;
                case Right:
                    move = 1;
                    break;
                case Down:
                    move = 2;
                    break;
                case Left:
                    move = 3;
                    break;
            }
            switch (i) {
                case 0:
                    nextMove = 0;
                    break;
                case 1:
                    nextMove = 1;
                    break;
                case 2:
                    nextMove = 2;
                    break;
                case 3:
                    nextMove = 3;
                    break;
            }

            int finalMoveNo;
            if (move + nextMove > 3) {
                finalMoveNo = move + nextMove - 4;
            } else {
                finalMoveNo = move + nextMove;
            }
            Direction finalMove = null;
            switch (finalMoveNo) {
                case 0:
                    finalMove = Direction.Up;
                    break;
                case 1:
                    finalMove = Direction.Right;
                    break;
                case 2:
                    finalMove = Direction.Down;
                    break;
                case 3:
                    finalMove = Direction.Left;
                    break;
            }
            if (finalMove != null) {
                C.move(finalMove);
                C.setCanMove(false);
                C.setDirection(finalMove);
            }
        }
    }

    @Override
    public int[] GetCharacter(String user) throws RemoteException {
        return this.game.GetCharacter(user);
    }

    @Override
    public synchronized void updateGame() {
        this.game.updateGame();
    }

    @Override
    public synchronized int getWidthCubes() {
        return this.game.getWidthCubes();
    }

    @Override
    public synchronized int getHeightCubes() {
        return this.game.getHeightCubes();
    }

    @Override
    public synchronized int getWidthPixels() {
        return this.game.getWidthPixels();
    }

    @Override
    public synchronized int getHeightPixels() {
        return this.game.getHeightPixels();
    }

    @Override
    public synchronized ArrayList<String[]> GetInformation() {
        return this.game.GetInformation();
    }

    @Override
    public String toString() {
        return this.lobbyName;
    }

    @Override
    public void addListener(RemotePropertyListener listener, String property) throws RemoteException {
        this.publisher.addListener(listener, property);
    }

    @Override
    public void removeListener(RemotePropertyListener listener, String property) throws RemoteException {
        this.publisher.removeListener(listener, property);
    }

    @Override
    public int getLevel() throws RemoteException {
        return this.game.getCurrentLevel();
    }
}
