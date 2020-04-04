package com.github.gungnirlaevatain.mq.producer.rabbitmq;

import com.github.gungnirlaevatain.mq.producer.Producer;
import com.github.gungnirlaevatain.mq.producer.ProducerProperty;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Objects;

@EnableConfigurationProperties({RabbitMqProducerProperty.class, ProducerProperty.class})
@AutoConfigureBefore(RabbitAutoConfiguration.class)
public class RabbitMqProducerAutoConfig {

    @Autowired
    private RabbitMqProducerProperty mqProperty;

    @Bean
    @ConditionalOnMissingBean
    public Producer producer() {
        RabbitMqProducer rabbitMqProducer = new RabbitMqProducer();
        rabbitMqProducer.init(mqProperty);
        return rabbitMqProducer;
    }

    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    @ConditionalOnMissingBean(RabbitTemplate.class)
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) throws IllegalAccessException, InstantiationException {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        if (mqProperty.getMessageConverter() != null) {
            rabbitTemplate.setMessageConverter(mqProperty.getMessageConverter().newInstance());
        }
        rabbitTemplate.setMandatory(determineMandatoryFlag());
        RabbitProperties.Template templateProperties = mqProperty.getTemplate();
        RabbitProperties.Retry retryProperties = templateProperties.getRetry();
        if (retryProperties.isEnabled()) {
            rabbitTemplate.setRetryTemplate(createRetryTemplate(retryProperties));
        }
        if (templateProperties.getReceiveTimeout() != null) {
            rabbitTemplate.setReceiveTimeout(templateProperties.getReceiveTimeout().getSeconds());
        }
        if (templateProperties.getReplyTimeout() != null) {
            rabbitTemplate.setReplyTimeout(templateProperties.getReplyTimeout().getSeconds());
        }
        return rabbitTemplate;
    }

    private boolean determineMandatoryFlag() {
        Boolean mandatory = mqProperty.getTemplate().getMandatory();
        return (mandatory != null ? mandatory : mqProperty.isPublisherReturns());
    }

    private RetryTemplate createRetryTemplate(RabbitProperties.Retry properties) {
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(properties.getMaxAttempts());
        template.setRetryPolicy(policy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(properties.getInitialInterval().getSeconds());
        backOffPolicy.setMultiplier(properties.getMultiplier());
        backOffPolicy.setMaxInterval(properties.getMaxInterval().getSeconds());
        template.setBackOffPolicy(backOffPolicy);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public ConnectionFactory rabbitConnectionFactory()
            throws Exception {
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        factory.setPort(mqProperty.getPort());
        factory.setHost(mqProperty.getHost());
        factory.setUsername(mqProperty.getUsername());
        factory.setPassword(mqProperty.getPassword());
        factory.setVirtualHost(mqProperty.getVirtualHost());
        factory.setConnectionTimeout(mqProperty.getConnectionTimeout());
        factory.setRequestedHeartbeat(mqProperty.getRequestedHeartbeat());
        factory.afterPropertiesSet();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(Objects.requireNonNull(factory.getObject()));
        connectionFactory.setPublisherConfirms(mqProperty.isPublisherConfirms());
        connectionFactory.setAddresses(mqProperty.determineAddresses());
        connectionFactory.setPublisherReturns(mqProperty.isPublisherReturns());
        connectionFactory.setCacheMode(mqProperty.getCacheMode());
        connectionFactory.setChannelCacheSize(mqProperty.getChannelSize());
        connectionFactory.setConnectionCacheSize(mqProperty.getConnectionSize());
        connectionFactory.setChannelCheckoutTimeout(mqProperty.getCheckoutTimeout());
        return connectionFactory;
    }
}
