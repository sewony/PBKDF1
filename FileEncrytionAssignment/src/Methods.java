import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Methods {
	static byte[] KeyGeneration(String password, byte[]salt, int dkLen, int iteration)throws Exception {
	
	Security.addProvider(new BouncyCastleProvider());
	MessageDigest md= MessageDigest.getInstance("SHA1","BC");
	byte[] input= new byte[password.length()+salt.length];//password+salt���̸� ������ �迭 ����
	
	System.arraycopy(Utils.toByteArray(password), 0, input, 0, password.length());//input�迭�� password�� �ٿ��ִ´�.
	System.arraycopy(salt, 0, input, password.length(), salt.length);//input�迭�� salt�� �ٿ��ִ´�.
	
	System.out.println("Input : "+Utils.toHexString(input));
	
	md.update(input);//PBKDF1�� ������� �����Ѵ�.
	
	for(int i=0;i<iteration-1;i++) {
		byte T[]=md.digest();
		md.update(T);
	}
	byte output[]=md.digest();
	byte[] derivedKey=new byte[16];
	
	System.arraycopy(output, 0, derivedKey, 0, dkLen);//16byte�� ũ��� derived key�� �̾Ƴ���.
	
	System.out.println("derivedKey : "+Utils.toHexString(derivedKey));
	return derivedKey;
	}

	static void FileEnc(byte[] salt, byte[] DerivedKey, String Path, String o_Path)throws Exception{
		Security.addProvider(new BouncyCastleProvider());
	byte[]	ivBytes=new byte[] {
		0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
		0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00};
	
	SecretKeySpec key= new SecretKeySpec(DerivedKey, "AES");
	IvParameterSpec iv= new IvParameterSpec(ivBytes);
	
	Cipher cipher=null;
	cipher=Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
	cipher.init(Cipher.ENCRYPT_MODE, key, iv);//��ȣȭ ���� initialization 
	
	byte[] password_check=new byte[16];//password�� üũ�ϱ� ���� �迭 ����
	byte[] password_check_out=new byte[24];

	System.arraycopy(DerivedKey, 0, password_check_out, 0, DerivedKey.length);//password_check_out�� derivedKey�� ����
	System.arraycopy(salt, 0, password_check_out, 16, salt.length);//password_check_out�� salt�� ����
	MessageDigest hash= MessageDigest.getInstance("SHA1", "BC");

	password_check_out=hash.digest(password_check_out);//hash�Լ��� �ְ� ����� ����.
	System.arraycopy(password_check_out, 0, password_check, 0, 16);//��� �� 16byte�� password_check�� ����
	System.out.println("password_check: "+Utils.toHexString(password_check));

	FileInputStream Inputstream= new FileInputStream(Path);//Inputstream ��ü����
	FileOutputStream Outputstream= new FileOutputStream(o_Path);//Outputstream ��ü����

	Outputstream.write(salt);//salt�� Outputstream ��ü�� �ִ´�.
	Outputstream.write(password_check);//password_check���� Outputstream ��ü�� �ִ´�.

	int BUF_SIZE=1024;//�Ʒ��δ� ���Ͼ�ȣȭ �ڵ�
	byte[] buffer= new byte[BUF_SIZE];  

	int read=BUF_SIZE;
	
	while((read=Inputstream.read(buffer,0,BUF_SIZE))==BUF_SIZE) {
		Outputstream.write(cipher.update(buffer,0,read));
	}
	Outputstream.write(cipher.doFinal(buffer,0,read));

	Inputstream.close();
	Outputstream.close();
	}

	static void FileDec(byte[] salt2, byte[] DerivedKey2, String o_Path2, String dec_path) throws Exception {

		byte[]		ivBytes=new byte[] {
		0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
		0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00};
	
		byte[] 	password_check=new byte[16];
		byte[] 	password_check_out=new byte[24];
		byte[] 	password_checkd=new byte[16];
		byte[] 	password_check_outd=new byte[24];

	SecretKeySpec key = new SecretKeySpec(DerivedKey2, "AES");
	IvParameterSpec iv= new IvParameterSpec(ivBytes);
	
	Security.addProvider(new BouncyCastleProvider());
	
	System.arraycopy(DerivedKey2, 0, password_check_out, 0, DerivedKey2.length);//password_check_out�� derivedKey�� ����
	System.arraycopy(salt2, 0, password_check_out, 16, salt2.length);//password_check_out�� salt�� ����
	MessageDigest hash= MessageDigest.getInstance("SHA1", "BC");
	password_check_out=hash.digest(password_check_out);//hash�Լ��� �ְ� ����� ����.
	System.arraycopy(password_check_out, 0, password_check, 0, 16);//��� �� 16byte�� password_check�� ����
	System.out.println("password_check: "+Utils.toHexString(password_check));//�Ķ���ͷ� ���� ������ �� �޼ҵ忡�� ���� password check��
	
	FileInputStream Inputstream= new FileInputStream(o_Path2);//Inputstream ��ü ����
	FileOutputStream Outputstream= new FileOutputStream(dec_path);//Outputstream ��ü ����

	

	password_check_outd=Inputstream.readNBytes(24);//Inputstream���� ���� 24����Ʈ�� �о��
	for(int i=8;i<24;i++) {
	password_checkd[i-8]=password_check_outd[i];	//Inputstream�� password_check �κи� password_checkd�� ����
	}
	System.out.println("password_checkd: "+Utils.toHexString(password_checkd));//Inputstream ��ü���� ������ password check��

	int sum=0;
	for(int i=0;i<16;i++) {
	if(password_check[i]==password_checkd[i]) {//�� ����Ʈ���� ������ Ȯ���Ͽ� ���ٸ� sum++
		sum++;
		}
	}
	if(sum==16) {//password_check�� password_checkd�� ��ġ�ϸ� ��ȣȭ����

		Cipher cipher=null;
		cipher=Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		cipher.init(Cipher.DECRYPT_MODE, key, iv);

		int BUF_SIZE=1024;
		byte[] buffer= new byte[BUF_SIZE];
		int read=BUF_SIZE;

		while((read=Inputstream.read(buffer,0,BUF_SIZE))==BUF_SIZE) {
			Outputstream.write(cipher.update(buffer,0,read));
		}
		Outputstream.write(cipher.doFinal(buffer,0,read));

		Inputstream.close();
		Outputstream.close();
	}
	}}