/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

/**
 *
 * @author Bart
 */
public interface Iuser {
    public String getPassword();
    public String getUsername(); 
    public Iuser User(String username , String password);
}
