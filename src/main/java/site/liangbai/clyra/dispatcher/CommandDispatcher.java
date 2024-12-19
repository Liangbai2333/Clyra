package site.liangbai.clyra.dispatcher;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import site.liangbai.clyra.annotation.CommandParam;
import site.liangbai.clyra.boot.properties.ClyraProperties;
import site.liangbai.clyra.bus.CommandBus;
import site.liangbai.clyra.chatmodel.ChatModelEngine;
import site.liangbai.clyra.di.InjectSourceProvider;
import site.liangbai.clyra.dto.CommandStructure;
import site.liangbai.clyra.registry.CommandHandlerResolver;
import site.liangbai.clyra.utils.RegexUtils;
import site.liangbai.clyra.utils.StringUtils;
import site.liangbai.clyra.utils.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
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
            if (command.isEmpty()) {
                log.debug("empty command to execute");
                return DispatchResult.INVALID;
            }
        } else if (isChatMode && command.startsWith(clyraProperties.getChatModelCommandPrefix())) {
            command = StringUtils.removePrefix(command, clyraProperties.getChatModelCommandPrefix());
            if (command.isEmpty()) {
                log.debug("empty message to transform");
                return DispatchResult.INVALID;
            }
            String textOriginal = command;
            command = chatModelEngine.transformCommand(command);
            if (command.equalsIgnoreCase("null")) {
                log.debug("no command found by message: {}", textOriginal);
                return DispatchResult.NOT_FOUND_COMMAND;
            } else if (command.equalsIgnoreCase("busy")) {
                log.debug("executing too many commands at one time by message: {}", textOriginal);
                return DispatchResult.BUSY;
            }
        } else {
            log.debug("invalid command format: {}", command);
            return DispatchResult.INVALID;
        }

        return internalDispatch(command, injectSourceProviders);
    }

    private DispatchResult internalDispatch(String originalCommand, Collection<InjectSourceProvider> injectSourceProviders) {
        CommandHandlerResolver resolver = CommandBus.matchCommandWithAllArgs(originalCommand);
        if (resolver == null) {
            log.debug("a command with a suspected conversion error was encountered： {}", originalCommand);
            return DispatchResult.NOT_FOUND_COMMAND;
        }
        String argsWithNode = StringUtils.trimStart(StringUtils.removePrefix(originalCommand, resolver.getCommandAnno().value()));
        String node = resolver.parseNode(argsWithNode); // TODO ROOT NODE
        if (node == null) {
            log.debug("a command with a suspected conversion error was encountered： {}", originalCommand);
            return DispatchResult.NOT_FOUND_NODE;
        }
        List<String> args = new ArrayList<>(Arrays.asList(StringUtils.trimStart(StringUtils.removePrefix(argsWithNode, node)).split(" ")));
        Parameter[] parameters = resolver.getParametersByNode(node);
        List<Object> argsWithOrder = new ArrayList<>();
        Arrays.stream(parameters)
                .filter(it -> it.isAnnotationPresent(CommandParam.class))
                .forEach(it -> {
                    if (args.isEmpty()) {
                        log.warn("missing argument: {}", it.getName());
                        return;
                    }
                    Class<?> type = it.getType();
                    String arg = args.remove(0);
                    Object o;
                    try {
                        o = TypeUtils.convertToPrimitiveType(type, arg);
                    } catch (Exception ex1) {

                        try {
                            log.debug("argument type convert failed: {}, try to fix by algorithm", arg);
                            o = TypeUtils.convertToPrimitiveType(type, RegexUtils.fixParameter(arg));
                        } catch (Exception ignored) {
                            log.warn("unsupported argument ({}), try to raise an issue", arg);
                            o = null;
                        }
                    }
                    argsWithOrder.add(o);
                });

        Map<String, Object> injectParameters = new HashMap<>();
        injectSourceProviders.forEach(it -> injectParameters.putAll(it.getInjectSources(originalCommand)));

        try {
            resolver.resolveCommand(new CommandStructure(node, argsWithOrder), injectParameters);
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
            log.warn("method invoke exception occurred: {}", e.getMessage());
            return DispatchResult.FAIL;
        }

        return DispatchResult.SUCCESS;
    }
}
