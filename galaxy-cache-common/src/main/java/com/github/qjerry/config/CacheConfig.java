package com.github.qjerry.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Getter
@Setter
@ToString
public class CacheConfig {

	/**
	 *
	 */
	private String[] cacheNames;

	/**
	 * cache key
	 */
	private String key;

	/**
	 * If ignore exception
	 */
	private boolean ignoreException;

	/**
	 * Whether all the entries inside the cache(s) are removed.
	 */
	private boolean allEntries;

	/**
	 * Cache level
	 * 1：local cache，2：cloud cache(redis)，3：local cache + cloud cache
	 */
	private Integer cacheLevel;

	public static CacheConfig build(String[] cacheNames, String key, boolean ignoreException, Integer cacheLevel) {
		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setCacheNames(cacheNames);
		cacheConfig.setKey(key);
		cacheConfig.setIgnoreException(ignoreException);
		cacheConfig.setCacheLevel(cacheLevel);
		return cacheConfig;
	}

	public static CacheConfig build(String[] cacheNames, String key, boolean allEntries) {
		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setCacheNames(cacheNames);
		cacheConfig.setKey(key);
		cacheConfig.setAllEntries(allEntries);
		return cacheConfig;
	}

	public static CacheConfig build(String[] cacheNames, String key, Integer cacheLevel) {
		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setCacheNames(cacheNames);
		cacheConfig.setKey(key);
		cacheConfig.setCacheLevel(cacheLevel);
		return cacheConfig;
	}
}
