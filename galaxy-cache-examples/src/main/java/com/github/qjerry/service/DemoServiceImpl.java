package com.github.qjerry.service;

import com.github.qjerry.CacheNameConfig;
import com.github.qjerry.annotation.CacheEvict;
import com.github.qjerry.annotation.CachePut;
import com.github.qjerry.annotation.Cacheable;
import com.github.qjerry.listener.CacheSubMessage;
import com.github.qjerry.listener.CacheSubTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
@Service
public class DemoServiceImpl implements DemoService {

	@Autowired
	private RedisTemplate redisTemplate;

	@Cacheable(cacheNames = CacheNameConfig.cacheFor1, key = "#s1.concat(#s2)", cacheLevel = 3)
	@Override
	public String getData(String s1, String s2) {
		System.out.println("none cache for cache, start to save...");
		return "success";
	}

	@CacheEvict(cacheNames = CacheNameConfig.cacheFor1, key = "#s1.concat(#s2)")
	@Override
	public void clearData(String s1, String s2) {
		// empty list
	}

	@CachePut(cacheNames = CacheNameConfig.cacheFor1, key = "#s1.concat(#s2)", cacheLevel = 3)
	@Override
	public String putData(String s1, String s2) {
		System.out.println("put data to cache, start to save...");
		return "success1";
	}

	@Override
	public void sendMessage() {
		CacheSubMessage subMessage = CacheSubMessage.builder().cacheName(CacheNameConfig.cacheFor1)
				.messageType(CacheSubTypeEnum.CLEAR)
				.sendTime(System.currentTimeMillis())
				.expireTime(10000).build();
		redisTemplate.convertAndSend("galaxy-cache:cache1", subMessage);
	}
}
