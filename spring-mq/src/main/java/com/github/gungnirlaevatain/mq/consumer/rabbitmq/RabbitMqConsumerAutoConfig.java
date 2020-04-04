package com.github.gungnirlaevatain.mq.consumer.rabbitmq;

import com.github.gungnirlaevatain.mq.consumer.ConsumerProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Slf4j
@EnableConfigurationProperties({RabbitMqConsumerProperty.class, ConsumerProperty.class})
@AutoConfigureBefore(RabbitAutoConfiguration.class)
public class RabbitMqConsumerAutoConfig {

    @Autowired
    private RabbitMqConsumerProperty mqProperty;

    @Bean(name = RabbitListenerConfigUtils.RABBIT_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME)
    public RabbitListenerEndpointRegistry defaultRabbitListenerEndpointRegistry() {
        return new RabbitListenerEndpointRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitListenerAnnotationBeanPostProcessor rabbitListenerAnnotationBeanPostProcessor()
            throws IllegalAccessException, InstantiationException {
        CustomRabbitAnnotationBeanPostProcessor beanPostProcessor = new CustomRabbitAnnotationBeanPostProcessor();
        if (mqProperty.getMessageConverter() != null) {
            beanPostProcessor.setMessageConverter(mqProperty.getMessageConverter().newInstance());
        }
        return beanPostProcessor;
    }


    @Bean(name = "rabbitListenerContainerFactory")
    @ConditionalOnMissingBean(name = "rabbitListenerContainerFactory")
    public RabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) throws IllegalAccessException, InstantiationException {
        AbstractRabbitListenerContainerFactory factory;
        switch (mqProperty.getListener().getType()) {
            case DIRECT: {
                factory = createDirectRabbitListenerContainerFactory();
                break;
            }
            case SIMPLE:
            default: {
                factory = createSimpleRabbitListenerContainerFactory();
            }
        }
        factory.setConnectionFactory(connectionFactory);
        if (mqProperty.getMessageConverter() != null) {
            factory.setMessageConverter(mqProperty.getMessageConverter().newInstance());
        }
        if (mqProperty.getConsumerTagStrategy() != null) {
            factory.setConsumerTagStrategy(mqProperty.getConsumerTagStrategy().newInstance());
        }
        return factory;
    }

    private AbstractRabbitListenerContainerFactory createDirectRabbitListenerContainerFactory() {
        DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
        RabbitProperties.DirectContainer listenerConfig = mqProperty.getListener().getDirect();
        factory.setAutoStartup(listenerConfig.isAutoStartup());
        if (listenerConfig.getAcknowledgeMode() != null) {
            factory.setAcknowledgeMode(listenerConfig.getAcknowledgeMode());
        }
        if (listenerConfig.getIdleEventInterval() != null) {
            factory.setIdleEventInterval(listenerConfig.getIdleEventInterval().get(ChronoUnit.MILLIS));
        }
        if (listenerConfig.getConsumersPerQueue() != null) {
            factory.setConsumersPerQueue(listenerConfig.getConsumersPerQueue());
        }
        if (listenerConfig.getPrefetch() != null) {
            factory.setPrefetchCount(listenerConfig.getPrefetch());
        }
        if (listenerConfig.getDefaultRequeueRejected() != null) {
            factory.setDefaultRequeueRejected(listenerConfig.getDefaultRequeueRejected());
        }
        RabbitProperties.ListenerRetry retryConfig = listenerConfig.getRetry();
        if (retryConfig.isEnabled()) {
            RetryInterceptorBuilder<?, ?> builder = (retryConfig.isStateless()
                    ? RetryInterceptorBuilder.stateless()
                    : RetryInterceptorBuilder.stateful());
            builder.maxAttempts(retryConfig.getMaxAttempts())
                    .backOffOptions(retryConfig.getInitialInterval().get(ChronoUnit.MILLIS),
                            retryConfig.getMultiplier(), retryConfig.getMaxInterval().get(ChronoUnit.MILLIS))
                    .recoverer(new RejectAndDontRequeueRecoverer());
            factory.setAdviceChain(builder.build());
        }
        return factory;
    }


    private AbstractRabbitListenerContainerFactory createSimpleRabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        RabbitProperties.SimpleContainer listenerConfig = mqProperty.getListener().getSimple();
        factory.setAutoStartup(listenerConfig.isAutoStartup());
        if (listenerConfig.getAcknowledgeMode() != null) {
            factory.setAcknowledgeMode(listenerConfig.getAcknowledgeMode());
        }
        if (listenerConfig.getConcurrency() != null) {
            factory.setConcurrentConsumers(listenerConfig.getConcurrency());
        }
        if (listenerConfig.getMaxConcurrency() != null) {
            factory.setMaxConcurrentConsumers(listenerConfig.getMaxConcurrency());
        }
        if (listenerConfig.getPrefetch() != null) {
            factory.setPrefetchCount(listenerConfig.getPrefetch());
        }
        if (listenerConfig.getTransactionSize() != null) {
            factory.setTxSize(listenerConfig.getTransactionSize());
        }
        if (listenerConfig.getDefaultRequeueRejected() != null) {
            factory.setDefaultRequeueRejected(listenerConfig.getDefaultRequeueRejected());
        }
        RabbitProperties.ListenerRetry retryConfig = listenerConfig.getRetry();
        if (retryConfig.isEnabled()) {
            RetryInterceptorBuilder<?, ?> builder = (retryConfig.isStateless()
                    ? RetryInterceptorBuilder.stateless()
                    : RetryInterceptorBuilder.stateful());
            builder.maxAttempts(retryConfig.getMaxAttempts());
            builder.backOffOptions(retryConfig.getInitialInterval().get(ChronoUnit.MILLIS),
                    retryConfig.getMultiplier(), retryConfig.getMaxInterval().get(ChronoUnit.MILLIS));
            builder.recoverer(new RejectAndDontRequeueRecoverer());
            factory.setAdviceChain(builder.build());
        }
        return factory;
    }


    @Bean
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public ConnectionFactory rabbitConnectionFactory()
            throws Exception {
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        factory.setHost(mqProperty.getHost());
        factory.setPort(mqProperty.getPort());
        factory.setUsername(mqProperty.getUsername());
        factory.setPassword(mqProperty.getPassword());
        factory.setVirtualHost(mqProperty.getVirtualHost());
        factory.setRequestedHeartbeat(mqProperty.getRequestedHeartbeat());
        factory.setConnectionTimeout(mqProperty.getConnectionTimeout());
        factory.afterPropertiesSet();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(Objects.requireNonNull(factory.getObject()));
        connectionFactory.setAddresses(mqProperty.determineAddresses());
        connectionFactory.setPublisherConfirms(mqProperty.isPublisherConfirms());
        connectionFactory.setPublisherReturns(mqProperty.isPublisherReturns());
        connectionFactory.setChannelCacheSize(mqProperty.getChannelSize());
        connectionFactory.setCacheMode(mqProperty.getCacheMode());
        connectionFactory.setConnectionCacheSize(mqProperty.getConnectionSize());
        connectionFactory.setChannelCheckoutTimeout(mqProperty.getCheckoutTimeout());
        return connectionFactory;
    }
}
