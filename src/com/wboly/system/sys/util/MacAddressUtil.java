package com.wboly.system.sys.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 
 * @author dwh
 *
 */
public class MacAddressUtil {
	public static void main(String[] args) throws UnknownHostException, SocketException {

		InetAddress ia = InetAddress.getLocalHost();
		System.out.println(ia);
		System.out.println(getLocalMac());
	}

	public static String getLocalMac(InetAddress ia) throws SocketException {
		// 获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// 字节转换为整数
			int temp = mac[i] & 0xff;
			String str = Integer.toHexString(temp);
			if (str.length() == 1) {
				sb.append("0" + str);
			} else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();
	}

	public static String getLocalMac(){
		try {
			InetAddress ia = null;
			try {
				ia = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			// 获取网卡，获取地址
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append("-");
				}
				// 字节转换为整数
				int temp = mac[i] & 0xff;
				String str = Integer.toHexString(temp);
				if (str.length() == 1) {
					sb.append("0" + str);
				} else {
					sb.append(str);
				}
			}
			return sb.toString().toUpperCase();
		} catch (SocketException e) {
			e.printStackTrace();
			return "00000000";
		}
	}

}
