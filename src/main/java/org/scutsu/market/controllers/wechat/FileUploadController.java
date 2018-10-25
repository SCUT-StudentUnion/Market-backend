package org.scutsu.market.controllers.wechat;

import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.scutsu.market.models.Photo;
import org.scutsu.market.repositories.PhotoRepository;
import org.scutsu.market.storage.InvalidFileNameException;
import org.scutsu.market.storage.StorageService;
import org.scutsu.market.storage.StoreResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Data
@Component
@ConfigurationProperties(prefix = "upload")
class UploadProperties {

	/**
	 * URI that the uploaded files will be served. leave null to serve uploaded files automatically.
	 */
	private URI uri;
}

@Component
class PhotoUriGenerator {

	private final UploadProperties uploadProperties;

	@Autowired
	public PhotoUriGenerator(UploadProperties uploadProperties) {
		this.uploadProperties = uploadProperties;
	}

	public URI generateUri(Photo photo) {

		URI baseUri = uploadProperties.getUri();
		if (baseUri == null) {
			try {
				Method method = FileUploadController.class.getMethod("serveFile", String.class);
				return linkTo(method, photo.getFileName()).toUri();
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("should never happen", e);
			}
		} else {
			return baseUri.resolve(photo.getFileName());
		}
	}
}

@RequestMapping("/wechat")
@RestController
@Slf4j
public class FileUploadController {

	private final StorageService storageService;
	private final PhotoRepository photoRepository;
	private final RepositoryEntityLinks entityLinks;
	private final PhotoUriGenerator photoUriGenerator;

	@Autowired
	public FileUploadController(StorageService storageService, PhotoRepository photoRepository, RepositoryEntityLinks entityLinks, PhotoUriGenerator photoUriGenerator) {
		this.storageService = storageService;
		this.photoRepository = photoRepository;
		this.entityLinks = entityLinks;
		this.photoUriGenerator = photoUriGenerator;
	}

	@GetMapping("/files/{fileName}")
	public ResponseEntity<Resource> serveFile(@PathVariable String fileName) throws IOException {

		Resource resource = storageService.loadAsResource(fileName);
		MediaType mimeType = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));
		return ResponseEntity.ok().contentType(mimeType).body(resource);
	}

	@PostMapping("/uploadfile")
	public ResponseEntity handleUploadFile(@RequestParam("file") MultipartFile file) throws IOException, URISyntaxException {
		if (file.isEmpty()) {
			log.error("上传的图片不存在");
			return ResponseEntity.badRequest().body("未找到上传的文件");
		}

		try {
			StoreResult result = storageService.store(file);
			Photo newPhoto = new Photo();
			newPhoto.setFileName(result.getFileName());
			newPhoto = photoRepository.save(newPhoto);
			URI entityUri = new URI(entityLinks.linkToSingleResource(Photo.class, newPhoto.getId()).getHref());

			return ResponseEntity.created(entityUri).body(new UploadResult(entityUri, photoUriGenerator.generateUri(newPhoto)));
		} catch (InvalidFileNameException e) {
			return ResponseEntity.badRequest().body("无效的文件名");
		}
	}

	@Value
	private class UploadResult {
		URI entityUri;
		URI photoUri;
	}
}
