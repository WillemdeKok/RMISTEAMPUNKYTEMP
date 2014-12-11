/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.rmi.RemoteException;

/**
 *
 * @author Willem
 */
public interface IServer {
    public void AddObserver(IObserver observer) throws RemoteException;
    public void RemoveObserver(IObserver observer) throws RemoteException;
    public void NotifyObserversUsers() throws RemoteException;
    public void NotifyObserversLobbies() throws RemoteException;
}
