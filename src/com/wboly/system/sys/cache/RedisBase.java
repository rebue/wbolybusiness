package com.wboly.system.sys.cache;

import java.util.Map;

import com.wboly.system.sys.util.PropertiesUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * @author 
 */
public class RedisBase {

	private static JedisPool pool = null;

	public static JedisPool getJedisPool() {

		Map<String, Map<String, String>> properties = PropertiesUtil.getAllProperties();
		Map<String, String> map = properties.get("redis.properties");

		if (map != null) {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxIdle(Integer.valueOf(map.get("JedisPool.maxIdle").trim()));
			jedisPoolConfig.setMaxTotal(Integer.valueOf(map.get("JedisPool.maxTotal").trim()));
			jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(map.get("JedisPool.testOnBorrow").trim()));

			String host = map.get("redis.host").trim();
			Integer port = Integer.valueOf(map.get("redis.port").trim());
			Integer timeout = Integer.valueOf(map.get("redis.timeout").trim());
			String password = map.get("redis.password").trim();

			pool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
		}
		return pool;
	}

	@SuppressWarnings("deprecation")
	public static void returnResource(JedisPool pool, Jedis jedis) {
		if (jedis != null) {
			pool.returnResource(jedis);
		}
	}

	/** 
     * 在多线程环境同步初始化 
     */  
    public static synchronized void poolInit() {  
        if (pool == null) {    
        	getJedisPool();  
        }  
    }  
    
    public synchronized static Jedis getJedis() {    
        if (pool == null) {    
            poolInit();  
        }  
        Jedis jedis = null;  
        try {    
            if (pool != null) {    
                jedis = pool.getResource();   
            }  
        } catch (Exception e) {    
           
        } 
        return jedis;  
    }  
}
