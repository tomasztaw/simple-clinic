package pl.taw.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "doctors.descriptions")
public class AppConfig {

    // to było dodane dla obsługi opisów lekarzy, pewnie będzie do usunięcia !!!

    private String folder;

    private String prefix;


}
