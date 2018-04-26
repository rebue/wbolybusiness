package com.wboly.system.sys.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 获取方法执行时间
 */
public class MethodTimeLog implements MethodInterceptor {
	private static final Log log = LogFactory.getLog(MethodTimeLog.class);

	/**
	 * 拦截要执行的目标方法
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// 用 commons-lang 提供的 StopWatch 计时，Spring 也提供了一个 StopWatch
		StopWatch clock = new StopWatch();
		clock.start(); // 计时开始
		Object result = invocation.proceed();
		clock.stop(); // 计时结束

		// 方法参数类型，转换成简单类型
		@SuppressWarnings("rawtypes")
		Class[] params = invocation.getMethod().getParameterTypes();
		String[] simpleParams = new String[params.length];
		for (int i = 0; i < params.length; i++) {
			simpleParams[i] = params[i].getSimpleName();
		}

		log.info("方法运行时间:" + clock.getTime() + " ms ["
				+ invocation.getThis().getClass().getName() + "."
				+ invocation.getMethod().getName() + "("
				+ StringUtils.join(simpleParams, ",") + ")] ");
		return result;
	}
}
