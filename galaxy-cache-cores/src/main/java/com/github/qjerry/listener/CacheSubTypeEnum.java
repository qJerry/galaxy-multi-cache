package com.github.qjerry.listener;

import java.util.Arrays;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: cache subscription type</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
public enum CacheSubTypeEnum {
	EVICT(1, "delete by key"),
	CLEAR(2, "clear all keys from cache name"),
	PREHEAT(3, "pre heat");

	CacheSubTypeEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}

	private final int code;

	private final String message;

	public String getMessage() {
		return this.message;
	}

	public static CacheSubTypeEnum get(int type) {
		return Arrays.<CacheSubTypeEnum>stream(values()).filter(t -> (type == t.code)).findFirst().get();
	}

	public int getCode() {
		return this.code;
	}
	}
