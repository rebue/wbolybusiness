package com.wboly.modules.controller.Util;

import com.wboly.system.sys.cache.RedisBase;

import redis.clients.jedis.Jedis;

public class RediscaCheUtil {

	public static boolean getIsKey(String target) {
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				if (null == jedis.get(target) || "".equals(jedis.get(target)) || "[]".equals(jedis.get(target))) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return false;
	}

	public static void setKey(String target, String json) {
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				jedis.set(target, json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	public static String getKey(String target) {
		Jedis jedis = null;
		;
		String x = null;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				x = jedis.get(target);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return x;
	}

	public static void delKey(String target) {
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				jedis.del(target);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	public static void setkeyTime(String target) {
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				jedis.expire(target, 1800);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	public static void setkeyTimeThreeHours(String target) {
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				jedis.expire(target, 10800);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}
}
