package com.wboly.modules.controller.Util;

import javax.servlet.http.HttpServletRequest;

import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.util.CookiesUtil;

public abstract class GetUsersUtil {

	public static long getUsersId(HttpServletRequest request) {
		String usersKey = CookiesUtil.getUKCookie(request);
		long usersId = SysCache.getUserid(usersKey, request);
		return usersId;
	}
}
