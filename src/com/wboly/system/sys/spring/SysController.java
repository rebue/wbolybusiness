package com.wboly.system.sys.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.wboly.system.sys.pojo.Messge;
import com.wboly.system.sys.pojo.Page;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.util.JsonUtil;

/**
 * 本系统所有控制类都继承此类
 * 
 * @author Sea
 *
 */
@Controller
public class SysController {

	protected final Log log = LogFactory.getLog(SysController.class);

	public static final String DEFAULT_CONTENT_TYPE = "text/plain;charset=GBK";

	public static Map<String, Object> retuenShop() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("shopId", "0");
		map.put("shopName", "请选择店铺...");
		return map;
	}

	/**
	 * @Name: 验证用户是否已登录
	 * @Author: nick
	 */
	protected boolean verifyLogin(HttpServletRequest request, HttpServletResponse response, String parameter) {
		if (parameter == null || parameter.equals("")) {
			this.render(response, "{\"result\":\"undefined key in parameter !\",\"flag\":false}");
			return false;
		}

		String userId = request.getParameter(parameter);
		if (userId == null || userId.equals("")) {
			this.render(response, "{\"result\":\"未获取到  parameter 的 值 !\",\"flag\":false}");
			return false;
		}

		if (userId != null && SysCache.getUserJson(userId) != null) {
			return true;
		} else {
			this.render(response, "{\"result\":\"您未登陆 !\",\"flag\":false}");
			return false;
		}
	}

	/**
	 * 未定义 ajax返回String
	 * 
	 * @param response
	 * @param text
	 */
	protected void render(HttpServletResponse response, String text) {
		try {
			response.setContentType(DEFAULT_CONTENT_TYPE);
			response.getWriter().write(text);
		} catch (IOException e) {
			this.log.error(e);
		}
	}

	protected void render(HttpServletResponse response, String text, String contentType) {
		try {
			response.setContentType(contentType);
			response.getWriter().write(text);
		} catch (IOException e) {
			this.log.error(e);
		}
	}

	/**
	 * ajax返回json,传参Object
	 * 
	 * @author Sea
	 * @param response
	 * @param object
	 */
	protected void render(HttpServletResponse response, Map<String, Object> map) {
		render(response, JsonUtil.objectToJson(map));
	}

	protected void render(HttpServletResponse response, List<Map<String, Object>> list) {
		render(response, JsonUtil.objectToJson(list));
	}

	protected void render(HttpServletResponse response, Page page) {
		render(response, JsonUtil.objectToJson(page));
	}

	protected void render(HttpServletResponse response, Collection<?> collection) {
		render(response, JsonUtil.objectToJson(collection));
	}

	protected void render(HttpServletResponse response, Object object) {
		JSONObject jsonObject = new JSONObject(object, true);
		render(response, jsonObject.toString());
	}

	/**
	 * ajax返回json，msg
	 * 
	 * @author Sea
	 * @param response
	 * @param result
	 * @param title
	 * @param msg
	 */
	protected void renderMsg(HttpServletResponse response, Boolean bool, String title, String content) {
		render(response, new Messge(bool, title, content));
	}

	/**
	 * 输出json
	 * 
	 * @author dwh
	 * @param response
	 * @param json
	 */
	public static void writerJson(HttpServletResponse response, String json) {
		PrintWriter writer;
		try {
			response.setCharacterEncoding("utf-8");
			writer = response.getWriter();
			writer.write(json);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 输出json
	 * 
	 * @author dwh
	 * @param response
	 * @param object
	 */
	public static void writerJson(HttpServletResponse response, Object object) {
		PrintWriter writer;
		Gson gson = new Gson();
		try {
			response.setCharacterEncoding("gbk");
			writer = response.getWriter();
			writer.write(gson.toJson(object));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void render(HttpServletResponse response, int object) {
		render(response, object + "");
	}
}
