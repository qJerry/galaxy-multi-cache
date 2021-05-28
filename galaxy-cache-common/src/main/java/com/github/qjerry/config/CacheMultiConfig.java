package com.github.qjerry.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: local cache + redis cache config</p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CacheMultiConfig implements Serializable {

	private static final long serialVersionUID = -2850434356334517992L;

	/**
	 * internal key
	 */
	private String internalKey;

	/**
	 * If allow null value
	 */
	private boolean isAllowNullValue;

	/**
	 * Cache level
	 * 1：local cache，2：cloud cache(redis)，3：local cache + cloud cache
	 */
	private Integer cacheLevel;

	private CacheLevel1Config level1Config;

	private CacheLevel2Config level2Config;

	public static CacheMultiConfig build(CacheLevel1Config level1Config, CacheLevel2Config level2Config) {
		CacheMultiConfig multiConfig = new CacheMultiConfig();
		multiConfig.setLevel1Config(level1Config);
		multiConfig.setLevel2Config(level2Config);
		multiConfig.internalKey();
		return multiConfig;
	}

	private void internalKey() {
		StringBuilder sb = new StringBuilder();
		if (Objects.nonNull(this.level1Config))
			sb.append(this.level1Config.getTimeUnit().toMillis(this.level1Config.getExpire()));
		sb.append("-");
		if (Objects.nonNull(this.level2Config)) {
			sb.append(this.level2Config.getTimeUnit().toMillis(this.level2Config.getExpire()));
			sb.append("-");
			sb.append(this.level2Config.getTimeUnit().toMillis(this.level2Config.getPreload()));
		}
		this.internalKey = sb.toString();
	}
}

