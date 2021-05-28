package com.github.qjerry.annotation;

import com.github.qjerry.layer.Cache;

import java.lang.annotation.*;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: Annotation indicating that a method (or all methods on a class)
 * triggers a {@link Cache#put(Object, Object) cache put} operation.</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CachePut {

	/**
	 * Names of the caches to use for the cache put operation.
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
	 * cache level
	 * 1：local cache，2：cloud cache(redis)，3：local cache + cloud cache
	 * @return
	 */
	int cacheLevel() default 1;

	/**
	 * local cache config
	 * @return
	 */
	Level1Cache level1Cache() default @Level1Cache();

	/**
	 * cloud cache config
	 * @return
	 */
	Level2Cache level2Cache() default @Level2Cache();
}
