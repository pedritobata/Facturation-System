package com.pedro.springboot.app.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.pedro.springboot.app.auth.service.JWTService;
import com.pedro.springboot.app.auth.service.JWTServiceImpl;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter{
	
	private JWTService jwtService;

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTService jwtService) {
		super(authenticationManager);
		this.jwtService = jwtService;
		
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String header = request.getHeader(JWTServiceImpl.HEADER_STRING);

		if (!requiresAuthentication(header))  {
			chain.doFilter(request, response);
			return;
		}
		
		UsernamePasswordAuthenticationToken auth = null;
		
		if(jwtService.validate(header)) {
			
			auth = new UsernamePasswordAuthenticationToken(jwtService.getUsername(header), null,jwtService.getRoles(header));
		}
		
		
		SecurityContextHolder.getContext().setAuthentication(auth);
		chain.doFilter(request, response);
		
	}
	
	protected boolean requiresAuthentication(String header) {
		if (header == null || !header.startsWith(JWTServiceImpl.PREFIJO_TOKEN)) {
			return false;
		}
		
		return true;
	}
	
	

}
