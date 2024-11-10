package com.pet.config.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisConnectionFactory {

	private static final JedisPool JEDIS_POOL;
	static {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		//最大連接數量
		jedisPoolConfig.setMaxTotal(8);
		//最大空閒數量
		jedisPoolConfig.setMaxIdle(8);
		//最小空閒數量
		jedisPoolConfig.setMinIdle(0);
		//最長等待時間,ms
		jedisPoolConfig.setMaxWaitMillis(200);
		JEDIS_POOL = new JedisPool(jedisPoolConfig,"192.168.59.128",6379,1000,"123456");
	}
	
	//獲取Jedis連線
	public static Jedis getJedis() {
		return JEDIS_POOL.getResource();
		
	}
	//關閉Jedis連線
	public static void returnJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
	
	
	//這個是會關閉整個連線池的方法
//	 public static void shutdownPool() {
//	        if (JEDIS_POOL != null && !JEDIS_POOL.isClosed()) {
//	            JEDIS_POOL.close();
//	        }
//	    }
	
}
