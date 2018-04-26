package com.wboly.system.sys.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

/**
 * 后台接口请求
 * 
 * @author Sea
 *
 */
public class HttpUtil {

	/**
	 * @Name: POST 请求
	 * @param reqUrl
	 *            请求url
	 * @param jsonData
	 *            json字符数据请求
	 * @Author: knick
	 */
	public static String POST(String reqUrl, String jsonData) {

		try {

			URL url = new URL(reqUrl);

			HttpURLConnection url_con = (HttpURLConnection) url.openConnection();

			url_con.setDoOutput(true);// 使用 URL 连接进行输出
			url_con.setDoInput(true);// 使用 URL 连接进行输入
			url_con.setUseCaches(false);// 忽略缓存
			url_con.setRequestMethod("POST");
			url_con.setConnectTimeout(5000);// （单位：毫秒）jdk
			url_con.setReadTimeout(15000);// （单位：毫秒）jdk 1.5换成这个,读操作超时

			byte[] b = jsonData.getBytes();

			OutputStream out = url_con.getOutputStream();

			out.write(b, 0, b.length);

			out.flush();

			out.close();

			InputStream in = url_con.getInputStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String tempLine = rd.readLine();

			StringBuffer tempStr = new StringBuffer();

			while (tempLine != null) {
				tempStr.append(tempLine);
				tempLine = rd.readLine();
			}

			rd.close();

			in.close();

			url_con.disconnect();

			return tempStr.toString();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return "";
	}

	public static String getUrl(String url) {
		BufferedReader in = null;
		try {

			StringBuffer content = new StringBuffer();
			URL net = new URL(url);
			// 打开和URL之间的连接
			URLConnection urlConection = net.openConnection();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(urlConection.getInputStream(), "UTF-8"));

			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}

			return content.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * post提交数据
	 * 
	 * @param reqUrl
	 * @param parameters
	 * @param recvEncoding
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String postUrl(String reqUrl, Map<String, Object> parameters) {
		HttpURLConnection url_con = null;
		InputStream in = null;
		BufferedReader rd = null;
		// System.out.println("------------走通知------------");
		try {
			StringBuffer params = new StringBuffer();
			for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
				Entry element = (Entry) iter.next();
				params.append(element.getKey().toString());
				params.append("=");
				params.append(URLEncoder.encode(element.getValue().toString(), "UTF-8"));
				params.append("&");
			}
			if (params.length() > 0) {
				params = params.deleteCharAt(params.length() - 1);
			}
			// System.out.println("------传的字符-------:"+params);
			URL url = new URL(reqUrl);
			url_con = (HttpURLConnection) url.openConnection();
			// System.out.println("********建立连接*********");
			url_con.setDoOutput(true);// 使用 URL 连接进行输出
			url_con.setDoInput(true);// 使用 URL 连接进行输入
			url_con.setUseCaches(false);// 忽略缓存
			url_con.setRequestMethod("POST");
			// System.setProperty("sun.net.client.defaultConnectTimeout", String
			// .valueOf(HttpRequestProxy.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
			// System.setProperty("sun.net.client.defaultReadTimeout", String
			// .valueOf(HttpRequestProxy.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
			url_con.setConnectTimeout(50000);// （单位：毫秒）jdk
			// 1.5换成这个,连接超时
			url_con.setReadTimeout(50000);// （单位：毫秒）jdk 1.5换成这个,读操作超时

			byte[] b = params.toString().getBytes();
			url_con.getOutputStream().write(b, 0, b.length);
			url_con.getOutputStream().flush();
			url_con.getOutputStream().close();

			in = url_con.getInputStream();
			rd = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String tempLine = rd.readLine();
			StringBuffer tempStr = new StringBuffer();
			// String crlf = System.getProperty("line.separator");
			while (tempLine != null) {
				tempStr.append(tempLine);
				// tempStr.append(crlf);
				tempLine = rd.readLine();
			}
			return tempStr.toString();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (url_con != null) {
				url_con.disconnect();
			}
		}

	}

	/**
	 * post提交数据
	 * 
	 * @param reqUrl
	 * @param parameters
	 * @param recvEncoding
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String postUrl(String reqUrl, Map<String, Object> parameters, String charset) {
		HttpURLConnection url_con = null;
		InputStream in = null;
		BufferedReader rd = null;
		// System.out.println("------------走通知------------");
		try {
			StringBuffer params = new StringBuffer();
			for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
				Entry element = (Entry) iter.next();
				params.append(element.getKey().toString());
				params.append("=");
				params.append(URLEncoder.encode(element.getValue().toString(), charset));
				params.append("&");
			}
			if (params.length() > 0) {
				params = params.deleteCharAt(params.length() - 1);
			}
			// System.out.println("------传的字符-------:"+params);
			URL url = new URL(reqUrl);
			url_con = (HttpURLConnection) url.openConnection();
			// System.out.println("********建立连接*********");
			url_con.setDoOutput(true);// 使用 URL 连接进行输出
			url_con.setDoInput(true);// 使用 URL 连接进行输入
			url_con.setUseCaches(false);// 忽略缓存
			url_con.setRequestMethod("POST");
			// System.setProperty("sun.net.client.defaultConnectTimeout", String
			// .valueOf(HttpRequestProxy.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
			// System.setProperty("sun.net.client.defaultReadTimeout", String
			// .valueOf(HttpRequestProxy.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
			url_con.setConnectTimeout(50000);// （单位：毫秒）jdk
			// 1.5换成这个,连接超时
			url_con.setReadTimeout(50000);// （单位：毫秒）jdk 1.5换成这个,读操作超时

			byte[] b = params.toString().getBytes();
			url_con.getOutputStream().write(b, 0, b.length);
			url_con.getOutputStream().flush();
			url_con.getOutputStream().close();

			in = url_con.getInputStream();
			rd = new BufferedReader(new InputStreamReader(in, charset));

			String tempLine = rd.readLine();
			StringBuffer tempStr = new StringBuffer();
			String crlf = System.getProperty("line.separator");
			while (tempLine != null) {
				tempStr.append(tempLine);
				tempStr.append(crlf);
				tempLine = rd.readLine();
			}
			return tempStr.toString();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (url_con != null) {
				url_con.disconnect();
			}
		}
	}

	/**
	 * 移动下单 二维码上传到图片服务器
	 * 
	 * @param filepath
	 *            王太阳
	 */
	public static boolean upLoadFile(String filepath) {
		String targetURL = null;// TODO 指定URL
		File targetFile = null;// TODO 指定上传文件

		targetFile = new File(filepath);
		// targetURL = "http://115.29.192.168:8189/vbolyupload/file/upload.do";
		// //servleturl
		targetURL = "http://localhost:8080/vbolyweb/file/imageUpload?SYS_KEY=d0970714757783e6cf17b26fb8e2298f";
		PostMethod filePost = new PostMethod(targetURL);

		try {
			Part[] parts = { new FilePart(targetFile.getName(), targetFile) };
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			int status = client.executeMethod(filePost);
			if (status == HttpStatus.SC_OK) {
				System.out.println("上传成功");
				return true;
			} else {
				System.out.println("上传失败");
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			filePost.releaseConnection();
		}
	}

	public static void main(String[] args) {

		// String url="sfsdf";
		// String content=HttpUtil.getUrl(url);
		// System.out.println(content);

	}

}
