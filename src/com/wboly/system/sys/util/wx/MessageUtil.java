package com.wboly.system.sys.util.wx;

/**
* @Name: 微信返回消息模板
* @Author: knick
*/
public class MessageUtil {
	
	
	/**
	* @Name: 文本消息模板
	* @Author: knick
	* @param fromUserName 公众号(服务端)
	* @param fromUserName 粉丝号(客户端)
	* @param textContent 返回消息内容
	*/
	public static String textMessage(Object fromUserName,Object toUserName,Object textContent){
		
		try {
			StringBuffer str = new StringBuffer();  
			
			str.append("<xml>");  
			str.append("<ToUserName><![CDATA[" + fromUserName + "]]></ToUserName>");  // 粉丝号(客户端)
			str.append("<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>");  // 公众号(服务端)
			str.append("<CreateTime>" + System.currentTimeMillis() / 1000 + "</CreateTime>");  // 当前系统时间
			str.append("<MsgType><![CDATA[text]]></MsgType>");  
			str.append("<Content><![CDATA[" + textContent + "]]></Content>");  // 文本内容
			str.append("</xml>");  
			
			return str.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
