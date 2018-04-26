package com.wboly.system.sys.annotation;

import java.lang.reflect.Method;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ArgsLogImpl {

	ThreadLocal<Long> time = new ThreadLocal<Long>();

	ThreadLocal<String> tag = new ThreadLocal<String>();
	public static final Logger logger = Logger.getLogger(ArgsLogImpl.class);

	/**
	 * 在所有标注@ArgsLog的地方注入
	 * 
	 * @param joinPoint
	 */
	@SuppressWarnings("unused")
	@Before("@annotation(com.wboly.system.sys.annotation.ArgsLog)")
	public void beforeExce(JoinPoint joinPoint) {
		time.set(System.currentTimeMillis());
		tag.set(UUID.randomUUID().toString());
		info(joinPoint);
		MethodSignature ms = (MethodSignature) joinPoint.getSignature();
		Method method = ms.getMethod();
	}

	@After("@annotation(com.wboly.system.sys.annotation.ArgsLog)")
	public void afterExce(JoinPoint joinPoint) {
		MethodSignature ms = (MethodSignature) joinPoint.getSignature();
		Method method = ms.getMethod();
		logger.info("标记为" + tag.get() + "的方法" + method.getName() + "运行消耗" + (System.currentTimeMillis() - time.get()) + "ms");
	}

	@Around("@annotation(com.wboly.system.sys.annotation.ArgsLog)")
	public void aroundExce(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();
	}

	private void info(JoinPoint joinPoint) {
		Object object = joinPoint.getTarget();
		System.out.println(object);
		Object[] os = joinPoint.getArgs();
		System.out.println("Args:");
		for (int i = 0; i < os.length; i++) {
			logger.info("\t==>参数[" + i + "]:\t" + os[i].toString());
		}
		logger.info("Signature:\t" + joinPoint.getSignature());
		logger.info("SourceLocation:\t" + joinPoint.getSourceLocation());
		logger.info("StaticPart:\t" + joinPoint.getStaticPart());
	}
}
