package com.wboly.system.sys.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtil {
	
	
	/**
	 * 邮箱验证
	 * @param email
	 * @return
	 */
	public static Boolean isEmail(String email){
		Pattern p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"); 
		Matcher m = p.matcher(email);  
		return m.matches();	
	}

	
	
	public static void main(String [] args){
		System.out.println(isEmail("23@1.com"));
	}
}
