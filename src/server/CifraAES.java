/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author Asus
 */
public class CifraAES 
{
	/*
	public  SecretKey generateKey() throws NoSuchAlgorithmException
	{
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		SecretKey aesKey = kgen.generateKey();
		return aesKey;		
	}
	*/
	
	public static String generateKey()
	{
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[8];
		secureRandom.nextBytes(key);
		String  secretKey = new String(Hex.encode(key));
                System.out.println(secretKey);
		return secretKey;		
	}
	
	public static byte[] generateIV()
	{
		SecureRandom secureRandom = new SecureRandom();
		byte[] iv = new byte[16]; 
		secureRandom.nextBytes(iv);
                System.out.println("IV"+iv.toString());
		return iv;		
	}

	public static byte[] encrypt(String str, String secretKey, byte[] iv) throws Exception
	{		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
		cipher.init(Cipher.ENCRYPT_MODE,key,new IvParameterSpec(iv));
		byte [] cipherText=cipher.doFinal(str.getBytes("UTF-8"));
		return cipherText;
	}	

	public static String decrypt(byte[] cipherText, String secretKey, byte[] iv) throws Exception
	{
			
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
		cipher.init(Cipher.DECRYPT_MODE,key,new IvParameterSpec(iv));
		return new String(cipher.doFinal(cipherText));
	}

	
	
}
