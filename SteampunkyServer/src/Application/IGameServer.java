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
 * @author Mal
 */
public interface IGameServer extends Remote {

    public ArrayList<ILobby> getLobbies() throws RemoteException;

    public ArrayList<String> getUsers() throws RemoteException;

    public void Connectionstring() throws RemoteException;

    public boolean createUser(String username, String password) throws RemoteException;

    public boolean loginUser(String username, String password) throws RemoteException;

    public boolean createLobby(String lobbyName, String password, String username, IGameServer server) throws RemoteException;

    public boolean joinLobby(ILobby lobby, String user, String password) throws RemoteException;

    public boolean leaveLobby(ILobby lobby, String user) throws RemoteException;

    public boolean deleteLobby(ILobby lobby) throws RemoteException;

    public IUser Getuser(String username) throws RemoteException;

    public int GetRating(String username) throws RemoteException;

    public int DecreaseRating(String username, int rating) throws RemoteException;

    public int IncreaseRating(String username, int rating) throws RemoteException;

    public ArrayList<String> GetTotalrating() throws RemoteException;

    public boolean RemoveUser(String tempuser) throws RemoteException;
}
