package com.github.qjerry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: cache expire mode</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Getter
@AllArgsConstructor
public enum ExpireModeEnum {
	WRITE("after the last write"),
	ACCESS("after the last access");

	String desc;

}
