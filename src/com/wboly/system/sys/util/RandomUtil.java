package com.wboly.system.sys.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class RandomUtil {

	private static String[] code = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
			"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6",
			"7", "8", "9", "0" };

	/**
	* @Name: 生成多少个中文字符串
	* @param charLen 长度
	* @Author: knick
	*/
	public static String getRandomCH(int charLen){
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
	
	
	public static String getRandomStr(int num) {

		String randomNum = "";
		Random random = new Random();
		for (int i = 0; i < num; i++) {
			randomNum += code[random.nextInt(code.length - 1)];
		}
		return randomNum;
	}

	public static String getRandomNum(Integer num) {

		String randomNum = "";
		for (int i = 0; i < num; i++) {
			int num1 = (int) (Math.random() * (9 - 1));
			randomNum += num1;
		}
		return randomNum;
	}

	public static void main(String[] args) {
		System.out.println(getRandomNum(6));
		System.out.println(getRandomStr(6));
	}

}
