package com.pedro.springboot.app.auth.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedro.springboot.app.auth.SimpleGrantedAuthorityMixin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTServiceImpl implements JWTService {
	
	public static final String SECRET = Base64Utils.encodeToString("Alguna.clave.secreta.1234".getBytes());
	public static final long EXPIRATION_DATE = 3600000L;
	public static final String PREFIJO_TOKEN = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	

	@Override
	public String create(Authentication auth) throws IOException {
        String username = ((User)auth.getPrincipal()).getUsername();
		
		Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
		
		Claims claims = Jwts.claims();
		claims.put("authorities", new ObjectMapper().writeValueAsString(roles));//le paso los roles parseados como json pa que lo vean bien los clientes
		
		String token = Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
				.setIssuedAt(new Date())//fecha de creacion
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE))//3600000 milis = 1 hora
				.compact();
		
		return token;
	}

	@Override
	public boolean validate(String token) {
		try {
			getClaims(token);
			return true;
		}catch(JwtException | IllegalArgumentException ex) {
			return false;
		}
	}

	@Override
	public Claims getClaims(String token) {
		return Jwts.parser()
				.setSigningKey(SECRET.getBytes())
				.parseClaimsJws(resolve(token)).getBody();
	}

	@Override
	public String getUsername(String token) {
		
		return getClaims(token).getSubject();
	}

	@Override
	public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
		Object roles = getClaims(token).get("authorities");
		
		//Acá habrá un problema cuando el ObjectMapper quiera parsear a Json los objetos de la clase SimpleGrantedAuthority
		//porque invocará su constructor vacío pero en realidad esta clase no lo tiene!!
		//Tendremos que crear una clase abstracta SimpleGrantedAuthorityMixin  con todas las caracteristicas que
		//se necesita para parsear correctamente los authorities y poderla mixear con la SimpleGrantedAuthority
		
		Collection<? extends GrantedAuthority> authorities = 
				Arrays.asList(new ObjectMapper()
						.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
						.readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));
		return authorities;
	}

	@Override
	public String resolve(String token) {
		if(token != null && token.startsWith(PREFIJO_TOKEN)) {
			return token.replace(PREFIJO_TOKEN, "");
		}
		return null;
	}

}
