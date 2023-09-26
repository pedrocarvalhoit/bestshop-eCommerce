package com.bestshop.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.bestshop.common.entity", "com.bestshop.admin.user", "com.bestshop.admin.category", "com.bestshop.admin.brand"})
public class BestshopBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(BestshopBackEndApplication.class, args);
	}

}
