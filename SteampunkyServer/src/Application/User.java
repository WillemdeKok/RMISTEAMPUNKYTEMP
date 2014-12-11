/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.util.*;

/**
 OK
 <p>
 @author Linda
 */
public class User
{

    //************************datavelden*************************************
    private int userID = 0;
    private String username;
    private String password;
    private int rating;
    private int wins;
    private int losses;
    //relaties
    private Character character;
    private Lobby currentLobby;

    //***********************constructoren***********************************
    /**
     The Constructor for an existing user.
     <p>
     @param userID   An int which is this users ID.
     @param username A String which is this Users Username.
     @param password A String which is this Users Password.
     */
    public User(String username , String password)
    {
        this.userID++;
        this.username = username;
        this.password = password;
    }

    /**
     The Constructor for a new user.
     <p>
     @param userID   An int which is this users ID.
     @param username A String which is this Users Username.
     @param password A String which is this Users Password.
     @param rating   An int which is this Users Rating.
     @param wins     An int which is the # of wins this user has.
     @param losses   An int which is the # of losses this user has.
     */
    public User(String username , String password , int rating , int wins , int losses)
    {
        if (wins < 0 || losses < 0 || rating < 0)
        {
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
     The Getter of this Users Rating
     <p>
     @return An int which holds the current rating of this User.
     */
    public int getRating()
    {
        return this.rating;
    }

    /**
     The Getter of this Users Username
     <p>
     @return A String which is this users Username.
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     The Getter for this Users Wins & Losses.
     <p>
     @return A two dimensional int[].
     */
    public int[] getWinLoss()
    {
        int[] winst = new int[]
        {
            wins , losses
        };
        return winst;
    }

    /**
     The Getter for this Users UserID.
     <p>
     @return An int which is this Users UserID.
     */
    public int getUserID()
    {
        return this.userID;
    }
    
    public Character getCharacter()
    {
        return this.character;
    }
    
    public void setCharacter(Character c)
    {
        this.character = c;
    }

    /**
     The Setter of this Users Rating.
     <p>
     @param rating An int which is the new Rating of this User.
     */
    public void setRating(int rating)
    {
        this.rating = rating;
    }

    /**
     The Setter of this Users Password
     <p>
     @param password A String which is the new password of this User.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     A Method for Joining a Lobby.
     <p>
     @param lobby An Object of the Class Lobby, which is the lobby you want to join.
     <p>
     @return A boolean to show if joining this Lobby was succesfull or not.
     */
    public boolean joinLobby(Lobby lobby)
    {
        if (lobby.assignSlot(this))
        {
            this.currentLobby = lobby;
            return true;
        }
        return false;
    }

    /**
     A Method for Leaving a Lobby.
     <p>
     @param lobby An Object of the Class Lobby, which is the lobby you want to leave.
     <p>
     @return A boolean to show if leaving this Lobby was succesfull or not.
     */
    public boolean leaveLobby(Lobby lobby)
    {
        if (lobby.removeUser(this) == 1)
        {
            this.currentLobby = null;
            return true;
        }
        return false;
    }

    /**
     A Method for checking if entered password is this Users password
     <p>
     @param password A String which is the password that has been entered.
     <p>
     @return A boolean which shows if the entered password is this Users password.
     */
    public boolean checkPassword(String password)
    {
        boolean correct = false;
        if (password.equals(this.password))
        {
            correct = true;
        }
        return correct;
    }
    
    @Override
    public String toString() {
        return this.username;
    }
}
