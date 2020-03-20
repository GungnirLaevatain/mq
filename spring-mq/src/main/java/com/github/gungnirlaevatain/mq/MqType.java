
package com.github.gungnirlaevatain.mq;

import com.github.gungnirlaevatain.mq.consumer.kafka.KafkaConsumerAutoConfig;
import com.github.gungnirlaevatain.mq.producer.kafka.KafkaProducerAutoConfig;
import lombok.Getter;

public enum MqType {

    /**
     * 消息队列类型.
     */
    Kafka("kafka", KafkaConsumerAutoConfig.class, KafkaProducerAutoConfig.class);

    @Getter
    private String name;

    @Getter
    private Class<?> consumerConfig;
    @Getter
    private Class<?> producerConfig;

    MqType(String name, Class<?> consumerConfig, Class<?> producerConfig) {
        this.name = name;
        this.consumerConfig = consumerConfig;
        this.producerConfig = producerConfig;
    }

    public static MqType getByNameIgnoreCase(String name) {
        for (MqType type : MqType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
