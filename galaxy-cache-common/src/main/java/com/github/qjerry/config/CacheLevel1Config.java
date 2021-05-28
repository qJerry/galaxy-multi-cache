package com.github.qjerry.config;

import com.github.qjerry.enums.ExpireModeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: local cache config</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Getter
@Setter
@ToString
public class CacheLevel1Config implements Serializable {

	private static final long serialVersionUID = 4315187537002205632L;

	/**
	 * Init cap
	 */
	private int initialCapacity = 10;

	/**
	 * Max size
	 */
	private int maximumSize = 500;

	/**
	 * Cache effective time
	 */
	private int expire = 0;

	/**
	 * Cache time unit
	 */
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	/**
	 * Cache expire mode, default WRITE
	 */
	private ExpireModeEnum expireMode = ExpireModeEnum.WRITE;

	/**
	 * If allow null value
	 */
	private boolean isAllowNullValue = false;
}
