package com.github.qjerry.manager;

import com.github.qjerry.config.CacheMultiConfig;
import com.github.qjerry.layer.Cache;
import com.github.qjerry.layer.CustomCaffeineCache;
import com.github.qjerry.layer.CustomMultiCache;
import com.github.qjerry.layer.CustomRedisCache;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
public class MultiLevelCacheManager extends AbstractCacheManager {

	public MultiLevelCacheManager(RedisTemplate<String, Object> redisTemplate) {
		this.setRedisTemplate(redisTemplate);
		cacheManagers.add(this);
	}

	@Override
	protected Cache getMissingCache(String name, CacheMultiConfig multiConfig) {
		Integer cacheLevel = multiConfig.getCacheLevel();
		CustomCaffeineCache caffeineCache;
		CustomRedisCache redisCache;
		if(cacheLevel == 1) {
			caffeineCache = new CustomCaffeineCache(name, multiConfig.getLevel1Config());
			return caffeineCache;
		}
		if(cacheLevel == 2) {
			redisCache = new CustomRedisCache(name, getRedisTemplate(), multiConfig.getLevel2Config());
			return redisCache;
		}
		caffeineCache = new CustomCaffeineCache(name, multiConfig.getLevel1Config());
		redisCache = new CustomRedisCache(name, getRedisTemplate(), multiConfig.getLevel2Config());
		return new CustomMultiCache(name, caffeineCache, redisCache, multiConfig, getRedisTemplate());
	}
}
