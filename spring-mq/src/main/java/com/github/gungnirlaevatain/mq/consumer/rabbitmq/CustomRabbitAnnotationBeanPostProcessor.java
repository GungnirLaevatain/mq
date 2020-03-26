

package com.github.gungnirlaevatain.mq.consumer.rabbitmq;

import com.github.gungnirlaevatain.mq.ReflectAnnotationUtil;
import com.github.gungnirlaevatain.mq.consumer.ConsumerUtil;
import com.github.gungnirlaevatain.mq.consumer.MqListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.core.MethodIntrospector;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CustomRabbitAnnotationBeanPostProcessor extends RabbitListenerAnnotationBeanPostProcessor {

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    @Setter
    private MessageConverter messageConverter = new SimpleMessageConverter();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!nonAnnotatedClasses.contains(bean.getClass())) {
            Map<Method, Set<MqListener>> annotatedMethods = ConsumerUtil.getAnnotatedMethods(bean);
            if (annotatedMethods.isEmpty()) {
                nonAnnotatedClasses.add(bean.getClass());
                if (log.isTraceEnabled()) {
                    log.trace("No @RabbitListener annotations found on bean type: " + bean.getClass());
                }
            } else {
                // Non-empty set of methods
                for (Map.Entry<Method, Set<MqListener>> entry : annotatedMethods.entrySet()) {
                    Method sourceMethod = entry.getKey();
                    Method methodToUse = ConsumerUtil.checkProxy(sourceMethod, bean);
                    for (MqListener listener : entry.getValue()) {
                        //由自定义的消息处理者进行消息的代理转发
                        RabbitMqMessageHandler handler = new RabbitMqMessageHandler(bean, methodToUse, messageConverter);
                        Map<Method, Set<RabbitListener>> kafkaListenerMethods = MethodIntrospector.selectMethods(RabbitMqMessageHandler.class,
                                (MethodIntrospector.MetadataLookup<Set<RabbitListener>>) method -> {
                                    Set<RabbitListener> listenerMethods = ConsumerUtil.findListenerAnnotations(method, RabbitListener.class);
                                    return (!listenerMethods.isEmpty() ? listenerMethods : null);
                                });

                        for (Map.Entry<Method, Set<RabbitListener>> invokeEntry : kafkaListenerMethods.entrySet()) {
                            Method invokeMethod = invokeEntry.getKey();
                            for (RabbitListener rabbitListener : invokeEntry.getValue()) {
                                updateAnnotationValue(rabbitListener, listener);
                                processAmqpListener(rabbitListener, invokeMethod, handler, beanName);
                            }
                        }
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug(annotatedMethods.size() + " @RabbitListener methods processed on bean '"
                            + beanName + "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }

    private void updateAnnotationValue(RabbitListener rabbitListener, MqListener listener) {
        Map<String, Object> valueCache = ReflectAnnotationUtil.getAnnotationMemberValues(rabbitListener);
        // 修改 value 属性值
        valueCache.put("group", listener.groupName());
        QueueBinding[] bindings = (QueueBinding[]) valueCache.get("bindings");
        //2个binging,一个为exchange(topic)的name,一个为queue(tags)的name
        Map<String, Object> memberValues = ReflectAnnotationUtil.getAnnotationMemberValues(bindings[0]);
        if (listener.topics().length > 0) {
            memberValues.put("key", listener.topics()[0]);
        }
        Map<String, Object> memberValues2 = ReflectAnnotationUtil.getAnnotationMemberValues(bindings[1]);
        if (listener.tags().length > 0) {
            memberValues2.put("key", listener.tags()[0]);
        }
        updateExchangeAnnotationValue((Exchange) memberValues.get("exchange"), listener);
        updateQueueAnnotationValue((Queue) memberValues.get("value"), listener);
        updateExchangeAnnotationValue((Exchange) memberValues2.get("exchange"), listener);
        updateQueueAnnotationValue((Queue) memberValues2.get("value"), listener);
    }

    private void updateExchangeAnnotationValue(Exchange exchange, MqListener listener) {
        if (listener.topics().length <= 0) {
            return;
        }
        Map<String, Object> valueCache = ReflectAnnotationUtil.getAnnotationMemberValues(exchange);
        // 修改 value 属性值
        valueCache.put("value", listener.topics()[0]);
    }

    private void updateQueueAnnotationValue(Queue queue, MqListener listener) {
        if (listener.tags().length <= 0) {
            return;
        }
        Map<String, Object> valueCache = ReflectAnnotationUtil.getAnnotationMemberValues(queue);
        // 修改 value 属性值
        valueCache.put("value", listener.tags()[0]);
    }


}


