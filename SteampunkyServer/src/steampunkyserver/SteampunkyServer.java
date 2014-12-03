/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package steampunkyserver;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


/**
 *
 * @author Koen
 */
public class SteampunkyServer extends UnicastRemoteObject
{
     // Set flag createRegistry when binding using registry
    // Reset flag createRegistry when binding using Naming
    private static boolean createRegistry = true;
    
    // Set port number
    private static int portNumber = 1099;
    
    // Set binding name for Mockeffectenbeurs
    private static String bindingName = "server";
    
    // References to registry and Mockeffectenbeurs
    private Registry registry = null;
    private Server server = null;
    
    /**
     *
     */
    public SteampunkyServer() throws RemoteException{
         // Print port number for registry
        System.out.println("Server: Port number " + portNumber);
        
        // Create student administration
        try {
            server = Server.getServer();
            System.out.println("Server: server created");
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot create server");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            server = null;
        }
        
        // Bind student administration
        if (createRegistry) {
            // Create registry at port number
            registry = createRegistry();
        
            // Bind student administration using registry
            if (registry != null && server != null) {
                bindMockUsingRegistry();
                System.out.println("Server: server bound to " + bindingName);
            }
            else {
                System.out.println("Server: server not bound with registry");
            }
        }
        else {
            // Bind student adiministration using Naming
            if (server != null) {
                bindMockUsingNaming();
                System.out.println("Server: server bound to " + bindingName);
            }
            else {
                System.out.println("Server: server not bound with naming");
            }
        }
    }    
    
    private Registry createRegistry() {
        
        // Create registry at port number
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(portNumber);
            System.out.println("Server: Registry created on port number " + portNumber);
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot create registry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            registry = null;
        }
        return registry;
    }
    
    private void bindMockUsingRegistry() {
        try {
            registry.rebind(bindingName, server);
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot bind server");
            System.out.println("Server: RemoteException: " + ex.getMessage());
        }
    }
        
    private void bindMockUsingNaming() {
        try {
            LocateRegistry.createRegistry(portNumber);
            Naming.rebind(bindingName, server);
        } catch (MalformedURLException ex) {
            System.out.println("Server: Cannot bind server");
            System.out.println("Server: MalformedURLException: " + ex.getMessage());
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot bind server");
            System.out.println("Server: RemoteException: " + ex.getMessage());
        }
    }

   
    
     public static void main(String[] args)
    {
       // Welcome message
        if (createRegistry) {
            System.out.println("SERVER USING CREATE REGISTRY");
        }
        else {
            System.out.println("SERVER USING NAMING");
        }
        
        // Create server
        try {
            SteampunkyServer rmiserver = new SteampunkyServer();
        } catch(RemoteException ex) {
            System.out.println("RMI Server could not be created");
        }
    }    
}
