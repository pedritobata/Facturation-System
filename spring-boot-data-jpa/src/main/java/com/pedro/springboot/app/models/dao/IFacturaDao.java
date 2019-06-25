package com.pedro.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.pedro.springboot.app.models.entity.Factura;

public interface IFacturaDao extends CrudRepository<Factura, Long>{
	
	//creamos un metodo para realizar una sola consulta cuando el query requiere varias tablas
	//Esto es para ya no usar varias consultas independientes con el modo LAZY
	
	@Query("select f from Factura f join fetch f.cliente c join fetch f.items l join fetch l.producto where f.id=?1")
	Factura fetchByIdWithCLienteWithItemFacturaWithProducto(Long id);
	
}
