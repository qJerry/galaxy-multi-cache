package com.github.qjerry.layer;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.qjerry.config.CacheLevel1Config;
import com.github.qjerry.enums.ExpireModeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Slf4j
public class CustomCaffeineCache extends AbstractValueAdaptingCache {
	private final String name;
	private final Cache<Object, Object> cache;

	public CustomCaffeineCache(String name, CacheLevel1Config level1Config) {
		super(level1Config.isAllowNullValue());
		this.name = name;
		this.cache = newCache(level1Config);
	}

	@Override
	public final String getName() {
		return this.name;
	}

	@Override
	public final Cache<Object, Object> getNativeCache() {
		return this.cache;
	}

	@Override
	public Object get(Object key) {
		log.debug("[Cache-Caffeine] name={} key={} call get", this.name, key);
		if (this.cache instanceof LoadingCache) {
			return  ((LoadingCache)this.cache).get(key);
		} else {
			return this.cache.getIfPresent(key);
		}
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		log.debug("[Cache-Caffeine] name={} key={} call get", this.name, key);
		Object result = this.cache.getIfPresent(key);
		if(Objects.isNull(result)) {
			log.debug("[Cache-Caffeine] name={} key={} is null, try to call value loader={}", this.name, key, valueLoader);
			result = loadValue(key, valueLoader);
			if(isAllowNullValues() || Objects.nonNull(result)) {
				this.put(key, result);
			}
		}
		return (T) result;
	}

	@Override
	protected Object lookup(Object key) {
		log.debug("[Cache-Caffeine] name={} key={} call lookup", this.name, key);
		return this.cache.getIfPresent(key);
	}

	@Override
	public void put(Object key, Object value) {
		log.debug("[Cache-Caffeine] name={} key={} value={} call put", this.name, key, value);
		this.cache.put(key, this.toStoreValue(value));
	}

	@Override
	public void evict(Object key) {
		log.debug("[Cache-Caffeine] name={} key={} call evict", this.name, key);
		this.cache.invalidate(key);
	}

	@Override
	public void clear() {
		log.debug("[Cache-Caffeine] name={} call clear", this.name);
		this.cache.invalidateAll();
	}

	private <T> Object loadValue(Object key, Callable<T> valueLoader) {
		try {
			T call = valueLoader.call();
			log.debug("[Cache-Caffeine] name={} key={} call loadValue, result={}", this.name, key, call);
			return toStoreValue(call);
		} catch (Exception e) {
			throw new ValueLoadedException(key, e);
		}
	}

	private static Cache<Object, Object> newCache(CacheLevel1Config level1Config) {
		Caffeine<Object, Object> builder = Caffeine.newBuilder();
		builder.initialCapacity(level1Config.getInitialCapacity());
		builder.maximumSize(level1Config.getMaximumSize());
		if (ExpireModeEnum.WRITE.equals(level1Config.getExpireMode())) {
			builder.expireAfterWrite(level1Config.getExpire(), level1Config.getTimeUnit());
		} else if (ExpireModeEnum.ACCESS.equals(level1Config.getExpireMode())) {
			builder.expireAfterAccess(level1Config.getExpire(), level1Config.getTimeUnit());
		}
		return builder.build();
	}
}