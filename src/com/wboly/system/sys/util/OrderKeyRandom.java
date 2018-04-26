package com.wboly.system.sys.util;

import java.io.ByteArrayInputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class OrderKeyRandom extends Thread {

	private static String[] _source = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
			"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9" };

	private static SecureRandom rngp = new SecureRandom();

	/// <summary>
	/// 生成订单号，uid后四位相同经单机3000并发经6000次测试并未发现生成相同订单号。
	/// 如果uid后四位不相同，则再高的并发都不可能生成同一单号
	/// </summary>
	/// <returns>返回生成的订单号</returns>
	public static String GenerateOrderNo() {
		String ustr = GetIntString(5);
		String result = "";
		Calendar cd = Calendar.getInstance();
		int today = cd.get(Calendar.DAY_OF_YEAR);
		int week = today / 7 + 1;
		if (week >= 10) {
			result += String.valueOf(week);
		} else {
			result += "1" + String.valueOf(week);
		}
		String mid = "";
		result += GetIntString(6);
		mid = String.valueOf(Thread.currentThread().getId());
		result += mid.substring(mid.length() - 1);
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("SSS");
		mid = dateFormatGmt.format(new Date());
		result += mid.substring(mid.length() - 3);
		mid = ustr;
		result += "000" + mid.substring(mid.length() - 1);
		result += GetIntString(2);
		return result;
	}

	/// <summary>
	/// 生成一个能支持20万以上并发不重复的key，key的长度6位
	/// </summary>
	/// <returns>返回6位的随机key</returns>
	public static String GetKey() {
		return GetKey(6);
	}

	/// <summary>
	/// 生成int类型的随机数字符串
	/// </summary>
	/// <param name="length"></param>
	/// <returns></returns>
	public static String GetIntString(int length) {
		String result = "";
		for (int i = 0; i < length; i++) {
			result += String.valueOf(GetRam(9, 0));
		}
		return result;
	}

	/// <summary>
	/// 6个字符以上则能支持并发20万以上级别不重复key
	/// </summary>
	/// <param name="max">生成的长度</param>
	/// <returns>返回生成的key</returns>
	public static String GetKey(int max) {
		int index = 0;
		String key = "";
		for (int i = 0; i < max; i++) {
			index = GetRam(10000, 1) % _source.length;
			key += _source[index];
		}
		return key;
	}

	/// <summary>
	/// 在指定大小范围内返回一个随机整形数值（高并发时候会出现重复随机数）
	/// </summary>
	/// <param name="MinVal">最小值（随机数可取该下界值）</param>
	/// <param name="MaxVal">最大值（随机数不能取该上界值）</param>
	/// <returns>返回生成的随机数</returns>
	public static int GetRamNum(int MinVal, int MaxVal) {
		return GetRam(MaxVal, MinVal);
	}

	/// <summary>
	/// 在指定大小范围内返回一个随机整形数值（高并发时候会出现重复随机数）
	/// </summary>
	/// <param name="MaxVal">最小值（随机数可取该下界值）</param>
	/// <param name="MinVal">最大值（随机数不能取该上界值）</param>
	/// <returns>返回生成的随机数</returns>
	private static int GetRam(int MaxVal, int MinVal) {
		int seed = 0;
		{
			byte[] idArray = UUID.randomUUID().toString().toUpperCase().getBytes();

			int id1, id2, id3, id4;
			id1 = id2 = id3 = id4 = 0;
			id1 |= (int) idArray[0];
			id1 |= (int) idArray[1] << 8;
			id1 |= (int) idArray[2] << 16;
			id1 |= (int) idArray[3] << 24;
			id2 |= (int) idArray[4];
			id2 |= (int) idArray[5] << 8;
			id2 |= (int) idArray[6] << 16;
			id2 |= (int) idArray[7] << 24;
			id3 |= (int) idArray[8];
			id3 |= (int) idArray[9] << 8;
			id3 |= (int) idArray[10] << 16;
			id3 |= (int) idArray[11] << 24;
			id4 |= (int) idArray[12];
			id4 |= (int) idArray[13] << 8;
			id4 |= (int) idArray[14] << 16;
			id4 |= (int) idArray[15] << 24;
			seed = id1 ^ id2 ^ id3 ^ id4 ^ (int) Thread.currentThread().getId();

		}
		byte[] rb = new byte[8];
		rngp.nextBytes(rb);
		ByteArrayInputStream in = new ByteArrayInputStream(rb);

		int value = in.read();

		// int value = BitConverter.ToInt32(rb, 0);
		value += seed;

		int nValue = value % MaxVal + MinVal;
		if (nValue < 0) {
			nValue = -nValue;
		}
		return nValue;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100000; i++) {
			long s1 = System.currentTimeMillis();
			System.out.println("result:" + GenerateOrderNo());
			System.out.println(System.currentTimeMillis() - s1);
		}
	}

}
