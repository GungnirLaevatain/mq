
package com.github.gungnirlaevatain.mq.consumer.kafka;

import com.github.gungnirlaevatain.mq.ReflectAnnotationUtil;
import com.github.gungnirlaevatain.mq.consumer.ConsumerUtil;
import com.github.gungnirlaevatain.mq.consumer.CustomBeanPostProcessor;
import com.github.gungnirlaevatain.mq.consumer.ListenerType;
import com.github.gungnirlaevatain.mq.consumer.MqListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodIntrospector;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CustomKafkaAnnotationBeanPostProcessor<K, V> extends KafkaListenerAnnotationBeanPostProcessor<K, V> implements CustomBeanPostProcessor {


    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));


    @Override
    public void setEndpointRegistry(@Autowired KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        super.setEndpointRegistry(kafkaListenerEndpointRegistry);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!nonAnnotatedClasses.contains(bean.getClass())) {
            Map<Method, Set<MqListener>> annotatedMethods = ConsumerUtil.getAnnotatedMethods(bean);
            if (annotatedMethods.isEmpty()) {
                nonAnnotatedClasses.add(bean.getClass());
                if (log.isTraceEnabled()) {
                    log.trace("No @KafkaListener annotations found on bean type: " + bean.getClass());
                }
            } else {
                // Non-empty set of methods
                for (Map.Entry<Method, Set<MqListener>> entry : annotatedMethods.entrySet()) {
                    Method sourceMethod = entry.getKey();
                    Method methodToUse = ConsumerUtil.checkProxy(sourceMethod, bean);
                    for (MqListener listener : entry.getValue()) {
                        //由自定义的消息处理者进行消息的代理转发
                        KafkaMessageHandler handler = new KafkaMessageHandler(bean, methodToUse);
                        Map<Method, Set<KafkaListener>> kafkaListenerMethods = MethodIntrospector.selectMethods(KafkaMessageHandler.class,
                                (MethodIntrospector.MetadataLookup<Set<KafkaListener>>) method -> {
                                    Set<KafkaListener> listenerMethods = ConsumerUtil.findListenerAnnotations(method, KafkaListener.class);
                                    return (!listenerMethods.isEmpty() ? listenerMethods : null);
                                });

                        for (Map.Entry<Method, Set<KafkaListener>> invokeEntry : kafkaListenerMethods.entrySet()) {
                            Method invokeMethod = invokeEntry.getKey();
                            for (KafkaListener kafkaListener : invokeEntry.getValue()) {
                                updateAnnotationValue(kafkaListener, listener);
                                processKafkaListener(kafkaListener, invokeMethod, handler, beanName);
                            }
                        }
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug(annotatedMethods.size() + " @KafkaListener methods processed on bean '"
                            + beanName + "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }

    private void updateAnnotationValue(KafkaListener kafkaListener, MqListener listener) {
        // 获取 valueCache
        Map<String, Object> valueCache = ReflectAnnotationUtil.getAnnotationMemberValues(kafkaListener);
        // 修改 value 属性值
        valueCache.put("topics", listener.topics());
        if (listener.type().equals(ListenerType.BROADCASTING)) {
            valueCache.put("groupId", listener.groupName() + "@" + UUID.randomUUID().toString());
        } else {
            valueCache.put("groupId", listener.groupName());
        }
    }

}
