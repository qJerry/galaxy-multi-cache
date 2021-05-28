package com.github.qjerry.service;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
public interface DemoService {

	String getData(String s1, String s2);

	void clearData(String s1, String s2);

	String putData(String s1, String s2);
	
	void sendMessage();
}
