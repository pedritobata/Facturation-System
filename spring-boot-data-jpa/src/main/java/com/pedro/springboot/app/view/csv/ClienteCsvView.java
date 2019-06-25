package com.pedro.springboot.app.view.csv;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.pedro.springboot.app.models.entity.Cliente;

@Component("listar.csv")
public class ClienteCsvView extends AbstractView{
	

	public ClienteCsvView() {
		setContentType("text/csv");
	}

	@Override
	protected boolean generatesDownloadContent() {
		// Acá decimos que esta clase genera un descargable y no una vista en sí
		return true;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"clientes.csv\"");
		response.setContentType(getContentType());
		
		@SuppressWarnings("unchecked")
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
		
		//el beanWriter transforma un Bean a una linea dentro del archivo plano csv
		//solo hay que especificarle qué atributos pintar
		ICsvBeanWriter beanWriter = new CsvBeanWriter(response.getWriter(),CsvPreference.STANDARD_PREFERENCE);
		//los atributos que le especifico en el arreglo tienen que llamarse IGUALITO a como se llaman en la clase
		String[] header = {"id","nombre", "apellido", "email", "createAt"};
		beanWriter.writeHeader(header);
		for(Cliente cliente : clientes) {
			beanWriter.write(cliente,header);
		}
		
		beanWriter.close();
		
	}
	
	

}
