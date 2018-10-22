package org.scutsu.market.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.scutsu.market.models.Photo;
import org.scutsu.market.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
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
	private String uri;
	
	@NotNull
	private String dir;

}

@Component
@Slf4j
public class StorageServiceUnit implements StorageService{
	
	private final PhotoRepository photoRepository;

	private final UploadProperties config;
	
	@Autowired
	public StorageServiceUnit(UploadProperties config,PhotoRepository photoRepository) {
		this.config=config;
		this.photoRepository=photoRepository;
	}
	
	@Override
	public UploadResult store(MultipartFile file) {

		String fileName;
		do {
			fileName=getRandomString(10);
		}
		while(photoRepository.findByFileName(fileName)!=null);
		
		Photo photo = new Photo();
		photo.setFileName(fileName);
		photoRepository.save(photo);
		log.info("上传的文件名为："+fileName);
		
		String UploadDir=config.getDir();
		String UploadUri=config.getUri();
		File dest=new File(UploadDir+fileName);
		
		if(!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		try {
			file.transferTo(dest);
			log.info("上传成功后的文件路径： "+UploadDir+fileName);
			return new UploadResult(UploadUri+fileName);
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
	public Resource loadAsResource(String filename) throws IOException {
		String photo_path=config.getDir()+filename;
		Resource resource = new FileSystemResource(photo_path);
		
		return resource;
	}

	public static String getRandomString(int length){
	     String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random=new Random();
	     StringBuffer sb=new StringBuffer();
	     for(int i=0;i<length;i++){
	       int number=random.nextInt(62);
	       sb.append(str.charAt(number));
	     }
	     return sb.toString();
	 }
}
