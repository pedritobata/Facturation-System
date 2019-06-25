package com.pedro.springboot.app;

import java.nio.file.Paths;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class MvcConfig implements WebMvcConfigurer{
	
	private final Logger log = LoggerFactory.getLogger(MvcConfig.class);

	//comento el metodo addResourceHandlers() porque ya no usaré este manejador para
	//llamar desde la vista a las fotos que se hayan subido
	//voy usar un metodo especial para hacer eso programáticamente en el controller
	
	/*@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		
		//Creo la ruta absoluta que apunta hacia el proyecto dentro del servidor
		//el metodo toUri() le agrega al Path el schema "file:\\" 
		String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
		log.info(resourcePath);
		
		registry.addResourceHandler("/upload/**")
		//.addResourceLocations("file:\\C:\\programacion\\practicas\\spring\\andres\\uploads\\");
		.addResourceLocations(resourcePath);
	}
	*/
	
	//Este metodo tiene que llamarse exactamente addViewControllers para que Spring lo reconozca
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/error_403").setViewName("error_403");
	}
	
	//En SpringBoot 5 se usaría el encriptador bcrypt
		//El MD5 ya no es recomendable
		@Bean
		public BCryptPasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
		
	@Bean	
    public LocaleResolver localeResolver() {
		//guardamos el locale en sesion http
    	SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    	localeResolver.setDefaultLocale(new Locale("es", "ES"));
    	return localeResolver;
    }
	
	//Este interceptor cambiará el locale cada vez que se envíe el parámetro del lenguaje en
	//un request http
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
		localeInterceptor.setParamName("lang");//defino como se llamará el parámetro que tiene que buscar el interceptor para obtener qué lenguaje se requiere
		return localeInterceptor;
	}

	//Registramos el interceptor en la configuracion de Spring
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
	
	
	@Bean
	//Este bean se encargará de serializar los objetos a xml
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(new Class[] {com.pedro.springboot.app.view.xml.ClienteList.class});
		return marshaller;
	}
		

}
