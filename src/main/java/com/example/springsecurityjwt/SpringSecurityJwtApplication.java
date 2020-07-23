package com.example.springsecurityjwt;
import com.example.springsecurityjwt.services.FileStorageService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootApplication
public class SpringSecurityJwtApplication /*implements CommandLineRunner*/ {

	@Resource
	FileStorageService fileStorageService;

	public static void main(String[] args) throws IOException {

		SpringApplication.run(SpringSecurityJwtApplication.class, args);
	}


//	@Override
//	public void run(String... args) throws Exception {
//		fileStorageService.deleteAll();
//		fileStorageService.init();
//	}
}
