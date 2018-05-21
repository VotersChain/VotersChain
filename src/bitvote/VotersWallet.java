/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;


public class VotersWallet {
        public int n_votes;
	public PrivateKey privateKey;
	public PublicKey publicKey;
    
	public VotersWallet()
	{		
		this.n_votes = 1;
		KeyPair keyPar = new KeysGeneration().generateKeys();
		privateKey = keyPar.getPrivate();
		publicKey = keyPar.getPublic();	   
	}
	
        public VotersWallet(PrivateKey pvk, PublicKey puk){
            this.n_votes = 1;
            privateKey = pvk;
            publicKey = puk;
        }
        public VotersWallet(PublicKey puk){
            this.n_votes = 1;
            publicKey = puk;
        }
	
	public VotersWallet(int n)
	{		
		KeyPair keyPar = new KeysGeneration().generateKeys();
		privateKey = keyPar.getPrivate();
		publicKey = keyPar.getPublic();	   
		n_votes = n;
	}
		
		
		
        public Vote sendVote(long candidateNonce, int value) throws Exception{
            Vote newVote= new Vote(publicKey, candidateNonce, value);
            newVote.generateSignature(privateKey);
            
            return newVote;      
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
