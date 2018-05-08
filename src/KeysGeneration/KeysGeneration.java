
package KeysGeneration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

/**
 *
 * @author Asus
 */

// Geração de par de chaves com Curvas Elipticas

public final class KeysGeneration
{
	public KeyPair keyPar;
	
	public KeyPair generateKeys()
	{
		try
		{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
			
			//Inicializa o gerador de chaves e gera um par de chaves
			keyGen.initialize(ecSpec, random);
			KeyPair keyPar = keyGen.genKeyPair();
			
			return keyPar;
			
			/*
			privateKey = keyPair.getPrivate()
			publicKey = keyPair.getPublic()
			*/
								
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}	
	}
	
}

