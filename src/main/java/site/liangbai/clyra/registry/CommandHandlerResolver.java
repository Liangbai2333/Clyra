package site.liangbai.clyra.registry;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import site.liangbai.clyra.annotation.Command;
import site.liangbai.clyra.annotation.CommandHandler;
import site.liangbai.clyra.annotation.CommandParam;
import site.liangbai.clyra.annotation.InjectSource;
import site.liangbai.clyra.di.InjectSourceProvider;
import site.liangbai.clyra.dto.CommandStructure;
import site.liangbai.clyra.exception.TypeInjectOrderException;
import site.liangbai.clyra.utils.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
public class CommandHandlerResolver {
    private final Object bean;
    private final BeanFactory factory;
    private final Method[] declaredMethods;

    private final Map<String, Method> nodes = new ConcurrentHashMap<>();
    private final Map<String, CommandHandler> commandHandlerMap = new ConcurrentHashMap<>();
    private final Map<CommandHandler, List<CommandParam>> orderedCommandParams = new ConcurrentHashMap<>();

    @Getter
    private final Command commandAnno;
    private final List<InjectSourceProvider> injectSourceProviders = new CopyOnWriteArrayList<>();

    public CommandHandlerResolver(Object bean, BeanFactory factory, Method... declaredMethods){
        this.bean = bean;
        this.factory = factory;
        this.declaredMethods = declaredMethods;
        this.commandAnno = bean.getClass().getAnnotation(Command.class);
        this.resolve();
    }

    public HandlerDescription getDescription() {
        return new HandlerDescription(commandAnno, commandHandlerMap.values(), orderedCommandParams);
    }

    private void resolve(){
        InjectSource injectSource = bean.getClass().getAnnotation(InjectSource.class);
        if (injectSource != null) {
            for (Class<? extends InjectSourceProvider> aClass : injectSource.value()) {
                try {
                    injectSourceProviders.add(aClass.getConstructor().newInstance());
                } catch (Exception e) {
                    log.error("inject source provider {} init failed", aClass.getName());
                }
            }
        }
        for (Method method : declaredMethods) {
            CommandHandler annotation = method.getAnnotation(CommandHandler.class);
            if (annotation == null) continue;

            commandHandlerMap.put(annotation.value(), annotation);
            addCommandHandler(annotation.value(), method);

            List<CommandParam> list = orderedCommandParams.computeIfAbsent(annotation, it -> new CopyOnWriteArrayList<>());
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(CommandParam.class)) {
                    CommandParam commandParam = parameter.getAnnotation(CommandParam.class);
                    list.add(commandParam);
                }
            }
        }
    }

    public String parseNode(String args) {
        return commandHandlerMap.values().stream().filter(it -> args.startsWith(it.value()))
                .max(Comparator.comparingInt(a -> a.value().length()))
                .map(CommandHandler::value)
                .orElse(null);
    }

    public Parameter[] getParametersByNode(String node) {
        Method method = nodes.get(node);
        if (method == null) return null;

        return method.getParameters();
    }

    private void addCommandHandler(String node, Method method){
        nodes.put(node, method);
    }

    public boolean resolveCommand(CommandStructure command, Map<String, Object> injectParameters) throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        return invokeCommandMethod(command.getNode(), injectParameters, command.getData());
    }

    private boolean invokeCommandMethod(String node, Map<String, Object> injectParameters, List<Object> argsWithOrder) throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        if (!nodes.containsKey(node)) return false;
        Method method = nodes.get(node);
        Parameter[] parameters = method.getParameters();
        Object[] objects = new Object[parameters.length];
        Map<Class<?>, Object> classObjectMap = injectParameters.values().stream().collect(Collectors.toMap(Object::getClass, o -> o));

        // 一次注入依赖
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (injectParameters.containsKey(parameter.getName())) {
                objects[i] = injectParameters.get(parameter.getName());
            } else {
                Class<?> type = parameter.getType();
                if (classObjectMap.containsKey(type)) {
                    objects[i] = classObjectMap.get(type);
                }
            }
        }
        // 二次注入参数
        for (int i = 0; i < parameters.length; i++) {
            if (argsWithOrder.isEmpty()) break;
            if (objects[i] != null) continue;
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            if (!TypeUtils.isPrimitiveOrWrapper(type)) {
                log.warn("command {} args without dependency inject should be primitive type: {}", node, parameter.getName());
                continue;
            }
            Object injectObject = argsWithOrder.remove(0);
            if (injectObject == null) {
                log.warn("command {} args required but accept null: {}", node, parameter.getName());
                continue;
            }
            // 处理基础类型和包装类之间的匹配
            if (type.isPrimitive()) {
                // 如果目标类型是基础类型，则检查 injectObject 是否是对应的包装类
                if (injectObject.getClass() == TypeUtils.wrap(type)) {
                    objects[i] = injectObject;  // 装箱对象赋值
                } else if (injectObject.getClass().isAssignableFrom(type)) {
                    objects[i] = injectObject;  // 基础类型赋值
                } else throw new TypeInjectOrderException("Inject error occurred");
            } else {
                // 如果目标类型是包装类，直接匹配
                if (type.isAssignableFrom(injectObject.getClass())) {
                    objects[i] = injectObject;
                } else throw new TypeInjectOrderException("Inject error occurred");
            }
        }
        method.invoke(bean, objects);
        return true;
    }
}
