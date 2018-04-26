package com.wboly.system.sys.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
* @Name: 生成随机工具
* @Author: knick
*/
public class GeneUtil {
	
	public static void main(String[] args) {
		String str = "";
		for (int i = 0; i < 10; i++) {
			str += getRandomChar();
		}
		System.err.println(str);
	}
	
	/**
	* @Name: 生成多少个中文字符串
	* @param charLen 长度
	* @Author: knick
	*/
	public static String getRandomStr(int charLen){
		String str = "";
		for (int i = 0; i < charLen; i++) {
			str += getRandomChar();
		}
		return str;
	}
	
    /**
    * @Name: 随机生成常见的汉字
    * @Author: knick
    */
    public static char getRandomChar() {
        String str = "";
        int hightPos; 
        int lowPos;

        Random random = new Random();

        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str.charAt(0);
    }

}
