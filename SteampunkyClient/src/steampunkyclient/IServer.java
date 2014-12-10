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
public interface IServer extends Remote {

    boolean loginUser(String username, String password);

    boolean createUser(String username, String password);

    void Userlogedin(IUser user);
    
    Server getServer();
        
    }
}
