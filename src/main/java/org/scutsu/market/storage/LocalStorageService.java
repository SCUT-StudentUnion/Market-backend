package org.scutsu.market.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;


@Data
@Validated
@Component
@ConfigurationProperties(prefix = "upload.local")
class LocalStoreProperties {

	/**
	 * Where the uploaded files will be saved locally.
	 */
	@NotNull
	private Path directory;

	@PostConstruct
	public void createDirectory() throws IOException {
		directory = directory.toAbsolutePath();
		Files.createDirectories(directory);
	}
}

@Component
@Slf4j
public class LocalStorageService implements StorageService {

	private final LocalStoreProperties config;

	@Autowired
	public LocalStorageService(LocalStoreProperties config) {
		this.config = config;
	}

	@Override
	public StoreResult store(MultipartFile file) throws IOException, InvalidFileNameException {

		final String originFileName = file.getOriginalFilename();
		if (originFileName == null) {
			throw new InvalidFileNameException();
		}
		int i = originFileName.lastIndexOf('.');
		if (i == -1) {
			throw new InvalidFileNameException();
		}
		String extension = originFileName.substring(i);

		String fileName = UUID.randomUUID() + extension;

		Path uploadDir = config.getDirectory();
		File dest = uploadDir.resolve(fileName).toFile();

		file.transferTo(dest);
		log.info("上传成功后的文件路径：{}", dest.getPath());
		return new StoreResult(fileName);
	}

	@Override
	public Resource loadAsResource(String fileName) {
		return new FileSystemResource(config.getDirectory().resolve(fileName).toFile());
	}
}
