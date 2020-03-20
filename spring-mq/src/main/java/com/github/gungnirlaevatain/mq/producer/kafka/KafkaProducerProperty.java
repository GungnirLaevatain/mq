
package com.github.gungnirlaevatain.mq.producer.kafka;

import com.github.gungnirlaevatain.mq.MqType;
import com.github.gungnirlaevatain.mq.producer.ProducerProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = KafkaProducerProperty.PREFIX)
@Data
public class KafkaProducerProperty extends ProducerProperty {
    public static final String PREFIX = ProducerProperty.PREFIX + ".kafka";

    public KafkaProducerProperty() {
        super();
        super.setType(MqType.Kafka);
    }

    private String server;

    private Integer retries;

    private Integer batchSize;

    private Integer lingerMs;

    private Integer bufferMemory;

    private String compressType = "lz4";

    private Class<?> keySerializerClass = StringSerializer.class;

    private Class<?> valueSerializerClass = StringSerializer.class;

    private Class<ProducerListener<?, ?>> producerListener;

    private Class<RecordMessageConverter> messageConverter;
}
