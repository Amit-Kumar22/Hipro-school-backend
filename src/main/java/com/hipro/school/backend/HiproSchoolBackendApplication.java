package com.hipro.school.backend;

import java.util.TimeZone;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
//@EnableCaching
public class HiproSchoolBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiproSchoolBackendApplication.class, args);
	}
	
	@PostConstruct
	public void init() {
	    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	}
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
