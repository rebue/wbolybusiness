package com.wboly.system.sys.util;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookiesUtil {

	/**
	 * @Name: 保存微信 openid 到 cookie
	 * @Author: nick
	 */
	public static void setCookie(String openid, HttpServletResponse response) {
		if (openid == null || openid.equals("")) {
			return;
		}
		Cookie cookie1 = new Cookie("wechatuid", openid);
		cookie1.setPath("/");
		cookie1.setMaxAge(1000 * 60 * 60 * 24 * 20);// 二十天内有效
		Cookie cookie2 = new Cookie("wechatupa", MD5CodeUtil.md5(UUID.randomUUID().toString()));
		cookie2.setPath("/");
		cookie2.setMaxAge(1000 * 60 * 60 * 24 * 20);// 二十天内有效
		response.addCookie(cookie1);
		response.addCookie(cookie2);
	}

	/**
	 * @Name: 从 cookie 获取key
	 * @Author: knick
	 */
	public static String getCookieKey(HttpServletRequest request) {

		String uid = "", password = "";

		// 获取客户端所有Cookie信息
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("wechatuid".equals(cookie.getName())) {
					uid = cookie.getValue();
				}

				if ("wechatupa".equals(cookie.getName())) {
					password = cookie.getValue();
				}
			}
		}

		if (!uid.equals("") && !password.equals("")) {
			System.out.println("getCookieKey:" + uid);
			return uid;
		} else {
			return "";
		}
	}
	
	/**
	 * 根据传进来的key和value存进cookie
	 * @param request
	 * @param key
	 * @param value
	 */
	public static void setCookieKey(HttpServletResponse response, String key, String value) {
		Cookie cookie = new Cookie(key, value);
		response.addCookie(cookie);
	}
	
	/**
	 * 根据传进来的key值获取cookie信息
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getCookieKey(HttpServletRequest request, String key) {
		String value = "";
		// 获取客户端所有Cookie信息
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (key.equals(cookie.getName())) {
					value = cookie.getValue();
				}
			}
		}
		return value;
	}

	/// <summary>
	/// 取得随机字符串0~9 a~Z ~|
	/// </summary>
	/// <returns></returns>
	@SuppressWarnings("unused")
	private static String CreateCheckCodeString(int num) {
		String[] beforeShuffle = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
				"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
				"z" };
		List<String> list = Arrays.asList(beforeShuffle);
		Collections.shuffle(list);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++) {
			sb.append(list.get(i));
		}
		String result = sb.toString();
		// String result = afterShuffle.subString(5, 9);
		return result;
	}

	/// <summary>
	/// 写论坛临时验证码cookie
	/// </summary>
	/// <param name="cookieDomain">cookie域</param>
	/// <param name="salt">临时验证码</param>
	/// <param name="cookiekey">cookie密匙</param>
	/// <param name="cookiePre">论坛cookie前缀</param>
	public static void WriteLoginCookieSalt(String salt, String cookieDomain, String cookiekey, String cookiePre,
			HttpServletRequest request, HttpServletResponse response) {
		String cookieKey = cookiePre + "saltkey";
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			cookies = new Cookie[] {};
		}
		Cookie cookie = new Cookie(cookieKey, salt);

		// if (StringUtils.isNotEmpty(cookieDomain))
		// {
		// cookie.setDomain(cookieDomain);
		// }
		cookie.setPath("/");
		cookie.setMaxAge(1800);

		response.addCookie(cookie);
	}

	/// <summary>
	/// 通用登录cookie写入
	/// </summary>
	/// <param name="loginPswd">业务登录密码</param>
	/// <param name="cookieDomain">cookie域</param>
	/// <param name="uid">用户编号</param>
	/// <param name="dzPwd">公共通讯密匙</param>
	/// <param name="cookiePre">论坛cookie前缀</param>
	/// <param name="salt">临时登录验证字符串</param>
	@SuppressWarnings("deprecation")
	public static void WriteLoginCookie(String loginPswd, long uid, String cookieDomain, String dzPwd, String salt,
			String cookiePre, HttpServletRequest request, HttpServletResponse response) {
		String cookieKey = cookiePre + "auth";
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			cookies = new Cookie[] {};
		}

		String decodeUserInfo = loginPswd + "\t" + uid;
		String actionCookie = dzPwd + salt;
		actionCookie = MD5CodeUtil.md5(actionCookie);
		String authStr = Authcode.authcodeEncode(decodeUserInfo, actionCookie);
		Cookie cookie = new Cookie(cookieKey, URLEncoder.encode(authStr));

		/*
		 * if (StringUtils.isNotEmpty(cookieDomain)) { cookie.setDomain(cookieDomain); }
		 */
		cookie.setMaxAge(1800);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 生成userkey cookei
	 * 
	 * @param userkey
	 * @param cookieDomain
	 * @param request
	 * @param response
	 */
	public static void WriteUKCookie(String userkey, String cookieDomain, HttpServletRequest request,
			HttpServletResponse response) {
		String cookieKey = "USERKEY";
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			cookies = new Cookie[] {};
		}
		boolean succ = false;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("USERKEY")) {
				cookie.setValue(userkey);
				cookie.setMaxAge(1800);
				cookie.setPath("/");
				response.addCookie(cookie);
				succ = true;
				break;
			}
		}
		if (!succ) {
			Cookie cookie = new Cookie(cookieKey, userkey);
			/*
			 * if (StringUtils.isNotEmpty(cookieDomain)){ cookie.setDomain(cookieDomain); }
			 */
			cookie.setMaxAge(1800);
			cookie.setPath("/");
			response.addCookie(cookie);
		}

	}

	/**
	 * 取出userkey cookei
	 * 
	 * @param userkey
	 * @param cookieDomain
	 * @param request
	 * @param response
	 */
	public static String getUKCookie(HttpServletRequest request) {
		String userkey = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("USERKEY")) {
					userkey = cookie.getValue();
					break;
				}
			}
		}
		return userkey;
	}

	/**
	 * 取出userkey cookei
	 * 
	 * @param userkey
	 * @param cookieDomain
	 * @param request
	 * @param response
	 */
	public static String delCookie(HttpServletRequest request, HttpServletResponse response) {
		String userkey = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("USERKEY") || cookie.getName().contains("saltkey")
						|| cookie.getName().contains("auth")) {
					cookie.setValue(null);
					cookie.setMaxAge(0);
					// Cookie c = new Cookie(cookie.getName(),null);
					cookie.setPath("/");
					response.addCookie(cookie);
					request.setAttribute("USERKEY", null);
				}
			}
		}
		return userkey;
	}
}