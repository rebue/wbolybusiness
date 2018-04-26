package com.wboly.system.sys.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author dwh
 *
 */
public class SendMoblieUitl {

	private static final String serviceSendSMSURL = "http://sdk.zhongguowuxian.com:98/ws/BatchSend2.aspx?";
	private static final String corpID = "GZLKJ0005218";
	private static final String pwd = "fjx@666";

	/*
	 * 方法名称：sendSMS 功 能：发送短信 ,传多个手机号就是群发(群发手机号码逗号隔开)，一个手机号就是单条提交 参
	 * 数：mobile,content,sendTime,(手机号，内容，，定时时间方法（不定时可用空字符）) 返 回 值：大于0的整数，提交成功,
	 * -1账号未注册，-2其他错误，-3帐号或密码错误，-5余额不足，请充值，-6定时发送时间不是有效的时间格式，
	 * -7提交信息末尾未加签名，请添加中文的企业签名【 】，-8发送内容需在1到300字之间。-9发送号码为空，-10定时时间不能小于系统当前时间，
	 * 101调用接口速度太快
	 */
	public static String sendSMS(String mobile, String content, String sendTime) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CorpID", corpID); // 账号
		map.put("Pwd", pwd); // 密码
		map.put("Mobile", mobile);// 发送手机号码（号码之间用英文逗号隔开，建议100个号码）例如：13812345678,13519876543,15812349876
		map.put("Content", content);// 发送内容
		map.put("Cell", ""); // 扩展号（必须为数字或为空）
		map.put("SendTime", ""); // 定时发送时间（可为空）
		String result = HttpUtil.postUrl(serviceSendSMSURL, map, "gb2312");
		return result;
	}

	/**
	 * 注册验资码
	 * 
	 * @param mobile
	 * @return String
	 */
	public static String sendLoginSMS(String mobile, String syscode) throws Exception {
		String content = "您的验证码是：" + syscode + "，此验证码30分钟内有效，请及时做好相关操作（此信息由系统自动发送，请勿回复）";
		return sendSMS(mobile, content, "");
	}

	public static void main(String[] args) throws Exception {
		String syscode = RandomUtil.getRandomNum(6);
		System.out.println(syscode);
		String num = sendLoginSMS("15107810004", syscode);
		System.out.println(num);
	}

}
