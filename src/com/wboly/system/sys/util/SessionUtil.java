package com.wboly.system.sys.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
* @Name: SessionUtil.java
* @Author: nick
*/
public class SessionUtil {
	
	/**
	* @Name: 根据门店字段获取门店字段值
	* @Author: nick
	*/
	@SuppressWarnings("unchecked")
	public static Object getShopByColumn(HttpServletRequest request,String column){
		Object obj = getShopData(request);
		Object object = null;
		if(obj != null){
			Map<String, Object> shopData = (Map<String, Object>)obj;
			object = shopData.get(column);
		}
		return object;
	}
	
	/**
	* @Name: 从Session中获取门店数据
	* @Author: nick
	*/
	public static Object getShopData(HttpServletRequest request){
		return request.getSession().getAttribute("ASD");
	}
}
