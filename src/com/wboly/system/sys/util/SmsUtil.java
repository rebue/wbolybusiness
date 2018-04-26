package com.wboly.system.sys.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import com.wboly.system.sys.cache.RedisBase;
import com.wboly.system.sys.system.SysCache;

import redis.clients.jedis.Jedis;

public class SmsUtil {

	private static final String serviceSendSMSURL = "http://sdk.zhongguowuxian.com:98/ws/BatchSend2.aspx?";
	private static final String corpID = "GZLKJ0005653";
	private static final String pwd = "bk9Z2Z5DjqmxzAhRBk%mNHu508@AijTm";

	/**
	 * 注册发送验证码
	 * 
	 * @param mobile
	 * @return String
	 */
	public static String sendLoginSMS(HttpServletRequest request, String mobile, String mac) throws Exception {

		String unionid = String.valueOf(request.getSession().getAttribute("unionid")); // 获取微信用户unionid
		String openid = CookiesUtil.getCookieKey(request); // 获取微信openid
		String uip = IpUtil.getIp(request); // 获取用户ip地址
		String umac = MacAddressUtil.getLocalMac(); // 获取用户mac地址
		System.err.println(mobile + ": 注册的ip地址为：" + uip + ", mac地址为：" + umac + ", unionid为：" + unionid);
		// 判断是否是从微信进来注册，如果是则unionid不为空，如果unionid为空，则说明是有人刷
		if (unionid != null && !unionid.equals("") && !"null".equals(unionid) || openid != null && !openid.equals("") && !openid.equals("null")) {
			String code = RandomUtil.getRandomNum(6);
			String content = "您的验证码是：" + code + "，此验证码30分钟内有效，请及时做好相关操作（此信息由系统自动发送，请勿回复）";
			SysCache.setMobileVerificationCode(mobile, code);
			Jedis jedis = null;

			try {
				jedis = RedisBase.getJedis();
				if (null != jedis) {
					jedis.select(0);
					jedis.set("mobile:" + mobile + mac, code);
					jedis.expire("mobile:" + mobile + mac, 30 * 60);

					return sendSMS(request, mobile, content, "");
				} else {
					System.err.println("无法获取 redis 实例");
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (null != jedis) {
					jedis.close();
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * 校验验证码
	 * 
	 * @param mobile
	 * @return String
	 */
	public static boolean ValidateSmsCode(String mobile, String mac, String code) throws Exception {

		Jedis jedis = null;
		boolean flag = false;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				jedis.select(0);
				jedis.set("mobile:" + mobile, code);
				if (jedis.exists("mobile:" + mobile)) {
					String value = jedis.get("mobile:" + mobile + mac);
					if (value != null && value.equals(code)) {
						flag = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}

		return flag;
	}

	/*
	 * public static void main(String [] args)throws Exception{ String syscode =
	 * RandomUtil.getRandomNum(6); System.out.println(syscode); String
	 * num=sendLoginSMS("15107810004"); System.out.println(num); }
	 */

	/*
	 * 方法名称：sendSMS 功 能：发送短信 ,传多个手机号就是群发(群发手机号码逗号隔开)，一个手机号就是单条提交 参
	 * 数：mobile,content,sendTime,(手机号，内容，，定时时间方法（不定时可用空字符）) 返 回 值：大于0的整数，提交成功,
	 * -1账号未注册，-2其他错误，-3帐号或密码错误，-5余额不足，请充值，-6定时发送时间不是有效的时间格式，
	 * -7提交信息末尾未加签名，请添加中文的企业签名【 】，-8发送内容需在1到300字之间。-9发送号码为空，-10定时时间不能小于系统当前时间，
	 * 101调用接口速度太快 -103 短信运营商已停止服务
	 */
	public static String sendSMS(HttpServletRequest request, String mobile, String content, String sendTime)
			throws Exception {
		String unionid = String.valueOf(request.getSession().getAttribute("unionid")); // 获取当前注册用户的微信unionid
		String openid = CookiesUtil.getCookieKey(request); // 获取微信openid
		String uip = IpUtil.getIp(request); // 获取当前注册用户的ip地址
		String umac = MacAddressUtil.getLocalMac(); // 获取当前微信注册用户的网卡地址
		System.err.println(mobile + ": 注册的ip地址为：" + uip + ", 注册的mac地址为：" + umac + ", 注册的微信unionid为：" + unionid);
		String result = "";
		if (unionid != null && !unionid.equals("") && !unionid.equals("null") || openid != null && !openid.equals("") && !openid.equals("null")) {
			if (!"".equals(mobile) && mobile != null && !mobile.equals("null")) {
				String mobiles = mobile.trim();
				if (!"".equals(mobiles) && mobiles != null) {
					String[] mobiless = mobiles.split(",");
					for (String str : mobiless) {
						String sendNum = SysCache.getSendMoblieNum(str);
						String sendTimes = SysCache.getSendMoblieTime(str);
						long times = Long.valueOf(sendTimes);
						int i = 0;
						if (!"".equals(sendTimes)) {
							System.out.println("两次发送的时间间隔为：" + (System.currentTimeMillis() - times));
							if ((System.currentTimeMillis() - times) < (60 * 1000)) {
								return "-410";
							}
						}
						if (!"".equals(sendNum)) {
							int num = Integer.valueOf(sendNum);
							if (num > 5) {
								System.err.println(str + " 发送短信次数已超出每天的发送次数");
								return "-401";
							} else {
								System.err.println(str + "发送次数为:" + num);
								++num;
								SysCache.setSendMoblieNum(str, num);
								SysCache.setSendMoblieTime(str, System.currentTimeMillis());
								String sendNums = SysCache.getSendMoblieNum(str);
								int numsNew = Integer.valueOf(sendNums);
								System.err.println("存储的num值为==" + num);
								System.err.println("nums的值为==" + numsNew);
								System.err.println("sendNum的值为==" + sendNum);
								if (numsNew != num || numsNew == 0) {
									System.err.println(str + " 存储数值跟当前数值不一样");
									return "-109";
								}
							}
						} else {
							i = 1;
							SysCache.setSendMoblieTime(str, System.currentTimeMillis());
							SysCache.setSendMoblieNum(str, i);
							String sendNums = SysCache.getSendMoblieNum(str);
							int nums = Integer.valueOf(sendNums);
							if (nums != i) {
								System.err.println(str + " 存储数值跟当前数值不一样");
								return "-109";
							}
							System.err.println(str + " 首次发送短信");
						}

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("CorpID", corpID); // 账号
						map.put("Pwd", pwd); // 密码
						map.put("Mobile", str);// 发送手机号码（号码之间用英文逗号隔开，建议100个号码）例如：13812345678,13519876543,15812349876
						map.put("Content", content);// 发送内容
						map.put("Cell", ""); // 扩展号（必须为数字或为空）
						map.put("SendTime", ""); // 定时发送时间（可为空）
						System.err.println("短信发送内容:" + content);
						
						result = HttpUtil.postUrl(serviceSendSMSURL, map, "gb2312");
						if (null == result || "".equals(result)) {
							System.err.println("访问短信接口超时--- 返回null");
							return null;
						}
						String[] ReS = new String[] { "-1", "-2", "-3", "-5", "-6", "-7", "-8", "-9", "-10", "-101",
								"-103" };
						for (String string : ReS) {
							if (result == string) {
								System.err.println("调用短信接口返回====" + string);
								if (i > 0) {
									i = i - i;
								}
								SysCache.setSendMoblieTime(str, System.currentTimeMillis());
								SysCache.setSendMoblieNum(str, i);
								System.err.println(
										"调用短信接口失败重新存储的数值为：" + SysCache.getSendMoblieNum(str) + "====原来的存储的数值为：" + i);
							}
						}
						System.err.println("短信返回内容:" + result);
						return result;
					}
				} else {
					result = "-1";
				}
			} else {
				result = "-1";
			}
		} else {
			result = "110";
		}
		return result;
	}

}
