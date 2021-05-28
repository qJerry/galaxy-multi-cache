package com.github.qjerry.layer;

import com.github.qjerry.config.CacheLevel2Config;
import com.github.qjerry.support.NullValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
public class CustomRedisCache extends AbstractValueAdaptingCache {

	private final String name;
	private final RedisTemplate<String, Object> redisTemplate;
	private final CacheLevel2Config level2Config;

	public CustomRedisCache(String name, RedisTemplate<String, Object> redisTemplate, CacheLevel2Config level2Config) {
		super(level2Config.isAllowNullValue());
		this.name = name;
		this.redisTemplate = redisTemplate;
		this.level2Config = level2Config;
	}

	@Override
	protected Object lookup(Object key) {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public RedisTemplate<String, Object> getNativeCache() {
		return this.redisTemplate;
	}

	@Override
	public Object get(Object key) {
		log.debug("[Cache-Redis] name={} key={} call get", this.name, key);
		String cacheKey = getKey(key);
		return this.redisTemplate.opsForValue().get(cacheKey);
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		log.debug("[Cache-Redis] name={} key={} call get", this.name, key);
		String cacheKey = getKey(key);
		Object result = this.redisTemplate.opsForValue().get(cacheKey);
		if(Objects.nonNull(result) || redisTemplate.hasKey(cacheKey)) {
			// TODO refresh cache
//			refreshCache(cacheKey, valueLoader, result);
			return (T)fromStoreValue(result);
		}
		log.debug("[Cache-Redis] name={} key={} call get and not match, ready to load", this.name, key);
		// TODO add lock
		return loaderAndPutValue(cacheKey, valueLoader);
	}

//	private <T> void refreshCache(String cacheKey, Callable<T> valueLoader, Object result) {
//		Long ttl = this.redisTemplate.getExpire(cacheKey);
//		Long preload = Long.valueOf(level2Config.getPreload());
//		TimeUnit timeUnit = level2Config.getTimeUnit();
//		boolean flag = (isAllowNullValues() && (result instanceof NullValue || result == null));
//		if (flag)
//			preload = Long.valueOf(preload.longValue() / getMagnification());
//		if (null != ttl && ttl.longValue() > 0L && timeUnit.toMillis(ttl.longValue()) <= preload.longValue())
//			if (! level2Config.isForceRefresh()) {
//				log.debug("rediskey={} , redisCacheKey.getKey());", softRefresh(redisCacheKey);
//			} else {
//				log.debug("rediskey={} , redisCacheKey.getKey());", forceRefresh(redisCacheKey, valueLoader);
//			}
//	}

	@Override
	public void put(Object key, Object value) {
		String cacheKey = getKey(key);
		log.debug("[Cache-Redis] name={} key={} value={} call put", this.name, key, value);
//        Object result = serializeCacheValue(toStoreValue(value));
		putValue(cacheKey, value);
	}

	@Override
	public void evict(Object key) {
		String cacheKey = getKey(key);
		log.debug("[Cache-Redis] name={} key={} call evict", name, key);
		redisTemplate.delete(cacheKey);
	}

	@Override
	public void clear() {
		String pattern = name + "*";
		if(level2Config.isUsePrefix()) {
			log.debug("[Cache-Redis] name={} call clear", name);
			Set<String> allKeys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
				if (connection instanceof RedisClusterConnection) {
					// Cluster mode TODO
					return redisTemplate.keys(pattern);
				}
				Set<String> keys = new HashSet<>();
				// Stand-alone mode
				try (Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(pattern).count(100).build())) {
					while (cursor.hasNext()) {
						keys.add(new String(cursor.next(), "UTF-8"));
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return keys;
			});
			redisTemplate.delete(allKeys);
		}
	}

	private <T> T loaderAndPutValue(String key, Callable<T> valueLoader) {
		try {
			Object result = putValue(key, valueLoader.call());
			return (T)fromStoreValue(result);
		} catch (Exception e) {
			throw new AbstractValueAdaptingCache.LoaderCacheValueException(key, e);
		}
	}

	private Object putValue(String key, Object value) {
		Object result = toStoreValue(value);
		if (result == null) {
			this.redisTemplate.delete(key);
			return null;
		}
		if (!isAllowNullValues() && result instanceof NullValue) {
			this.redisTemplate.delete(key);
			return result;
		}
		long expirationTime = level2Config.getExpire();
		if (isAllowNullValues() && result instanceof NullValue)
			expirationTime /= level2Config.getMagnification();
		this.redisTemplate.opsForValue().set(key, result, expirationTime, level2Config.getTimeUnit());
		return result;
	}

	/**
	 *
	 * @return
	 */
	public String getKey(Object key) {
		return new String(createAndConvertCacheKey(key));
	}

	/**
	 * return serialize cache Key
	 * @return
	 */
	private byte[] createAndConvertCacheKey(Object key) {
		byte[] byteKey = serializeCacheKey(key);
		if(! level2Config.isUsePrefix())
			return byteKey;

		byte[] bytePrefix = serializeCacheKey(prefixCacheKey());
		byte[] bytePrefixKey = Arrays.copyOf(bytePrefix, bytePrefix.length + byteKey.length);
		System.arraycopy(byteKey, 0, bytePrefixKey, bytePrefix.length, byteKey.length);
		return bytePrefixKey;
	}

	/**
	 * build prefix key
	 * @return
	 */
	private String prefixCacheKey() {
		return CacheKeyPrefix.simple().compute(name);
	}

	/**
	 * serializer cache key
	 * @param key
	 * @return
	 */
	private byte[] serializeCacheKey(Object key) {
		RedisSerializer keySerializer = redisTemplate.getKeySerializer();
		if(Objects.isNull(keySerializer) && key instanceof byte[])
			return (byte[]) key;
		return keySerializer.serialize(key);
	}

	/**
	 * serializer cache value
	 * @param value
	 * @return
	 */
	protected byte[] serializeCacheValue(Object value) {
		RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
		return valueSerializer.serialize(value);
	}

	@FunctionalInterface
	interface CacheKeyPrefix {
		String compute(String name);

		static CacheKeyPrefix simple() {
			return (name) -> {
				return name + "::";
			};
		}
	}
}
