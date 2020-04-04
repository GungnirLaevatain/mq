package com.github.gungnirlaevatain.mq.producer.kafka;

import com.github.gungnirlaevatain.mq.MqType;
import com.github.gungnirlaevatain.mq.producer.Producer;
import com.github.gungnirlaevatain.mq.producer.ProducerProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({ProducerProperty.class})
@Import(KafkaAutoConfiguration.class)
@Slf4j
public class KafkaProducerAutoConfig {

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public Producer createProducer() {
        Producer producer = new KafkaProducer();
        producer.init(null);
        log.info("MQ Type [{}] producer has been created", MqType.Kafka);
        return producer;
    }
}
