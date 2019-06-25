package com.pedro.springboot.app.models.dao;

import java.util.List;

import com.pedro.springboot.app.models.entity.Cliente;

public interface IClienteDao {

	 List<Cliente> findAll();
	 
	 void save(Cliente cliente);
	 
	 Cliente findOne(Long id);
	 
	 void delete(Long id);
	
}
