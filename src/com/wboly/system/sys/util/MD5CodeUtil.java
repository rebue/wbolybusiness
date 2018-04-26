package com.wboly.system.sys.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.apache.log4j.Logger;

/**
 * @author yanghao_zfxt(yanghao@citsinfo.com) 类说明: MD5编码工具类
 */
public class MD5CodeUtil {

	protected final Logger logger = Logger.getLogger(MD5CodeUtil.class);

	/** 小写十六进制字符 */
	protected final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	/** 大写十六进制字符 */
	protected final static String[] HEXDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
			"E", "F" };

	/**
	 * 构造器
	 */
	public MD5CodeUtil() {
	}
	
	/**
	* @Name: sha1 加密
	* @Author: knick
	*/
	public static String SHA1(String encodeStr){
		try{
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(encodeStr.getBytes("UTF-8"));
            encodeStr = byteToHex(crypt.digest());
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
		return encodeStr;
	}
	
	
	/** 
     * 随机加密 
     * @param hash 
     * @return 
     */  
    private static String byteToHex(final byte[] hash) { 
        try {
			Formatter formatter = new Formatter();  
			for (byte b : hash){  
			    formatter.format("%02x", b);  
			}  
			String result = formatter.toString();  
			formatter.close();  
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}  
        return "";
    }
	

	/**
	 * 二进制数组转十六进制字符串
	 *
	 * @param b
	 * @return
	 */
	public static final String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * 二进制数转十六进制字符串
	 *
	 * @param b
	 * @return
	 */
	public static final String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		return hexDigits[n / 16] + hexDigits[n % 16];
	}

	/**
	 * 对文件生成MD5码
	 *
	 * @param fileAbsolutePath
	 *            文件绝对路径
	 * @return 生成的MD5码, 不为空
	 */
	public static String encodeFile(String fileAbsolutePath) {
		String resultString = "";

		// 不论什麽异常，都不会影响计算，只是如果异常发生，那么该值为"",显然不是md5结果串
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			File file = new File(fileAbsolutePath);
			if (file.exists()) {
				FileInputStream in = new FileInputStream(file);
				byte[] fileBytes = new byte[(int) file.length()];
				in.read(fileBytes);

				resultString = byteArrayToHexString(md.digest(fileBytes));
				in.close();
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultString;
	}

	/**
	 * 进行MD5编码，获得字节数组
	 *
	 * @param origin
	 * @return
	 */
	public static final byte[] encodeToBytes(String origin) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(new String(origin).getBytes());
		} catch (Exception e) {
			System.err.println("无法获得MD5实例");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 进行MD5编码
	 *
	 * @param origin
	 * @return
	 */
	public static final String encode(String origin) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return byteArrayToHexString(md.digest(new String(origin).getBytes()));
		} catch (Exception e) {
			System.err.println("无法获得MD5实例");
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 进行MD5编码
	 *
	 * @param origin
	 * @return
	 */
	public static final byte[] encode(byte[] origin) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(origin);
		} catch (Exception e) {
			System.err.println("无法获得MD5实例");
			e.printStackTrace();
			return new byte[0];
		}
	}

	/**
	 * 使用Java的MessageDigest类将x字符串进行MD5加密
	 *
	 * @param x
	 *            String
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String md5(String x) {
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(x.getBytes("UTF8"));
			byte s[] = m.digest();
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < s.length; i++) {
				result.append(Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用Java的MessageDigest类将x字符串进行MD5加密
	 *
	 * @param x
	 *            String
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String md5GB2312(String x) {
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(x.getBytes("gb2312"));
			byte s[] = m.digest();
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < s.length; i++) {
				result.append(Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		System.out.println(md5GB2312("122469af8f50mH1lwo1pnM"));
		System.out.println(md5("122469af8f50mH1lwo1pnM"));
	}

}
