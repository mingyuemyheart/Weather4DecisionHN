package com.android.lib.util;

import java.security.MessageDigest;


/**
 * Depiction:MD5
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年3月30日 下午4:52:59
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class MD5 {
	
	private MD5() {
	}
	
	public static String md5(String source) {
		char hexDigits[] = {
		        '0',
		        '1',
		        '2',
		        '3',
		        '4',
		        '5',
		        '6',
		        '7',
		        '8',
		        '9',
		        'A',
		        'B',
		        'C',
		        'D',
		        'E',
		        'F'
		};
		try {
			byte[] btInput = source.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
