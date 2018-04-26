package com.wboly.modules.controller.Util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wboly.rpc.entity.IndexEntity;

public final class JsonUtil {

	public static final String DEFAULT_CONTENT_TYPE = "text/plain;charset=UTF-8";

	// *********************************json get
	// value*************************************

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
	 * 实体类转化成JSON字符串
	 * 
	 * @author Sea
	 * @param object
	 * @return String
	 */
	public static String objectToString(Object object) {
		JSONObject jsonObject = new JSONObject(object, true);
		return jsonObject.toString();
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
		Object value = "";
		try {
			jsonArray = new JSONArray("[" + json + "]}");
			value = jsonArray.getJSONObject(0).get(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 格式化首页JsonList
	 * 
	 * @author dwh
	 * @param json
	 * @return
	 */
	public static List<IndexEntity> formatJsonIndexList(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, new TypeToken<List<IndexEntity>>() {
		}.getType());
	}

	/**
	 * 格式化首页JsonMap
	 * 
	 * @author dwh
	 * @param json
	 * @return
	 */
	public static Map<String, IndexEntity> formatJsonIndexMap(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, new TypeToken<Map<String, IndexEntity>>() {
		}.getType());
	}

	public static List<String> formatList(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, new TypeToken<List<String>>() {
		}.getType());
	}
}
