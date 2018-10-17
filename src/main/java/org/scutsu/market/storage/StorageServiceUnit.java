package org.scutsu.market.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.imageio.IIOException;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Validated
@Component
@ConfigurationProperties(prefix = "upload")
class UploadProperties{
	
	@NotNull
	private String dir;

}

@Component
@Slf4j
public class StorageServiceUnit implements StorageService{

	private final UploadProperties config;
	
	@Autowired
	public StorageServiceUnit(UploadProperties config) {
		this.config=config;
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public UploadResult store(MultipartFile file) {
		// TODO Auto-generated method stub
		String fileName=file.getOriginalFilename();
		log.info("上传的文件名为："+fileName);
		String UploadDir=config.getDir();
		File dest=new File(UploadDir+fileName);
		
		if(!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		try {
			file.transferTo(dest);
			log.info("上传成功后的文件路径： "+UploadDir+fileName);
			return new UploadResult(UploadDir+fileName);
		}
		catch(IllegalStateException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return new UploadResult(null);
	}

	@Override
	public Stream<Path> loadAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path load(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource loadAsResource(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

}
