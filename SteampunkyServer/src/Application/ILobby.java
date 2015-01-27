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
public interface ILobby extends Remote {

    public String GetLobbyname() throws RemoteException;

    public ArrayList<String> getSpectators() throws RemoteException;

    public ArrayList<String> getPlayers() throws RemoteException;

    public ArrayList<String> getChat() throws RemoteException;

    public boolean checkPassword(String password) throws RemoteException;

    public boolean getGameEnd() throws RemoteException;

    public boolean createGame(double timelimit, int botDifficulty, int level, int width, int height) throws RemoteException;

    public boolean addUser(IUser user) throws RemoteException;

    public boolean clearSlot(String user) throws RemoteException;

    public boolean assignSlot(String user) throws RemoteException;

    public int removeUser(String user) throws RemoteException;

    public void Addchatmessage(String message) throws RemoteException;

    public void move(String user, Direction d) throws RemoteException;

    public void dropBallista(String user) throws RemoteException;

    public void updateGame() throws RemoteException;

    public int getLevel() throws RemoteException;

    public int getWidthCubes() throws RemoteException;

    public int getHeightCubes() throws RemoteException;

    public int getWidthPixels() throws RemoteException;

    public int getHeightPixels() throws RemoteException;

    public String getAdminName() throws RemoteException;

    public ArrayList<String[]> GetInformation() throws RemoteException;

    public int[] GetCharacter(String user) throws RemoteException;

    public boolean GetHasStarted() throws RemoteException;

    public void setHasStarted(boolean bool) throws RemoteException;
}
