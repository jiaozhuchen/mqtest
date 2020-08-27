package com.cc.mqproducer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cc.mqproducer.dao")
public class MqProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqProducerApplication.class, args);
	}

}
