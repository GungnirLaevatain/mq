
package com.github.gungnirlaevatain.mq.producer.rocketmq;

import com.github.gungnirlaevatain.mq.MqType;
import com.github.gungnirlaevatain.mq.producer.Producer;
import com.github.gungnirlaevatain.mq.producer.ProducerProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RocketMqProducerProperty.class, ProducerProperty.class})
@Slf4j
public class RocketMqProducerAutoConfig {
    @Autowired
    private RocketMqProducerProperty rocketMqProducerProperty;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public Producer createProducer() {
        System.setProperty("rocketmq.client.log.loadconfig", "false");
        Producer producer = new RocketMqProducer();
        producer.init(rocketMqProducerProperty);
        log.info("MQ Type [{}] producer has been created", MqType.RocketMQ);
        return producer;
    }
}
