/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

/**
 *
 * @author Mal
 */
public class Client {

    private String user;
    private String password;
    private int rating;
    private IUser Iuser;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public Client() {

    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }
    
    public void setIUser(IUser user) {
        this.Iuser = user;
    }
    
    public IUser getIUser() {
        return this.Iuser;
    }
}
