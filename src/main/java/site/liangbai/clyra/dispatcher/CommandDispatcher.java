package site.liangbai.clyra.dispatcher;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.liangbai.clyra.annotation.CommandParam;
import site.liangbai.clyra.boot.properties.ClyraProperties;
import site.liangbai.clyra.bus.CommandBus;
import site.liangbai.clyra.chatmodel.ChatModelEngine;
import site.liangbai.clyra.di.InjectSourceProvider;
import site.liangbai.clyra.dto.CommandStructure;
import site.liangbai.clyra.registry.CommandHandlerResolver;
import site.liangbai.clyra.utils.StringUtils;
import site.liangbai.clyra.utils.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
@Component
public class CommandDispatcher {
    @Resource
    private ClyraProperties clyraProperties;
    @Resource
    private ChatModelEngine chatModelEngine;

    public DispatchResult dispatch(String command) {
        return dispatch(command, Collections.emptyList());
    }

    public DispatchResult dispatch(String command, InjectSourceProvider... injectSourceProvider) {
        return dispatch(command, Arrays.asList(injectSourceProvider));
    }

    public DispatchResult dispatch(String command, Collection<InjectSourceProvider> injectSourceProviders) {
        return dispatch(command, injectSourceProviders, clyraProperties.getEnableChatModelCommand(), clyraProperties.getEnableOriginalCommand());
    }

    public DispatchResult dispatch(String command, Collection<InjectSourceProvider> injectSourceProviders, boolean isChatMode, boolean isOriginal) {
        if (isOriginal && command.startsWith(clyraProperties.getOriginalCommandPrefix())) {
            command = StringUtils.removePrefix(command, clyraProperties.getOriginalCommandPrefix());
        } else if (isChatMode && command.startsWith(clyraProperties.getChatModelCommandPrefix())) {
            command = StringUtils.removePrefix(command, clyraProperties.getChatModelCommandPrefix());
            command = chatModelEngine.transformCommand(command);
            if (command.equalsIgnoreCase("null")) {
                return DispatchResult.NOT_FOUND_COMMAND;
            } else if (command.equalsIgnoreCase("busy")) {
                return DispatchResult.BUSY;
            }
        } else {
            return DispatchResult.INVALID;
        }

        return internalDispatch(command, injectSourceProviders);
    }

    private DispatchResult internalDispatch(String originalCommand, Collection<InjectSourceProvider> injectSourceProviders) {
        CommandHandlerResolver resolver = CommandBus.matchCommandWithAllArgs(originalCommand);
        if (resolver == null) {
            return DispatchResult.NOT_FOUND_COMMAND;
        }
        String argsWithNode = StringUtils.trimStart(StringUtils.removePrefix(originalCommand, resolver.getCommandAnno().value()));
        String node = resolver.parseNode(argsWithNode); // TODO ROOT NODE
        if (node == null) {
            return DispatchResult.NOT_FOUND_NODE;
        }
        List<String> args = Arrays.asList(StringUtils.trimStart(StringUtils.removePrefix(argsWithNode, node)).split(" "));
        Parameter[] parameters = resolver.getParametersByNode(node);
        if (args.size() < parameters.length) {
            throw new IllegalArgumentException("invalid params");
        }
        List<Object> argsWithOrder = new ArrayList<>();
        Arrays.stream(parameters)
                .filter(it -> it.isAnnotationPresent(CommandParam.class))
                .forEach(it -> {
                    Class<?> type = it.getType();
                    argsWithOrder.add(TypeUtils.convertToPrimitiveType(type, args.remove(0)));
                });

        Map<String, Object> injectParameters = new HashMap<>();
        injectSourceProviders.forEach(it -> injectParameters.putAll(it.getInjectSources(originalCommand)));

        try {
            resolver.resolveCommand(new CommandStructure(node, argsWithOrder), injectParameters);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("resolve command failed", e);
            return DispatchResult.FAIL;
        }

        return DispatchResult.SUCCESS;
    }
}
