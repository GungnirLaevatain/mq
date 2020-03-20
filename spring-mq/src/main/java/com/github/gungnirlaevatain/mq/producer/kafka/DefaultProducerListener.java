

package com.github.gungnirlaevatain.mq.producer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;

@Slf4j
public class DefaultProducerListener implements ProducerListener {
    @Override
    public void onSuccess(String topic, Integer partition, Object key, Object value, RecordMetadata recordMetadata) {
        log.debug("kafka send message success, that topic is {}, partition is {}, key is {}, value is {}, recordMetadata is {}",
                topic, partition, key, value, recordMetadata);
    }

    @Override
    public void onError(String topic, Integer partition, Object key, Object value, Exception exception) {
        log.debug("kafka send message fail, that topic is {}, partition is {}, key is {}, value is {}, exception is {}",
                topic, partition, key, value, exception);

    }

    @Override
    public boolean isInterestedInSuccess() {
        return true;
    }
}
