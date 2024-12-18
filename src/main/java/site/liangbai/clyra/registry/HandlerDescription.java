package site.liangbai.clyra.registry;

import site.liangbai.clyra.annotation.Command;
import site.liangbai.clyra.annotation.CommandHandler;
import site.liangbai.clyra.annotation.CommandParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public record HandlerDescription(Command commandAnno, Collection<CommandHandler> commandHandlers, Map<CommandHandler, List<CommandParam>> commandParams) {
}
