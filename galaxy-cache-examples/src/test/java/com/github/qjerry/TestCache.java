package com.github.qjerry;

import com.github.qjerry.service.DemoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>Title:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages= {"source.cache"})
@SpringBootTest
public class TestCache {
	@Autowired
	DemoService demoService;

	@Test
	public void demo() throws Exception {
		for(int i = 0; i < 10; i++) {
//			new Thread(() -> {
				demoService.getData("1", "2");
//			}).start();
		}
		Thread.sleep(5*1000);
//        demoService.clearData("1", "2");
		demoService.sendMessage();
		Thread.sleep(5*1000);
		demoService.getData("1", "2");
	}
}
