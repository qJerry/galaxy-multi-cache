package com.github.qjerry.layer;

import com.sun.istack.internal.Nullable;

import java.util.concurrent.Callable;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
public interface Cache {

	/**
	 * Return the cache name.
	 * @return
	 */
	String getName();

	/**
	 * Return the underlying native cache provider.
	 * @return
	 */
	Object getNativeCache();

	/**
	 * Return the value to which this cache maps the specified key.
	 *
	 * @param key
	 * @return
	 */
	@Nullable
	Object get(Object key);

	/**
	 * Return the value to which this cache maps the specified key.
	 *
	 * @param key
	 * @param type
	 * @param <T>
	 * @return
	 */
	@Nullable
	<T> T get(Object key, @Nullable Class<T> type);

	/**
	 * Return the value to which this cache maps the specified key.
	 *
	 * @param key
	 * @param valueLoader
	 * @param <T>
	 * @return
	 */
	@Nullable
	<T> T get(Object key, Callable<T> valueLoader);

	/**
	 * Associate the specified value with the specified key in this cache.
	 *
	 * @param key
	 * @param value
	 */
	void put(Object key, @Nullable Object value);

	/**
	 * Evict the mapping for this key from this cache if it is present.
	 * @param key
	 */
	void evict(Object key);

	/**
	 * Clear the cache through removing all mappings.
	 */
	void clear();
}
