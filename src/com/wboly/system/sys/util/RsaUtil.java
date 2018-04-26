package com.wboly.system.sys.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by huangxue on 2017/2/28.
 */

public class RsaUtil {

    private static String RSA = "RSA";
    
    /**
	 * @Name: rsa 解密
	 * @Author: nick
	 */
	public  String RsaDecode(String strKey) {
		InputStream inPrivate = this.getClass().getClassLoader().getResourceAsStream("private_key_pcks8.pem");
		try {
			PrivateKey privateKey = RsaUtil.loadPrivateKey(inPrivate);
			byte[] decryptByte = RsaUtil.decryptData(Base64.decode(strKey), privateKey);
			String decryptStr = new String(decryptByte, "utf-8");
			return decryptStr;
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @Name: rsa 加密
	 * @Author: nick
	 */
	public String RsaEncode(String strKey) {
		InputStream inPrivate = this.getClass().getClassLoader().getResourceAsStream("rsa_public_key.pem");
		try {
			PublicKey publicKey = RsaUtil.loadPublicKey(inPrivate);
			byte[] decryptByte =RsaUtil.encryptData(strKey.getBytes(), publicKey);
			return Base64.encode(decryptByte);
		} catch (Exception e) {
			return null;
		}
	}
    
    /**
     * 用公钥加密 <br>
     * 每次加密的字节数，不能超过密钥的长度值减去11
     * @param data 需加密数据的byte数据
     * @return 加密后的byte型数据
     */
    public static byte[] encryptData(byte[] data, PublicKey publicKey)
    {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 传入编码数据并返回编码结果
            return cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 加载公钥
     * @param in 公钥数据流
     * @throws Exception 加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(InputStream in) throws Exception
    {
            byte[] buffer = Base64.decode(readKey(in));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
    }


    public static byte[] decryptData(byte[] encryptedData, PrivateKey privateKey)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e)
        {
            return null;
        }
    }



    public static PrivateKey loadPrivateKey(InputStream in) throws Exception
    {
            byte[] buffer = Base64.decode(readKey(in));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }


    private static String readKey(InputStream in) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null)
        {
            if (readLine.charAt(0) == '-')
            {
                continue;
            } else
            {
                sb.append(readLine);
                sb.append('\r');
            }
        }
        return sb.toString();
    }
}
