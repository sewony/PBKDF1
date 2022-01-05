import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class FileEncryption {

	public static void main(String args[]) throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());
		
		byte[]		keyBytes=new byte[] {
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
			0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
		byte[]		ivBytes=new byte[] {
			0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
			0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00};
		
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec iv= new IvParameterSpec(ivBytes);
		
		Cipher cipher=null;
		
		cipher=Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		
		int BUF_SIZE=1024;
		
		byte[] buffer= new byte[BUF_SIZE];
		
		FileInputStream fis= new FileInputStream("09. 개체 인증.pdf");
		FileOutputStream fos= new FileOutputStream("enc");
		int read=BUF_SIZE;
		
		while((read=fis.read(buffer,0,BUF_SIZE))==BUF_SIZE) {
			fos.write(cipher.update(buffer,0,read));
		}
		fos.write(cipher.doFinal(buffer,0,read));
		
		fis.close();
		fos.close();
		
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		fis=new FileInputStream("enc");
		fos=new FileOutputStream("09. 개체 인증.pdf");
		
		while((read=fis.read(buffer,0,BUF_SIZE))==BUF_SIZE) {
			fos.write(cipher.update(buffer,0,read));
		}
		fos.write(cipher.doFinal(buffer,0,read));
		
		fis.close();
		fos.close();
	}
	
}
