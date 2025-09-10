package damnosol.triggeriq.erp.infra.storage;

import damnosol.triggeriq.erp.core.application.port.out.FileStoragePort;
import damnosol.triggeriq.erp.core.domain.model.Document;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileSystemStorageAdapter implements FileStoragePort {

    private final Path storageRoot;

    public FileSystemStorageAdapter(Path storageRoot) {
        this.storageRoot = storageRoot;
    }

    @Override
    public List<Document> storeFiles(List<File> files, Long productId, Long inventoryId) {
        List<Document> storedDocs = new ArrayList<>();
        for (File file : files) {
            try {
                Path target = storageRoot.resolve(file.getName());
                Files.copy(file.toPath(), target);
                storedDocs.add(new Document(
                        null,
                        file.getName(),
                        Files.probeContentType(target),
                        Files.readAllBytes(target),
                        LocalDateTime.now(),
                        productId,
                        inventoryId
                ));
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file " + file.getName(), e);
            }
        }
        return storedDocs;
    }

    public Path saveFile(String filename, byte[] content) throws IOException {
        Path destination = storageRoot.resolve(filename);
        Files.write(destination, content);
        return destination;
    }
}