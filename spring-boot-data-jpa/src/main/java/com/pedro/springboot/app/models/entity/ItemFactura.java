package com.pedro.springboot.app.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="facturas_items")
public class ItemFactura implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Integer cantidad;
	
	@ManyToOne(fetch=FetchType.LAZY)//al usar LAZY  hay problema para serializar a json las facturas
	//debido a que con Lazy spring usa un bean proxy de Producto el cual tiene otros atributos extra que no existen en el Producto original
	//y no tienen como serializaarse. Para solucionar esto se pudo usar EAGER pero no es eficiente ya que siempre se estarían haciendo los
	//querys a producto cada vez que se invoca a las facturas y eso no lo queremos siempre. La solucion es usar @JsonIgnoreProperties para que se ignoren
	//los atributos específicos que creó spring en el proxy y que estén causando el error al serializar
	//OJO que @JsonIgnoreProperties tambien se puede colocar como decorador en la misma clase Producto , es decir , en la definicion del Bean
	@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
	@JoinColumn(name="producto_id")//No es obligatorio poner el JoinColumn, ya que jpa crea automaticamente una
	//llave foranea en esta tabla facturas_items llamada igual : "producto_id"
	private Producto producto;
	
		

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	public Double calcularImporte() {
		return cantidad.doubleValue() * producto.getPrecio();
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	} 
	
	
	
}
