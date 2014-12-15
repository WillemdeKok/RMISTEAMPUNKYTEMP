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
    public String GetLobbyname();
    public boolean addUser(Iuser user);
}
