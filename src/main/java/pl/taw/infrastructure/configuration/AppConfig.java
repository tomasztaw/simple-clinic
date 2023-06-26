package pl.taw.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "doctors.descriptions")
public class AppConfig {

    private String folder;

    private String prefix;


}
