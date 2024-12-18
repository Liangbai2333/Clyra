package site.liangbai.clyra.bus;

import site.liangbai.clyra.registry.CommandHandlerResolver;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandBus {
    private static final Map<String, CommandHandlerResolver> commandHandlers = new ConcurrentHashMap<>();

    public static void register(String command, CommandHandlerResolver commandHandlerResolver) {
        commandHandlers.put(command, commandHandlerResolver);
    }

    public static CommandHandlerResolver get(String command) {
        return commandHandlers.get(command);
    }

    public static CommandHandlerResolver matchCommandWithAllArgs(String command) {
        return commandHandlers.values().stream().filter(it -> command.startsWith(it.getCommandAnno().value()))
                .max(Comparator.comparingInt(a -> a.getCommandAnno().value().length()))
                .orElse(null);
    }

    public static Collection<CommandHandlerResolver> getHandlers() {
        return commandHandlers.values();
    }
}
