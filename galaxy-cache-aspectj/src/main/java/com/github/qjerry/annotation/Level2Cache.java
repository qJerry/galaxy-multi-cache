package com.github.qjerry.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: redis cache config</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Level2Cache {

	/**
	 * Cache effective time
	 * @return
	 */
	long expire() default 10L;

	/**
	 * Forced to refresh the cache time before invalidation
	 * @return
	 */
	long preload() default 0;

	/**
	 * Cache time unit
	 * @return
	 */
	TimeUnit timeunit() default TimeUnit.SECONDS;

	/**
	 * If forece refresh
	 * @return
	 */
	boolean forceRefresh() default false;

	/**
	 * If allow null value
	 * @return boolean
	 */
	boolean isAllowNullValue() default false;

	/**
	 * The time multiplier between the non-null value and the null value, the default is 1. allowNullValue=true is valid.
	 * @return
	 */
	int magnification() default 1;
}
