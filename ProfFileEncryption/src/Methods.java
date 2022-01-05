import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Methods {
static byte[] KeyGeneration(String password, byte[]salt, int dkLen, int iteration)throws Exception {
		
		Security.addProvider(new BouncyCastleProvider());
		MessageDigest md= MessageDigest.getInstance("SHA1","BC");
		byte[] input= new byte[password.length()+salt.length];
		
		System.arraycopy(Utils.toByteArray(password), 0, input, 0, password.length());
		System.arraycopy(salt, 0/*salt복사시작위치*/, input/*복사해서넣는곳*/, password.length()/*복사시작위치*/, salt.length/*이만큼복사*/);
		
		System.out.println("Input : "+Utils.toHexString(input));
		
		md.update(input);
		
		for(int i=0;i<iteration-1;i++) {
			byte T[]=md.digest();
			md.update(T);
		}
		byte output[]=md.digest();
		byte[] derivedKey=new byte[16];
		
		System.arraycopy(output, 0, derivedKey, 0, dkLen);
		
		System.out.println("derivedKey : "+Utils.toHexString(derivedKey));
		return derivedKey;
	}
	
}
