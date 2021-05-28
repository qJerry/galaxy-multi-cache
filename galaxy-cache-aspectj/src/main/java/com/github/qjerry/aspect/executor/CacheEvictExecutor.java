package com.github.qjerry.aspect.executor;

import com.github.qjerry.annotation.Level1Cache;
import com.github.qjerry.annotation.Level2Cache;
import com.github.qjerry.config.CacheConfig;
import com.github.qjerry.config.CacheLevel1Config;
import com.github.qjerry.config.CacheLevel2Config;
import com.github.qjerry.config.CacheMultiConfig;
import com.github.qjerry.layer.Cache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
@Component
public class CacheEvictExecutor extends AbstractExecutor {

	@Override
	public Object execute(ProceedingJoinPoint joinPoint, CacheConfig cacheConfig, Level1Cache level1Cache, Level2Cache level2Cache) throws Throwable {
		String[] cacheNames = cacheConfig.getCacheNames();
		boolean allEntries = cacheConfig.isAllEntries();
		if (allEntries) {
			for (String cacheName : cacheNames) {
				Collection<Cache> caches = this.cacheManager.getCache(cacheName);
				if (CollectionUtils.isEmpty(caches)) {
					CacheLevel1Config level1Config = new CacheLevel1Config();
					CacheLevel2Config level2Config = new CacheLevel2Config();
					Cache cache = this.cacheManager.getCache(cacheName, new CacheMultiConfig().setLevel1Config(level1Config).setLevel2Config(level2Config));
					cache.clear();
				} else {
					for (Cache cache : caches) {
						cache.clear();
					}
				}
			}
		} else {
			Method method = getMethod(joinPoint);
			delete(cacheNames, cacheConfig.getKey(), method, joinPoint.getArgs(), joinPoint.getTarget());
		}
		return getValueLoaderInvoker(joinPoint).invoke();
	}
}
