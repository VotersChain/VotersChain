/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author jferr
 */
public class Vote {
    public String voteId; //contém o hash do voto
    public PublicKey sender;
    public long candidateNonce;
    public int numberVotes;
    public byte[] signature;
    
    private static int id = 0; //numero de votos que ja foram gerados
    
    public Vote(PublicKey from, long to, int value){
        this.sender = from;
        this.candidateNonce = to;
        this.numberVotes = value;
    }

    Vote(PublicKey publicKey, PublicKey publicKey0, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public void generateSignature(PrivateKey privateKey) throws Exception{
        String data = StringUtils.getStringFromKey(sender) + Long.toString(candidateNonce) + Integer.toString(numberVotes);
        signature = SignatureUtils.signString(data, privateKey);
    }

    public boolean verifySignature() throws Exception{
        String data = StringUtils.getStringFromKey(sender) + Long.toString(candidateNonce) + Integer.toString(numberVotes); 
        return SignatureUtils.verifyString(data, signature, sender);
    }
    
    public boolean processVote() throws Exception{
        if(verifySignature()==false){
            System.out.println("Assinatura do Voto na Verificada");
            return false;
        }
        
        voteId = calculateHash();
       
        return true; 
    }
   
   private String calculateHash(){
       id++;
       return HashUtils.hashFuncSHA256(StringUtils.getStringFromKey(sender) + 
               Long.toString(candidateNonce)+
               Integer.toString(numberVotes) + id
               );
   }
   
   
   
   
}
