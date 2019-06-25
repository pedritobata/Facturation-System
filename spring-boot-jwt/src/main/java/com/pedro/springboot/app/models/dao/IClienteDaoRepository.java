package com.pedro.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.pedro.springboot.app.models.entity.Cliente;

//Usamos la Interface CrudRepository de Spring para manejar la persistencia
//esta Interface es generica y utiliza la clase Entity(Cliente) y una clase para el id o llave primaria(Long)

//Puedo usar tambien la interface PagingAndSortingRepository , la cual es hija de CrudRepository!!
//con esa interfaz puedo trabajar paginacion de la data
public interface IClienteDaoRepository extends PagingAndSortingRepository<Cliente, Long>{
	
	//La interface CrudRepository, por dentro usa jpa para hacer los querys(usa @Query) necesarios
	
	//Ya no tenemos que implementar las llamadas a la BD para cada operacion
	//Todo lo hacen los metodos heredados de CrudRepository!!!! 
	
	//Solo si quisiera metodos customizados , recién metería código en esta interface :)
	
	//Y justo aquí va un query personalizado para cuando hay relaciones entre varias tablas y no se 
	//quiere hacer varias llamadas a la BD usando Lazy
	
	@Query("select c from Cliente c left join fetch c.facturas f where c.id=?1")
	//Ojo que un query solo con join me trae informacion del cliente siempre y cuando tenga tambien informacion en
	//la tabla facturas.
	//Para que me traiga los clientes sin importar si tienen facturas o no, usamos el left join!!
	Cliente fetchByIdWithFacturas(Long id);

}
