package com.wboly.system.sys.util.wx;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;

import com.wboly.system.sys.util.MD5CodeUtil;

/**
* @Name: 微信公众号签字工具通用类
* @Author: knick
*/
@SuppressWarnings({"rawtypes","unchecked"})
public class WXSignUtils {
	
	private static String Key = WxConfig.partnerkey;
	
	public static String accessToken = null;  
	
	/**
	 * 微信支付签名算法sign
	 * @param characterEncoding
	 * @param parameters
	 * @return
	 */
	public static String createSign(String characterEncoding,SortedMap<Object,Object> parameters){
		StringBuffer sb = new StringBuffer();
		Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
		Iterator it = es.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			Object v = entry.getValue();
			if(null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + Key);
		System.err.println("字符串拼接后是："+sb.toString());
		String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
		return sign;
	}

	public static String getSign(Map<String,String> paramMap,String key){
		List list = new ArrayList(paramMap.keySet());
		Object[] ary = list.toArray();
		Arrays.sort(ary);
		list = Arrays.asList(ary);
		String str = "";
		for(int i=0;i<list.size();i++){
			str+=list.get(i)+"="+paramMap.get(list.get(i)+"")+"&";
		}
		str +="key="+key;
		str = MD5CodeUtil.md5(str).toUpperCase();

		return str;
	}
    
	/**
    * @Name: JSSDK 签名 sign
    */
    public static Map<String, String> createSign(String url) { 
    	
        accessToken = WeixinUtil.getAccessToken();
        
        String jsapi_ticket = WeixinUtil.getJsapiTicket(accessToken);
          
        Map<String, String> ret = new HashMap<String, String>();  
        String nonce_str = create_nonce_str();  
        String timestamp = create_timestamp();  
        String string1;  
        String signature = "";  // 签字
  
        //注意这里参数名必须全部小写，且必须有序  
        /*string1 = "jsapi_ticket=" + jsapi_ticket +  
                  "&noncestr=" + nonce_str +  
                  "&timestamp=" + timestamp +  
                  "&url=" + url;*/  
        
        String[] paramArr = new String[] { "jsapi_ticket=" + jsapi_ticket,
				"timestamp=" + timestamp, "noncestr=" + nonce_str, "url=" + url };
        
		Arrays.sort(paramArr);
		
		// 将排序后的结果拼接成一个字符串
		string1 = paramArr[0].concat("&"+paramArr[1]).concat("&"+paramArr[2]).concat("&"+paramArr[3]);
		
        System.err.println("jssdk:"+string1);  
  
        try  
        {  
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");  
            crypt.reset();  
            crypt.update(string1.getBytes("UTF-8"));  
            signature = byteToHex(crypt.digest());  
            System.err.println("JSSDK签字:"+signature);
        }  
        catch (NoSuchAlgorithmException e)  
        {  
            e.printStackTrace();  
        }  
        catch (UnsupportedEncodingException e)  
        {  
            e.printStackTrace();  
        }  
  
        ret.put("url", url);  
        ret.put("jsapi_ticket", jsapi_ticket);  
        ret.put("nonceStr", nonce_str);  
        ret.put("timestamp", timestamp);  
        ret.put("signature", signature);  
        ret.put("appid", WxConfig.appid);  
  
       /* System.out.println("1.ticket(原始)="+jsapi_ticket);  
        System.out.println("2.url="+ret.get("url"));  
        System.out.println("3.jsapi_ticket（处理后）="+ret.get("jsapi_ticket"));  
        System.out.println("4.nonceStr="+ret.get("nonceStr"));  
        System.out.println("5.signature="+ret.get("signature"));  
        System.out.println("6.timestamp="+ret.get("timestamp"));  */
          
        return ret;  
    }  
    
    
    /**
    * @Name: JSSDK 签名 sign
    * @param jsapi_ticket 公众号用于调用微信JS接口的临时票据(jsapi_ticket的有效期为7200秒)
    * @param url 签名用的url必须是调用JS接口页面的完整URL
    */
    public static Map<String, String> createSign(String jsapi_ticket, String url) {
    	
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
        
        System.err.println("jssdk:"+string1);

        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }
      
      
    /** 
     * 随机加密 
     * @param hash 
     * @return 
     */  
    private static String byteToHex(final byte[] hash) {  
        Formatter formatter = new Formatter();  
        for (byte b : hash)  
        {  
            formatter.format("%02x", b);  
        }  
        String result = formatter.toString();  
        formatter.close();  
        return result;  
    }  
  
    /** 
     * 产生随机串--由程序自己随机产生 
     * @return 
     */  
    private static String create_nonce_str() {  
        return UUID.randomUUID().toString().replaceAll("-", "");  
    }  
  
    /** 
     * 由程序自己获取当前时间 
     * @return 
     */  
    private static String create_timestamp() {  
        return Long.toString(System.currentTimeMillis() / 1000);  
    }  

    
    public static void main(String[] args) {
		Map<String, String> createSign = createSign("http://serverapp.wboly.com/wbolybusiness/wechat/oauth2/regnewuser/page.htm");
		System.err.println(createSign);
		createSign.clear();
		createSign = createSign(WeixinUtil.getJsapiTicket(WeixinUtil.getAccessToken()),"http://serverapp.wboly.com/wbolybusiness/wechat/oauth2/regnewuser/page.htm");
		System.err.println(createSign);
	}
    
}
