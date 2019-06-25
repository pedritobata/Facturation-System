package com.pedro.springboot.app.auth.handler;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.SessionFlashMapManager;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	@Autowired
    private MessageSource messageSource;
	
	@Autowired
    private LocaleResolver localeResolver;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		FlashMap flashMap = new FlashMap();
		
		Locale locale = localeResolver.resolveLocale(request);
		String mensaje = String.format(messageSource.getMessage("text.login.success", null, locale), authentication.getName());
		
		flashMap.put("success", mensaje);
		//flashMap.put("success", "Hola " + authentication.getName() +  ", has iniciado sesión con éxito");
		flashMapManager.saveOutputFlashMap(flashMap, request, response);
		if(authentication != null) {
			//Puedo invocar logger directamente porque la clase SimpleUrlAuthenticationSuccessHandler ya lo tiene como atributo y entonces lo heredo 
			logger.info("El usuario '" + authentication.getName() + "' ha iniciado sesión");
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	

}
