package com.sz.lyq.springcloudconfigclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;

@SpringBootApplication
@EnableScheduling
public class SpringCloudConfigClientApplication {
	//刷新配置服务器配置信息API
	private final ContextRefresher contextRefresher;
	//构造器注入
	@Autowired
	public SpringCloudConfigClientApplication(ContextRefresher contextRefresher) {
		this.contextRefresher = contextRefresher;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigClientApplication.class, args);
	}

	//刷新服务器配置
	@Scheduled(fixedRate = 1000L)
	public void update() {
		Set<String> keys = contextRefresher.refresh();

		if (!keys.isEmpty()) {
			System.out.println("该次更新的配置：" + keys);
		}
	}
}
