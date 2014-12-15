/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.rmi.Remote;
import java.rmi.RemoteException;
import javafx.collections.ObservableList;

/**
 *
 * @author Willem
 */
public interface ILobby extends Remote{
    public String GetLobbyname() throws RemoteException;
    public ObservableList<String> getSpectators() throws RemoteException;
    public ObservableList<String> getPlayers() throws RemoteException;
    public ObservableList<String> getChat() throws RemoteException;
    public boolean checkPassword(String password) throws RemoteException;
    public boolean createGame(double timelimit, int botDifficulty, String level, int rounds) throws RemoteException;
    public boolean addUser(String user) throws RemoteException;
    public int removeUser(String user) throws RemoteException;
    public void Addchatmessage(String message)throws RemoteException;
}