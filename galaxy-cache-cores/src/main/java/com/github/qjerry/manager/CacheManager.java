package com.github.qjerry.manager;

import com.github.qjerry.config.CacheMultiConfig;
import com.github.qjerry.layer.Cache;

import java.util.Collection;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
public interface CacheManager {

	/**
	 * Get a collection of the cache associated with the given name.
	 * @param name
	 * @return
	 */
	Collection<Cache> getCache(String name);

	/**
	 * Get the multi cache associated with the given name.
	 * @param name
	 * @param multiConfig
	 * @return
	 */
	Cache getCache(String name, CacheMultiConfig multiConfig);

	/**
	 * Get a collection of the cache names known by this manager.
	 * @return the names of all caches known by the cache manager
	 */
	Collection<String> getCacheNames();
}
