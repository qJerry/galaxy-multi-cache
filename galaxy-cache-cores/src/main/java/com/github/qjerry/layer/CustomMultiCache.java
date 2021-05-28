package com.github.qjerry.layer;

import com.github.qjerry.config.CacheMultiConfig;
import com.github.qjerry.listener.CacheSubMessage;
import com.github.qjerry.listener.CacheSubTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

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
@Getter
@Setter
public class CustomMultiCache extends AbstractValueAdaptingCache {

	private final String name;

	private CustomCaffeineCache caffeineCache;

	private CustomRedisCache redisCache;

	private RedisTemplate redisTemplate;

	public CustomMultiCache(String name, CustomCaffeineCache caffeineCache, CustomRedisCache redisCache, CacheMultiConfig multiConfig, RedisTemplate redisTemplate) {
		super(multiConfig.isAllowNullValue());
		this.name = name;
		this.caffeineCache = caffeineCache;
		this.redisCache = redisCache;
		this.redisTemplate = redisTemplate;
	}

	@Override
	protected Object lookup(Object key) {
		return getIfPresent(key, null);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Object getNativeCache() {
		return this;
	}

	private Object getIfPresent(Object key, Callable callable) {
		Object value;
		try {
			value = redisCache.get(key, callable);
			if(Objects.isNull(value)) {
				value = caffeineCache.get(key, callable);
				redisCache.put(key, value);
			} else {
//				if(Objects.nonNull(caffeineCache)) {
//					caffeineCache.put(key, value);
//				}
			}
		} catch (Exception e) {
			log.error("[Cache-Multi] get key: {} error: {}", key, e);
			value = caffeineCache.get(key, callable);
			redisCache.put(key, value);
		}
		log.debug("[Cache-Multi] query key: {}, value: {}", key, value);
		return value;
	}

	@Override
	public Object get(Object key) {
		return lookup(key);
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		try {
			Object v = getIfPresent(key, valueLoader);
			if(Objects.isNull(v)) {
				v = valueLoader.call();
				if(Objects.nonNull(v)) {
					this.put(key, v);
				}
			}
			return (T) v;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void put(Object key, Object value) {
		log.debug("[Cache-Multi] name={} key={} value={} call put", name, key, value);
		if(Objects.isNull(value)) {
			return;
		}
		redisCache.put(key, value);
		sendCacheDeleteMessage(CacheSubTypeEnum.EVICT, key);
	}

	@Override
	public void evict(Object key) {
		log.debug("[Cache-Multi] name={} key={} call evict", name, key);
		redisCache.evict(key);
		sendCacheDeleteMessage(CacheSubTypeEnum.EVICT, key);
	}

	@Override
	public void clear() {
		log.debug("[Cache-Multi] name={} call clear", name);
		redisCache.clear();
		sendCacheDeleteMessage(CacheSubTypeEnum.CLEAR);
	}

	public void sendCacheDeleteMessage(CacheSubTypeEnum cacheSubType, Object... keys) {
		Object key = null;
		if(Objects.nonNull(keys) && keys.length > 0) {
			key = keys[0];
		}
		CacheSubMessage subMessage = CacheSubMessage.builder()
				.cacheName(getName())
				.key(key)
				.expireTime(10000L)
				.messageType(cacheSubType)
				.sendTime(System.currentTimeMillis()).build();
		log.debug("[Cache-Multi] name={} key={}, subType={} pub", name, key, cacheSubType);
		redisTemplate.convertAndSend(ChannelTopic.of(getName()).getTopic(), subMessage);
	}
}
