/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

import java.security.Key;
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
        
        /*public static String getMerkleRoot(ArrayList<Transaction> transactions) {
            int count = transactions.size();

            List<String> previousTreeLayer = new ArrayList<String>();
            for(Transaction transaction : transactions) {
                    previousTreeLayer.add(transaction.transactionId);
            }
            List<String> treeLayer = previousTreeLayer;

            while(count > 1) {
                    treeLayer = new ArrayList<String>();
                    for(int i=1; i < previousTreeLayer.size(); i+=2) {
                            treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
                    }
                    count = treeLayer.size();
                    previousTreeLayer = treeLayer;
            }

            String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
            return merkleRoot;
	}*/
}
