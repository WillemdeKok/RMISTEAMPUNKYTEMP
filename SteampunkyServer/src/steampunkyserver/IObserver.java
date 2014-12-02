/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steampunkyserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Willem
 */
public interface IObserver extends Remote{
    public void AddObserver() throws RemoteException;
    public void RemoveObserver() throws RemoteException;
    public void NotifyObservers() throws RemoteException;
}
