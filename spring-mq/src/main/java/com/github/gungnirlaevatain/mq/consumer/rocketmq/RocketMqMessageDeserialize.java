package com.github.gungnirlaevatain.mq.consumer.rocketmq;

import java.util.Map;

public interface RocketMqMessageDeserialize {


    Object deserialize(byte[] bytes, Map<String, String> prop);

    <T> T deserialize(byte[] bytes, Map<String, String> prop, Class<T> clazz);

}
