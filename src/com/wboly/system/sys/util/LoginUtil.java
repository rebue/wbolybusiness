package com.wboly.system.sys.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class LoginUtil {
      //普遍Cookie键
     // private static final String COOKIEKEY="vboly.com";
      //用户Cookie键
    //  private static final String USERCOOKIEKEY="vboly.user";
      /**
       * 返回cookies中的用户uid
       * @param authStr:加密的用户信息
       * @param dzPwd:DX密匙
       * @param cookieDomain:cookie域
       * @param cookiePre:论坛cookie前缀
       * @return
       */
      public static int getCookieUid(String authStr,String dzPwd,String cookieDomain,String cookiePre){
    	  String salt=getUserCookie("saltkey",cookieDomain,cookiePre);
    	  String actionCookie=dzPwd+salt;
    	  actionCookie=MD5CodeUtil.md5(actionCookie);
    	  String auth=Authcode.authcodeDecode(authStr, actionCookie);
    	  String[] data=auth.split("\t");
    	  if(data.length==2){
    		  return 0;
    	  }
    	  return Integer.parseInt(data[1]);
      }
      /**
       * 写论坛临时验证码cookie
       * @param cookieDomain:cookie域
       * @param salt:临时验证码
       * @param cookiekey:cookie密匙
       * @param cookiePre:论坛cookie前缀
       */
      public static void writeLoginCookieSalt(HttpServletRequest request,HttpServletResponse response,String cookieDomain,String salt,String cookiekey,String cookiePre){
    	// 获取cookie集合
  		Map<String, String> cookieMap = getCookieMap(request);
  		String cookieKey = cookiePre + "saltkey";
  		String cookieVal = cookieMap.get(cookieKey);
  		//判断某字符串是否为空或长度为0或由空白符(whitespace) 构成 
  		if (StringUtils.isBlank(cookieVal)) {
  			// 如果为空
  			Cookie cookie = new Cookie(cookiekey, salt);
  			cookie.setPath("/");
  			cookie.setDomain(cookieDomain);
  			// 添加cookie
  			response.addCookie(cookie);
  		}  
  
       }
     
      /**
       * 写普通cookie值
       * @param strName:cookie键
       * @param strValue:cookie值
       * @param cookieDomain:cookie域
       */
      public static void writeCookie(HttpServletRequest request,HttpServletResponse response,String strName,String strValue,String cookieDomain){
    	// 获取cookie集合
    	  Map<String,String>cookieMap=getCookieMap(request);
    	  String cookiesVal=cookieMap.get(strName);
    	//判断某字符串是否为空或长度为0或由空白符(whitespace) 构成 
    	  if(StringUtils.isBlank(cookiesVal)){
    		  
    	  }
      }
      /**
       * 写普通cookie值,时间秒钟计
       * @param strName:cookie键
       * @param strValue:cookie值
       * @param expires:过期时间(单位：秒)
       * @param cookieDomain:cookie域
       */
      public static void writeCookie(String strName,String strValue,int expires,String cookieDomain){
       	  
      }
      /**
       * 获得域cookie值
       * @param strName
       * @return
       */
      public static String getCookie(String strName){
    	  return null;
      }
 
      /**
       * 获取用户cookie值
       * @param strName:项
       * @param cookieDomain:cookie域
       * @param cookiePre:论坛cookie前缀
       * @return
       */
      public static String getUserCookie(String strName,String cookieDomain,String cookiePre){
    	  return null;
      }
 
      /**
       * 清除登录用户的cookie
       * @param cookieDomain:cookie域
       * @param cookiePre:论坛cookie前缀
       */
      public static void clearUserCookie(String cookieDomain,String cookiePre){
    	  
      }
 
      /**
       * 清除指定名称的cookie
       * @param cookieName
       * @param cookiePre
       */
      public static void clearCookie(String cookieName,String cookiePre){
    	  
      }
 
      /**
       * 是否为有效域
       * @param host
       * @return
       */
      public static boolean isValidDomain(String host){
    	  return true;
      }

      /**
       * 加密字符串
       * @param str:加密前的原字符串
       * @param key:密钥
       * @return
       */
      public static String setCookiePassword(String str,String key){
    	  return null;
      }
  
	   /**
	    * 通用登录cookie写入
	    * @param loginPwd:业务登录密码
	    * @param uid:用户编号
	    * @param cookieDomain:cookie域
	    * @param dzPwd:公共通讯密匙
	    * @param salt:临时登录验证字符串
	    * @param cookiePre:论坛cookie前缀
	    */
      public static void writeLoginCookie(String loginPwd,long uid,String cookieDomain,String dzPwd,String salt,String cookiePre){
    	  
      }
      /**
       * 根据密码明文获取加密后的密码
       * @param pwd:密码明文
       * @param salt
       * @return
       */
      public static String getDecodePwd(String pwd,String salt){
    	  return null;
      }

      /**
       * 获取cookie值
       * @param cookieName:项
       * @param cookieDomain:cookie域
       * @return
       */
      public static String getCookieValue(String cookieName,String cookieDomain){
    	  return null;
      }

      /**
       * 清除单个指定名称的cookie
       * @param cookieName
       * @param cookieDomain
       */
      public static void clearSingleCookie(String cookieName,String cookieDomain){
    	  
      }
	     /**
	  	 * 获取Map格式的Cookie集合
	  	 * 
	  	 * @author lsz
	  	 * @param request
	  	 * @return
	  	 */
	  	public static Map<String, String> getCookieMap(HttpServletRequest request) {
	  		Map<String, String> cookieMap = new HashMap<String, String>();
	  		Cookie[] cookies = request.getCookies();// 获取cookie数组
	  		if (null != cookies) {
	  			for (Cookie cookie : cookies) {
	  				cookieMap.put(cookie.getName(), cookie.getValue());
	  			}
	  		}
	
	  		return cookieMap;
	  	}
}