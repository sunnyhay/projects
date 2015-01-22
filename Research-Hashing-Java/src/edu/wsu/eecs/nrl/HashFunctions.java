package edu.wsu.eecs.nrl;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*
 * hash functions. 
 */
public class HashFunctions {
	/**
	 * getHash() regarding hash function selection
	 * return long[0] of key and long[1] of index 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static long[] getHash(int hashSelection, String feed, int tableNo, int tableSize) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		long hash[] = new long[2];  //hash digests
		
		if (hashSelection == 0) {
			hash = HashFunctions.getHashCRC32(feed, tableNo, tableSize);
		} else if (hashSelection == 1) {
			hash = HashFunctions.getHashMsgDigest(feed, tableNo, tableSize);
		} else {
			System.out.println("impossible hash function selection");
		}
		
		return hash;
	}
	
	/**
	 * input: seed of string, table order, capacity of a hash table
	 * output: first key, second index for current table of modulo capacity using CRC32 checksum. 
	 */
	 
	public static long[] getHashCRC32(String feed, int tableNo, int tableSize){
		long hash[] = new long[2];
		byte bytes[] = feed.getBytes();
		Checksum checksum = new CRC32();
		long checksumValue;
		
		checksum.update(bytes, 0, bytes.length-tableNo);
		checksumValue = checksum.getValue();
		//System.out.println("CRC32 checksum for input string is: " + checksumValue);
		
		hash[0] = checksumValue;
		hash[1] = checksumValue % tableSize;
		
		return hash;
	}
	
	/**
	 * another function family using MessageDigest or Mac.
	 * the first five algorithms are for MessageDigest, the last five for Mac
	 * so at most TEN hash functions can be used.
	 * output: first key, second index for current table of modulo capacity
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static long[] getHashMsgDigest(String feed, int tableNo, int tableSize) 
			throws NoSuchAlgorithmException, InvalidKeyException{
		long hash[] = new long[2];
		final int len = 15;   //the fixed length for Long type from HEX
		String[] algs = {"MD5","SHA","SHA-256","SHA-384","SHA-512","HmacMD5",
				"HmacSHA1","HmacSHA256","HmacSHA384","HmacSHA512"};
		
		if(tableNo < 5){
			MessageDigest md = MessageDigest.getInstance(algs[tableNo]);
			md.update(feed.getBytes());
			String temp = toHex(md.digest());
			hash[0] = Long.parseLong(temp.substring(0, len), 16);			
		}else if((tableNo < 10) && (tableNo > 4)){
			SecretKey key = new SecretKeySpec(feed.getBytes(), algs[tableNo]);
			Mac m = Mac.getInstance(algs[tableNo]);
			m.init(key);		
			byte[] mac = m.doFinal();
			hash[0] = Long.parseLong(toHex(mac).substring(0, len), 16);
		}else{
			System.out.println("impossible hash table No.");
		}
		hash[1] = hash[0] % tableSize;		
				
		return hash;
	}
	
	/** 
     * turn 16-byte[] to 32-bit String 
     * @param buffer 
     * @return 
     */  
    private static String toHex(byte buffer[]) {  
        StringBuffer sb = new StringBuffer(buffer.length * 2);  
        for (int i = 0; i < buffer.length; i++) {  
            sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));  
            sb.append(Character.forDigit(buffer[i] & 15, 16));  
        }  
  
        return sb.toString();  
    }

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
		/*String input = "I love my family for ever and this is a serious question!";
		byte bytes[] = input.getBytes();
		Checksum checksum = new CRC32();
		long checksumValue;
		int capacity = 10;
		
		for(int i = 0; i < capacity; i++){
			checksum.update(bytes, 0, bytes.length-i);
			checksumValue = checksum.getValue();

			System.out.println("CRC32 checksum for input string is: " + checksumValue);
		}*/
		
		String input = "I love my family for ever and this is a serious question!";
		int tableSize = 100000;
		long result[];
		for(int tableNo = 0; tableNo < 10; tableNo++){
			//result = HashFunctions.getHashCRC32(input, tableNo, tableSize);
			result = HashFunctions.getHashMsgDigest(input, tableNo, tableSize);
			System.out.println("hash key: " + result[0]);
			System.out.println("hash index: " + result[1]);
		}
		
	}

}
