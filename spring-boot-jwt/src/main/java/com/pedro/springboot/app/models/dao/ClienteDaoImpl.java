package com.pedro.springboot.app.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pedro.springboot.app.models.entity.Cliente;

@Repository("ClienteDaoJPA")
public class ClienteDaoImpl implements IClienteDao{
	
	@PersistenceContext//obtiene el data source o contexto de persistencia
	//dependiendo de qué ORM se esté usando. Si no especificamos nada en el application.properties, entonces
	//Spring usará la Base de Datos H2 , la cual está embebida en el proyecto Spring boot 
	private EntityManager em;

	@SuppressWarnings("unchecked")
	//@Transactional(readOnly=true)//toma el contenido del metodo y lo envuelve en una transaccion
	//read only es porque la consulta es solo de lectura
	@Override
	public List<Cliente> findAll() {
		
		return em.createQuery("from Cliente").getResultList();
	}

	@Override
	@Transactional
	//Este metodo se usará para crear o actualizar un registro en la BD
	public void save(Cliente cliente) {
		if(cliente.getId()!=null && cliente.getId()>0) {
			//Si ya existe el id, attacha al persistence context y actualiza 
			em.merge(cliente);
		}else {
			//Si no existe el id, inserta en la BD y attacha al persistence context
			em.persist(cliente);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public Cliente findOne(Long id) {
      return em.find(Cliente.class, id);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Cliente cliente = findOne(id);
		em.remove(cliente);
	}

}
