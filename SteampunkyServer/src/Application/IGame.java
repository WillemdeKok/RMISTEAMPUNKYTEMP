/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Willem
 */
public interface IGame extends Remote{
    public int getHeightPixels() throws RemoteException;
    public int getHeightCubes() throws RemoteException;
    public int getWidthPixels() throws RemoteException;
    public int getWidthCubes() throws RemoteException;
    public List<Object> getObjectsFromGrid(int x, int y);
    public Position getPosition(int x, int y);
    public List<Position> getGrid();
}
