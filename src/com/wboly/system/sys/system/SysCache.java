package com.wboly.system.sys.system;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wboly.system.sys.cache.RedisBase;
import com.wboly.system.sys.util.Base64EnOut;
import com.wboly.system.sys.util.CacheUtil;
import com.wboly.system.sys.util.CookiesUtil;
import com.wboly.system.sys.util.IpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.OrderKeyRandom;

import redis.clients.jedis.Jedis;

public class SysCache extends Base64EnOut {

	/**
	 * @Name: 清理该 key 下所有的信息
	 * @Author: knick
	 * @param key
	 *            需要清理的key
	 */
	public static void flushAll(String key) {
		if (null == key || "".equals(key)) {
			return;
		}
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				jedis.select(0);
				/************ 缓存清理 **************/
				String script = "";
				script = "local t = redis.call('keys', '" + key
						+ "*') if #t ~= 0 then redis.call('del', unpack(redis.call('keys', '" + key + "*')))  end";
				String code = jedis.scriptLoad(script);
				jedis.evalsha(code);
				/************ 缓存清理 **************/
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Name: 保存用户发送短信的次数(每个手机号每天只能发送五次)
	 * @Author: knick
	 * @param key
	 *            手机号码
	 * @param value
	 *            发送次数
	 */
	public static void setSendMoblieNum(String key, int value) {
		if (null == key || "".equals(key)) {
			return;
		}
		key = "mobileSendNum:" + key;

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 24); // 控制时
		calendar.set(Calendar.MINUTE, 0); // 控制分
		calendar.set(Calendar.SECOND, 0); // 控制秒

		Date time = calendar.getTime(); // 得出执行任务的时间

		// 算出距离当前凌晨十二点的秒数
		Integer ms = null;
		try {
			ms = (int) (time.getTime() / 1000 - System.currentTimeMillis() / 1000);
		} catch (Exception e) {
			ms = 60 * 60 * 24;
		}
		CacheUtil.putObject(key, ms, String.valueOf(value));
	}

	/**
	 * @Name: 读取该手机号一天发送的次数
	 * @Author: knick
	 * @param key
	 *            手机号码
	 */
	public static String getSendMoblieNum(String key) {
		if (null == key || "".equals(key)) {
			return "0";
		}
		key = "mobileSendNum:" + key;
		Object obj = CacheUtil.getObject(key);

		if (null == obj || "".equals(obj) || "null".equals(obj)) {
			return "0";
		}
		return String.valueOf(obj);
	}

	/**
	 * @Name: 读取该手机号上次发送的时间
	 * @Author: knick
	 * @param key
	 *            手机号码
	 */
	public static String getSendMoblieTime(String key) {
		if (null == key || "".equals(key)) {
			return "0";
		}
		key = "mobileTime:" + key;
		Object obj = CacheUtil.getObject(key);

		if (null == obj || "".equals(obj) || "null".equals(obj)) {
			return "0";
		}
		return String.valueOf(obj);
	}

	/**
	 * @Name: 保存用户发送短信的时间
	 * @Author: knick
	 * @param key
	 *            手机号码
	 * @param value
	 *            发送次数
	 */
	public static void setSendMoblieTime(String key, Long value) {
		if (null == key || "".equals(key)) {
			return;
		}
		String mobile = key;
		key = "mobileTime:" + key;

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 24); // 控制时
		calendar.set(Calendar.MINUTE, 0); // 控制分
		calendar.set(Calendar.SECOND, 0); // 控制秒

		Date time = calendar.getTime(); // 得出执行任务的时间

		// 算出距离当前凌晨十二点的秒数
		Integer ms = null;
		try {
			ms = (int) (time.getTime() / 1000 - System.currentTimeMillis() / 1000);
		} catch (Exception e) {
			ms = 60 * 60 * 24;
		}
		CacheUtil.putObject(key, ms, String.valueOf(value));
		String sendTime = getSendMoblieTime(mobile);
		System.err.println("缓存手机号：" + mobile + "缓存成功！内容为：" + sendTime);
	}
	
	/**
	 * 缓存用户手机验证码
	 * @param mobile
	 * @param verificationCode
	 */
	public static void setMobileVerificationCode(String mobile, String verificationCode) {
		if (mobile == null || verificationCode == null || mobile.equals("") || verificationCode.equals("")) {
			return;
		}
		String key = "wboly:wechat:mobileVerificationCode:" + mobile;
		CacheUtil.putObject(key, 60 * 30, verificationCode);
	}

	/**
	 * 跟据手机号码获取手机验证码
	 * @param mobile
	 * @return
	 */
	public static String getMobileVerificationCode(String mobile) {
		if (mobile.equals("") || mobile == null || mobile.equals("null")) {
			return "";
		}
		
		mobile = "wboly:wechat:mobileVerificationCode:" + mobile;
		Object obj = CacheUtil.getObject(mobile);
		if (obj != null && !obj.equals("") && !obj.equals("null")) {
			return obj.toString();
		}
		return "";
	}
	
	/**
	 * @Name: 保存用户微信支付的预支付ID(有效时间二十四小时)
	 * @Author: knick
	 */
	public static void setWxPayInfo(String key, String value) {
		if (null == key || "".equals(key)) {
			return;
		}
		key = "wboly:wechat:prepayid:" + MD5CodeUtil.md5(key);
		CacheUtil.putObject(key, 60 * 60 * 24, value);
	}

	/**
	 * @Name: 读取用户微信支付的预支付ID
	 * @Author: knick
	 */
	public static String getWxPayInfo(String key) {
		try {
			if (null == key || "".equals(key)) {
				return "";
			}
			key = "wboly:wechat:prepayid:" + MD5CodeUtil.md5(key);
			Object obj = CacheUtil.getObject(key);

			if (null == obj || "".equals(obj) || "null".equals(obj)) {
				return "";
			}
			return String.valueOf(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * @Name: 全局缓存 jsapi_ticket
	 * @Author: nick
	 */
	public static void setJsapiTicket(String value) {
		if (value == null || value.equals("")) {
			return;
		}
		String key = "wboly:wechat:jsapi:ticket";

		String expires_in = JsonUtil.GetJsonValue(value, "expires_in");

		if (expires_in == null || expires_in.equals("")) {
			return;
		}
		CacheUtil.putObject(key, Integer.parseInt(expires_in), value);
	}

	/**
	 * @Name: 读取 jsapi_ticket
	 * @Author: nick
	 */
	public static String getJsapiTicket() {
		String key = "wboly:wechat:jsapi:ticket";

		Object obj = CacheUtil.getObject(key);

		if (null == obj || "".equals(obj) || "null".equals(obj)) {
			return null;
		}

		String ticket = JsonUtil.GetJsonValue(obj.toString(), "ticket");

		if (ticket == null || ticket.equals("")) {
			return null;
		}
		return ticket;
	}

	/**
	 * @Name: 保存微信公众号基础支持的 access_token
	 * @Author: nick
	 */
	public static void setBaseAccessToken(String value) {
		if (value == null || value.equals("")) {
			return;
		}
		String key = "wboly:wechat:token:base";

		String expires_in = JsonUtil.GetJsonValue(value, "expires_in");

		if (expires_in == null || expires_in.equals("")) {
			return;
		}
		CacheUtil.putObject(key, Integer.parseInt(expires_in), value);
	}

	/**
	 * @Name: 读取微信公众号基础支持的 access_token
	 * @Author: nick
	 */
	public static String getBaseAccessToken() {
		String key = "wboly:wechat:token:base";

		Object obj = CacheUtil.getObject(key);

		if (null == obj || "".equals(obj) || "null".equals(obj)) {
			return null;
		}

		String access_token = JsonUtil.GetJsonValue(obj.toString(), "access_token");

		if (access_token == null || access_token.equals("")) {
			return null;
		}
		return access_token;
	}

	/**
	 * @Name: 保存微信 refresh_token 用于刷新授权的 access_token (二十天后失效)
	 * @Author: knick
	 */
	public static void setRefreshData(String openid, String value) {
		if (openid == null || openid.equals("")) {
			return;
		}
		String key = "wboly:wechat:token:refresh:" + Encode(MD5CodeUtil.md5(openid));
		CacheUtil.putObject(key, 60 * 60 * 24 * 20, value);
	}

	/**
	 * @Name: 获取微信 refresh_token 用于刷新授权的 access_token
	 * @Author: knick
	 */
	public static String getRefreshData(String openid) {
		if (openid == null || openid.equals("")) {
			return null;
		}
		String key = "wboly:wechat:token:refresh:" + Encode(MD5CodeUtil.md5(openid));
		Object obj = CacheUtil.getObject(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	/**
	 * @Name: 记录用户重置密码错误的次数(错误5次之后将被锁定三小时)
	 * @Author: knick
	 */
	public static void setResetPwdErrorNum(String uuid, String value) {
		String key = "wboly:wechat:error:resetpwdnum:" + uuid;
		CacheUtil.putObject(key, 10800, value);
	}

	/**
	 * @Name: 获取用户重置密码错误次数
	 * @Author: nick
	 */
	public static String getResetPwdErrorNum(String uuid) {
		String key = "wboly:wechat:error:resetpwdnum:" + uuid;
		Object obj = CacheUtil.getObject(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return "0";
		}
	}

	/**
	 * @Name: 记录用户绑定商超账户的次数(错误三次之后将被锁定三小时)
	 * @Author: knick
	 */
	public static void setBindErrorNum(String uuid, String value) {
		String key = "wboly:wechat:error:bindnum:" + uuid;
		CacheUtil.putObject(key, 10800, value);
	}

	/**
	 * @Name: 获取绑定错误次数
	 * @Author: nick
	 */
	public static String getBindErrorNum(String uuid) {
		String key = "wboly:wechat:error:bindnum:" + uuid;
		Object obj = CacheUtil.getObject(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return "0";
		}
	}

	/**
	 * @Name: 保存微信用户授权信息,有效时间二十天
	 * @Author: nick
	 */
	public static void setWeChatAuthData(String openid, String value) {
		if (openid == null || openid.equals("")) {
			return;
		}
		String key = "wboly:wechat:oauth:userinfo:" + openid;
		CacheUtil.putObject(key, 60 * 60 * 24 * 20, Encode(value));
		System.err.println(openid + "：已缓存成功授权数据：" + value);
	}

	/**
	 * @Name: 获取微信用户授权信息
	 * @Author: nick
	 */
	public static String getWeChatAuthData(String openid) {
		if (openid == null || openid.equals("")) {
			return null;
		}
		String key = "wboly:wechat:oauth:userinfo:" + openid;
		Object obj = CacheUtil.getObject(key);
		if (obj != null) {
			System.err.println(openid + "：已获取到授权信息：" + obj.toString());
			return Decode(obj.toString());
		} else {
			return null;
		}
	}

	/**
	 * @Name: 根据字段值获取该字段的用户信息
	 * @Author: nick
	 */
	public static String getWeChatUserByColumn(HttpServletRequest request, String Column) {
		String result = "";
		String cookieKey = CookiesUtil.getCookieKey(request);
		System.out.println("根据字段值获取字段的用户信息获取到的cookieKey为：" + cookieKey);
		if (!cookieKey.equals("")) {
			// 将application/x-www-form-urlencoded字符串转换成普通字符串
			result = JsonUtil.getJSONValue(getWechatUser(cookieKey), Column);
		}
		return result;
	}

	/**
	 * @Name: 保存用户登陆信息(有效期二十天)
	 * @Author: nick
	 */
	public static void setWechatUser(String openid, String userInfo) {
		if (openid == null || openid.equals("")) {
			return;
		}
		String key = "wboly:wechat:login:userinfo:" + Encode(MD5CodeUtil.md5(openid));
		System.err.println(openid + "正在缓存的信息为：" + userInfo);
		CacheUtil.putObject(key, 60 * 60 * 24 * 20, Encode(userInfo));
	}

	/**
	 * @Name: 获取用户登陆信息
	 * @Author: nick
	 */
	public static String getWechatUser(String openid) {
		System.out.println("获取用户登录信息的参数为：" + openid);
		if (openid == null || openid.equals("")) {
			return "";
		}

		String key = "wboly:wechat:login:userinfo:" + Encode(MD5CodeUtil.md5(openid));
		Object obj = CacheUtil.getObject(key);
		if (obj != null) {
			System.err.println(openid + "获取到登录的用户数据为：" + obj);
			return Decode(obj.toString());
		} else {
			System.err.println(openid + "没有获取到缓存的用户信息");
			return "";
		}
	}

	/**
	 * 用户登录失败次数赋值
	 * 
	 * @param username
	 * @return
	 */
	public static void setLoginErrorNum(String username, int num) {
		if (username != null) {
			String key = "wboly:com:user:login:errornum:" + MD5CodeUtil.md5(username);
			CacheUtil.putObject(key, 3600, String.valueOf(num));
		}
	}

	/**
	 * 获取用户登录失败次数
	 * 
	 * @param username
	 * @return
	 */
	public static int getLoginErrorNum(String username) {
		if (username != null) {
			String key = "wboly:com:user:login:errornum:" + MD5CodeUtil.md5(username);
			Object obj = CacheUtil.getObject(key);
			if (obj != null) {
				return Integer.parseInt(obj.toString());
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * 清除用户登录错误记录
	 * 
	 * @param username
	 * @return
	 */
	public static int deleteLoginErrorNum(String username) {
		try {
			if (username != null) {
				String key = "wboly:com:user:login:errornum:" + MD5CodeUtil.md5(username);
				CacheUtil.removeObject(key);
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 秘钥绑定用户ID
	 * 
	 * @param mykey
	 * @param userid
	 */
	public static String setUserid(String userid, HttpServletRequest request) {
		String ip = IpUtil.getIp(request);
		if (ip != null) {
			String userkey = MD5CodeUtil.md5(OrderKeyRandom.GenerateOrderNo());
			String key = "vboly:com:user:web:userkey:" + MD5CodeUtil.md5(userkey + ip);
			CacheUtil.putObject(key, 1800, String.valueOf(userid));
			return userkey;
		}
		return null;
	}

	/**
	 * 秘钥延时
	 * 
	 * @param userid
	 * @param userkey
	 */
	public static void setUserid(String userid, String userkey, HttpServletRequest request) {
		String ip = IpUtil.getIp(request);
		if (ip != null) {
			String key = "wboly:com:user:web:userkey:" + MD5CodeUtil.md5(userkey + ip);
			CacheUtil.putObject(key, 1800, String.valueOf(userid));
		}
	}

	/**
	 * 秘钥获取用户ID
	 * 
	 * @param mykey
	 * @param userid
	 */
	public static long getUserid(String userkey, HttpServletRequest request) {
		if (userkey != null) {
			String ip = IpUtil.getIp(request);
			if (ip != null) {
				String key = "wboly:com:user:web:userkey:" + MD5CodeUtil.md5(userkey + ip);
				Object obj = CacheUtil.getObject(key);
				if (obj != null) {
					return Long.parseLong(obj.toString());
				} else {
					return 0;
				}
			} else {
				return 0;
			}

		} else {
			return 0;
		}
	}

	/**
	 * 删除有效秘钥
	 * 
	 * @param mykey
	 * @param userid
	 */
	public static int deleteUserKey(String userkey) {
		try {
			if (userkey != null) {
				String key = "wboly:com:user:userkey:" + MD5CodeUtil.md5(userkey);
				CacheUtil.removeObject(key);
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 根据用户编号删除用户信息
	 * 
	 * @param userid
	 *            用户编号
	 */
	public static void removeUserJson(String userid) {
		String key = "wboly:com:user:info:" + MD5CodeUtil.md5(userid + "");
		CacheUtil.removeObject(key);
	}

	/**
	 * 用户ID存储用户信息
	 * 
	 * @param userid
	 *            用户编号
	 * @param user
	 *            用户信息
	 */
	public static void setUserJson(String userid, String user) {
		String key = "wboly:com:user:info:" + MD5CodeUtil.md5(userid + "");
		CacheUtil.putObject(key, user);
	}

	/**
	 * 秘钥获取用户
	 * 
	 * @param mykey
	 * @param userid
	 */
	public static String getUserJson(String userid) {
		String key = "wboly:com:user:info:" + MD5CodeUtil.md5(userid + "");
		Object obj = CacheUtil.getObject(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	/**
	 * 用户ID存储用户信息
	 * 
	 * @param mykey
	 * @param userid
	 */
	public static void setUserQRCodePath(String userid, String userQRCodePath) {
		String key = "com:wboly:user:QRCodePath:" + MD5CodeUtil.md5(userid + "");
		CacheUtil.putObject(key, userQRCodePath);
	}

	/**
	 * 秘钥获取用户
	 * 
	 * @param mykey
	 * @param userid
	 */
	public static String getUserQRCodePath(String userid) {
		String key = "com:wboly:user:QRCodePath:" + MD5CodeUtil.md5(userid + "");
		Object obj = CacheUtil.getObject(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	/**
	 * 王太阳 设置商家发布商品的时间戳(防止商家在发布商品的时候重复提交表单)
	 * 
	 * @param timestamp
	 *            //表单时间戳
	 * @param userid
	 *            //商家用户编号
	 */
	public static void setFromTimesTamp(String timestamp, String userid) {
		String key = "com:wboly:form:timestamp:" + userid;
		CacheUtil.putObject(key, userid);
	}

	/**
	 * 王太阳 设置商家发布商品的时间戳(防止商家在发布商品的时候重复提交表单)
	 * 
	 * @param timestamp
	 *            //表单时间戳
	 * @param userid
	 *            //商家用户编号
	 */
	public static String getFromTimesTamp(String userid) {
		String key = "com:vboly:form:timestamp:" + userid;
		Object obj = CacheUtil.getObject(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	/**
	 * 王太阳 设置商家发布商品的时间戳(防止商家在发布商品的时候重复提交表单)
	 * 
	 * @param timestamp
	 *            //表单时间戳
	 * @param userid
	 *            //商家用户编号
	 */
	public static String clearFromTimesTamp(String userid) {
		String key = "com:vboly:form:timestamp:" + userid;
		Object obj = CacheUtil.removeObject(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	/**
	 * 首页数据缓存
	 * 
	 * @param username
	 * @return
	 */
	public static void setIndexData(Map<String, Object> map) {
		if (map != null) {
			String key = "vboly:com:index:data";
			CacheUtil.putObject(key, 3600, map.toString());
		}
	}

	/**
	 * 获取首页数据
	 * 
	 * @param username
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getIndexData() {
		try {
			String key = "vboly:com:index:data";
			Map<String, Object> map = (Map<String, Object>) CacheUtil.getObject(key);
			if (map != null) {
				return map;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 缓存用户当前选则的门店信息
	 * 
	 * @param userId
	 * @param shopId
	 */
	public static void setUserAccessShopData(String userId, String shopdata) {
		if (!"".equals(shopdata) && shopdata != null && !"null".equals(shopdata)) {
			String key = "wboly:wechat:useraccessshopdata:" + userId;
			CacheUtil.setString(key, shopdata);
		}
	}

	/**
	 * 获取上次用户进入的门店信息
	 * 
	 * @param userId
	 * @return
	 */
	public static String getUserAccessShopData(String userId) {
		if (userId != null && !userId.equals("") && !"null".equals(userId)) {
			String shopdata = "";
			try {
				String key = "wboly:wechat:useraccessshopdata:" + userId;
				shopdata = CacheUtil.getString(key);
				return shopdata;
			} catch (Exception e) {
				e.printStackTrace();
				return shopdata;
			}
		} else {
			return "";
		}
	}
	
	/**
	 * 删除当前用户已缓存的上次进入的门店信息
	 * @param userId
	 * 2018年1月15日10:56:00
	 */
	public static void delUserAccessShopData(String userId) {
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			String key = "wboly:wechat:useraccessshopdata:" + userId;
			CacheUtil.delString(key);
		}
	}
	
	/**
	 * 保存用户提现次数
	 * @param userId
	 * @param num
	 * 2018年2月6日09:59:20
	 */
	public static void setUserWithdrawalNumber(String userId, String num) {
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			String key = "wboly:wechat:userwithdrawalnumber:" + userId;
			System.out.println("正在缓存提现记录的账号为：" + userId + ", 次数为：" + num);
			CacheUtil.putObject(key, 60 * 60 * 24, num);
		}
	}
	
	/**
	 * 获取用户提现次数
	 * @param userId
	 * @return
	 * 2018年2月6日10:07:17
	 */
	public static int getUserWithdrawalNumber(String userId) {
		int num = 0;
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			try {
				String key = "wboly:wechat:userwithdrawalnumber:" + userId;
				String nums = CacheUtil.getString(key);
				if (nums != null && !nums.equals("") && !nums.equals("null")) {
					num = Integer.parseInt(nums);
				}
				return num;
			} catch (Exception e) {
				e.printStackTrace();
				return num;
			}
		} else {
			return num;
		}
	}
	
	/**
	 * 缓存用户邮箱验证码
	 * @param openid
	 * @param code
	 * 2018年2月8日17:18:04
	 */
	public static void setEmailVerificationCode(String email, String emailCode) {
		if (email == null || emailCode == null || email.equals("") || emailCode.equals("")) {
			return;
		}
		String key = "wboly:wechat:emailverificationcode:" + email;
		CacheUtil.putObject(key, 60 * 30, emailCode);
	}
	
	/**
	 * 获取已缓存的用户邮箱验证码
	 * @param email
	 * @return
	 * 2018年2月8日17:22:30
	 */
	public static String getEmailVerificationCode(String email) {
		if (email != null && email.equals("") && email.equals("null")) {
			try {
				String key = "wboly:wechat:emailverificationcode:" + email;
				return CacheUtil.getString(key);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		} else {
			return "";
		}
	}
}
