package com.sz.lyq.configurationDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@ImportResource(locations = {
		"META-INF/spring/context.xml",
		"META-INF/spring/context_dev.xml"
})
public class ConfigurationDemoApplication implements EnvironmentAware {

	public static void main(String[] args) {
		//可以设置需要的profiles文件
		SpringApplication springApplication = new SpringApplication(ConfigurationDemoApplication.class);
		springApplication.setAdditionalProfiles("dev");
		springApplication.run(args);
	}

	@Override
	public void setEnvironment(Environment environment) {

		List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
		System.out.println("当前激活的profiles：" + activeProfiles);

		if(environment instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment env = ConfigurableEnvironment.class.cast(environment);
			MutablePropertySources propertySources = env.getPropertySources();
			Map<String,Object> source = new HashMap<>();
			/**
			 * 此处修改端口无效，原因是因为时机不对，可通过在springboot的spring.factories文件中
			 * 的applicationlistener中的configFileListener查看
			 *
			 * 需要通过代码编程的方式修改的话，需要实现applicationListener接口
			 * 在/context中
			 */
			source.put("server.port","8081");
			MapPropertySource mps = new MapPropertySource("java_env",source);
			propertySources.addFirst(mps);
		}
	}
}
