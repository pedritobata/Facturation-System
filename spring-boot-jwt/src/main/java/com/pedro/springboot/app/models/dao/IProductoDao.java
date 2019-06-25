package com.pedro.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.pedro.springboot.app.models.entity.Producto;


public interface IProductoDao extends CrudRepository<Producto, Long>{
	
	//creamos un metodo personalizado, ya que es para el autocomplete. CrudRepository  no tiene metodo para eso
	@Query("select p from Producto p where p.nombre like %?1%")//el ?1 hace referencia al primer parametro de la funcion, en este caso a term 
	List<Producto> findByNombre(String term);
	
	//otra forma de implementar un metodo para autocomplete
	//Acá Spring creará el query solito!!!. Debido a que el nombre de mi método sigue un estandar
	// find + By + <nombre de un campo> + And + <nombre de otro campo> + etc + LikeIgnoreCase "  . y así hay varios tipos de query
	//que puedo hacer que Spring genere el query siempre y cuando siga el estandar al nombrar mis metodos
	//En este caso Spring asume que el campo a usar para el where es uno solo y se llama "nombre"
	public List<Producto> findByNombreLikeIgnoreCase(String term);

}
