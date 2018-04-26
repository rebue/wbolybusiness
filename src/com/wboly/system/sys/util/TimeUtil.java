package com.wboly.system.sys.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import com.wboly.system.sys.system.SysCache;

public class TimeUtil {

	// 安排指定的任务task在指定的时间firstTime开始进行重复的固定速率period执行．
	// Timer.scheduleAtFixedRate(TimerTask task,Date firstTime,long period)
	public static void timer() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 24); // 控制时
		calendar.set(Calendar.MINUTE, 0); // 控制分
		calendar.set(Calendar.SECOND, 0); // 控制秒

		Date time = calendar.getTime(); // 得出执行任务的时间

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				System.err.println("------------------ 执行定时任务  ----------------------");
				// 清除重置密码错误次数
				SysCache.flushAll("wboly:wechat:error:resetpwdnum");
				// 清除绑定用户错误次数
				SysCache.flushAll("wboly:wechat:error:bindnum");
			}
		}, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行
	}

	/**
	 * 获取当前系统时间
	 * 
	 * @param pattern
	 * @return String
	 */
	public static String getNowTime(String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}

	/**
	 * 时间追加天
	 * 
	 * @param pattern
	 * @param monthnum
	 * @return
	 */
	public static Date getTimeAddDay(String pattern, Integer num) {

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, num);
			return calendar.getTime();// new Date()为获取当前系统时间
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 时间追加小时
	 * 
	 * @param pattern
	 * @param monthnum
	 * @return
	 */
	public static Date getTimeAddHour(String pattern, Integer num) {

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.HOUR, num);
			return calendar.getTime();// new Date()为获取当前系统时间
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 时间追加分
	 * 
	 * @param pattern
	 * @param monthnum
	 * @return
	 */
	public static Date getTimeAddMinutes(String pattern, Integer num) {

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.MINUTE, num);
			return calendar.getTime();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * java时间戳转换年月日时分秒
	 * 
	 * @param str
	 * @return String
	 */
	public static String getJavaDataForm(long str) {

		Date dat = new Date(System.currentTimeMillis() - str);
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(dat);
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String sb = format.format(gc.getTime());
		return sb;
	}

	public static long getJavaDateStamp(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sd = date.getTime() + "";
		String dateline = sd.substring(0, 10);
		return Long.parseLong(dateline);
	}

	/**
	 * sqlserver时间戳转换年月日时分秒
	 * 
	 * @param str
	 * @return
	 */
	public static String getSQlserverDataForm(long str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse("1970-01-01 08:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.add(Calendar.SECOND, (int) str);
		return format.format(now.getTime());
	}

	/**
	 * 将字符串转为时间戳
	 * 
	 * @param user_time
	 * @return
	 */
	public static String getTime(String user_time) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		try {

			d = sdf.parse(user_time);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}

	public static void main(String[] args) {
		// System.out.println(getNextYearTime("yyyy-MM-dd HH:mm:ss",12));
		System.out.println(getSQlserverDataForm(1464257408));
		// System.out.println(getJavaDataForm(1502027906));
		// System.out.println(System.currentTimeMillis());
		// System.out.println(getNowTime("yyyy\\MM\\dd"));
		// System.out.println(getTimeAddMonth("2016-01-27 11:11:11","yyyy-MM-dd
		// HH:mm:ss",5));
		// String a="4e4a7e9a2d1e401d9bacd0dc806adc72";
		// String b="9169ac89f9f96c686a1bf08febc48f30";

		/*
		 * String name="d:\\ddd\\aa.jsp";
		 * name=name.substring(name.lastIndexOf("."),name.length());
		 * System.out.println(name);
		 */
	}

}
