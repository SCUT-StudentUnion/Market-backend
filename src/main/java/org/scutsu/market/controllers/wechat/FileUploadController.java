package org.scutsu.market.controllers.wechat;

import org.scutsu.market.repositories.PhotoRepository;
import org.scutsu.market.storage.StorageService;
import org.scutsu.market.storage.StorageServiceUnit;
import org.scutsu.market.storage.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/wechat")
@RestController
@Slf4j
public class FileUploadController {

	private StorageService storageService;

	private PhotoRepository photoRepository;

	@Autowired
	public FileUploadController(StorageServiceUnit storageServiceUnit) {
		this.storageService=storageServiceUnit;
	}
	
	@GetMapping("/files/{id}")
	public ResponseEntity<String> ServeFile(@PathVariable Long id) {
		
		String filename=photoRepository.findById(id).get().getFileName();
		String uri = storageService.loadAsResource(filename);
		
		return ResponseEntity.ok().body(uri);
	}
	
	
	@PostMapping("/uploadfile")
	public ResponseEntity<UploadResult> handleUploadFile(@RequestParam("file") MultipartFile file) {
	if(file.isEmpty()) {
		log.error("上传的图片不存在");
			return ResponseEntity.status(400).body(new UploadResult(null));
		}
		return ResponseEntity.status(201).body(storageService.store(file));
	}
	
}
