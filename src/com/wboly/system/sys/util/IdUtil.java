package com.wboly.system.sys.util;

import java.util.Date;
import java.util.Random;

/**
 * 动态生成字符串ID
 * @author Sea
 *
 */
public class IdUtil {
	
	/**
	 * 获取当前时间，年月日时分秒毫秒
	 * @return String
	 */
	public static String  getNowTime(){
		String time = new java.text.SimpleDateFormat(
				"yyMMddHHmmssSSS").format(new Date());
		return time;
	}
	
	/**
	 * 获取7位随机数
	 * @return
	 */
	public static String getRandom(){
		Random rand = new Random();
		Long a = rand.nextLong();
		String num=a+"";
		num=num.substring(num.length()-7,num.length());
		return num;
	}
	
	/**
	 * 根据当前时间和随机数生成ID
	 * @return
	 */
	public static String getId(){
		return getNowTime()+getRandom();
	}


}
