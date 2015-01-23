/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Willem
 */
public interface IObserver extends Remote {

    public void updateLobbies(ArrayList<ILobby> lobbies) throws RemoteException;

    public void updateUsers(ArrayList<String> users) throws RemoteException;
}
