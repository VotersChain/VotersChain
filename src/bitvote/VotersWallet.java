/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author jferr
 */
public class VotersWallet {
    
	public PrivateKey privateKey;
	public PublicKey publicKey;
    
	public VotersWallet()
	{		
		KeyPair keyPar = new KeysGeneration().generateKeys();
		privateKey = keyPar.getPrivate();
		publicKey = keyPar.getPublic();	   
	}
	
	
	/*
	public static void main(String args[]) 
	{
		VotersWallet UserA = new VotersWallet();
		VotersWallet UserB = new VotersWallet();
				
		System.out.println("USER	A");
		System.out.println("Privada --> " + StringUtils.getStringFromKey(UserA.privateKey));		
		System.out.println("Publica --> " + StringUtils.getStringFromKey(UserA.publicKey));
		
		System.out.println("USER	B");
		System.out.println("Privada --> " + StringUtils.getStringFromKey(UserB.privateKey));		
		System.out.println("Publica --> " + StringUtils.getStringFromKey(UserB.publicKey));
					
	}
	*/
		
    
    //Construtor para utilizadores que façam o login
    
    
}
