

package com.github.gungnirlaevatain.mq.consumer.kafka;

import com.github.gungnirlaevatain.mq.consumer.ConsumerProperty;
import com.github.gungnirlaevatain.mq.consumer.ListenerType;
import lombok.Data;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@ConfigurationProperties(prefix = KafkaConsumerProperty.PREFIX)
@Data
public class KafkaConsumerProperty {
    public static final String PREFIX = ConsumerProperty.PREFIX + ".kafka";

    private int autoCommitIntervalMs = 1000;

    private int sessionTimeoutMs = 60 * 1000;

    private Class<?> keyDeserializerClass = StringDeserializer.class;

    private Class<?> valueDeserializerClass = StringDeserializer.class;

    private String groupId = "test-group";

    private String autoOffsetReset = "latest";

    private String server = "127.0.0.1:9092";

    private int concurrency = 3;

    private boolean enableAutoCommit = true;

    private long pollTimeout = 5000;

    private ListenerType listenerType = ListenerType.CLUSTERING;

    private ContainerProperties.AckMode ackMode = ContainerProperties.AckMode.BATCH;

    private Class<RecordFilterStrategy> recordFilter;

    private Class<RecordMessageConverter> messageConverter;

    private Class<ErrorHandler> errorHandler;

}