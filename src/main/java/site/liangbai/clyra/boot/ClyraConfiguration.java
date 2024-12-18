package site.liangbai.clyra.boot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import site.liangbai.clyra.boot.properties.ClyraProperties;
import site.liangbai.clyra.registry.CommandRegistry;

@Configuration
@Import({CommandRegistry.class})
@EnableConfigurationProperties(ClyraProperties.class)
public class ClyraConfiguration {
}
