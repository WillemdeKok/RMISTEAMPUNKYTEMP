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
public interface ILogin extends Remote {
    public boolean createUser(String username, String password) throws RemoteException;
    public boolean loginUser(String username, String password) throws RemoteException;
    public boolean createLobby(String lobbyName,String user,Iuser account);
    public ObservableList<ILobby> getLobbies();
}
