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
public interface IGame extends Remote {

    public void startRound() throws RemoteException;

    public int getHeightPixels() throws RemoteException;

    public int getHeightCubes() throws RemoteException;

    public int getWidthPixels() throws RemoteException;

    public int getWidthCubes() throws RemoteException;

    public int getBotDifficulty() throws RemoteException;

    public double getTotalTime() throws RemoteException;

    public double getCurrentTime() throws RemoteException;

    public int getCurrentLevel() throws RemoteException;

    public boolean getGameEnd() throws RemoteException;

    public boolean setBotDifficulty(int difficulty) throws RemoteException;

    public void addPlayer(IUser player) throws RemoteException;

    public boolean placeFillupBoxes() throws RemoteException;

    public boolean placeRandomPowerup() throws RemoteException;

    public boolean getRandomBool(double perc) throws RemoteException;

    public void setCurrentTime() throws RemoteException;

    public boolean setGameEnd() throws RemoteException;

    public void updateGame() throws RemoteException;

    public void setupGame() throws RemoteException;

    public void setupLevel() throws RemoteException;

    public ArrayList<String[]> GetInformation() throws RemoteException;

    public int[] GetCharacter(String user) throws RemoteException;
}
