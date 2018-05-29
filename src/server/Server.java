/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import bitvote.VoteChain;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *
 * @author jferr
 */
public interface Server extends Remote{
    
    String sayHello() throws RemoteException;
    String regist(String name,long idNumber) throws RemoteException;
    String loginStepOne(String pubkey) throws RemoteException;
    Boolean loginStepTwo(byte[] signNonce) throws RemoteException;
    String requestKey() throws RemoteException;
    VoteChain getBlockChain() throws RemoteException;
    void atualizaBlockChain( VoteChain VC) throws RemoteException;
            
}
