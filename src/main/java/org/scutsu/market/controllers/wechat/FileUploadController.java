package org.scutsu.market.controllers.wechat;

import org.scutsu.market.storage.StorageServiceUnit;
import org.scutsu.market.storage.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/wechat")
@Controller
@Slf4j
public class FileUploadController {

public StorageServiceUnit storageServiceUnit;

@Autowired
public FileUploadController(StorageServiceUnit storageServiceUnit) {
	this.storageServiceUnit=storageServiceUnit;
}
	
@GetMapping("/files/{filename:.+}")
@ResponseBody
public ResponseEntity<Resource> ServeFile(@PathVariable String filename) {
	Resource file = storageServiceUnit.loadAsResource(filename);
	
	return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
}


@PostMapping("/uploadfile")
@ResponseBody
public UploadResult handleUploadFile(@RequestParam("file") MultipartFile file) {
	if(file.isEmpty()) {
		log.error("上传的图片不存在");
		return new UploadResult(null);
	}
	return storageServiceUnit.store(file);
}
	
}
