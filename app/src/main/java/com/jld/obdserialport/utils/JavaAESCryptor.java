package com.jld.obdserialport.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class JavaAESCryptor {
	
	private static final String AES = "AES";

	//private static final String CRYPT_KEY = "abcde12345123451";
	
	private static String iv = "0123456789123456";
  private static byte ivBytes[] = iv.getBytes();

	/**
	 * 加密
	 * 
	 * @return
	 */
	public static byte[] encrypt(byte[] src,final String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);
		final IvParameterSpec iv = new IvParameterSpec(ivBytes);
		cipher.init(Cipher.ENCRYPT_MODE, securekey,iv);//设置密钥和加密形式
		return cipher.doFinal(src);
	}

	/**
	 * 解密
	 * 
	 * @return
	 */
	public static byte[] decrypt(byte[] src,final String key)  throws Exception  {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);//设置加密Key
		final IvParameterSpec iv = new IvParameterSpec(ivBytes);
		cipher.init(Cipher.DECRYPT_MODE, securekey,iv);//设置密钥和解密形式
		return cipher.doFinal(src);
	}
	
	/**
	 * 二行制转十六进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
	
	/**
	 * 解密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public final static String decrypt(String data,final String cryptKey) {
		try {
			return new String(decrypt(hex2byte(data.getBytes()), cryptKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public final static String encrypt(String data,final String cryptKey) {
		try {
//			CRYPT_KEY=cryptKey;
			return byte2hex(encrypt(data.getBytes(), cryptKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*public static void main(String[] args) {
		System.out.println(encrypt("1001,1,"+System.currentTimeMillis()/1000+"", "8rFjG0J579p45eSP"));
		
	}
	public static void main(String[] args) {
		System.out.println(decrypt("D71EEFAE0DA4398C9BC8298B82AE14EE", "8r1jG0J579p45eSP"));
	}
	*/
}


































