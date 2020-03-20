
package com.github.gungnirlaevatain.mq.producer.kafka;

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
@EnableConfigurationProperties({KafkaProducerProperty.class, ProducerProperty.class})
@Slf4j
public class KafkaProducerAutoConfig {
    @Autowired
    private KafkaProducerProperty kafkaProducerProperty;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public Producer createProducer() {
        Producer producer = new KafkaProducer();
        producer.init(kafkaProducerProperty);
        log.info("MQ Type [{}] producer has been created", MqType.Kafka);
        return producer;
    }
}
