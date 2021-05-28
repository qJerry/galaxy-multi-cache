package com.github.qjerry.listener;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

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
@Builder
public class CacheSubMessage implements Serializable {

	private static final long serialVersionUID = -5205816254195492417L;

	/**
	 * Names of the caches in which method invocation results are stored.
	 */
	private String cacheName;

	/**
	 * Support spring Expression Language (SpEL) expression.
	 */
	private Object key;

	/**
	 * timestamp
	 */
	private long sendTimestamp;

	/**
	 * expire time，unit milliseconds
	 */
	private long expireTime;

	/**
	 * cache subscription type
	 */
	private CacheSubTypeEnum messageType;
}
