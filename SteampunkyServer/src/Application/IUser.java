/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Willem
 */
public interface IUser extends Remote {
    public int getRating() throws RemoteException;
    public String getUsername() throws RemoteException;
    public String getPassword() throws RemoteException;
    public int[] getWinLoss() throws RemoteException;
    public int getUserID() throws RemoteException;
    public void setPassword(String password) throws RemoteException;
    public void setRating(int rating) throws RemoteException;
    public boolean checkPassword(String password) throws RemoteException;
    public void setCharacter(Character c) throws RemoteException;
}
