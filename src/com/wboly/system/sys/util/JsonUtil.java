package com.wboly.system.sys.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.wboly.system.sys.pojo.Page;

public class JsonUtil {

	public static final String DEFAULT_CONTENT_TYPE = "text/plain;charset=UTF-8";

	// *********************************json get
	// value*************************************

	/**
	* @Name: 根据key 获取 json value
	* @Author: nick
	* @param json json 字符串
	* @param key 该 json 的key
	* @return 返回该key的value,获取不到则返回null
	*/
	public static String GetJsonValue(String json,String key){
		
		try {
			if(json == null || "" == json) return null;
			
			JsonParser parser = new JsonParser();  //创建JSON解析器
			JsonObject obj =(JsonObject) parser.parse(json);
			if(obj.get(key)!=null){
				return obj.get(key).getAsString();
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	/**
	 * 根据属性名取值，通过JSON字符串
	 * 
	 * @author Sea
	 * @param json
	 * @param key
	 * @return String
	 */
	public static String getJSONValue(String json, String key) {

		try {
			if (json == null || json.equals("")) {
				return "";
			}

			net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) net.sf.json.JSONSerializer.toJSON(json);
			return jsonObject.get(key).toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 根据属性名取值，通过JSON
	 * 
	 * @author Sea
	 * @param JSONobject
	 * @param key
	 * @return String
	 */
	public static String getJSONValue(org.json.JSONObject JSONobject, String key) {
		try {
			return JSONobject.getString(key);
		} catch (JSONException e) {
			return "";
		}
	}

	// ***************************object to json
	// String********************************

	
	/**
	* @Name: 对象转json
	* @Author: nick
	*/
	public static String ObjectToJson(Object obj){
		try {
			Gson gson=new Gson();
			return gson.toJson(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	/**
	 * 实体类转化成JSON字符串
	 * 
	 * @author Sea
	 * @param object
	 * @return String
	 */
	public static String objectToString(Object object) {
		try {
			JSONObject jsonObject = new JSONObject(object, true);
			return jsonObject.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Map转化成JSON字符串
	 * 
	 * @author Sea
	 * @param map<String,Object>
	 * @return String
	 */
	public static String objectToJson(Map<String, Object> map) {
		StringBuffer json = new StringBuffer("{");
		for (Map.Entry<String, Object> my : map.entrySet()) {
			json.append(getJsonValue(my.getKey(), my.getValue()) + ",");
		}
		String content = json.toString();
		if (content.length() > 1) {
			content = content.substring(0, content.length() - 1) + "}";
		}
		return content;
	}

	/**
	 * @author Sea
	 * @param key
	 * @param obj
	 * @return String
	 */
	private static String getJsonValue(String key, Object obj) {

		if (obj != null) {
			String type = obj.getClass().getName();

			if (type.indexOf("com.vboly") != -1) {
				// 类
				return "\"" + key + "\":" + objectToString(obj);

			} else if (type.equals("java.util.ArrayList")) {
				// list
				return "\"" + key + "\":" + objectToString(obj);
			} else if (type.equals("java.util.Map")) {
				// map
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) obj;
				return "\"" + key + "\":\"" + objectToJson(map) + "\"";
			} else {
				// 普通类型
				return "\"" + key + "\":\"" + obj + "\"";
			}
		} else {
			return "\"" + key + "\":\"\"";
		}

	}

	/**
	 * List<Map<String,Object>>转化成JSON字符串
	 * 
	 * @author Sea
	 * @param list
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public static String objectToJson(List<Map<String, Object>> list) {
		if (list.size() > 0) {
			StringBuffer json = new StringBuffer("[");
			for (Object object : list) {
				Map<String, Object> map = (Map<String, Object>) object;
				json.append(objectToJson(map) + ",");

			}
			String content = json.toString();
			if (content.length() > 1) {
				content = content.substring(0, content.length() - 1) + "]";
			}
			return content;

		} else {
			return "[]";
		}

	}

	/**
	 * Page转化成JSON字符串
	 * 
	 * @author Sea
	 * @param page
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public static String objectToJson(Page page) {
		StringBuffer json = new StringBuffer("{");
		json.append("\"limit\":\"" + page.getLimit() + "\",");
		json.append("\"total\":\"" + page.getTotal() + "\",");
		json.append("\"start\":\"" + page.getStart() + "\",");
		json.append("\"pageNo\":\"" + page.getPageNo() + "\",");
		json.append("\"rows\":" + objectToJson(page.getRows()));
		json.append("}");
		return json.toString();
	}

	/**
	 * 实体类转化成JSON字符串
	 * 
	 * @author Sea
	 * @param collection
	 * @return String
	 */
	public static String objectToJson(Collection<?> collection) {
		String json = "[";
		for (Object object : collection) {
			JSONObject jsonObject = new JSONObject(object);
			json = json + jsonObject.toString() + ",";
		}
		if (!json.equals("[")) {
			json = json.substring(0, json.length() - 1);
		}
		json = json + "]";
		return json;
	}

	/**
	 * JSON字符串转化成map
	 * 
	 * @param json
	 * @return Map<String,Object>
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> jsonStringToMap(String jsonString) {

		Map<String, Object> map = new HashMap<String, Object>();
		net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) net.sf.json.JSONSerializer.toJSON(jsonString);

		for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
			String key = (String) iter.next();
			map.put(key, jsonObject.getString(key));
		}

		return map;
	}

	public static Object getJsonValue(String json, String key) {
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray("[" + json + "]}");
			if(jsonArray.getJSONObject(0).get(key) != null){
				return jsonArray.getJSONObject(0).get(key);
			}else{
				return null;
			}
		} catch (JSONException e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	/**
	* @Name: list集合的Json
	* @Author: nick
	* @param json json字符串
	* @param key 该json字符的key
	* @param level 该json 的层级
	*/
	public static Object getJsonValue(String json, String key,int level) {
		JSONArray jsonArray = null;
		Object value = "";
		try {
			if(json.startsWith("[")||json.endsWith("]")){
				jsonArray = new JSONArray(json);
			}else{
				jsonArray = new JSONArray("[" + json + "]}");
			}
			value = jsonArray.getJSONObject(level).get(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 将获取的json字符串转为listMap类型
	 * Title: resultMaps
	 * Description: 
	 * @param result
	 * @return
	 * @throws IOException
	 * @date 2018年3月28日 下午2:24:37
	 */
	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> listMaps(String result) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("将listmap字符串转为json的返回值为：" + result);
    	JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Map.class);  
    	//如果是Map类型  mapper.getTypeFactory().constructParametricType(HashMap.class,String.class, Bean.class);  
    	List<Map<String, Object>> list =  (List<Map<String, Object>>)mapper.readValue(result, javaType);
    	System.out.println("将json转为list的返回值为：" + String.valueOf(list));
    	return list;
	}

	public static void main(String[] args) {

	}

}
