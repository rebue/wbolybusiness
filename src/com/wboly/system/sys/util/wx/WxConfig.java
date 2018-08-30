package com.wboly.system.sys.util.wx;

/**
 * @Name: 微信公众号配置信息
 * @Author: knick
 */
public class WxConfig {

	/**
	 * 项目本地URL
	 */
	public static String onLineURL = "http://596038980.mynatapp.cc/wbolybusiness";

	/**
	 * 项目线上URL
	 */
	//public static String onLineURL = "http://serverapp.wboly.com/wbolybusiness";

	/**
	 * 公众号 token
	 */ 
	public static String token = "llashkdaskfnass234sd112mdasm1654";

	/**
	 * 消息加密密钥由43位字符组成，可随机修改，字符范围为A-Z，a-z，0-9
	 */
	public static String EncodingAESKey = "qHa385DFv3Ag4JAip1TRGV1f5o9R5WACqrZevcwCOwv";

	/**
	 * appid 公众号唯一标识
	 */
	//public static String appid = "wx9e24a0de9e3e136c";
	public static String appid = "wxa5f7f04a76cc41c5"; //线下

	/**
	 * secret 公众号的应用秘钥
	 */
	//public static String appsecret = "9ec229c0762f57c46b2d563333541424";
	public static String appsecret = "0b98b711e69a85c2b7303985f8a11fbb";// 线下

	/**
	 * 商户号（mch_id）
	 */
	public static String partner = "1444599902";

	/**
	 * 商户API初始秘钥
	 */
	public static String partnerkey = "hsjslejehgsabkl458shgdd4e47ehhss";

	/**
	 * 交易类型
	 */
	public static String trade_type = "JSAPI";

	/**
	 * 签字类型
	 */
	public static String signType = "MD5";

}
