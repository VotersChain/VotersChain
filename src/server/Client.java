/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import bitvote.SignatureUtils;
import bitvote.StringUtils;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author jferr
 */
public class Client {
    
    private static final int PORT = 2019;
    
    public static void main(String[] args) {
        try{
            // Make reference to SSL-based registry
            Registry registry = LocateRegistry.getRegistry("DESKTOP-C38TKIF", PORT, new RMISSLClientSocketFactory());
            
            
            // "obj" is the identifier that we'll use to refer
            // to the remote object that implements the "Hello"
            // interface
            Server obj = (Server) registry.lookup("Server");
            String chaves = "blank";
            chaves = obj.regist("Tó Brito",14679456732457L);

            String[] pk = chaves.split(",");

            String nonce = obj.loginStepOne(pk[0]);
            
            byte[] signNonce = SignatureUtils.signString(nonce, StringUtils.getPrivateKeyFromString(pk[1]));
            
            boolean isLoged = obj.loginStepTwo(signNonce);
            
            System.out.println(isLoged);

        }
        catch (Exception e) {
            System.out.println("HelloClient exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
