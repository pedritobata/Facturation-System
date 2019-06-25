package com.pedro.springboot.app.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Table(name="clientes")
public class Cliente implements Serializable{
	
	private static final long serialVersionUID = 1813158833773663580L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	//@Column se usa para especificar el nombre que tiene el campo en la BD
	//Si el nombre en la BD es el mismo que el nombre de esta variable
	//entonces NO es necesario poner la anotacion
	
	//puedo agregar reglas de validacion para mis campos del formulario usando anotaciones
	
	@SuppressWarnings("deprecation")
	@NotEmpty
	private String nombre;
	
	@NotEmpty
	private String apellido;
	
	@NotEmpty//verifica que el string no esté vacío
	@Email//Esta anotacion es de Hibernate	
	private String email;
	
	@NotNull//verifica que el string no sea null, pero SÍ puede ser vacío
	@Column(name="create_at")
	//Con esta anotacion jpa se encarga de transformar el java.util.Date al tipo de Date que maneje la BD
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createAt;
	
	//Relacion con Factura
	@OneToMany(mappedBy="cliente",fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)//con lazy solo me traigo la lista de facturas de la BD CUANDO INVOQUE al 
	//metodo getFacturas()!!
	//Cascade es para que se eliminen todos las facturas relacionadas a un Cliente, cuando este Cliente se elimine
	//** mapped by - sirve para indicar con qué atributo de la otra clase se relaciona este atributo lista de facturas. 
	//Aqui se crea la llave foranea en la clase Factura!!
	//orphanRemoval es para quitar registros huerfanos que3 no esten asociados a ningun cliente
	
	//@JsonIgnore//con este ignore solucionamos el problema de referencia circular entre clientes y facturas. Es como un transient!!
	//El problema es que al ignorar las facturas, estas no se serializarán y queremos que sí lo hagan. Para solucionar esto usamos con
	//JsonManagerReference y JsonBackReference 
	
	@JsonManagedReference
	private List<Factura> facturas;
	
	
	private String foto;
	
	//Este metodo es un callback del ciclo de vida de este objeto manejado por el EntityManager
	//será invocado por el em justo antes de insertar en la BD
//	@PrePersist
//	public void prePersist() {
//		createAt = new Date();
//	}
	
	
	// == Constructors ==
	
	public Cliente() {
		facturas = new ArrayList<>();
	}
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}
	
	public void addFactura(Factura factura) {
		facturas.add(factura);
	}



	@Override
	public String toString() {
		return  nombre + " " + apellido;
	}
	
	

}
