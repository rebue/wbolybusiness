package com.wboly.system.sys.system;

import java.util.List;
import java.util.Map;

import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * @author Sea
 *
 */
public class SysContext {
	
	/**
	 * 静态全局定义变量
	 */

	/**
	 * 项目名
	 */
	public static String SYS_NAME;  
	/**
	 * 系统在服务器的位置
	 */
	public static  String REALPATH;
	/**
	 * Spring:applicationContext
	 */
	public static  WebApplicationContext WAC ;
	/**
	 * 所有的properties文件信息
	 */
	public static Map<String,Map<String,String>> PROPERTIES;
	/**
	 * 系统公钥匙 
	 */
	public static String SYS_KEY="59c23bdde5603ef993cf03fe64e448f1";
	/**
	 * 系统域名
	 */
	public static String DOMAINNAME;
	/**
	 * 上传文件保存的路径
	 */
	public static String UPLOAD_FILE_PATH;
	/**
	 * 获取用户头像域名
	 */
	public static String USERPHOTOURL;
	/**
	 * 支付路径
	 */
	public static String PAYURL;
	/**
	 * 用户推广二维码图片保存路径
	 */
	public static String QRCODEPATH;
	/**
	 * 查询接口所有配置文件的key和value转化为map
	 */
	public static Map<String,String> CONFIGMAP;		
	/**
	 * 批量审核单号文件临时存储位置
	 */
	public static String TEMPPATH;
	/**
	 * V支付请求地址
	 */
	//public static String VPAYACTIONURL;
	/**
	 * V支付请求地址
	 */
	public static String UPLOADDOMAINNAME;
	/**
	 * 公共的接口
	 */
	public static List<String> PUBLIC_INTERFACE;
	
	/** 新v支付请求接口  **/
	public static String VPAYURL;
	
	/** 新用户中心请求接口  **/
	public static String USERCENTERURL;
	
	/**上线微服务地址 **/
	public static String ONLINEURL;
	
	/** 订单微服务地址  **/
	public static String ORDERURL;
	
	/** 区域微服务地址  **/
	public static String AREAURL;
	
	/** 图片路径  **/
	public static String IMGPATH;
	
	/** 图片请求url  **/
	public static String IMGURL;
	
	// 项目名称
	public static String PROJECTNAME;
}
