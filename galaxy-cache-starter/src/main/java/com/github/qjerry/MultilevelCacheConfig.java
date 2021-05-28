package com.github.qjerry;

import com.github.qjerry.aspect.CacheMultiAspect;
import com.github.qjerry.manager.CacheManager;
import com.github.qjerry.manager.MultiLevelCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
@EnableAspectJAutoProxy
public class MultilevelCacheConfig {

	@Autowired(required = false)
	private RedisTemplate redisTemplate;

//	@Bean
//	public RedisMessageListenerContainer messageListenerContainer() {
//		return new RedisMessageListenerContainer();
//	}

	@Bean
	public CacheManager multiLevelCacheManager() {
		MultiLevelCacheManager multiLevelCacheManager = new MultiLevelCacheManager(redisTemplate);
		return multiLevelCacheManager;
	}

	@Bean
	public CacheMultiAspect cacheMultiAspect() {
		return new CacheMultiAspect();
	}
}