package com.pedro.springboot.app.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LocaleController {
	
	@GetMapping("/locale")
	public String locale(HttpServletRequest request) {
		String ultimaUrl = request.getHeader("referer");//referer contiene la ultima url, con todos 
		//los demas parámetros que se estén enviando tambien (a parte del lang, el cual ya lo usó el interceptor), por ejemplo el page del paginador , etc
		//Si no uso este referer, el url del locale chancará todos los demas parámetros que pudiera haber
		
		//Dejo que el request siga su curso como debe ser, ya intercepté y cambié el idioma
		return "redirect:".concat(ultimaUrl);
	}

}
