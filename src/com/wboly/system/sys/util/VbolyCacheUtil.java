package com.wboly.system.sys.util;

import java.util.HashMap;
import java.util.Map;

import com.wboly.system.sys.system.SysContext;

/**
 * 清除网页缓存
 * @author Administrator
 *
 */
public class VbolyCacheUtil {
	
	
	/**
	 * 清除缓存
	 * @param innerInterface  vbl_config站内内部接口调用地址 innerInterface
	 * @param goodsid 	商品ID
	 * @param postuid 	操作人ID
	 * @param dzCookieKey	vbl_config 论坛cookie密匙 dzCookieKey
	 * @return
	 * @throws Exception
	 */
	public static String CleanCache( int goodsid,int postuid) throws Exception{
			
		String innerInterface=SysContext.CONFIGMAP.get("innerInterface");
		String dzCookieKey=SysContext.CONFIGMAP.get("dzCookieKey");
		
		String url = innerInterface+"goods/cleargoodscache/"+goodsid+"?";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("postuid", postuid);
		map.put("key", MD5CodeUtil.md5(""+goodsid+postuid+dzCookieKey));
		
		
		String result =HttpUtil.postUrl(url,map);
		
		
		return result;
	}
	
	/**
	 * 清除缓存
	 * @param innerInterface  vbl_config站内内部接口调用地址 innerInterface
	 * @param goodsid 	商品ID
	 * @param postuid 	操作人ID
	 * @param dzCookieKey	vbl_config 论坛cookie密匙 dzCookieKey
	 * @return
	 * @throws Exception
	 */
	public static String CleanCache( int goodsid,int postuid,int type) throws Exception{
			
		String innerInterface=SysContext.CONFIGMAP.get("innerInterface");
		String dzCookieKey=SysContext.CONFIGMAP.get("dzCookieKey");
		
		String url = innerInterface+"goods/cleargoodscache/"+goodsid+"?";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("postuid", postuid);
		map.put("type", type);
		map.put("key", MD5CodeUtil.md5(""+goodsid+postuid+dzCookieKey));
		
		
		String result =HttpUtil.postUrl(url,map);
		
		return result;
	}
	
	
	
	

}
