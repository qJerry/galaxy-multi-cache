package com.github.qjerry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
@SpringBootApplication
@EnableMultiLevelCache
public class CacheExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheExampleApplication.class, args);
	}
}
