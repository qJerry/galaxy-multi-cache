package com.github.qjerry.layer;

import com.github.qjerry.support.NullValue;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
public abstract class AbstractValueAdaptingCache implements Cache {

	private final boolean allowNullValues;

	/**
	 * Create an {@code AbstractValueAdaptingCache} with the given setting.
	 * @param allowNullValues whether to allow for {@code null} values
	 */
	protected AbstractValueAdaptingCache(boolean allowNullValues) {
		this.allowNullValues = allowNullValues;
	}

	/**
	 * Return whether {@code null} values are allowed in this cache.
	 */
	public final boolean isAllowNullValues() {
		return this.allowNullValues;
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		Object value = fromStoreValue(lookup(key));
		if (value != null && type != null && !type.isInstance(value)) {
			throw new IllegalStateException(
					"Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) value;
	}

	/**
	 * Perform an actual lookup in the underlying store.
	 * @param key the key whose associated value is to be returned
	 * @return the raw store value for the key, or {@code null} if none
	 */
	protected abstract Object lookup(Object key);


	/**
	 * Convert the given value from the internal store to a user value
	 * returned from the get method (adapting {@code null}).
	 * @param storeValue the store value
	 * @return the value to return to the user
	 */
	protected Object fromStoreValue(Object storeValue) {
		if (this.allowNullValues && storeValue == NullValue.INSTANCE) {
			return null;
		}
		return storeValue;
	}

	/**
	 * Convert the given user value, as passed into the put method,
	 * to a value in the internal store (adapting {@code null}).
	 * @param userValue the given user value
	 * @return the value to store
	 */
	protected Object toStoreValue(Object userValue) {
		if (userValue == null) {
			if (this.allowNullValues) {
				return NullValue.INSTANCE;
			}
			throw new IllegalArgumentException(
					"Cache '" + getName() + "' is configured to not allow null values but null was provided");
		}
		return userValue;
	}

	/**
	 *
	 */
	public class ValueLoadedException extends RuntimeException {

		private final Object key;

		public ValueLoadedException(Object key, Throwable ex) {
			super(String.format("[load-value] for key=%s error", key, ex));
			this.key = key;
		}

		public Object getKey() {
			return this.key;
		}
	}

	public class LoaderCacheValueException extends RuntimeException {
		private final Object key;

		public LoaderCacheValueException(Object key, Throwable ex) {
			super(String.format("key=[%s], load error", new Object[] { key }), ex);
			this.key = key;
		}

		public Object getKey() {
			return this.key;
		}
	}
}
