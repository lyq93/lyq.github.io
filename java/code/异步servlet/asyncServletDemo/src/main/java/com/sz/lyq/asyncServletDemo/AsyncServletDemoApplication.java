package com.sz.lyq.asyncServletDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan(basePackages = "com.sz.lyq.asyncServletDemo.servlet")
@SpringBootApplication
public class AsyncServletDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncServletDemoApplication.class, args);
	}

}
