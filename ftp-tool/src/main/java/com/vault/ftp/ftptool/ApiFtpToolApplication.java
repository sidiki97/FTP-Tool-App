package com.vault.ftp.ftptool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@ServletComponentScan
@SpringBootApplication
public class ApiFtpToolApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiFtpToolApplication.class, args);
	}

	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.ofBytes(52428800));
		factory.setMaxRequestSize(DataSize.ofBytes(52428800));
		return factory.createMultipartConfig();
	}

}
