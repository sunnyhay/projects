package org.lvyouzaike.util;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class EncrypUtil {
	private static final String DES = "DESede";
	private static final String MD5 = "MD5";
	private static Key key;
	private static Cipher cipher;
	
	public EncrypUtil() throws Exception {
		key = KeyGenerator.getInstance(DES).generateKey();
		cipher = Cipher.getInstance(DES);		
	}
	
	public static String md5Digest(String input) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance(MD5);
        md.update(input.getBytes()); 
     	return new String(md.digest());
	}

	public static void main(String[] args) throws Exception {
		EncrypUtil en = new EncrypUtil();
		byte[] encryptionBytes = en.encrypt("1975519");
		System.out.println("After encryption: " + encryptionBytes);
		System.out.println("Recovered: " + en.decrypt(encryptionBytes));
		
		System.out.println("md5 digest: " + EncrypUtil.md5Digest("1975519"));
		/*try {
	         MessageDigest md = MessageDigest.getInstance("MD5");
	         String input = "1975519";
	         md.update(input.getBytes()); 
	      	 byte[] output = md.digest();
	         System.out.println();
	         System.out.println("MD5(\""+input+"\") =");
	         System.out.println("   "+output);
	         
	      } catch (Exception e) {
	         System.out.println("Exception: "+e);
	      }*/
	}

	public byte[] encrypt(String input) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(input.getBytes());
	}

	public String decrypt(byte[] encryptionBytes) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(encryptionBytes));
	}
}
