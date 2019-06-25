package com.pedro.springboot.app.view.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pedro.springboot.app.models.entity.Cliente;

@XmlRootElement(name="clientes")
public class ClienteList {
	
	//Se crea esta clase Wrapper que engloba a la lista de clientes a seer serializados
	//Esto es así porque el marshaller no puede serializar directamente una collection pero sí un objeto
	//y por eso nuestro objeto encapsula a la lista real!!
	
	@XmlElement(name="cliente")
	//RECONTRA GUARDIA!!!  en la clase Factura hemos anotado como transient al atributo cliente para que al momento de hacer el marshall
	//no se genere una referencia circular cliente -> facturas -> cada factura -> cliente otra vez , y así infinitamente!!!
	List<Cliente> clientes;
	
	public ClienteList() {
		
	}
	
    public ClienteList(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}
	
	

}
