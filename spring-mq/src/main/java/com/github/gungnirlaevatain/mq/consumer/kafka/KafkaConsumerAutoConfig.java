

package com.github.gungnirlaevatain.mq.consumer.kafka;

import com.github.gungnirlaevatain.mq.MqException;
import com.github.gungnirlaevatain.mq.consumer.ConsumerProperty;
import com.github.gungnirlaevatain.mq.consumer.ListenerType;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerConfigUtils;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableConfigurationProperties({KafkaConsumerProperty.class, ConsumerProperty.class})
@SuppressWarnings("unchecked")
public class KafkaConsumerAutoConfig {

    @Autowired
    private KafkaConsumerProperty kafkaConsumerProperty;

    @Bean(name = KafkaListenerConfigUtils.KAFKA_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
    public KafkaListenerAnnotationBeanPostProcessor kafkaListenerAnnotationBeanPostProcessor() {
        return new CustomKafkaAnnotationBeanPostProcessor<>();
    }

    @Bean(name = KafkaListenerConfigUtils.KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME)
    public KafkaListenerEndpointRegistry defaultKafkaListenerEndpointRegistry() {
        return new KafkaListenerEndpointRegistry();
    }

    @Bean(name = KafkaListenerAnnotationBeanPostProcessor.DEFAULT_KAFKA_LISTENER_CONTAINER_FACTORY_BEAN_NAME)
    public KafkaListenerContainerFactory defaultKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaConsumerProperty));
        factory.setConcurrency(kafkaConsumerProperty.getConcurrency());
        factory.getContainerProperties().setPollTimeout(kafkaConsumerProperty.getPollTimeout());
        factory.getContainerProperties().setAckMode(kafkaConsumerProperty.getAckMode());

        Class<RecordFilterStrategy> recordFilterStrategy = kafkaConsumerProperty.getRecordFilter();

        if (recordFilterStrategy != null) {
            try {
                factory.setRecordFilterStrategy(recordFilterStrategy.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MqException(e);
            }
        } else {
            factory.setRecordFilterStrategy(new DafaultRecordFilterStrategy());
        }

        Class<RecordMessageConverter> converter = kafkaConsumerProperty.getMessageConverter();
        if (converter != null) {
            try {
                factory.setMessageConverter(converter.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MqException(e);
            }
        }

        Class<ErrorHandler> errorHandler = kafkaConsumerProperty.getErrorHandler();
        if (errorHandler != null) {
            try {
                factory.setErrorHandler(errorHandler.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MqException(e);
            }
        }
        return factory;
    }

    private ConsumerFactory<String, String> consumerFactory(KafkaConsumerProperty consumerProperty) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(consumerProperty));
    }

    private Map<String, Object> consumerConfigs(KafkaConsumerProperty consumerProperty) {
        Map<String, Object> propsMap = new HashMap<>(8);
        propsMap.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                consumerProperty.getServer());
        propsMap.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                consumerProperty.isEnableAutoCommit());
        propsMap.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
                consumerProperty.getAutoCommitIntervalMs());
        propsMap.put(org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
                consumerProperty.getSessionTimeoutMs());
        propsMap.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                consumerProperty.getKeyDeserializerClass());
        propsMap.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                consumerProperty.getValueDeserializerClass());
        if (consumerProperty.getListenerType() == ListenerType.BROADCASTING) {
            propsMap.put(ConsumerConfig.GROUP_ID_CONFIG,
                    consumerProperty.getGroupId() + "@" + UUID.randomUUID().toString());
        } else {
            propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperty.getGroupId());
        }
        propsMap.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                consumerProperty.getAutoOffsetReset());
        return propsMap;

    }
}
