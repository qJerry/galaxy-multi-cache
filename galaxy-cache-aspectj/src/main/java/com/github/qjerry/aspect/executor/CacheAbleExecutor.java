package com.github.qjerry.aspect.executor;

import com.github.qjerry.annotation.Level1Cache;
import com.github.qjerry.annotation.Level2Cache;
import com.github.qjerry.config.CacheConfig;
import com.github.qjerry.config.CacheMultiConfig;
import com.github.qjerry.layer.Cache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.redis.serializer.SerializationException;
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
@Slf4j
@Component
public class CacheAbleExecutor extends AbstractExecutor {

	@Override
	public Object execute(ProceedingJoinPoint joinPoint, CacheConfig cacheConfig, Level1Cache level1Cache, Level2Cache level2Cache) throws Throwable {
		String[] cacheNames = cacheConfig.getCacheNames();
		String cacheKey = cacheConfig.getKey();
		boolean ignoreException = cacheConfig.isIgnoreException();
		Method method = getMethod(joinPoint);
		try {
			String cacheName = cacheNames[0];
			Object key = generateKey(cacheKey, method, joinPoint.getArgs(), joinPoint.getTarget());
			if(Objects.isNull(key)) {
				throw new IllegalArgumentException("cache key not null");
			}

			Integer cacheLevel = cacheConfig.getCacheLevel();
			CacheMultiConfig multiConfig = buildCacheMultiConfig(cacheLevel, level1Cache, level2Cache);
			Cache cache = this.cacheManager.getCache(cacheName, multiConfig);
			return cache.get(key, () -> getValueLoaderInvoker(joinPoint).invoke());
		} catch (SerializationException ex) {
			delete(cacheNames, cacheKey, method, joinPoint.getArgs(), joinPoint.getTarget());
			if (ignoreException) {
				log.warn(ex.getMessage(), ex);
				return joinPoint.proceed();
			}
			throw ex;
		} catch (Exception e) {
			if (ignoreException) {
				log.warn(e.getMessage(), e);
				return joinPoint.proceed();
			}
			throw e;
		}
	}
}
