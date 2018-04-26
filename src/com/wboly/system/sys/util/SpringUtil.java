package com.wboly.system.sys.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil extends ClassPathXmlApplicationContext {

	private SpringUtil() {

	}

	private static class SpringUtilHolder {
		private static ApplicationContext instance = new ClassPathXmlApplicationContext("spring-rpc.xml");
	}

	public static ApplicationContext getInstace() {
		return SpringUtilHolder.instance;
	}
}
