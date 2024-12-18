package site.liangbai.clyra.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.type.AnnotationMetadata;
import site.liangbai.clyra.annotation.Command;
import site.liangbai.clyra.bus.CommandBus;

@Slf4j
public class CommandRegistry implements BeanPostProcessor, BeanFactoryAware {
    private ConfigurableListableBeanFactory factory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.factory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(factory.getBeanDefinition(beanName) instanceof AnnotatedBeanDefinition definition) {
            AnnotationMetadata metadata = definition.getMetadata();

            if (metadata.hasAnnotation(Command.class.getName())) {
                CommandHandlerResolver resolver = new CommandHandlerResolver(bean, factory, bean.getClass().getDeclaredMethods());
                CommandBus.register(resolver.getDescription().commandAnno().value(), resolver);
            }
        }

        return bean;
    }
}
