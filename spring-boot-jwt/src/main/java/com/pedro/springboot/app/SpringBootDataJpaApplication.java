package com.pedro.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pedro.springboot.app.models.service.IUploadFileService;

@SpringBootApplication
public class SpringBootDataJpaApplication implements CommandLineRunner{
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private IUploadFileService uploadFileService; 

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDataJpaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		uploadFileService.deleteAll();
		uploadFileService.init();
		
		String password= "1234";
		for (int i = 0; i < 2; i++) {
			//creo dos encriptaciones de la misma contraseña 12345
			//estas las usaré para la autenticacion por base de datos
			String bcryptPassword= encoder.encode(password);
			System.out.println(bcryptPassword);
		}
		
	}
}
