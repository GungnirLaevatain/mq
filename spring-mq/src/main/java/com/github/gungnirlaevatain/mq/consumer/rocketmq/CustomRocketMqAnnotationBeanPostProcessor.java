
package com.github.gungnirlaevatain.mq.consumer.rocketmq;

import com.github.gungnirlaevatain.mq.consumer.ConsumerUtil;
import com.github.gungnirlaevatain.mq.consumer.CustomBeanPostProcessor;
import com.github.gungnirlaevatain.mq.consumer.MqListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CustomRocketMqAnnotationBeanPostProcessor implements BeanPostProcessor, CustomBeanPostProcessor {

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    @Setter
    private RocketMqMessageDeserialize messageDeserialize;

    @Autowired
    private RocketMqListenerEndpointRegistry rocketMQListenerEndpointRegistry;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!nonAnnotatedClasses.contains(bean.getClass())) {
            Map<Method, Set<MqListener>> annotatedMethods = ConsumerUtil.getAnnotatedMethods(bean);
            if (annotatedMethods.isEmpty()) {
                nonAnnotatedClasses.add(bean.getClass());
                if (log.isTraceEnabled()) {
                    log.trace("No @RocketMQListener annotations found on bean type: " + bean.getClass());
                }
            } else {
                // Non-empty set of methods
                for (Map.Entry<Method, Set<MqListener>> entry : annotatedMethods.entrySet()) {
                    Method sourceMethod = entry.getKey();
                    Method methodToUse = ConsumerUtil.checkProxy(sourceMethod, bean);
                    for (MqListener listener : entry.getValue()) {
                        //由自定义的消息处理者进行消息的代理转发
                        RocketMqMessageHandler handler = new RocketMqMessageHandler(bean, methodToUse, messageDeserialize);
                        rocketMQListenerEndpointRegistry.addConsumer(listener, handler);
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug(annotatedMethods.size() + " @RocketMQListener methods processed on bean '"
                            + beanName + "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }
}
