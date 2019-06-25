package com.pedro.springboot.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.pedro.springboot.app.models.entity.Cliente;
import com.pedro.springboot.app.models.service.IClienteService;

@RestController//para esta anotacion la clase se convierte en un controlador que sólo responde Rest
//Se combinan por defecto las anotaciones @Controller y @ResponseBody
@RequestMapping("/api/clientes")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;

	@GetMapping(value ="/listar")
	public List<Cliente> listar() {
		return clienteService.findAll();
	}

}
