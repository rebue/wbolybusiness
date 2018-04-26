package com.wboly.system.sys.util;

import com.wboly.system.sys.cache.RedisBase;

import redis.clients.jedis.Jedis;

public class CacheUtil {

	/**
	 * 删除当前选择数据库中的所有key
	 */
	public static void clear() {

		Jedis jedis = null;

		try {
			jedis = RedisBase.getJedis();
			if (jedis != null) {
				jedis.flushDB();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

	}

	/**
	 * 覆盖原来的数据
	 * 
	 * @param key
	 * @param value
	 */
	public static void putObject(Object key, Object value) {
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				jedis.set(SerializeUtil.serialize(key.toString()), SerializeUtil.serialize(value));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

	}

	/**
	 * 设定有效时间赋值
	 * 
	 * @param key
	 * @param num
	 */
	public static void putObject(String key, int seconds, String value) {
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (jedis != null) {
				System.err.println("正在缓存的用户数据为：" + value);
				jedis.setex(key, seconds, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 根据key获取值
	 * 
	 * @param key
	 * @return Object
	 */
	public static Object getObject(String key) {
		Jedis jedis = null;

		Object content = null;

		try {
			jedis = RedisBase.getJedis();
			if (jedis != null) {
				content = jedis.get(key);
				System.err.println("已获取的用户数据为：" + content);
			}
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 获取当前选择数据库中的所有key个数
	 * 
	 * @return Integer
	 */
	public static Integer getSize() {

		Jedis jedis = null;

		int num = 0;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				num = Integer.valueOf(jedis.dbSize().toString());
			}
			return num;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return num;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

	}

	/**
	 * 删除
	 * 
	 * @param key
	 * @return Object
	 */
	public static Object removeObject(Object key) {
		Jedis jedis = null;
		Object content = null;
		try {
			jedis = RedisBase.getJedis();
			if (null != jedis) {
				content = jedis.del(SerializeUtil.serialize(key.toString()));
			}
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return content;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

	}
	
	/**
	 * jedis set 缓存字符串
	 * @param key
	 * @param value
	 */
	public static void setString(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (jedis != null) {
				jedis.set(key, value);
			}
			jedis.close();
		} catch (Exception e) {
			e.printStackTrace();
			jedis.close();
		}
	}
	
	/**
	 * 获取缓存的字符串
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		String value = "";
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (jedis != null) {
				value = jedis.get(key);
			}
			jedis.close();
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			jedis.close();
			return value;
		}
	}
	
	/**
	 * 删除key
	 * @param key
	 * 2018年1月15日10:54:24
	 */
	public static void delString(String key) {
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			if (jedis != null) {
				jedis.del(key);
			}
			jedis.close();
		} catch (Exception e) {
			jedis.close();
			e.printStackTrace();
		}
	}

}
