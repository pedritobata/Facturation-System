package com.pedro.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.pedro.springboot.app.controllers.ClienteController;

@Service
public class UploadFileServiceImpl implements IUploadFileService {
	
	private final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
	private final String UPLOADS_FOLDER = "uploads";
	

	@Override
	public Resource load(String filename) throws MalformedURLException {
		Path pathFoto = getPath(filename);
		log.info("Path foto : " + pathFoto);
		Resource recurso = null;
	
			recurso = new UrlResource(pathFoto.toUri());
			if(!recurso.exists() || !recurso.isReadable()) {
				throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
			}
	
		return recurso;
	}

	@Override
	public String copy(MultipartFile file) throws IOException{

		String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootPath = getPath(uniqueFileName);
		
		log.info("rootPath: " + rootPath);//path relativo

			Files.copy(file.getInputStream(), rootPath);
		
		
		return uniqueFileName;
	}

	@Override
	public boolean delete(String filename) {
		
		Path rootPath = getPath(filename);
		File archivo = rootPath.toFile();
		
		if(archivo.exists() && archivo.canRead())
		{
			if(archivo.delete()) {
				return true;
			}
		}		
		
		return false;
	}
	
	public Path getPath(String filename) {
		return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();//Path absoluto del archivo dentro del proyecto dentro del servidor
	}

	//Metodo para borrar el directorio uploads que está en la raiz de mi proyecto
	//y tambien todas las imagenes , para arrancar siempre desde cero
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
	}

	//Metodo para crear la carpeta uploads cada vez que arranque la app
	@Override
	public void init() throws IOException {
		Files.createDirectory(Paths.get(UPLOADS_FOLDER));
	}

}
