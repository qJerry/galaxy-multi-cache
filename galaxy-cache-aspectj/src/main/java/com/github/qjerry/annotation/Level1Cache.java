package com.github.qjerry.annotation;

import com.github.qjerry.enums.ExpireModeEnum;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: local cache config</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Level1Cache {

	/**
	 * Cache effective time
	 * @return
	 */
	int expire() default 5;

	/**
	 * Cache init capacity
	 * @return
	 */
	int initialCapacity() default 1;

	/**
	 * Cache max size
	 * @return
	 */
	int maximumSize() default 1000;

	/**
	 * Cache time unit
	 * @return
	 */
	TimeUnit timeunit() default TimeUnit.SECONDS;

	/**
	 * Cache expire mode, default WRITE
	 * @return
	 */
	ExpireModeEnum expireMode() default ExpireModeEnum.WRITE;
}
