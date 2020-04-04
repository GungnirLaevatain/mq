package com.github.gungnirlaevatain.mq.consumer.rocketmq;

import com.github.gungnirlaevatain.mq.consumer.ConsumerProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({RocketMqConsumerProperty.class, ConsumerProperty.class})
public class RocketMqConsumerAutoConfig {

    @Autowired
    private RocketMqConsumerProperty rocketMqConsumerProperty;

    @Bean
    @ConditionalOnMissingBean
    public CustomRocketMqAnnotationBeanPostProcessor customRocketMqAnnotationBeanPostProcessor()
            throws IllegalAccessException, InstantiationException {
        CustomRocketMqAnnotationBeanPostProcessor beanPostProcessor = new CustomRocketMqAnnotationBeanPostProcessor();
        if (rocketMqConsumerProperty.getMessageDeserialize() != null) {
            beanPostProcessor.setMessageDeserialize(rocketMqConsumerProperty.getMessageDeserialize().newInstance());
        } else {
            beanPostProcessor.setMessageDeserialize(new DefaultRocketMqMessageDeserialize());
        }
        return beanPostProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    public RocketMqListenerEndpointRegistry rocketMqListenerEndpointRegistry() {
        System.setProperty("rocketmq.client.log.loadconfig", "false");
        return new RocketMqListenerEndpointRegistry();
    }

}
