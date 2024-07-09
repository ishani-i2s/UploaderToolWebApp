package com.example.demo;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.http.HttpClient;

import static org.apache.http.impl.client.HttpClientBuilder.*;

@SpringBootApplication
public class DemoApplication {
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

//	@Bean
//	public RestTemplate restTemplatePatch() {
//		RestTemplate restTemplate = new RestTemplate();
//		HttpClient httpClient = HttpClientBuilder.create().build();
//		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//		restTemplate.setRequestFactory(requestFactory);
//		return restTemplate;
//	}


//	public RestTemplate restPatchTemplate() {
//		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//		return new RestTemplate(requestFactory);
//	}

//	@Bean
//	public RestTemplate restTemplatePatch() {
//		return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
//	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

//	@Bean
//	public WebMvcConfigurer configure(){
//		return new WebMvcConfigurer() {
//			public void addCorsMapping(CorsRegistry reg){
//				reg.addMapping("/**").allowedOrigins("*").allowedMethods("GET","POST","PUT","DELETE");
//			}
//		};
//	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}


}
