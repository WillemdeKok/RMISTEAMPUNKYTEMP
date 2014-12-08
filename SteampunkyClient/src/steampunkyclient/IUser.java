/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package steampunkyclient;

import java.rmi.Remote;

/**
 *
 * @author Mnesymne
 */
public interface IUser extends Remote{
    
    String getUsername();
    String getPassword();
}
