package com.github.qjerry.aspect.executor;

import com.github.qjerry.annotation.Level1Cache;
import com.github.qjerry.annotation.Level2Cache;
import com.github.qjerry.config.CacheConfig;
import com.github.qjerry.config.CacheLevel1Config;
import com.github.qjerry.config.CacheLevel2Config;
import com.github.qjerry.config.CacheMultiConfig;
import com.github.qjerry.expression.CacheOperationExpressionEvaluator;
import com.github.qjerry.layer.Cache;
import com.github.qjerry.manager.CacheManager;
import com.github.qjerry.support.ValueLoaderInvoker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.aop.support.AopUtils.getTargetClass;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
public abstract class AbstractExecutor {

	@Autowired(required = false)
	protected CacheManager cacheManager;

	public abstract Object execute(ProceedingJoinPoint joinPoint, CacheConfig cacheConfig, Level1Cache level1Cache, Level2Cache level2Cache) throws Throwable;

	private final KeyGenerator keyGenerator = new SimpleKeyGenerator();

	private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator();

	/**
	 * delete cache
	 * @param cacheNames
	 * @param keySpEL
	 * @param method
	 * @param args
	 * @param target
	 */
	protected void delete(String[] cacheNames, String keySpEL, Method method, Object[] args, Object target) {
		Object key = generateKey(keySpEL, method, args, target);
		if(Objects.isNull(key)) {
			throw new IllegalArgumentException(String.format("cache key [%s] not be null", keySpEL));
		}
		for (String cacheName : cacheNames) {
			Collection<Cache> caches = this.cacheManager.getCache(cacheName);
			if (CollectionUtils.isEmpty(caches)) {
				CacheMultiConfig multiConfig = CacheMultiConfig.build(new CacheLevel1Config(), new CacheLevel2Config());
				Cache cache = this.cacheManager.getCache(cacheName, multiConfig);
				cache.evict(key);
			} else {
				for (Cache cache : caches)
					cache.evict(key);
			}
		}
	}

	/**
	 * Get the caller of the operation to be performed
	 * can throw exception
	 * @param point
	 * @return
	 */
	protected ValueLoaderInvoker getValueLoaderInvoker(ProceedingJoinPoint point) {
		return () -> {
			try {
				return point.proceed();
			} catch (Throwable throwable) {
				throw new ValueLoaderInvoker.ThrowableWrapperException(throwable);
			}
		};
	}

	protected Object generateKey(String keySpEl, Method method, Object[] args, Object target) {
		Class<?> targetClass = getTargetClass(target);
		if (StringUtils.hasText(keySpEl)) {
			EvaluationContext evaluationContext = this.evaluator.createEvaluationContext(method, args, target, targetClass, CacheOperationExpressionEvaluator.NO_RESULT);
			AnnotatedElementKey methodCacheKey = new AnnotatedElementKey(method, targetClass);
			Object keyValue = this.evaluator.key(keySpEl, methodCacheKey, evaluationContext);
			return Objects.isNull(keyValue) ? "null" : keyValue;
		}
		return this.keyGenerator.generate(target, method, args);
	}

	protected Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
		Class[] clasz = new Class[joinPoint.getArgs().length];
		Arrays.asList(joinPoint.getArgs()).stream().map(item -> item.getClass()).collect(Collectors.toList()).toArray(clasz);

		return joinPoint.getSignature().getDeclaringType().getDeclaredMethod(joinPoint.getSignature().getName(), clasz);
	}

	/**
	 *
	 * @param cacheLevel
	 * @param level1Cache
	 * @param level2Cache
	 * @return
	 */
	protected CacheMultiConfig buildCacheMultiConfig(Integer cacheLevel, Level1Cache level1Cache, Level2Cache level2Cache) {
		CacheLevel1Config level1Config = new CacheLevel1Config();
		if(Objects.nonNull(level1Cache)) {
			level1Config.setExpireMode(level1Cache.expireMode());
			level1Config.setExpire(level1Cache.expire());
			level1Config.setAllowNullValue(true);
			level1Config.setInitialCapacity(level1Cache.initialCapacity());
			level1Config.setMaximumSize(level1Cache.maximumSize());
			level1Config.setTimeUnit(level1Cache.timeunit());
		}
		CacheLevel2Config level2Config = new CacheLevel2Config();
		if(Objects.nonNull(level2Cache)) {
			level2Config.setAllowNullValue(level2Cache.isAllowNullValue());
			level2Config.setExpire(level2Cache.expire());
			level2Config.setForceRefresh(level2Cache.forceRefresh());
			level2Config.setMagnification(level2Cache.magnification());
			level2Config.setPreload(level2Cache.preload());
			level2Config.setTimeUnit(level2Cache.timeunit());
		}
		CacheMultiConfig multiConfig = CacheMultiConfig.build(level1Config, level2Config);
		multiConfig.setCacheLevel(cacheLevel);
		return multiConfig;
	}
}
