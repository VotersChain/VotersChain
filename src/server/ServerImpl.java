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
import java.security.PublicKey;
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
            bd.closeBD();
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
            bd.closeBD();
        }
        
        return keys;
    }
    
    @Override
    public String loginStepOne(String pubkey){
        String stringNonce = "";
        publickey = pubkey;
        
        //verifica se a chave publica existe na bd
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
            bd.closeBD();
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
        
        SQLiteBD bd = new SQLiteBD();  
        Statement stmt = null;
        ResultSet res;
        int electionid = -1;
        
        String name = "";
        String insert="INSERT INTO Election(name,status) VALUES(?,1);";
        
        name = Read.readString();
        
        PreparedStatement prstmt = bd.returnPrStmt(insert);
        
        try {
            prstmt.setString(1,name);
            prstmt.executeUpdate();
            
            // obtem o id da eleição
            stmt = bd.returnStmt();
            res = stmt.executeQuery("SELECT * FROM Election ORDER BY id DESC LIMIT 1;");
            
            if (res.next()) {
                electionid = res.getInt(1);
            }
            bd.closeBD();
            
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            bd.closeBD();
        }
  
        System.out.print("Insira o número de candidatos:");
        
        int n = Read.readPositiveInt();

        bd = new SQLiteBD();
        for(int i=0;i<n;i++){
           
            System.out.print("Nome do Candidato:");

            name = Read.readString();
            insert = "INSERT INTO Candidate(name) VALUES(?);";
            prstmt = bd.returnPrStmt(insert);
            try {
                prstmt.setString(1, name);
                prstmt.executeUpdate();
                
                // obtem id do Candidato inserido
                stmt = bd.returnStmt();
                res=stmt.executeQuery("SELECT * FROM Candidate ORDER BY id DESC LIMIT 1;");
                int candidateid=-1;
                if(res.next()){
                    candidateid=res.getInt(1);
                }
                
                stmt.close();
                
                //inserção na tabela de Resultados para mais tarde registar o nº de votos
                insert = "INSERT INTO Result(candidateid,electionid,votesnumber) VALUES(?,?,?);";
                prstmt = bd.returnPrStmt(insert);
                prstmt.setInt(1, candidateid);
                prstmt.setInt(2, electionid);
                prstmt.setInt(3, 0);
                prstmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                bd.closeBD();
            }
            
        }
        
        bd.closeBD();
    }
    
    private static void endElection(){
        
        SQLiteBD bd = new SQLiteBD();
        PreparedStatement prstmt = null;
      
        
        Statement stmt = bd.returnStmt();
        ResultSet res;
        
        System.out.println("------------Eleições a Decorrer------------");
        int i = 0;
        try {
            res = stmt.executeQuery("SELECT * FROM Election WHERE status=1;");
            while(res.next()){
                System.out.println("ID das Eleições: "+res.getInt(1));
                System.out.println("Nome: "+res.getString(2));
                System.out.println("-------------------------------------------");
                i++;
            }
            bd.closeBD();
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(i==0){
            return;
        }
        
        System.out.print("ID das Eleições a terminar:");
        
        
        int electionid = Read.readIntInInterval(i+1);
        
        bd = new SQLiteBD();
        stmt = bd.returnStmt();
        try {
            stmt.executeUpdate("UPDATE Election SET status=0 WHERE id="+electionid+";");
            bd.closeBD();
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
            bd.closeBD();
        }
        
        //Calcular o nº de votos de cada Candidato 
        
        
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
        if (!fBD.exists()) {
            SQLiteBD bd = new SQLiteBD();
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
                    
                    switch(op){
                        case 1:
                            startElections();
                            break;
                        case 2:
                            endElection();
                            break;
                        case 0:
                            sair=1;
                            break;
                        default:
                            System.out.println("Opção errada");
                    }
                }    
            }
        ).start();
           

    }
}
