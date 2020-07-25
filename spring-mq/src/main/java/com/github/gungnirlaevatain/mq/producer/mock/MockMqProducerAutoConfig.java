package com.github.gungnirlaevatain.mq.producer.mock;

import com.github.gungnirlaevatain.mq.MqType;
import com.github.gungnirlaevatain.mq.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Slf4j
public class MockMqProducerAutoConfig {
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public Producer createProducer() {
        log.info("MQ Type [{}] producer has been created", MqType.Mock);
        return new MockProducer();
    }
}
