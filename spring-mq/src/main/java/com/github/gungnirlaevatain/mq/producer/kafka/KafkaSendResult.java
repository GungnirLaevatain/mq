
package com.github.gungnirlaevatain.mq.producer.kafka;

import com.github.gungnirlaevatain.mq.producer.MessageSendResult;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class KafkaSendResult extends MessageSendResult {

    private ProducerRecord producerRecord;

    private RecordMetadata recordMetadata;

}
