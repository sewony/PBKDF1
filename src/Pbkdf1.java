import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
public class Pbkdf1 {
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		// TODO Auto-generated method stub
		Security.addProvider(new BouncyCastleProvider());//Bouncycastle Provider�� �������� ���
		String pass="password";//pass�� "password"�� ����
		byte[] P = Utils.toByteArray(pass);//pass�ȿ� �ִ� String�� byte[]�� ��ȯ����
		byte[] S = new byte[]
				{0x78, 0x57, (byte)0x8e, 0x5a, 0x5d, 0x63, (byte)0xcb, 0x06};//S�� ���� ����
		int c=1000;//c�� ���� ����
		int dkLen=16;//dkLen�� ���� ����
		System.out.print("DK = "+Utils.toHexString(PBKDF1(P,S,c,dkLen)));//PBKDF1�Լ��� �׽�Ʈ����(P,S,c,dkLen)�� �־ ȣ���Ͽ� ���                                                                                                                                                                                                              
	}
	public static byte[] PBKDF1(byte[] P, byte[] S, int c, int dkLen) throws NoSuchAlgorithmException, NoSuchProviderException {
		byte[] T= new byte[] {};
		byte[] DK= new byte[dkLen];//dkLen��ŭ�� byte[] DK�� ����
		MessageDigest hash = MessageDigest.getInstance("SHA1","BC");//getIanstance�� ���� hash ��ü�� ����
		hash.update(P);//P�� update 
		hash.update(S);//S�� update
		for(int i=0;i<=c-1;i++) {
			T=hash.digest();//������ ���� ���� �ݺ��ؼ� digest�� �ϴ� �ڵ�
			hash.update(T);//digest�� T�� update
			if(i==c-1)//������ �� ���� for���� ��������
				break;
		}
		for(int j=0;j<dkLen;j++) {//dkLen ��ŭ�� DK�� ����
			DK[j]=T[j];
		}
		return DK;//DK���� return
	}
}
