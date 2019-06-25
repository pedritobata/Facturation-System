package com.pedro.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pedro.springboot.app.models.dao.IClienteDao;
import com.pedro.springboot.app.models.dao.IClienteDaoRepository;
import com.pedro.springboot.app.models.dao.IFacturaDao;
import com.pedro.springboot.app.models.dao.IProductoDao;
import com.pedro.springboot.app.models.entity.Cliente;
import com.pedro.springboot.app.models.entity.Factura;
import com.pedro.springboot.app.models.entity.Producto;

@Service
//Las clases service serán un Facade para los DAOs
public class ClienteServiceImpl implements IClienteService{
	
//	@Autowired
//	private IClienteDao clienteDao;
	
	//puedo usar una interfaz que extiende de CrudRepository en vez del dao
	//Con esto ya no necesitaría una clase que implemente el dao y haga las operaciones de la BD a mano!!
	
	@Autowired
	IClienteDaoRepository clienteDao;
	
	@Autowired
	private IProductoDao productoDao;
	
	@Autowired
	private IFacturaDao facturaDao;
	
	
	
	//Las anotaciones @Transactional, se han quitado del DAO y se están
	//usando acá en el service
	//Dentro de cada metodo se podría interactuar con varios DAOs dentro de la misma
	//transaccion

	@Override
	@Transactional(readOnly=true)
	public List<Cliente> findAll() {
		//return clienteDao.findAll();
		//para usar el findAll() de IClienteDaoRepository
		//necesito hacer un casteo
		return (List<Cliente>) clienteDao.findAll();
	}

	@Override
	@Transactional
	public void save(Cliente cliente) {
		clienteDao.save(cliente);
	}

	@Override
	@Transactional(readOnly=true)
	public Cliente findOne(Long id) {
		return clienteDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteDao.deleteById(id);
	}

	@Override
	//metodo para paginacion
	//Page y Pageable son clases de Spring que implementan Iterable
	public Page<Cliente> findAll(Pageable pageable) {
		return clienteDao.findAll(pageable);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Producto> findByNombre(String term) {
		
		//return productoDao.findByNombre(term);
		return productoDao.findByNombreLikeIgnoreCase("%" + term + "%");
	}

	@Override
	@Transactional
	public void saveFactura(Factura factura) {
		facturaDao.save(factura);
	}

	@Override
	@Transactional(readOnly=true)
	public Producto findProductoByid(Long id) {
		//En springboot 2 , hay un metodo findById que  retorna un Optional de Producto.
		//Con eso puedo validar si encontró el Producto o no
		//el metodo orElse sirve para eso: productoDao.findById(id).orElse(null);
		
		return productoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly=true)
	public Factura findFacturaById(Long id) {
		
		return facturaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteFactura(Long id) {
		facturaDao.deleteById(id);
		
	}

	@Override
	@Transactional(readOnly=true)
	public Factura fetchByIdWithCLienteWithItemFacturaWithProducto(Long id) {
		
		return facturaDao.fetchByIdWithCLienteWithItemFacturaWithProducto(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Cliente fetchByIdWithFacturas(Long id) {
		
		return clienteDao.fetchByIdWithFacturas(id);
	}
	
	

}
