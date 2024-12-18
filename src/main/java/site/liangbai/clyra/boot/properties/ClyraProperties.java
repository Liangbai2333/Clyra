package site.liangbai.clyra.boot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "clyra")
public class ClyraProperties {
    private Boolean enableChatModelCommand = false;

    private String chatModelCommandPrefix = "#";

    private Boolean enableOriginalCommand = true;

    private String originalCommandPrefix = "/";
}
