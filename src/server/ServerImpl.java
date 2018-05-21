/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import bitvote.SignatureUtils;
import bitvote.StringUtils;
import bitvote.VotersWallet;
import java.io.File;
import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jferr
 */
public class ServerImpl extends UnicastRemoteObject implements Server{
    
    private static final int PORT = 2019;
    private static long nonce;
    private static String publickey;
    
    public ServerImpl() throws Exception {
        super(PORT,new RMISSLClientSocketFactory(), new RMISSLServerSocketFactory());
    }
    
    @Override
    public String sayHello(){
        return "Hello World!";
    }
    
    @Override 
    //Registo dos votantes
    public String regist(String name,long idNumber){
        
        
        String keys = "";
        
        //Se já está registado retorna ""
        SQLiteBD bd = new SQLiteBD();
        String query = "SELECT * FROM User WHERE idnumber=?;";
        PreparedStatement prstmt = bd.returnPrStmt(query);
        ResultSet res = null;
        try {
            prstmt.setLong(1,idNumber);
            res = prstmt.executeQuery();
            if(res.next()){
                return keys;
            }
            bd.closeBD();

        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        VotersWallet wallet = new VotersWallet(); //Criar uma carteira
        keys = StringUtils.getStringFromKey(wallet.publicKey) + "," + StringUtils.getStringFromKey(wallet.privateKey); // Retorna um par de chaves ao votante
        
        bd = new SQLiteBD(); // Insere a chave publica do votante e o seu nome na bd       
        String insert = "INSERT INTO User(pubkey,name,idnumber) VALUES(?,?,?);";
        prstmt = bd.returnPrStmt(insert);
        try {
            prstmt.setString(1, StringUtils.getStringFromKey(wallet.publicKey));
            prstmt.setString(2,name);
            prstmt.setLong(3, idNumber);
            prstmt.executeUpdate();
            bd.closeBD(); //fecha bd e encripta
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return keys;
    }
    
    @Override
    public String loginStepOne(String pubkey){
        String stringNonce = "";
        publickey = pubkey;
        
        //verifica se a chave pubica existe na bd
        SQLiteBD bd = new SQLiteBD();
        String query = "SELECT * FROM User WHERE pubkey=?;";
        PreparedStatement prstmt = bd.returnPrStmt(query);
        ResultSet res = null;
        try {
            prstmt.setString(1, pubkey);
            res = prstmt.executeQuery();
            //senao existe retorna ""
            if(!res.next()){
                return stringNonce;
            }
            bd.closeBD();

        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        //gera nonce
        nonce = StringUtils.generateNonce();
        stringNonce = String.valueOf(nonce);
        
        return stringNonce;
    }
    
    @Override
    public Boolean loginStepTwo(byte[] signNonce){
        
        PublicKey pk = null;
        try {
            pk = StringUtils.getPublicKeyFromString(publickey);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        } 
        
        
        boolean isLoged=false;
        try {
            // verifica o nonce assinado
            isLoged = SignatureUtils.verifyString(String.valueOf(nonce), signNonce, pk);
        } catch (Exception ex1) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return isLoged;
    
    }
    
    @Override
    public String requestKey(){
        return "af1129c47f20f6394915309852624a8b9202abdeb6b696e540b41b5b4e9442b8";
    }
    
    private static void startElections(){
        
        System.out.print("Nome das eleições:");
        Scanner in = new Scanner(System.in);
        SQLiteBD bd = new SQLiteBD();
        
        String name = "";
        String insert="INSERT INTO Candidate(id,name) VALUES(?);";
        
        name = in.next();
        
        PreparedStatement prstmt = bd.returnPrStmt(insert);
        
        
  
        System.out.print("Insira o número de candidatos:");
        
        
        int n = in.nextInt();
        
        for(int i=0;i<n;i++){
            System.out.print("Nome do Candidato:");
            name = in.next();
            
            prstmt = bd.returnPrStmt(insert);
            
        }
    }
    
    public static void main(String[] args) throws IOException {
        
        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        
        try {
            // Create SSL-based registry
            Registry registry = LocateRegistry.createRegistry(PORT,new RMISSLClientSocketFactory(),new RMISSLServerSocketFactory());

            ServerImpl obj = new ServerImpl();

            // Bind this object instance to the name "HelloServer"
            registry.bind("Server", obj);

            System.out.println("Server bound in registry");
        } catch (Exception e) {
            System.out.println("ServerImpl err: " + e.getMessage());
            e.printStackTrace();
        }
        
        //Criar bd se não existir
        File fBD = new File("BitVote.db");
        SQLiteBD bd = new SQLiteBD();
        if (!fBD.exists()) {
            bd.createBD();
        }
        
        //Menu para implementar os requisitos do servidor
        new Thread(()
            -> {
                int sair = 0;
                while (sair == 0) {
                    System.out.println("***********************************");
                    System.out.println("1-Iniciar Eleições");
                    System.out.println("2-Terminar Eleições");
                    System.out.println("0-Sair");
                    System.out.print("-->");

                    Scanner in = new Scanner(System.in);
                    int op = in.nextInt();
                }    
            }
        ).start();
           

    }
}
