package com.github.qjerry.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: redis cache config</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Getter
@Setter
@ToString
public class CacheLevel2Config implements Serializable {

	private static final long serialVersionUID = -5547106941808877941L;

	/**
	 * Cache effective time
	 */
	private long expire = 0L;

	/**
	 * Forced to refresh the cache time before invalidation
	 */
	private long preload = 0L;

	/**
	 * Cache time unit
	 */
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	/**
	 * If forece refresh
	 */
	private boolean forceRefresh = false;

	/**
	 *
	 */
	private boolean usePrefix = true;

	/**
	 * If allow null value
	 */
	private boolean isAllowNullValue = false;

	/**
	 * The time multiplier between the non-null value and the null value, the default is 1. allowNullValue=true is valid.
	 */
	private int magnification = 1;
}
