package com.pedro.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.pedro.springboot.app.models.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{
	
	//Siguiendo el estandar de nombrar los metodos conseguimos que
	//Spring cree el Query por nosotros , algo así : Select u form Usuario u where u.username = ?1
	Usuario findByUserName(String username); 

}
