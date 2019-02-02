package org.scutsu.market.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

	StoreResult store(MultipartFile file) throws IOException, InvalidFileNameException;

	Resource loadAsResource(String filename);

}
