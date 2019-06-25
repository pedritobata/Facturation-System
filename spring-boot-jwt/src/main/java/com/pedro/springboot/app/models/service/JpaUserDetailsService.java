package com.pedro.springboot.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pedro.springboot.app.models.dao.IUsuarioDao;
import com.pedro.springboot.app.models.entity.Role;
import com.pedro.springboot.app.models.entity.Usuario;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService{
	
	@Autowired
	IUsuarioDao usuarioDao;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	@Transactional(readOnly=true)//Anotacion de Spring , no la de Java!
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioDao.findByUserName(username);
		if(usuario == null) {
			logger.error("Error login: no existe el usuario : '" + username + "'" );
			throw new UsernameNotFoundException("Username : " + username + " no existe en el sistema");
		}
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : usuario.getRoles()) {
			logger.info("Role : " + role.getAuthority());
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
		}
		
		if(authorities.isEmpty()) {
			logger.error("Error login: el usuario : '" + username + "'" + " no tiene roles asignados");
			throw new UsernameNotFoundException("Username : " + username + " no tiene roles asignados");
		}
		
		return new User(usuario.getUserName(), usuario.getPassword(), usuario.getEnabled(),true,true,true,authorities);
	}
	
	

}
