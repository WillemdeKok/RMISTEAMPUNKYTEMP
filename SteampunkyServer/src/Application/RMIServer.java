/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.util.Enumeration;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Example of RMI using Registry
 *
 * @author Bart
 */
public class RMIServer{

    private static boolean createRegistry = true;
    // Set port number
    private static final int portNumber = 1099;

    // Set binding name for student administration
    private static final String bindingName = "serverMock";

    // References to registry and student administration
    private Registry registry = null;
    private Server serverMock;

    // Constructor
    public RMIServer() {
        System.out.println("Server: Port number " + portNumber);
        try {
            serverMock = Server.getServer();
            
            System.out.println("Server: serverMock created");
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot create ServerMock");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            serverMock = null;
        }
        //Aanmaken van een regisrty
        if (createRegistry) {
            registry = createRegistry();
      
        // Als register aan maken is gelukt proberen te binden op register
        if (registry != null && serverMock != null) {
                bindMockUsingRegistry();
                System.out.println("Server: serverMock bound to " + bindingName);
            } else {
                System.out.println("Server: serverMock not bound");
            }
        } 
        // Als register aan maken is gelukt proberen te binden op naam
        else {
            if (serverMock != null) {
                bindMockUsingNaming();
                System.out.println("Server: serverMock bound to " + bindingName);
            } else {
                System.out.println("Server: serverMock not bound");
            }
        }
    }

    //Maakt een register aan de hand van het poort nummer
    private Registry createRegistry() {
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

    // probeert het register tebinden op bindingnaam en de classe met de RemotePublisher
    private void bindMockUsingRegistry() {
        try {
            registry.rebind(bindingName, serverMock);
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot bind serverMock");
            System.out.println("Server: RemoteException: " + ex.getMessage());
        }
    }
    
    // probeert het register tebinden op naam en de classe met de RemotePublisher
    private void bindMockUsingNaming() {
        try {
            LocateRegistry.createRegistry(portNumber);
            Naming.rebind(bindingName, serverMock);
        } catch (MalformedURLException ex) {
            System.out.println("Server: Cannot bind serverMock");
            System.out.println("Server: MalformedURLException: " + ex.getMessage());
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot bind serverMock");
            System.out.println("Server: RemoteException: " + ex.getMessage());
        }
    }

    private static void printIPAddresses() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            System.out.println("Server: IP Address: " + localhost.getHostAddress());
            InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
            if (allMyIps != null && allMyIps.length > 1) {
                System.out.println("Server: Full list of IP addresses:");
                for (InetAddress allMyIp : allMyIps) {
                    System.out.println("    " + allMyIp);
                }
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server: Cannot get IP address of local host");
            System.out.println("Server: UnknownHostException: " + ex.getMessage());
        }

        try {
            System.out.println("Server: Full list of network interfaces:");
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                System.out.println("    " + intf.getName() + " " + intf.getDisplayName());
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    System.out.println("        " + enumIpAddr.nextElement().toString());
                }
            }
        } catch (SocketException ex) {
            System.out.println("Server: Cannot retrieve network interface list");
            System.out.println("Server: UnknownHostException: " + ex.getMessage());
        }
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Welcome message
        System.out.println("SERVER USING REGISTRY");

        // Print IP addresses and network interfaces
        printIPAddresses();

        // Create server
        RMIServer server = new RMIServer();
    }
}
