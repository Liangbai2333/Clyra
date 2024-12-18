package site.liangbai.clyra.chatmodel.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import site.liangbai.clyra.annotation.Command;
import site.liangbai.clyra.annotation.CommandParam;
import site.liangbai.clyra.bus.CommandBus;
import site.liangbai.clyra.chatmodel.pojo.Model;
import site.liangbai.clyra.chatmodel.pojo.Node;
import site.liangbai.clyra.chatmodel.pojo.NodeList;
import site.liangbai.clyra.chatmodel.service.ParamService;
import site.liangbai.clyra.chatmodel.service.PromptService;
import site.liangbai.clyra.registry.CommandHandlerResolver;
import site.liangbai.clyra.registry.HandlerDescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PromptServiceImpl implements PromptService {
    private final ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SnakeCaseStrategy.INSTANCE);
    @Resource
    private ParamService paramService;

    @Override
    public String generateModelList() {
        Collection<CommandHandlerResolver> handlerResolvers = CommandBus.getHandlers();
        List<Model> models = new ArrayList<>();
        handlerResolvers.forEach(handler -> {
            Command command = handler.getCommandAnno();
            models.add(new Model(command.value(), command.description()));
        });
        try {
            return objectMapper.writeValueAsString(models);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateCommandDetails(String command) {
        CommandHandlerResolver commandHandlerResolver = CommandBus.get(command);
        HandlerDescription description = commandHandlerResolver.getDescription();
        List<Node> nodes = description.commandHandlers()
                        .stream()
                        .map(it -> {
                            List<CommandParam> params = description.commandParams().get(it);

                            return new Node(it.value(),
                                    paramService.createArgsStr(params),
                                    paramService.createArgsInfo(params),
                                    it.description());
                        })
                        .toList();
        NodeList nodeList = new NodeList(nodes);
        try {
            return objectMapper.writeValueAsString(nodeList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
