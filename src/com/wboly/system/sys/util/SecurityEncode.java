package com.wboly.system.sys.util;


public class SecurityEncode {

	/**
	 * 加密与解密
	 * @param text 加密或解密的text
	 * @param key  加密时所传的key
     * @return
     */
	public static String decoderOrEncoder(String text, long key) {
		char[] charsInt = text.toCharArray();
		for (int i = 0; i < charsInt.length; i++) {
			charsInt[i]^=key;
		}
		return new String(charsInt);
	}
	
	public static void main(String[] args) {
		System.out.println(decoderOrEncoder(decoderOrEncoder("方法 ", 1470294692), 1470294692));
	}
}
