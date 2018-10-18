package org.scutsu.market.storage;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    UploadResult store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    String loadAsResource(String filename);

    void deleteAll();

}
