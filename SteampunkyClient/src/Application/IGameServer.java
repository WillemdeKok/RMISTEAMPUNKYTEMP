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
 * @author Mal
 */
public interface IGameServer extends Remote {
    public ObservableList<String> getLobbies()throws RemoteException;
    public ObservableList<String> getUsers()throws RemoteException;
    public void Connectionstring()throws RemoteException;
    public void Userlogedin(String tempuser)throws RemoteException;
    public boolean createUser(String username, String password)throws RemoteException;
    public boolean loginUser(String username, String password)throws RemoteException;
    public boolean createLobby(String lobbyName,String password,String username)throws RemoteException;
    public boolean joinLobby(String lobby, String user, String password)throws RemoteException;
    public boolean leaveLobby(String lobby, String user)throws RemoteException;
    public boolean deleteLobby(String lobby)throws RemoteException; 
}
