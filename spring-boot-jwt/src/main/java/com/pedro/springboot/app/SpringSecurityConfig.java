package com.pedro.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import com.pedro.springboot.app.auth.filter.JWTAuthenticationFilter;
import com.pedro.springboot.app.auth.filter.JWTAuthorizationFilter;
import com.pedro.springboot.app.auth.handler.LoginSuccessHandler;
import com.pedro.springboot.app.auth.service.JWTService;
import com.pedro.springboot.app.models.service.JpaUserDetailsService;

@EnableGlobalMethodSecurity(securedEnabled= true, prePostEnabled=true)//prePostEnabled se usa solo cuando quiero
//usar la anotacion @PreAuthorize , la cual funciona igualito que @Secured!!
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	private DataSource dataSource;
	
	
	@Autowired
	private JpaUserDetailsService userDetailsService;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private JWTService jwtService;
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests().antMatchers("/css/**","/js/**","/images/**","/listar**","/locale").permitAll()
		/*.antMatchers("/ver/**").hasAnyRole("USER")
		.antMatchers("/uploads/**").hasAnyRole("USER")
		.antMatchers("/form/**").hasAnyRole("ADMIN")
		.antMatchers("/eliminar/**").hasAnyRole("ADMIN")
		.antMatchers("/factura/**").hasAnyRole("ADMIN")*/
		.anyRequest().authenticated()
		/*.and().formLogin().successHandler(successHandler).loginPage("/login").permitAll()
		.and().logout().permitAll()
		.and().exceptionHandling().accessDeniedPage("/error_403")
		*/
		.and()
		.addFilter(new JWTAuthenticationFilter(authenticationManager(),jwtService))//authenticationManager() es un metodo herdeado que retorna el AuthenticationManager
		.addFilter(new JWTAuthorizationFilter(authenticationManager(),jwtService))
		.csrf().disable()//deshabilitamos el csrf para trabajar ahora con tokens y rest , ya no con formularios
		// En la vista tmb se quitan el input hidden del csrf
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//deshabilitamos el uso de sesion para seguridad consumiendo la app por rest
	}


	

	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception{
		
		/*PasswordEncoder encoder = this.encoder;
		//el metodo withDefaultPasswordEncoder() está deprecado
		//UserBuilder users = User.withDefaultPasswordEncoder();
	    UserBuilder users = User.builder().passwordEncoder(encoder::encode);//(password -> encoder.encode(password));
	    //Guardamos los usuarios en memoria
	    builder.inMemoryAuthentication()
	    .withUser(users.username("admin").password("1234").roles("ADMIN","USER"))
	    .withUser(users.username("pedro").password("1234").roles("USER"));
	    */
	    
		
		//Usando Jdbc para la autenticacion
		/*
		builder.jdbcAuthentication().dataSource(dataSource).passwordEncoder(encoder)
		.usersByUsernameQuery("select username, password,enabled from users where username=?")
		.authoritiesByUsernameQuery("select u.username,a.authority from authorities a inner join users u on (a.userid=u.id) where u.username=?");
		*/
		
		//Usando JPA para la autenticacion
		builder.userDetailsService(userDetailsService).passwordEncoder(encoder);
	
		
	}
	
	//Este bean lo agregué yo googleando porque al parecer spring no reconocía el dialecto de thymeleaf para spring security!!
	@Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }
	
}
