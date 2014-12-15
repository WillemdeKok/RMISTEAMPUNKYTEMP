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
    public ObservableList<Lobby> getLobbies()throws RemoteException;
    public ObservableList<User> getUsers()throws RemoteException;
    public void Connectionstring()throws RemoteException;
    public void Userlogedin(User tempuser)throws RemoteException;
    public boolean createUser(String username, String password)throws RemoteException;
    public boolean loginUser(String username, String password)throws RemoteException;
    public boolean createLobby(String lobbyName,String password,String username)throws RemoteException;
    public boolean joinLobby(Lobby lobby, User user, String password)throws RemoteException;
    public boolean leaveLobby(Lobby lobby, User user)throws RemoteException;
    public boolean deleteLobby(Lobby lobby)throws RemoteException; 
}
