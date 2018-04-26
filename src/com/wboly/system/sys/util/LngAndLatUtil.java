package com.wboly.system.sys.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/**
* @Name: 获取经纬度工具.java
* @Author: nick
*/
public class LngAndLatUtil {
	
	/**
	* @Name: 获取经度纬度
	* @Author: nick
	*/
	public static Map<String,Double> getLngAndLat(String address){
		
        Map<String,Double> map=new HashMap<String, Double>();
        String url = "http://api.map.baidu.com/geocoder/v2/?address="+address+"&output=json&ak=F454f8a5efe5e577997931cc01de3974";
        String json = postURL(url);
        JSONObject obj = JSONObject.fromObject(json);
        if(obj.get("status").toString().equals("0")){
            double lng=obj.getJSONObject("result").getJSONObject("location").getDouble("lng");
            double lat=obj.getJSONObject("result").getJSONObject("location").getDouble("lat");
            map.put("lng", lng);
            map.put("lat", lat);
        }else{
            System.err.println("未找到相匹配的经纬度！");
        }
        return map;
    }

    /**
    * @Name: 发送请求
    * @Author: nick
    */
    public static String postURL (String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return json.toString();
    }
    
    public static void main(String[] args){
        Map<String,Double> map = getLngAndLat("安吉华尔街工谷");
        System.out.println("经度："+map.get("lng")+"---纬度："+map.get("lat"));
    }
	

}
