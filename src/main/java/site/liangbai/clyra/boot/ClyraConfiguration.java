package site.liangbai.clyra.boot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import site.liangbai.clyra.boot.properties.ClyraProperties;
import site.liangbai.clyra.chatmodel.ChatModelEngine;
import site.liangbai.clyra.chatmodel.frame.ToolTemplate;
import site.liangbai.clyra.chatmodel.service.ParamService;
import site.liangbai.clyra.chatmodel.service.PromptService;
import site.liangbai.clyra.chatmodel.service.impl.ParamServiceImpl;
import site.liangbai.clyra.chatmodel.service.impl.PromptServiceImpl;
import site.liangbai.clyra.dispatcher.CommandDispatcher;
import site.liangbai.clyra.registry.CommandRegistry;

@Configuration
@Import({CommandRegistry.class})
@EnableConfigurationProperties(ClyraProperties.class)
public class ClyraConfiguration {
    @Bean
    public CommandDispatcher commandDispatcher() {
        return new CommandDispatcher();
    }

    @Bean
    public ChatModelEngine chatModelEngine() {
        return new ChatModelEngine();
    }

    @Bean
    public PromptService promptService() {
        return new PromptServiceImpl();
    }

    @Bean
    public ParamService paramService() {
        return new ParamServiceImpl();
    }

    @Bean
    public ToolTemplate toolTemplate() {
        return new ToolTemplate();
    }
}
