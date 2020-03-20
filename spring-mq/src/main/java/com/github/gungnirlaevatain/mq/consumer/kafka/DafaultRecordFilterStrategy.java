
package com.github.gungnirlaevatain.mq.consumer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

@Slf4j
public class DafaultRecordFilterStrategy implements RecordFilterStrategy {
    /**
     * Return true if the record should be discarded.
     *
     * @param consumerRecord the record.
     * @return true to discard.
     */
    @Override
    public boolean filter(ConsumerRecord consumerRecord) {
        log.debug("partition is {},key is {},topic is {}",
                consumerRecord.partition(), consumerRecord.key(), consumerRecord.topic());
        return false;
    }
}
