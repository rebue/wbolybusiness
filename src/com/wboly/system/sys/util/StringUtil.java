package com.wboly.system.sys.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StringUtil {
	
	
	/**
	 * 获取淘口令
	 * @param content
	 * @return
	 */
	public static String getTaoKouLing(String content){

		if(content.lastIndexOf("$$")>content.indexOf("$$")){
			//System.out.println("$$淘口令$$:"+content.substring(content.indexOf("$$")+2, content.lastIndexOf("$$")));
			return content.substring(content.indexOf("$$")+2, content.lastIndexOf("$$"));
		}else{
			return "";
		}
		
	}
	
	
	/**
	 * 过滤掉HTML标签
	 * @param sql
	 * @return String
	 */	
	public static String cleanHTML(String content){
		if(content.indexOf("<")<content.indexOf(">")&&content.indexOf("<")!=-1){
			return cleanHTML(content.replace(content.substring(content.indexOf("<"),content.indexOf(">")+1), "")).replace("&nbsp;", "").replace(" ", "").trim().replace("&amp;", "&");
		}else{
			return content.replace("&nbsp;", "").replace(" ", "").trim();
		}
	}

	public static String getAHref(String content,String value){
		try {
			if (content.indexOf(value) != -1) {
				content = content.substring(0, content.indexOf(value));
				content = content.substring(content.lastIndexOf("<a"));
				content = content.substring(content.indexOf("href=") + 6);
				content = content.substring(0,content.indexOf('\"'));
				return content.replace("&amp;", "&");
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	
	
	
	
	/**
	 * 获取首页广告轮播图
	 * @param content
	 * @return
	 */
	public static List<Map<String ,Object>> indexAdvertisement(String content){
		List<Map<String ,Object>> list=new ArrayList<Map<String ,Object>>();
		
		if (content!=null) {
			String[] beans = content.split("}");
			for (String bean : beans) {
				if (bean != null) {
					if (bean.indexOf(".jpg") !=-1 && bean.indexOf("http://") !=-1) {
						String[] values = bean.split("\"");
						Map<String, Object> map = new HashMap<String, Object>();
						for (String value : values) {
							if (value != null) {
								if (value.indexOf(".jpg") >= 0) {
									map.put("img", value);
								}
								if (value.indexOf("http://") >= 0) {
									map.put("url", value);
								}
							}
						}
						list.add(map);

					}
				}

			}
		}
		return list;
	}
	public static String hidename(String name){
		int length = name.length();
		if(length<2){
			name = name+"****";
		}else{
			name = name.substring(0,1)+"****"+name.substring(name.length()-1,name.length());
		}
		
		return name;
	}
	
	public static void main(String[] args) {
		
		String content="a:5:{i:1;a:4:{s:5:\"image\";s:14:\"1453791004.jpg\";s:5:\"title\";s:6:\"圣诞\";s:3:\"url\";s:30:\"http://www.vboly.com/list.html\";s:8:\"dateline\";i:1453791004;}i:2;a:4:{s:5:\"image\";s:14:\"1453340820.jpg\";s:5:\"title\";s:9:\"惠而美\";s:3:\"url\";s:41:\"http://www.vboly.com/list?sellerid=125792\";s:8:\"dateline\";i:1453340820;}i:3;a:4:{s:5:\"image\";s:14:\"1454406002.jpg\";s:5:\"title\";s:6:\"美白\";s:3:\"url\";s:34:\"http://detail.vboly.com/29421.html\";s:8:\"dateline\";i:1454406002;}i:4;a:4:{s:5:\"image\";s:14:\"1453702453.jpg\";s:5:\"title\";s:9:\"活性炭\";s:3:\"url\";s:34:\"http://detail.vboly.com/32407.html\";s:8:\"dateline\";i:1453702453;}i:5;a:4:{s:5:\"image\";s:14:\"1453103039.jpg\";s:5:\"title\";s:9:\"置物架\";s:3:\"url\";s:41:\"http://www.vboly.com/list?sellerid=122711\";s:8:\"dateline\";i:1453103039;}}";
		List<Map<String ,Object>>list=indexAdvertisement(content);
		System.out.println(JsonUtil.objectToJson(list));
	}
		
	
	
}
