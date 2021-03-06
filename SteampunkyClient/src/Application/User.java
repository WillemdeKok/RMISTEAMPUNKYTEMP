/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * OK
 * <p>
 * @author Linda
 */
public class User extends UnicastRemoteObject implements IUser {

    //************************datavelden*************************************
    private int userID = 0;
    private String username;
    private String password;
    private int rating;
    private int wins;
    private int losses;
    //relaties
    private CharacterPlayer character;
    private Lobby currentLobby;
    private boolean canMove = true;

    //***********************constructoren***********************************
    /**
     * The Constructor for an existing user.
     * <p>
     * @param userID An int which is this users ID.
     * @param username A String which is this Users Username.
     * @param password A String which is this Users Password.
     */
    public User(String username, String password) throws RemoteException {
        this.userID++;
        this.username = username;
        this.password = password;
    }

    /**
     * The Constructor for a new user.
     * <p>
     * @param userID An int which is this users ID.
     * @param username A String which is this Users Username.
     * @param password A String which is this Users Password.
     * @param rating An int which is this Users Rating.
     * @param wins An int which is the # of wins this user has.
     * @param losses An int which is the # of losses this user has.
     */
    public User(String username, String password, int rating, int wins, int losses) throws RemoteException {
        if (wins < 0 || losses < 0 || rating < 0) {
            throw new IllegalArgumentException("You can't have a negative win,loss or rating.");
        }
        this.userID++;
        this.username = username;
        this.password = password;
        this.rating = rating;
        this.wins = wins;
        this.losses = losses;
        this.currentLobby = null;
    }

    //**********************methoden****************************************
    /**
     * The Getter of this Users Rating
     * <p>
     * @return An int which holds the current rating of this User.
     */
    @Override
    public int getRating() throws RemoteException {
        return this.rating;
    }

    /**
     * The Getter of this Users Username
     * <p>
     * @return A String which is this users Username.
     */
    @Override
    public String getUsername() throws RemoteException {
        return this.username;
    }

    @Override
    public String getPassword() throws RemoteException {
        return this.password;
    }

    /**
     * The Getter for this Users Wins & Losses.
     * <p>
     * @return A two dimensional int[].
     */
    @Override
    public int[] getWinLoss() throws RemoteException {
        int[] winst = new int[]{
            wins, losses
        };
        return winst;
    }

    /**
     * The Getter for this Users UserID.
     * <p>
     * @return An int which is this Users UserID.
     */
    @Override
    public int getUserID() throws RemoteException {
        return this.userID;
    }

    public ICharacter getCharacter() {
        return this.character;
    }

    @Override
    public void setCharacter(CharacterPlayer c) throws RemoteException {
        this.character = c;
    }

    /**
     * The Setter of this Users Password
     * <p>
     * @param password A String which is the new password of this User.
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * The Setter of this Users Rating.
     * <p>
     * @param rating An int which is the new Rating of this User.
     */
    @Override
    public void setRating(int rating) throws RemoteException {
        this.rating = rating;
    }

    /**
     * A Method for checking if entered password is this Users password
     * <p>
     * @param password A String which is the password that has been entered.
     * <p>
     * @return A boolean which shows if the entered password is this Users
     * password.
     */
    @Override
    public boolean checkPassword(String password) throws RemoteException {
        boolean correct = false;
        if (password.equals(this.password)) {
            correct = true;
        }
        return correct;
    }

    @Override
    public void move(Direction d) throws RemoteException {

    }

    @Override
    public String toString() {
        return this.username;
    }

}
