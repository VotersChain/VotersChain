/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KeysGeneration;

import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Base64;


public class User 
{
	public static PrivateKey privateKey;
	public static PublicKey publicKey;
	
	public static String getStringFromKey(Key key) 
	{
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	public static void main(String args[])
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		KeyPair keyPar = new KeysGeneration().generateKeys();
		privateKey = keyPar.getPrivate();
		publicKey = keyPar.getPublic();
		
		System.out.println("Privada --> " + privateKey);
		System.out.println(getStringFromKey(privateKey));
		
		System.out.println("Publica --> " + publicKey);
		System.out.println(getStringFromKey(publicKey));
		
	
		
	}
	
}
