package com.github.gungnirlaevatain.mq.consumer.kafka;

import com.github.gungnirlaevatain.mq.consumer.ConsumerProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerConfigUtils;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;

import java.time.Duration;


@EnableConfigurationProperties({ConsumerProperty.class})
public class KafkaConsumerAutoConfig {


    @Bean(name = KafkaListenerConfigUtils.KAFKA_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
    public KafkaListenerAnnotationBeanPostProcessor kafkaListenerAnnotationBeanPostProcessor() {
        return new CustomKafkaAnnotationBeanPostProcessor<>();
    }

    @Bean(name = KafkaListenerConfigUtils.KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME)
    public KafkaListenerEndpointRegistry defaultKafkaListenerEndpointRegistry() {
        return new KafkaListenerEndpointRegistry();
    }

    @ConditionalOnBean(KafkaConsumerAutoConfig.class)
    @Import(KafkaAutoConfiguration.class)
    @Configuration
    public static class kafkaListenerContainerConfiguration {
        @Autowired
        private KafkaProperties properties;

        @Autowired
        private RecordMessageConverter messageConverter;

        @Autowired
        private KafkaTemplate<Object, Object> kafkaTemplate;

        @Autowired
        private KafkaAwareTransactionManager<Object, Object> transactionManager;

        @Autowired
        private ErrorHandler errorHandler;

        @Autowired
        private AfterRollbackProcessor<Object, Object> afterRollbackProcessor;

        @Bean
        @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
        public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConsumerFactory<Object, Object> kafkaConsumerFactory) {
            ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
            configure(factory, kafkaConsumerFactory);
            return factory;
        }


        public void configure(ConcurrentKafkaListenerContainerFactory<Object, Object> listenerFactory,
                              ConsumerFactory<Object, Object> consumerFactory) {
            listenerFactory.setConsumerFactory(consumerFactory);
            configureListenerFactory(listenerFactory);
            configureContainer(listenerFactory.getContainerProperties());
        }

        private void configureListenerFactory(ConcurrentKafkaListenerContainerFactory<Object, Object> factory) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            KafkaProperties.Listener properties = this.properties.getListener();
            map.from(properties::getConcurrency).to(factory::setConcurrency);
            map.from(messageConverter).to(factory::setMessageConverter);
            map.from(kafkaTemplate).to(factory::setReplyTemplate);
            map.from(properties::getType)
                    .whenEqualTo(KafkaProperties.Listener.Type.BATCH)
                    .toCall(() -> factory.setBatchListener(true));
            map.from(errorHandler).to(factory::setErrorHandler);
            map.from(afterRollbackProcessor).to(factory::setAfterRollbackProcessor);
        }

        private void configureContainer(ContainerProperties container) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            KafkaProperties.Listener properties = this.properties.getListener();
            map.from(properties::getAckMode).to(container::setAckMode);
            map.from(properties::getClientId).to(container::setClientId);
            map.from(properties::getAckCount).to(container::setAckCount);
            map.from(properties::getAckTime).as(Duration::toMillis).to(container::setAckTime);
            map.from(properties::getPollTimeout).as(Duration::toMillis).to(container::setPollTimeout);
            map.from(properties::getNoPollThreshold).to(container::setNoPollThreshold);
            map.from(properties::getIdleEventInterval).as(Duration::toMillis).to(container::setIdleEventInterval);
            map.from(properties::getMonitorInterval).as(Duration::getSeconds).as(Number::intValue)
                    .to(container::setMonitorInterval);
            map.from(properties::getLogContainerConfig).to(container::setLogContainerConfig);
            map.from(transactionManager).to(container::setTransactionManager);
        }
    }

}
