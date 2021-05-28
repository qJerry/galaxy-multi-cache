package com.github.qjerry.annotation;

import java.lang.annotation.*;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: Annotation indicating that the result of invoking a method
 * (or all methods in a class) can be cached.</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable {

	/**
	 * Names of the caches in which method invocation results are stored.
	 * @return
	 */
	String[] cacheNames() default {};

	/**
	 * Support spring Expression Language (SpEL) expression.
	 * @return
	 */
	String key() default "";

	/**
	 * TODO
	 * @return
	 */
	String cacheManager() default "";

//    /**
//     * The bean name of the custom {@link org.springframework.cache.interceptor.CacheResolver}
//     * to use.
//     * @see CacheConfig#cacheResolver
//     */
//    String cacheResolver() default "";

	/**
	 * TODO
	 * @return
	 */
	String condition() default "";

	/**
	 * TODO
	 * @return
	 */
	String unless() default "";

	/**
	 * TODO
	 * @return
	 */
	boolean sync() default false;

	/**
	 * Cache level
	 * 1：local cache，2：cloud cache(redis)，3：local cache + cloud cache
	 * @return
	 */
	int cacheLevel() default 1;

	/**
	 * Local cache config
	 * @return
	 */
	Level1Cache level1Cache() default @Level1Cache();

	/**
	 * Cloud cache config
	 * @return
	 */
	Level2Cache level2Cache() default @Level2Cache();

	/**
	 * If ignore exception
	 * @return
	 */
	boolean ignoreException() default true;
}

