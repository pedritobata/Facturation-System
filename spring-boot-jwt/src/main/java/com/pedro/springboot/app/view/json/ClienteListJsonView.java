package com.pedro.springboot.app.view.json;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.pedro.springboot.app.models.entity.Cliente;

@Component("listar.json")
public class ClienteListJsonView extends MappingJackson2JsonView{
	//Para implementar vistas tipo json no es necesario agregar  ninguna dependencia aparte, con el 
	//web-starter de spring boot ya viene eso configurado por defecto
	//Ademas con json ya no necesitamos envolver nuestra lista en una clase ya que json puede serializar listas directamente

	@Override
	@SuppressWarnings("unchecked")
	protected Object filterModel(Map<String, Object> model) {
		model.remove("titulo");
		model.remove("page");
		
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
		model.remove("clientes");
		
		model.put("clientes", clientes.getContent());
		return super.filterModel(model);
	}
	
	

}
