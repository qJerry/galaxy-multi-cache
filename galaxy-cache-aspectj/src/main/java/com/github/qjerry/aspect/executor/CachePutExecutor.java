package com.github.qjerry.aspect.executor;

import com.github.qjerry.annotation.Level1Cache;
import com.github.qjerry.annotation.Level2Cache;
import com.github.qjerry.config.CacheConfig;
import com.github.qjerry.config.CacheMultiConfig;
import com.github.qjerry.layer.Cache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
@Component
public class CachePutExecutor extends AbstractExecutor {

	@Override
	public Object execute(ProceedingJoinPoint joinPoint, CacheConfig cacheConfig, Level1Cache level1Cache, Level2Cache level2Cache) throws Throwable {
		String[] cacheNames = cacheConfig.getCacheNames();
		String cacheKey = cacheConfig.getKey();
		Method method = getMethod(joinPoint);
		Object key = generateKey(cacheKey, method, joinPoint.getArgs(), joinPoint.getTarget());
		if(Objects.isNull(key)) {
			throw new IllegalArgumentException("cache key not null");
		}
		CacheMultiConfig multiConfig = buildCacheMultiConfig(cacheConfig.getCacheLevel(), level1Cache, level2Cache);
		Object result = getValueLoaderInvoker(joinPoint).invoke();
		for (String cacheName : cacheNames) {
			Cache cache = this.cacheManager.getCache(cacheName, multiConfig);
			cache.put(key, result);
		}
		return result;
	}
}
