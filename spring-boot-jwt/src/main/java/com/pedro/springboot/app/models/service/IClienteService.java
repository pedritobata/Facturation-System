package com.pedro.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pedro.springboot.app.models.entity.Cliente;
import com.pedro.springboot.app.models.entity.Factura;
import com.pedro.springboot.app.models.entity.Producto;

public interface IClienteService {
	
     List<Cliente> findAll();
     
     //este metodo es el paginable
     //usa clases de Spring data
     Page<Cliente> findAll(Pageable pageable);
	 
	 void save(Cliente cliente);
	 
	 Cliente findOne(Long id);
	 
	 Cliente fetchByIdWithFacturas(Long id);
	 
	 void delete(Long id);
	 
	 List<Producto> findByNombre(String term);
	 
	 void saveFactura(Factura factura);
	 
	 Producto findProductoByid(Long id);
	 
	 Factura findFacturaById(Long id);
	 
	 void deleteFactura(Long id);
	 
	 Factura fetchByIdWithCLienteWithItemFacturaWithProducto(Long id);

}
