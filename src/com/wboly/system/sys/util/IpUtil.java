package com.wboly.system.sys.util;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {

	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		ip = ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
		return ip;
	}

	public static String getIp2(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");  
		if (null != ip && !"unKnown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (null != ip  && !"unKnown".equalsIgnoreCase(ip)) { 
			return ip;
		}
		return request.getRemoteAddr();
	}

}
