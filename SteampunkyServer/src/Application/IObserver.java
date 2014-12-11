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
public interface IObserver extends Remote{
    public void updateLobbies(ObservableList<Lobby> lobbies) throws RemoteException;
    public void updateUsers(ObservableList<User> users) throws RemoteException;
}
