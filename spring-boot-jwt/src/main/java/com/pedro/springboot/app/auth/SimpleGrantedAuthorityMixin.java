package com.pedro.springboot.app.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SimpleGrantedAuthorityMixin {

	@JsonCreator
	//authority es el nombre exacto que el GrantedAuhority le daba a los roles, así que hay que llamarlo igualito!!
	public SimpleGrantedAuthorityMixin(@JsonProperty("authority") String role) {
		
	}
	
	

}
