package com.pedro.springboot.app.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pedro.springboot.app.models.dao.IClienteDao;
import com.pedro.springboot.app.models.entity.Cliente;
import com.pedro.springboot.app.models.service.ClienteServiceImpl;
import com.pedro.springboot.app.models.service.IClienteService;
import com.pedro.springboot.app.models.service.IUploadFileService;
import com.pedro.springboot.app.models.service.UploadFileServiceImpl;
import com.pedro.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/*
	@Autowired
	@Qualifier("ClienteDaoJPA")//usamos esta anotacion para que la inyeccion por tipo no sea ambigua
	//debido a que estamos esperando una interface y varias clases podrían ser candidatas a ser inyectadas
	//ClienteDaoJPA es el nombre que le hemos especificado al @Repository que queremos inyectar
	private IClienteDao clienteDao;
	*/
	
	//Voy a inyectar el servicio en vez del DAO
	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileServiceImpl;
	
	@Autowired
	public MessageSource messageSource;

	// private final Logger log =
	// org.slf4j.LoggerFactory.getLogger(ClienteController.class);

	@Secured(value= {"ROLE_USER"}) //el value puede ser tambien un ARREGLO DE PERFILES separados por comas o un valor unico
	@GetMapping(value = "/upload/{filename:.+}") // Con la expresion regular ".+" Spring NO truncará la extension del
													// archivo
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		/*
		 * Path pathFoto =
		 * Paths.get("uploads").resolve(filename).toAbsolutePath();//Path absoluto del
		 * archivo dentro del proyecto dentro del servidor log.info("Path foto : " +
		 * pathFoto); Resource recurso = null; try { recurso = new
		 * UrlResource(pathFoto.toUri()); if(!recurso.exists() || !recurso.isReadable())
		 * { throw new RuntimeException("Error: no se puede cargar la imagen: " +
		 * pathFoto.toString()); } } catch (MalformedURLException e) { 
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		Resource recurso = null;
		try {
			recurso = uploadFileServiceImpl.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);

	}

	@PreAuthorize("hasRole('ROLE_USER')")//Puedo usar esta anotacion en lugar de @Secured
	//Con el metodo hasAnyRole() podría validar MAS DE UN PERFIL , separados por comas 
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Cliente cliente = clienteService.fetchByIdWithFacturas(id);// clienteService.findOne(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la BD");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Detalle cliente : " + cliente.getNombre());

		return "ver";
	}

	@RequestMapping(value = { "/listar", "/" }, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication, HttpServletRequest request, Locale locale) {

		if (authentication != null) {
			logger.info("Hola usuario autenticado, tu username es : ".concat(authentication.getName()));
		}
		
		//Otra forma de obtener la autenticación usando metodo estático de SecurityContextHolder
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null) {
			logger.info("Utilizando el contexto=> Hola usuario autenticado, tu username es : ".concat(authentication.getName()));
		}
		
		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso"));
		}else {
			logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso"));
		}
		
		//otra forma de validar el rol, usdando el objeto request
		//El segundo argumento es un prefijo que queramos usar para buscar, en nuestro caso todos nuestros roles
		//los hemos hecho comenzar con ROLE_
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
		if(securityContext.isUserInRole("ADMIN")) {
			logger.info("Usando SecurityContextHolderAwareRequestWrapper : Hola ".concat(auth.getName()).concat(" tienes acceso"));
		}
		
		//otra forma de validar el rol, usdando el objeto request
		if(request.isUserInRole("ADMIN")) {
			logger.info("Usando objeto request : Hola ".concat(auth.getName()).concat(" tienes acceso"));
		}

		// Usando paginacion
		// Creo un page request con la cantidad de paginas?? y la cantidad de registros
		// por pagina que quiero mostrar??
		Pageable pageRequest = new PageRequest(page, 5);

		Page<Cliente> clientes = clienteService.findAll(pageRequest);

		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		model.addAttribute("page", pageRender);
		//Esta es la forma de obtener un valor de los properties de i18N programáticamente
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo", null, locale));
		// model.addAttribute("clientes",clienteService.findAll());
		model.addAttribute("clientes", clientes);
		return "listar";
	}
	
	
	@GetMapping(value ="/listar-rest")
	//este metodo devolverá una respuesta Rest
	//Ojo que esta url debe ser validad por Spring security, para lo cual hemos utilizado listar** en la configuracion del permitAll()
	//@ResponseBody convierte cualquier POJO que tenga metodos getters y setters , en un formato Rest (json o xml)
	public @ResponseBody List<Cliente> listarRest() {
		
		return clienteService.findAll();
		//si quiero acceder a la respuesta Rest en formato xml debo usar la url : listar-rest?format=xml
		//pero para eso este metodo debería rertornar una clase Wraper ClienteList
		// return new ClienteList(clienteService.findAll());
		
	}

	@Secured(value="ROLE_ADMIN")
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model) {
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		return "form";
	}

	// Pasamos un path variable
	@RequestMapping(value = "/form/{id}")
	@Secured(value="ROLE_ADMIN")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Cliente cliente = null;
		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "El id del cliente no existe en la BD!");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El id del cliente no puede ser cero!");
			return "redirect:/listar";
		}

		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		return "form";
	}

	@Secured(value="ROLE_ADMIN")
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	// Uso BindingResult para la validacion del formulario
	// Esta validacion se ha configurado con anotaciones en la clase Cliente.java
	// Se tiene que anotar con @Valid al objeto back Bean que se está validando
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}

		if (!foto.isEmpty()) {
//			Path directorioRecursos = Paths.get("src//main//resources//static//upload");
//			String rootPath = directorioRecursos.toFile().getAbsolutePath();

			// Para usar rutas a las fotos que estén en el servidor , FUERA del proyecto
			// String rootPath = "C:\\programacion\\practicas\\spring\\andres\\uploads";

			// si quiero reemplazar la foto del cliente por una nueva, primero valido que si
			// ya tiene foto, esta se elimine
			// y permanezca solo la nueva
			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {
//				Path rootPath = Paths.get("uploads").resolve(cliente.getFoto()).toAbsolutePath();
//				File archivo = rootPath.toFile();
//				
//				if(archivo.exists() && archivo.canRead())
//				{
//					archivo.delete();
//				}	
//				
//				flash.addFlashAttribute("info","Se ha subido correctamente '" + uniqueFileName + "'");
//				cliente.setFoto(uniqueFileName);

				uploadFileServiceImpl.delete(cliente.getFoto());

			}

			String uniqueFileName = null;

			try {
				uniqueFileName = uploadFileServiceImpl.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", "Se ha subido correctamente '" + uniqueFileName + "'");
			cliente.setFoto(uniqueFileName);

			/*
			 * //Ahora uso una ruta absoluta dentro del proyecto, No del servidor //creo un
			 * identificador unico para cada imagen que quiera subir String uniqueFileName =
			 * UUID.randomUUID().toString() + "_" + foto.getOriginalFilename(); //creo el
			 * sufijo. Este Path se ubicará EN LA RAIZ DEL PROYECTO!! por default gracias a
			 * Paths.get Path rootPath = Paths.get("uploads").resolve(uniqueFileName);
			 * //defino la ruta absoluta Path rootAbsolutePath = rootPath.toAbsolutePath();
			 * 
			 * log.info("rootPath: " + rootPath);//path relativo
			 * log.info("rootAbsolutePath: " + rootAbsolutePath);//path absoluto hacia el
			 * proyecto dentro del servidor, //lo cual es diferente a un path hacia el war
			 * desplegado en el servidor!!
			 * 
			 * try { // byte[] bytes = foto.getBytes(); // Path rutaCompleta =
			 * Paths.get(rootPath + "//" + foto.getOriginalFilename()); //
			 * Files.write(rutaCompleta, bytes);
			 * 
			 * //Esta es otra forma de copiar un file Files.copy(foto.getInputStream(),
			 * rootAbsolutePath);
			 * flash.addFlashAttribute("info","Se ha subido correctamente '" +
			 * uniqueFileName + "'"); cliente.setFoto(uniqueFileName); } catch (IOException
			 * e) { e.printStackTrace(); }
			 */
		}

		String mensajeFlash = cliente.getId() != null ? "Cliente editado con éxito!" : "Cliente creado con éxito!";

		clienteService.save(cliente);
		// elimino la session del @SessionAttributes, porque ya guardé un cliente y ya
		// no quiero
		// que sus datos estén disponibles para ser capturados en la vista
		status.setComplete();
		// RedirectAttributes redirige los atributos a otro handler
		// ya que este handler está haciendo eso justamente!! redirect:/listar
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/listar";// usamos redirect para invocar otro RequestMapping o controller
	}

	@Secured(value="ROLE_ADMIN")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);

			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito!");

//			Path rootPath = Paths.get("uploads").resolve(cliente.getFoto()).toAbsolutePath();
//			File archivo = rootPath.toFile();
//			
//			if(archivo.exists() && archivo.canRead())
//			{
//				if(archivo.delete()) {
//					flash.addFlashAttribute("info", "Foto :" + cliente.getFoto() + " eliminada con éxito!");
//				}
//			}	

			if (uploadFileServiceImpl.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto :" + cliente.getFoto() + " eliminada con éxito!");
			}

		}

		return "redirect:/listar";
	}
	
	
	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		if(context == null) {
			return false;
		}
		
		Authentication auth = context.getAuthentication();
		if(auth == null) {
			return false;
		}
		
		//cualquier clase implemente GrantedAuthority en Spring Security será un rol
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		/*for(GrantedAuthority authority : authorities) {
			if(role.equals(authority.getAuthority())){
				logger.info("Hola ".concat(auth.getName()).concat(" tu rol es : ").concat(authority.getAuthority()));
				return true;
			}
		}
		return false;
		*/
		
		//Otra forma de validar si el usuario tiene el rol que buscamos
		return authorities.contains(new SimpleGrantedAuthority(role));
		
		
	}

}
