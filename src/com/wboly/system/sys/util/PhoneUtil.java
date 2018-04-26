package com.wboly.system.sys.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtil {
	
	
	/**
	 * 手机号验证
	 * @param mobile
	 * @return
	 */
	public static Boolean isMobile(String mobile){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");   
		Matcher m = p.matcher(mobile);  
		return m.matches();	
	}
	
	
	
	/**
	 * 电话号验证,格式(0770-2838250)
	 * @param phone
	 * @return
	 */
	public static Boolean isPhone(String phone){
		Pattern p = Pattern.compile("[0]{1}[0-9]{2,3}-[0-9]{7,8}");
		Matcher m = p.matcher(phone);  
		return m.matches();		
	}
	
	
	public static void main(String [] args){
		System.out.println(isPhone("1770-2838250"));
	}
	
	

}
