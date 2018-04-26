package com.wboly.system.sys.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * 
 * @author dwh
 *
 */

public class WriterJsonUtil {
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
			response.setCharacterEncoding("gbk");
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
	
	/**
	 * 输出json
	 * 
	 * @author nick
	 * @param response
	 * @param object
	 */
	public static void writerJson(HttpServletResponse response, Object object, String type) {
		PrintWriter writer;
		Gson gson = new Gson();
		try {
			response.setCharacterEncoding(type);
			writer = response.getWriter();
			writer.write(gson.toJson(object));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
