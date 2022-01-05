import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Fileencryption {

public static void main(String args[]) throws Exception
{
	Security.addProvider(new BouncyCastleProvider());
	
	byte[]        keyBytes = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            };

	byte[]        ivBytes = new byte[] {
            0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
            0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00
            };

	SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
	IvParameterSpec iv = new IvParameterSpec(ivBytes);

	Cipher cipher = null;
   cipher =  Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");//객체생성
   cipher.init(Cipher.ENCRYPT_MODE, key, iv);
   
   FileInputStream fis=new FileInputStream("C:/Users/sewon/Desktop/assignm.pdf");//파일 읽어오기
   FileOutputStream fos=new FileOutputStream("C:/Users/sewon/Desktop/assignm.pdf");//파일 저장 
  // System.out.println("input : "+fis);//Utils.toHexString());
   int BUF_SIZE=1024;
   byte[] buffer=new byte[BUF_SIZE];//cipher.getOutputSize(BUF_SIZE)];//버퍼 할당
   int read=BUF_SIZE;
     while((read=fis.read(buffer,0,BUF_SIZE))>0){//읽을게 있을 동안 읽어내기
    	 fos.write(buffer,0,BUF_SIZE);
    	 cipher.update(buffer,0,read); //update
     	 cipher.doFinal(buffer);	//doFinal
     }
     System.out.println("암호문: "+buffer);
   fis.close();//fis close
   fos.close();//fos  close
   
	cipher.init(Cipher.DECRYPT_MODE, key, iv);
	FileInputStream fis2=new FileInputStream("C:/Users/sewon/Desktop/assignm.pdf");//파일 읽어오기
	   FileOutputStream fos2=new FileOutputStream("C:/Users/sewon/Desktop/assignm.pdf");//파일 저장 
	   System.out.println("input : "+fis2);//Utils.toHexString());
	   byte[] plainText=new byte[BUF_SIZE];//cipher.getOutputSize(BUF_SIZE)];//버퍼 할당
	     while((read=fis2.read(buffer,0,BUF_SIZE))>0){ //읽을게 있을 동안 읽어내기
	    	 fos.write(buffer,0,BUF_SIZE);
	    	 cipher.update(buffer,0,read); 
	     	 cipher.doFinal(plainText);
	     }
	    System.out.println("평문: "+plainText); 
	    fis2.close();//fis2 close
	    fos2.close();//fos2 close
		}
    }