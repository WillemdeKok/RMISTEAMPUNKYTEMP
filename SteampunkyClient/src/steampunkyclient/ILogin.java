/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steampunkyclient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Mal
 */
public interface ILogin extends Remote {
    public boolean createUser(String username, String password) throws RemoteException;
    public boolean loginUser(String username, String password) throws RemoteException;
}
