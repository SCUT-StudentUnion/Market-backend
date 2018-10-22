package org.scutsu.market.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
	
    public UploadResult store(MultipartFile file);
    public Resource loadAsResource(String filename) throws IOException;

}
