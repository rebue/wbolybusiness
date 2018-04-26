package com.wboly.system.sys.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wboly.system.sys.system.SysContext;

/**
 * 
 * @author dwh
 *
 */
public class SendEmailUtil {

	/**
	 * 发送注册验证码到用户注册邮箱
	 * 
	 * @author dwh
	 * @param request
	 * @return
	 */
	public static String sendMeailVerifictionCode(HttpServletRequest request) {
		String usersId = request.getParameter("userid");
		String email = request.getParameter("email");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);
		map.put("postkey", MD5CodeUtil.md5(email + SysContext.CONFIGMAP.get("dzCookieKey")));
		map.put("uid", usersId);
		return HttpUtil.postUrl(SysContext.CONFIGMAP.get("innerInterface") + "user/postgeneralemail?", map);
	}

	/**
	 * 发送邮箱验证码
	 * 
	 * @author dwh
	 * @param email
	 * @return
	 */
	public static String sendMeailCode(String email) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("postkey", MD5CodeUtil.md5(email + SysContext.CONFIGMAP.get("dzCookieKey")));
		map.put("email", email);
		map.put("uid", "123");
		return HttpUtil.postUrl(SysContext.CONFIGMAP.get("innerInterface") + "user/postgeneralemail?", map);
	}

}
