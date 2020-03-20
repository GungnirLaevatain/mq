
package com.github.gungnirlaevatain.mq;

import lombok.Getter;

public enum MqType {

    ;

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
