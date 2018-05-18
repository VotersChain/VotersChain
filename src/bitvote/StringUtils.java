/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 *
 * @author paulo
 */
public class StringUtils {
    //Short hand helper to turn Object into a json string
	/*public static String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}*/
	
	//Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"  
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
	
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
        
        public static String getStringFromSignature(byte[] SignatureBytes){
            return Base64.getEncoder().encodeToString(SignatureBytes);
        }
        public static long generateNonce(){
            SecureRandom sc = new SecureRandom();
            long nonce = sc.nextLong();
            if(nonce < 0){
                nonce = nonce *(-1);
            }
            return nonce;
        }
        
        public static String getMerkleRoot(ArrayList<Vote> votes) {
            int count = votes.size();

            List<String> previousTreeLayer = new ArrayList<String>();
            for(Vote vote : votes) {
                    previousTreeLayer.add(vote.voteId);
            }
            List<String> treeLayer = previousTreeLayer;

            while(count > 1) {
                    treeLayer = new ArrayList<String>();
                    for(int i=1; i < previousTreeLayer.size(); i+=2) {
                            treeLayer.add(HashUtils.hashFuncSHA256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
                    }
                    count = treeLayer.size();
                    previousTreeLayer = treeLayer;
            }

            String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
            return merkleRoot;
	}
        
}
