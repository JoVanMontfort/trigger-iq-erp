package damnosol.triggeriq.erp.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class StorageConfig {

    @Bean
    public Path storagePath(@Value("${storage.path}") String path) {
        return Path.of(path).toAbsolutePath().normalize();
    }
}