package com.haoyunhu.tools.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

public class AES256Utils {

	public static void main(String[] args) throws Exception {
		String key = "3b3ed1f02d8943939bd8840a0f1fc7c4";

		String plainText;
		String encodeText;
		String decodeText;
		// Encrypt
		plainText = "{\"carrierCode\":\"13200000000\",\"pageIndex\":\"1\",\"pageSize\":\"10\"}";
		encodeText = AES_Encode(plainText, key);
		System.out.println("AES256_Encode : " + encodeText);

		// Decrypt
//		encodeText = "p3uzF7Xvy9nAU++UzMZWamXD7VT2DNOGahWT0T+lrwE=";
//		encodeText="qPaJ0MPIXxiqMoaCEchEMwOOo6y2MYafKCIe8u0F8xQZksrK9swWOJNko0v/o9cjRSgmZmdR3X5i599Q0knmkQ==";
		decodeText = aesDecode(encodeText, key);
		System.out.println("AES256_Decode : " + decodeText);
	}

	public static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	public static String AES_Encode(String str, String key) throws Exception {
		byte[] textBytes = str.getBytes("UTF-8");
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = null;
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
		return Base64.encodeBase64String(cipher.doFinal(textBytes));
	}

	public static String aesDecode(String str, String key) throws Exception {
		byte[] textBytes = Base64.decodeBase64(str);
		// byte[] textBytes = str.getBytes("UTF-8");
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
		return new String(cipher.doFinal(textBytes), "UTF-8");
	}
}
